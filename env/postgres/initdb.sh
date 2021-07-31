#!/bin/bash
set -e

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" \ <<-EOSQL
  CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

  CREATE USER $KEYCLOAK_USER WITH PASSWORD '${KEYCLOAK_PASSWORD}';
  CREATE SCHEMA IF NOT EXISTS keycloak AUTHORIZATION $KEYCLOAK_USER;

  CREATE USER $ACCOUNT_USER WITH PASSWORD '${ACCOUNT_PASSWORD}';
  CREATE SCHEMA IF NOT EXISTS account AUTHORIZATION $ACCOUNT_USER;

  CREATE USER $AUDIT_USER WITH PASSWORD '${AUDIT_PASSWORD}';
  CREATE SCHEMA IF NOT EXISTS audit AUTHORIZATION $AUDIT_USER;
EOSQL