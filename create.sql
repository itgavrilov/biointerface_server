CREATE TABLE IF NOT EXISTS icd
(
    id      INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    name    VARCHAR(35)                       NOT NULL,
    version INTEGER                           NOT NULL,
    comment TEXT
);

CREATE TABLE IF NOT EXISTS device
(
    id             INTEGER PRIMARY KEY NOT NULL,
    amountChannels INTEGER             NOT NULL,
    comment        TEXT
);

CREATE TABLE IF NOT EXISTS channelName
(
    id      INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    name    VARCHAR(35) UNIQUE                NOT NULL,
    comment TEXT
);

CREATE TABLE IF NOT EXISTS patientRecord
(
    id         INTEGER PRIMARY KEY NOT NULL,
    secondName VARCHAR(20)         NOT NULL,
    firstName  VARCHAR(20)         NOT NULL,
    patronymic VARCHAR(20)         NOT NULL,
    birthday   DATE                NOT NULL,
    icd_id     INTEGER,
    comment    TEXT,
    FOREIGN KEY (icd_id) REFERENCES icd (id) ON DELETE RESTRICT
);

CREATE TABLE IF NOT EXISTS examination
(
    id               INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    startTime        TIMESTAMP                         NOT NULL,
    patientRecord_id INTEGER                           NOT NULL,
    device_id        INTEGER                           NOT NULL,
    comment          TEXT                              NULL,
    FOREIGN KEY (patientRecord_id) REFERENCES patientRecord (id) ON DELETE RESTRICT,
    FOREIGN KEY (device_id) REFERENCES device (id) ON DELETE RESTRICT
);

CREATE TABLE IF NOT EXISTS channel
(
    id             INTEGER NOT NULL,
    examination_id INTEGER NOT NULL,
    channelName_id INTEGER,
    PRIMARY KEY (id, examination_id),
    FOREIGN KEY (examination_id) REFERENCES examination (id) ON DELETE CASCADE,
    FOREIGN KEY (channelName_id) REFERENCES channelName (id) ON DELETE RESTRICT
);

CREATE TABLE IF NOT EXISTS sample
(
    id             INTEGER NOT NULL,
    channel_id     INTEGER NOT NULL,
    examination_id INTEGER NOT NULL,
    value          INTEGER NOT NULL,
    PRIMARY KEY (id, channel_id, examination_id),
    FOREIGN KEY (channel_id, examination_id) REFERENCES channel (id, examination_id) ON DELETE CASCADE
);