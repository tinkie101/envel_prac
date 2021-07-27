CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

ALTER TABLE IF EXISTS account.account
    ALTER COLUMN balance
    TYPE decimal;