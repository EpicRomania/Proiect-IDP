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

$ExistingClusters = & $Kind get clusters
if (-not ($ExistingClusters -contains $ClusterName)) {
    Write-Host "kind cluster '$ClusterName' does not exist. Nothing to stop."
    exit 0
}

kubectl config use-context "kind-$ClusterName"

Write-Host "Deleting EventHub Kubernetes resources..."
kubectl delete -f k8s/ --ignore-not-found=true

Write-Host "Deleting kind cluster '$ClusterName'..."
& $Kind delete cluster --name $ClusterName

Write-Host "EventHub Kubernetes stack stopped and local cluster deleted."
