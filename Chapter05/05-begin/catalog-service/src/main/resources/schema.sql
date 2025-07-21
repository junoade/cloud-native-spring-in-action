DROP TABLE IF EXISTS book;

CREATE TABLE book (
    id          BIGSERIAL PRIMARY KEY NOT NULL,
    author      VARCHAR(255) NOT NULL,
    isbn        VARCHAR(255) NOT NULL,
    price       float8 NOT NULL,
    title       VARCHAR(255) NOT NULL,
    version     integer NOT NULL
);