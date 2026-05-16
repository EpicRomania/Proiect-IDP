$ErrorActionPreference = "Stop"

$ProjectRoot = "C:\Users\Tactu\Desktop\Proiect-IDP-main"
$ClusterName = "eventhub"

function Resolve-KindCommand {
    $kindCommand = Get-Command kind -ErrorAction SilentlyContinue
    if ($kindCommand) {
        return $kindCommand.Source
    }

    $wingetKindPath = Join-Path $env:LOCALAPPDATA "Microsoft\WinGet\Packages\Kubernetes.kind_Microsoft.Winget.Source_8wekyb3d8bbwe\kind.exe"
    if (Test-Path $wingetKindPath) {
        return $wingetKindPath
    }

    throw "kind was not found. Install it with: winget install --exact --id Kubernetes.kind"
}

Set-Location $ProjectRoot
$Kind = Resolve-KindCommand

Write-Host "Using project root: $ProjectRoot"
Write-Host "Using kind: $Kind"

$ExistingClusters = & $Kind get clusters
if ($ExistingClusters -contains $ClusterName) {
    Write-Host "kind cluster '$ClusterName' already exists. Skipping cluster creation."
} else {
    & $Kind create cluster --name $ClusterName --config kind-config.yml
}

kubectl config use-context "kind-$ClusterName"

docker build -t eventhub/user-authentication-service:local ./services/user-authentication-service
docker build -t eventhub/event-management-service:local ./services/event-management-service
docker build -t eventhub/participation-service:local ./services/participation-service

& $Kind load docker-image eventhub/user-authentication-service:local --name $ClusterName
& $Kind load docker-image eventhub/event-management-service:local --name $ClusterName
& $Kind load docker-image eventhub/participation-service:local --name $ClusterName

kubectl apply -f k8s/
kubectl get pods -A -l app.kubernetes.io/part-of=eventhub

kubectl rollout status deployment/user-authentication-service -n eventhub-app
kubectl rollout status deployment/event-management-service -n eventhub-app
kubectl rollout status deployment/participation-service -n eventhub-app
kubectl rollout status deployment/kong -n eventhub-gateway
kubectl rollout status deployment/prometheus -n eventhub-observability
kubectl rollout status deployment/grafana -n eventhub-observability
kubectl rollout status deployment/adminer -n eventhub-management
kubectl rollout status deployment/portainer -n eventhub-management

Write-Host ""
Write-Host "EventHub Kubernetes stack is ready."
Write-Host "Grafana:    http://localhost:30030"
Write-Host "Adminer:    http://localhost:30088"
Write-Host "Portainer:  http://localhost:30090"
Write-Host "Prometheus: http://localhost:30091"
Write-Host "Kong API:   http://localhost:30080"
