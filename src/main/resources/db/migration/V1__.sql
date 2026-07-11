CREATE SEQUENCE IF NOT EXISTS revinfo_seq START WITH 1 INCREMENT BY 50;

CREATE TABLE event_publication
(
    id                     UUID                        NOT NULL,
    publication_date       TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    listener_id            VARCHAR                     NOT NULL,
    serialized_event       VARCHAR                     NOT NULL,
    event_type             VARCHAR                     NOT NULL,
    completion_date        TIMESTAMP WITHOUT TIME ZONE,
    last_resubmission_date TIMESTAMP WITHOUT TIME ZONE,
    completion_attempts    INTEGER                     NOT NULL,
    status                 VARCHAR(255),
    CONSTRAINT pk_event_publication PRIMARY KEY (id)
);

CREATE TABLE event_publication_archive
(
    id                     UUID                        NOT NULL,
    publication_date       TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    listener_id            VARCHAR                     NOT NULL,
    serialized_event       VARCHAR                     NOT NULL,
    event_type             VARCHAR                     NOT NULL,
    completion_date        TIMESTAMP WITHOUT TIME ZONE,
    last_resubmission_date TIMESTAMP WITHOUT TIME ZONE,
    completion_attempts    INTEGER                     NOT NULL,
    status                 VARCHAR(255),
    CONSTRAINT pk_event_publication_archive PRIMARY KEY (id)
);

CREATE TABLE movie
(
    movie_id    VARCHAR(255) NOT NULL,
    title       VARCHAR(255) NOT NULL,
    description VARCHAR(255) NOT NULL,
    CONSTRAINT pk_movie PRIMARY KEY (movie_id)
);

CREATE TABLE revchanges
(
    rev        BIGINT NOT NULL,
    entityname VARCHAR(255)
);

CREATE TABLE review
(
    id          VARCHAR(255) NOT NULL,
    movie_id    VARCHAR(255)                            NOT NULL,
    review_text VARCHAR(255),
    rating      INTEGER,
    CONSTRAINT pk_review PRIMARY KEY (id)
);

CREATE TABLE revinfo
(
    rev      BIGINT NOT NULL,
    revtstmp BIGINT,
    CONSTRAINT pk_revinfo PRIMARY KEY (rev)
);

CREATE TABLE users
(
    id         VARCHAR(255) NOT NULL,
    full_name  VARCHAR(255) NOT NULL,
    email      VARCHAR(100) NOT NULL,
    password   VARCHAR(255) NOT NULL,
    role       VARCHAR(255) NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE,
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_users PRIMARY KEY (id)
);

ALTER TABLE movie
    ADD CONSTRAINT uc_movie_title UNIQUE (title);

ALTER TABLE users
    ADD CONSTRAINT uc_users_email UNIQUE (email);

ALTER TABLE revchanges
    ADD CONSTRAINT fk_revchanges_on_default_tracking_modified_entities_changelog FOREIGN KEY (rev) REFERENCES revinfo (rev);