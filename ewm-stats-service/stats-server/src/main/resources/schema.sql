CREATE TABLE IF NOT EXISTS endpoint_hit (
                                            id       SERIAL CONSTRAINT endpoint_hit_pk PRIMARY KEY,
                                            app_name VARCHAR(128) NOT NULL,
                                            uri      VARCHAR(2000) NOT NULL,
                                            ip       VARCHAR(39) NOT NULL,
                                            hit_time TIMESTAMP NOT NULL
);
