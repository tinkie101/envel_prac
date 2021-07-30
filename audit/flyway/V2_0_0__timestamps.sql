ALTER TABLE IF EXISTS transaction
    ADD COLUMN created_on timestamp with time zone NOT NULL DEFAULT NOW();