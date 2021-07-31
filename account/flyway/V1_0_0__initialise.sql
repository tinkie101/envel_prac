CREATE TABLE IF NOT EXISTS account
(
    id      uuid NOT NULL PRIMARY KEY default uuid_generate_v4(),
    balance int                       DEFAULT 0
);