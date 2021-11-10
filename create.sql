CREATE DATABASE biointerface
    WITH
    OWNER = postgres
    ENCODING = 'UTF8'
    LC_COLLATE = 'Russian_Russia.1251'
    LC_CTYPE = 'Russian_Russia.1251'
    TABLESPACE = pg_default
    CONNECTION LIMIT = -1
;

CREATE TABLE IF NOT EXISTS icd
(
    id      SERIAL PRIMARY KEY NOT NULL,
    name    VARCHAR(35)        NOT NULL,
    version INTEGER            NOT NULL,
    comment TEXT               NULL
);

CREATE TABLE IF NOT EXISTS device
(
    id              INTEGER PRIMARY KEY NOT NULL,
    amount_channels INTEGER             NOT NULL,
    comment         TEXT                NULL
);

CREATE TABLE IF NOT EXISTS channel_name
(
    id      SERIAL PRIMARY KEY NOT NULL,
    name    VARCHAR(35) UNIQUE NOT NULL,
    comment TEXT               NULL
);

CREATE TABLE IF NOT EXISTS patient
(
    id          INTEGER PRIMARY KEY NOT NULL,
    second_name VARCHAR(20)         NOT NULL,
    first_name  VARCHAR(20)         NOT NULL,
    patronymic  VARCHAR(20)         NOT NULL,
    birthday    DATE                NOT NULL,
    icd_id      INTEGER             NULL,
    comment     TEXT                NULL,
    FOREIGN KEY (icd_id) REFERENCES icd (id) ON DELETE RESTRICT
);

CREATE TABLE IF NOT EXISTS examination
(
    id         SERIAL PRIMARY KEY NOT NULL,
    startTime  TIMESTAMP          NOT NULL,
    patient_id INTEGER            NOT NULL,
    device_id  INTEGER            NOT NULL,
    comment    TEXT               NULL,
    FOREIGN KEY (patient_id) REFERENCES patient (id) ON DELETE RESTRICT,
    FOREIGN KEY (device_id) REFERENCES device (id) ON DELETE RESTRICT
);

CREATE TABLE IF NOT EXISTS channel
(
    number          INTEGER NOT NULL,
    examination_id  INTEGER NOT NULL,
    channel_name_id INTEGER NULL,
    PRIMARY KEY (number, examination_id),
    FOREIGN KEY (examination_id) REFERENCES examination (id) ON DELETE CASCADE,
    FOREIGN KEY (channel_name_id) REFERENCES channel_name (id) ON DELETE RESTRICT
);

CREATE TABLE IF NOT EXISTS sample
(
    id             INTEGER NOT NULL,
    channel_number INTEGER NOT NULL,
    examination_id INTEGER NOT NULL,
    value          INTEGER NOT NULL,
    PRIMARY KEY (id, channel_number, examination_id),
    FOREIGN KEY (channel_number, examination_id) REFERENCES channel (number, examination_id) ON DELETE CASCADE
);

INSERT INTO public.patient(id, second_name, first_name, patronymic, birthday, icd_id, comment)
VALUES ('3', 'Гаврилов', 'Степан', 'Александрович', '1988-11-19', null, null);