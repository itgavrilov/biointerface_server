CREATE SCHEMA IF NOT EXISTS main_service;

CREATE TABLE IF NOT EXISTS main_service.icd
(
    id      uuid    NOT NULL,
    name    VARCHAR NOT NULL,
    version INTEGER NOT NULL,
    comment TEXT    NULL,
    CONSTRAINT pk_icd PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS main_service.device
(
    id              uuid    NOT NULL,
    number          VARCHAR NOT NULL,
    amount_channels INTEGER NOT NULL,
    comment         TEXT    NULL,
    CONSTRAINT pk_device PRIMARY KEY (id),
    CONSTRAINT ix_device_number UNIQUE (number)
);

CREATE TABLE IF NOT EXISTS main_service.channel_name
(
    id      uuid           NOT NULL,
    name    VARCHAR UNIQUE NOT NULL,
    comment TEXT           NULL,
    CONSTRAINT pk_channel_name PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS main_service.patient
(
    id          uuid    NOT NULL,
    second_name VARCHAR NOT NULL,
    first_name  VARCHAR NOT NULL,
    patronymic  VARCHAR NOT NULL,
    birthday    DATE    NOT NULL,
    icd_id      uuid    NULL,
    comment     TEXT    NULL,
    CONSTRAINT pk_patient PRIMARY KEY (id),
    CONSTRAINT fk_patient__icd FOREIGN KEY (icd_id)
        REFERENCES main_service.icd (id) ON DELETE RESTRICT
);

CREATE TABLE IF NOT EXISTS main_service.examination
(
    id         uuid      NOT NULL,
    startTime  TIMESTAMP NOT NULL,
    patient_id uuid      NOT NULL,
    device_id  uuid      NOT NULL,
    comment    TEXT      NULL,
    CONSTRAINT pk_examination PRIMARY KEY (id),
    CONSTRAINT fk_examination__patient FOREIGN KEY (patient_id)
        REFERENCES main_service.patient (id) ON DELETE RESTRICT,
    CONSTRAINT fk_examination__device FOREIGN KEY (device_id)
        REFERENCES main_service.device (id) ON DELETE RESTRICT
);

CREATE TABLE IF NOT EXISTS main_service.channel
(
    number          bytea NOT NULL,
    examination_id  uuid  NOT NULL,
    channel_name_id uuid  NULL,
    CONSTRAINT pk_channel PRIMARY KEY (number, examination_id),
    CONSTRAINT fk_channel__examination FOREIGN KEY (examination_id)
        REFERENCES main_service.examination (id) ON DELETE CASCADE,
    CONSTRAINT fk_channel__channel_name FOREIGN KEY (channel_name_id)
        REFERENCES main_service.channel_name (id) ON DELETE RESTRICT
);

CREATE TABLE IF NOT EXISTS main_service.sample
(
    number         BIGINT  NOT NULL,
    channel_number bytea   NOT NULL,
    examination_id uuid    NOT NULL,
    value          INTEGER NOT NULL,
    CONSTRAINT pk_sample PRIMARY KEY (number, channel_number, examination_id),
    CONSTRAINT fk_sample__channel FOREIGN KEY (channel_number, examination_id)
        REFERENCES main_service.channel (number, examination_id) ON DELETE CASCADE
);

INSERT INTO main_service.patient(id, second_name, first_name, patronymic, birthday, icd_id, comment)
VALUES (gen_random_uuid(), 'Иванов', 'Иван', 'Иванович', '1980-03-03', null, null);