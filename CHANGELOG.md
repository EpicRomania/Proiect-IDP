# Changelog - EventHub
Proiect: EventHub - platforma de gestionare a evenimentelor  
Echipa: Negrea Andrei, Iancu Andrei-Vlad  
Grupa: 344C4  
Repository principal: https://github.com/EpicRomania/Proiect-IDP

## [Unreleased]

### Planned
- Finalizarea functionalitatilor backend pentru toate cele 3 microservicii.
- Rularea aplicatiei intr-un cluster Docker Swarm cu un manager si doi workers.
- Adaugarea tag-urilor de deployment in configuratia de stack.
- Configurarea pipeline-ului de CI/CD pentru build si deployment automat.
- Stabilizarea rutelor expuse prin Kong API Gateway.
- Extinderea dashboard-urilor Grafana si a metricilor colectate cu Prometheus.

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
- Actualizarea structurii proiectului pentru a pregati extinderea catre Docker Swarm.

### Fixed
- Corectarea configuratiilor initiale de conectare intre microservicii si bazele de date.
- Ajustarea variabilelor de mediu folosite pentru conectarea serviciilor in containere.

### In Progress
- Validarea completa a fluxului de autentificare si autorizare.
- Integrarea completa intre `event-management-service` si `participation-service`.
- Testarea rutelor expuse prin Kong API Gateway.
- Finalizarea configuratiei de monitoring pentru metrici aplicative relevante.
- Pregatirea configuratiei de deployment pentru Docker Swarm.

### Progress Summary
- Stadiu estimat proiect: aproximativ 40%.
- Functionalitatile principale aflate in progres sunt autentificarea, administrarea evenimentelor, inscrierea la evenimente si rularea serviciilor in containere Docker.
- In aceasta etapa s-a pus accent pe structura microserviciilor, containerele de baza, bazele de date, conectivitatea initiala si componentele suport necesare pentru etapa finala.

### Contributions

#### Negrea Andrei
- Implementarea structurii de baza pentru `user-authentication-service`.
- Implementarea structurii de baza pentru `event-management-service`.
- Implementarea structurii de baza pentru `participation-service`.
- Definirea fluxurilor principale pentru autentificare, evenimente si participare.
- Configurarea initiala a monitorizarii cu Prometheus si Grafana.
- Integrarea initiala a microserviciilor cu mediul Docker.

#### Iancu Andrei-Vlad
- Configurarea `user-authentication-db-service` folosind PostgreSQL.
- Configurarea `event-data-db-service` folosind PostgreSQL.
- Configurarea serviciului Adminer pentru administrarea bazelor de date.
- Configurarea initiala a Kong API Gateway pentru rutarea cererilor externe.
- Configurarea Portainer pentru administrarea vizuala a containerelor.
- Definirea si ajustarea retelelor Docker necesare separarii logice a componentelor.

### Commit Evidence
> Completati aceasta sectiune dupa ce dati push la modificarile reale pentru Etapa 2. Cerinta mentioneaza ca implementarea de 40% trebuie dovedita prin commit-uri in repository.

- `TODO_HASH_1` - Initializare structura microservicii Spring Boot - Negrea Andrei
- `TODO_HASH_2` - Adaugare modele si endpoint-uri initiale pentru autentificare - Negrea Andrei
- `TODO_HASH_3` - Adaugare endpoint-uri initiale pentru evenimente si participare - Negrea Andrei
- `TODO_HASH_4` - Configurare servicii PostgreSQL si Adminer - Iancu Andrei-Vlad
- `TODO_HASH_5` - Configurare Kong, Portainer si retele Docker - Iancu Andrei-Vlad
- `TODO_HASH_6` - Configurare Prometheus si Grafana - Negrea Andrei

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
- Stabilirea rularii locale cu Docker Compose si a extinderii ulterioare catre Docker Swarm.
- Crearea repository-ului principal pentru proiect.

### Contributions

#### Negrea Andrei
- Asumarea implementarii microserviciilor proprii.
- Asumarea configurarii monitorizarii cu Prometheus si Grafana.

#### Iancu Andrei-Vlad
- Asumarea configurarii serviciilor de baza de date.
- Asumarea configurarii Kong API Gateway, Portainer si Adminer.

### Commit Evidence
- `3f126f9` - Initial commit - EpicRomania

