CREATE EXTENSION
IF NOT EXISTS pgcrypto;
    CREATE TABLE IF NOT EXISTS client
        (
            id       BIGINT NOT NULL         ,
            email    VARCHAR ( 255 ) NOT NULL,
            name     VARCHAR ( 255 ) NOT NULL,
            password VARCHAR ( 255 ) NOT NULL,
            PRIMARY KEY ( id )
        )
    ;
    CREATE TABLE IF NOT EXISTS income
        (
            id          BIGINT NOT NULL                            ,
                        date DATE                                  ,
            description TEXT                                       ,
            value       NUMERIC ( 38, 2 )                          ,
            client_id   BIGINT                                     ,
            PRIMARY KEY ( id )
        )
    ;
    CREATE TABLE IF NOT EXISTS expense
        (
            id          BIGINT NOT NULL                            ,
                        date DATE                                  ,
            description TEXT                                       ,
            value       NUMERIC ( 38, 2 )                          ,
            category    SMALLINT CHECK ( category BETWEEN 0 AND 9 ),
            client_id   BIGINT                                     ,
            PRIMARY KEY ( id )
        )
    ;
    CREATE TABLE IF NOT EXISTS authority
        (
            id   BIGINT NOT NULL         ,
            name VARCHAR ( 255 ) NOT NULL,
            PRIMARY KEY ( id )
        )
    ;
    CREATE TABLE IF NOT EXISTS client_authorities
        (
            client_id      BIGINT NOT NULL                            ,
            authorities_id BIGINT NOT NULL                            ,
            PRIMARY KEY ( client_id, authorities_id )                 ,
            FOREIGN KEY ( authorities_id ) REFERENCES authority ( id ),
            FOREIGN KEY ( client_id ) REFERENCES client ( id )
        )
    ;
    COMMIT;