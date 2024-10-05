DROP SEQUENCE IF EXISTS author_id_seq;
CREATE SEQUENCE author_id_seq INCREMENT BY 50 START WITH 1;

DROP TABLE IF EXISTS authors;
CREATE TABLE authors (
                         id BIGINT NOT NULL DEFAULT nextval('author_id_seq'),
                         name VARCHAR(255),
                         age SMALLINT,
                         description VARCHAR(512),
                         image VARCHAR(512),
                         CONSTRAINT authors_pk PRIMARY KEY (id)
);

DROP TABLE IF EXISTS books;
CREATE TABLE books (
                       isbn VARCHAR(19) NOT NULL,
                       title VARCHAR(512),
                       description VARCHAR(2048),
                       image VARCHAR(512),
                       author_id BIGINT NOT NULL REFERENCES authors(id),
                       CONSTRAINT books_pk PRIMARY KEY (isbn)
);
