# Proiect-IDP

EventHub este o platforma cloud-native pentru gestionarea evenimentelor, organizata in microservicii si rulata local cu Docker Compose sau intr-un cluster Kubernetes.

## Servicii implementate

- `user-authentication-service`: conturi, roluri, autentificare si token-uri de acces.
- `event-management-service`: creare, listare, modificare si stergere evenimente.
- `participation-service`: inscriere la evenimente, retragere si listarea participantilor.
- `user-authentication-db-service`: PostgreSQL pentru conturi si autentificare.
- `event-data-db-service`: PostgreSQL pentru evenimente si participari.
- `adminer`: administrare baze de date.
- `kong`: API Gateway pentru expunerea rutelor publice.
- `portainer`: administrare vizuala pentru cluster/containere.
- `prometheus` si `grafana`: colectare si vizualizare metrici expuse prin Spring Boot Actuator.

## Rulare locala cu Docker Compose

```powershell
docker compose up -d --build
docker compose ps
```

Servicii expuse local:

- Kong API Gateway: `http://localhost:8000`
- Prometheus: `http://localhost:9090`
- Grafana: `http://localhost:3000`
- Adminer: `http://localhost:8088`
- Portainer: `http://localhost:9000`

Oprire fara stergerea volumelor:

```powershell
docker compose stop
```

## Rulare cu Kubernetes

Instalare `kind`, daca lipseste:

```powershell
winget install --exact --id Kubernetes.kind
```

Creare cluster local:

```powershell
kind create cluster --name eventhub --config kind-config.yml
```

Build imagini locale:

```powershell
docker build -t eventhub/user-authentication-service:local ./services/user-authentication-service
docker build -t eventhub/event-management-service:local ./services/event-management-service
docker build -t eventhub/participation-service:local ./services/participation-service
```

Incarcare imagini in clusterul `kind`:

```powershell
kind load docker-image eventhub/user-authentication-service:local --name eventhub
kind load docker-image eventhub/event-management-service:local --name eventhub
kind load docker-image eventhub/participation-service:local --name eventhub
```

Aplicare manifesturi:

```powershell
kubectl apply -f k8s/
```

Verificare rollout:

```powershell
kubectl get pods -A -l app.kubernetes.io/part-of=eventhub
kubectl get svc -A -l app.kubernetes.io/part-of=eventhub
kubectl rollout status deployment/user-authentication-service -n eventhub-app
kubectl rollout status deployment/event-management-service -n eventhub-app
kubectl rollout status deployment/participation-service -n eventhub-app
```

Servicii expuse prin NodePort:

- Kong API Gateway: `http://localhost:30080`
- Kong Admin API: `http://localhost:30081`
- Adminer: `http://localhost:30088`
- Portainer: `http://localhost:30090`
- Prometheus: `http://localhost:30091`
- Grafana: `http://localhost:30030`

Stergere resurse Kubernetes:

```powershell
kubectl delete -f k8s/
kind delete cluster --name eventhub
```

## Rute principale prin Kong

Autentificare:

- `POST /auth/register`
- `POST /auth/login`
- `GET /auth/me`
- `GET /auth/users`

Evenimente:

- `POST /events`
- `GET /events`
- `GET /events/{id}`
- `PUT /events/{id}`
- `DELETE /events/{id}`

Participari:

- `POST /participations`
- `DELETE /participations?eventId={eventId}&userId={userId}`
- `GET /participations/events/{eventId}`
- `GET /participations/users/{userId}`

## Verificare

Validare Docker Compose:

```powershell
docker compose config --quiet
```

Validare manifesturi Kubernetes:

```powershell
kubectl apply --dry-run=client -f k8s/
```

Testele se ruleaza din directorul fiecarui microserviciu:

- `services/user-authentication-service`
- `services/event-management-service`
- `services/participation-service`

```powershell
docker run --rm -v "${PWD}:/workspace" -w /workspace maven:3.9.9-eclipse-temurin-21 mvn -B test
```
