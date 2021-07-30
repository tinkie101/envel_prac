ALTER TABLE IF EXISTS account
    ADD COLUMN created_on timestamp with time zone NOT NULL DEFAULT NOW();