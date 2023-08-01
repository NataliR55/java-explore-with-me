drop table IF EXISTS stats;

create TABLE IF NOT EXISTS stats
(
    id    BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY NOT NULL,
    app                 VARCHAR(100)                            NOT NULL,
    uri                 VARCHAR(255)                            NOT NULL,
    ip                  VARCHAR(40)                             NOT NULL,
    timestamp           TIMESTAMP  WITHOUT TIME ZONE            NOT NULL
);

