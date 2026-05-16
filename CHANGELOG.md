# Changelog - EventHub

Proiect: EventHub - platforma de gestionare a evenimentelor
Echipa: Negrea Andrei, Iancu Andrei-Vlad
Grupa: 344C4
Repository principal: https://github.com/EpicRomania/Proiect-IDP/tree/main

## [0.8.0] - 2026-05-16

### Added

- Finalizarea rularii EventHub pe Kubernetes cu manifesturi pentru namespace-uri separate:
  - `eventhub-app`;
  - `eventhub-data`;
  - `eventhub-gateway`;
  - `eventhub-observability`;
  - `eventhub-management`.
- Adaugarea resurselor Kubernetes pentru cele 3 microservicii proprii, bazele de date PostgreSQL, Kong, Adminer, Portainer, Prometheus si Grafana.
- Adaugarea Secret-urilor pentru credentialele bazelor de date, ConfigMap-urilor pentru Kong/Prometheus/Grafana si PVC-urilor pentru PostgreSQL, Grafana si Portainer.
- Adaugarea etichetelor de deployment, NetworkPolicy-urilor si initContainer-elor care asteapta bazele de date inainte de pornirea serviciilor Spring Boot.
- Adaugarea configuratiei `kind` si a scripturilor PowerShell pentru pornirea si oprirea stack-ului Kubernetes local.
- Actualizarea documentatiei de rulare si verificare pentru scenariul Kubernetes.

### Verification / Validation

```text
Verification showcase:
- PowerShell scripts: syntax OK
- Maven tests: auth 3/3, events 4/4, participation 5/5
- Docker Compose config: OK
- Kubernetes start script: OK
- Kubernetes pods: 10/10 Running, 0 restarts
- Kong routes: /auth, /events, /participations
- API smoke: register/login/event CRUD/participation flow OK
- Expected errors: duplicate register 409, bad login 401, duplicate participation 409, missing withdraw 404
- Prometheus targets: auth=1, events=1, participation=1
- Grafana health: ok
- Adminer: HTTP 200
- Portainer: 2.21.5
- Kubernetes stop script: OK
```

## [0.7.0] - 2026-05-10

### Added

- Configurarea monitorizarii Negrea pentru cele 3 servicii Spring Boot prin Actuator si Micrometer Prometheus.
- Definirea joburilor Prometheus pentru `user-authentication-service`, `event-management-service` si `participation-service`.
- Provisionarea sursei de date Prometheus in Grafana si adaugarea dashboard-ului pentru disponibilitatea serviciilor si rata requesturilor HTTP.

## [0.5.0] - 2026-05-06

### Added

- Integrarea modificarilor Iancu pentru infrastructura Docker Compose: politici `restart: unless-stopped` pentru bazele de date, Adminer, Kong si Portainer.
- Pin-uirea imaginilor `adminer:4.8.1` si `portainer/portainer-ce:2.21.5` pentru rulare reproductibila.
- Adaugarea dependentelor Adminer pe healthcheck-urile celor doua baze de date PostgreSQL.
- Atasarea Portainer la `management-network` si adaugarea retelei dedicate pentru administrare.

## [0.3.0] - 2026-05-01

### Added

- Finalizarea de catre Negrea a `user-authentication-service` cu utilizatori, roluri, BCrypt pentru parole, token-uri de acces si endpointurile `/auth/register`, `/auth/login`, `/auth/me`, `/auth/users`.
- Finalizarea de catre Negrea a `event-management-service` cu creare, listare, interogare, modificare si stergere evenimente persistate in PostgreSQL.
- Finalizarea de catre Negrea a `participation-service` cu inscriere, retragere, listarea participantilor si verificarea existentei evenimentelor in baza de date.

## [0.2.0] - 2026-04-26

### Added

- Adaugarea structurii initiale pentru cele 3 microservicii proprii:
  - `user-authentication-service`, responsabil de autentificare, autorizare, conturi si token-uri de acces;
  - `event-management-service`, responsabil de creare, editare, stergere si listare evenimente;
  - `participation-service`, responsabil de inscrierea utilizatorilor la evenimente, retragere si listarea participantilor.
- Implementarea initiala a modelelor, controllerelor, serviciilor si repository-urilor pentru functionalitatile de baza.
- Configurarea bazelor de date PostgreSQL pentru utilizatori/autentificare si pentru datele legate de evenimente.
- Adaugarea serviciului Adminer pentru inspectarea si administrarea bazelor de date.
- Adaugarea configuratiei initiale pentru Kong API Gateway, cu rolul de a expune rutele publice ale aplicatiei.
- Adaugarea serviciului Portainer pentru administrarea vizuala a containerelor si a stack-ului.
- Adaugarea configuratiei initiale pentru Prometheus si Grafana, folosite pentru monitorizare si observabilitate.
- Adaugarea fisierelor Docker/Docker Compose necesare pentru rularea locala a serviciilor dezvoltate pana in aceasta etapa.

### Changed

- Organizarea proiectului in componente separate, astfel incat fiecare microserviciu sa poata fi dezvoltat si rulat independent.
- Separarea logica a serviciilor si a bazelor de date prin retele Docker dedicate.
- Actualizarea structurii proiectului pentru a pregati extinderea catre Kubernetes.

### Fixed

- Corectarea configuratiilor initiale de conectare intre microservicii si bazele de date.
- Ajustarea variabilelor de mediu folosite pentru conectarea serviciilor in containere.

### In Progress

- Validarea completa a fluxului de autentificare si autorizare.
- Integrarea completa intre `event-management-service` si `participation-service`.
- Testarea rutelor expuse prin Kong API Gateway.
- Finalizarea configuratiei de monitoring pentru metrici aplicative relevante.
- Pregatirea configuratiei de deployment pentru Kubernetes.

### Progress Summary

- Stadiu estimat proiect: aproximativ 40%.
- Functionalitatile principale aflate in progres sunt autentificarea, administrarea evenimentelor, inscrierea la evenimente si rularea serviciilor in containere Docker.
- In aceasta etapa s-a pus accent pe structura microserviciilor, containerele de baza, bazele de date, conectivitatea initiala si componentele suport necesare pentru etapa finala.

## [0.1.0] - 2026-03-29

### Added

- Formarea echipei de proiect: Negrea Andrei si Iancu Andrei-Vlad.
- Alegerea temei proiectului: EventHub, platforma cloud-native pentru gestionarea evenimentelor.
- Stabilirea arhitecturii initiale bazate pe microservicii si containere Docker.
- Definirea componentelor principale:
  - `user-authentication-service`;
  - `event-management-service`;
  - `participation-service`;
  - servicii PostgreSQL pentru date;
  - Adminer;
  - Kong API Gateway;
  - Portainer;
  - Prometheus;
  - Grafana.
- Stabilirea comunicarii intre servicii prin HTTP/REST.
- Stabilirea rularii locale cu Docker Compose si a extinderii ulterioare catre Kubernetes.
- Crearea repository-ului principal pentru proiect.
