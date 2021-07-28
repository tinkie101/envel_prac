CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE IF NOT EXISTS audit.transaction
(
    id         uuid    NOT NULL PRIMARY KEY default uuid_generate_v4(),
    account_id uuid    NOT NULL,
    type       varchar NOT NULL,
    amount     decimal                      DEFAULT 0
);