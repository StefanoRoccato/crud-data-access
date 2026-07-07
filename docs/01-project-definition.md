# Definizione progetto

## Obiettivo
Generare automaticamente il **core backend Java** di accesso dati per CRUD banali COBOL/Oracle, mantenendo l'accesso reale tramite procedure PL/SQL omonime al modulo COBOL.

## Standard Java recepiti
- Java 17/21, Spring Boot 3.x, Maven
- package structure enterprise-style
- constructor injection only
- DTO come `record` Java
- SLF4J logging
- JUnit 5 / Mockito / Testcontainers predisposti
- constants dedicate
- niente business logic nel repository

## Adattamento deliberato rispetto allo standard allegato
Non vengono generate:
- `controller`
- `model.entity`
- `mapper` JPA
- repository Spring Data JPA

Motivo: il componente generato non è un microservizio completo, ma un **motore di accesso dati riusabile**.
