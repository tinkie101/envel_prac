# Getting Started

## Docker

The easiest way to get up and running is with Docker and Docker-Compose.

1. Navigate into the `env` directory
2. Rename the `.env-example` to `.env` and set the secrets accordingly * DO NOT COMMIT `.env` with your secrets
3. run `docker-compose up --env-file .env up -d` or in the project's root directory run `make dev-up` to use the
   Makefile

This will start the required docker containers with the variables set in the `docker-compose.yml` file. By default, this
is:

1. Postgres
    * Port 4321
    * Runs `./env/.env/postgres/initdb.sh` on startup
        * Creates 3 schemes with users (defined in `./env/.env` file)
            * Keycloak, account, audit
            * Users can only access their own schema
2. Keycloak
    * Port 5001
    * User credentials stored in `./env/.env`
    * Automatically imports default `envel` realm defined in `./env/keycloak/realm-export`

The `./env/.env` file is used to load sensitive info such as usernames and password, do not commit into repository.

## Flyway

It is required to run the flyway migrate command manually before starting either service. This will generate the
required tables in the relevant schemas for the 2 microservices.

It is important to run these commands with the following environment variables set:

* AUDIT_USER
* POSTGRES_USER
* ACCOUNT_PASSWORD
* AUDIT_PASSWORD
* POSTGRES_PASSWORD
* ACCOUNT_USER

You can do this by either adding it into your IDE's run configuration, or by setting the environment variables in your
console before executing the gradle task. For example while in project root, run linux
command:  `set -a; source ./env/.env; set +a` and then `./gradlew flywayMigrate`

## Spring Services

There are 2 main spring services

1. Account
2. Audit

Make sure to have the following environment variables set when running the services:
* POSTGRES_PASSWORD
* POSTGRES_USER

where each service will use it's own schema and postgres user for that schema 

The `Account` service should be the main entry point, as it has the client facing functionality and uses the `Audit`
service behind the scenes. Each service has 3 parts:

1. `main`: Source code
2. `test`: Unit tests
3. `integration`: Integration tests

Integration tests are split out since they require an environment to start up around it.

### 1. Account service

Runs default on `http://localhost:8080` and has 2 endpoints

1. `/gui`:
    * An unsecured endpoint that load GraphIQL client gui
    * Set the `Authorization: Bearer <token>` in the gui (bottom left corner) to allow communication to
      secured `/graphql` endpoint
2. `/graphql`:
    * Secured GraphQL endpoint. Can only access it with `Authorization: Bearer <token>` header.
    * Mutation calls require the user the have the `mutate` role assigned in keycloak. After assigning make sure to get
      a new token that contains the role

### 2. Audit service

Runs default on `http://localhost:8081` and has 2 endpoints

1. `/gui`:
    * An unsecured endpoint that load GraphIQL client gui
    * Set the `Authorization: Bearer <token>` in the gui (bottom left corner) to allow communication to
      secured `/graphql` endpoint
2. `/graphql`:
    * Secured GraphQL endpoint. Can only access it with `Authorization: Bearer <token>` header.
    * Mutation calls require the user the have the `mutate` role assigned in keycloak. After assigning make sure to get
      a new token that contains the role

## GraphQL server

Both microservices use [graphql-spqr-spring-boot-starter](https://github.com/leangen/graphql-spqr-spring-boot-starter)
and more details can also be found [here](https://github.com/leangen/graphql-spqr)

## GraphQL client

The `Account` microservice
uses [GraphQL Kotlin](https://opensource.expediagroup.com/graphql-kotlin/docs/client/client-overview/)
The client is configured to not run introspection but rather use a local schema file. This is to allow unit/integration
tests without relying on a GraphQL server to be up and running