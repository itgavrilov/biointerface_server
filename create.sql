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
    id      BIGSERIAL PRIMARY KEY  NOT NULL,
    name    VARCHAR(35)                       NOT NULL,
    version INTEGER                           NOT NULL,
    comment TEXT NULL
);

CREATE TABLE IF NOT EXISTS device
(
    id             BIGINT PRIMARY KEY NOT NULL,
    amountChannels INTEGER             NOT NULL,
    comment        TEXT NULL
);

CREATE TABLE IF NOT EXISTS channelName
(
    id      BIGSERIAL PRIMARY KEY NOT NULL,
    name    VARCHAR(35) UNIQUE     NOT NULL,
    comment TEXT NULL
);

CREATE TABLE IF NOT EXISTS patientRecord
(
    id         BIGINT PRIMARY KEY  NOT NULL,
    secondName VARCHAR(20)         NOT NULL,
    firstName  VARCHAR(20)         NOT NULL,
    patronymic VARCHAR(20)         NOT NULL,
    birthday   DATE                NOT NULL,
    icd_id     BIGINT   NULL,
    comment    TEXT NULL,
    FOREIGN KEY (icd_id) REFERENCES icd (id) ON DELETE RESTRICT
);

CREATE TABLE IF NOT EXISTS examination
(
    id               BIGSERIAL PRIMARY KEY NOT NULL,
    startTime        TIMESTAMP                         NOT NULL,
    patientRecord_id BIGINT                           NOT NULL,
    device_id        BIGINT                           NOT NULL,
    comment          TEXT                              NULL,
    FOREIGN KEY (patientRecord_id) REFERENCES patientRecord (id) ON DELETE RESTRICT,
    FOREIGN KEY (device_id) REFERENCES device (id) ON DELETE RESTRICT
);

CREATE TABLE IF NOT EXISTS channel
(
    id             INTEGER NOT NULL,
    examination_id BIGINT NOT NULL,
    channelName_id BIGINT NULL,
    PRIMARY KEY (id, examination_id),
    FOREIGN KEY (examination_id) REFERENCES examination (id) ON DELETE CASCADE,
    FOREIGN KEY (channelName_id) REFERENCES channelName (id) ON DELETE RESTRICT
);

CREATE TABLE IF NOT EXISTS sample
(
    id             BIGINT NOT NULL,
    channel_id     INTEGER NOT NULL,
    examination_id BIGINT NOT NULL,
    value          INTEGER NOT NULL,
    PRIMARY KEY (id, channel_id, examination_id),
    FOREIGN KEY (channel_id, examination_id) REFERENCES channel (id, examination_id) ON DELETE CASCADE
);

INSERT INTO public.patientrecord(
    id, secondname, firstname, patronymic, birthday, icd_id, comment)
VALUES ('3', 'Гаврилов', 'Степан', 'Александрович', '1988-11-19', null, null);