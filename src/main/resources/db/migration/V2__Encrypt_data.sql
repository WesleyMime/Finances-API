CREATE EXTENSION
IF NOT EXISTS pgcrypto;
    CREATE TABLE client_backup AS
    SELECT
        *
    FROM
        client;
    -- Step 1: Temporarily cast the text to bytea for encryption
    UPDATE
        client
    SET
        email = pgp_sym_encrypt(email::text, '${encryption_key}'::text)
    WHERE
        email IS NOT NULL;
    UPDATE
        client
    SET
        name = pgp_sym_encrypt(name::text, '${encryption_key}'::text)
    WHERE
        name IS NOT NULL;
    -- Step 2: Now alter the column type, which should succeed as the data is already encrypted bytea
    ALTER TABLE client
        ALTER COLUMN email TYPE bytea USING email::bytea,
        ALTER COLUMN name TYPE bytea USING name::bytea;
    COMMIT;
    CREATE TABLE income_backup AS
    SELECT
        *
    FROM
        income;
    UPDATE
        income
    SET
        description = pgp_sym_encrypt(description::text, '${encryption_key}'::text)
    WHERE
        description IS NOT NULL;
    ALTER TABLE income
        ALTER COLUMN description TYPE bytea USING description::bytea;
    COMMIT;
    CREATE TABLE expense_backup AS
    SELECT
        *
    FROM
        expense;
    UPDATE
        expense
    SET
        description = pgp_sym_encrypt(description::text, '${encryption_key}'::text)
    WHERE
        description IS NOT NULL;
    ALTER TABLE expense
        ALTER COLUMN description TYPE bytea USING description::bytea;
    COMMIT;
    DROP TABLE client_backup;
    DROP TABLE income_backup;
    DROP TABLE expense_backup;