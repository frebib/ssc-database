CREATE TABLE Title (
    titleid     SERIAL      NOT NULL,
    title       VARCHAR(4)  NOT NULL,

    PRIMARY KEY(titleid)
);

INSERT INTO Title (title)
VALUES ('Mrs'),('Miss'),('Ms'),('Mr'),('Dr');

CREATE TABLE RegistrationType (
    regtypeid   SERIAL      NOT NULL,
    descr       VARCHAR(16) NOT NULL,

    PRIMARY KEY(regtypeid)
);

INSERT INTO RegistrationType (descr)
VALUES ('Normal'),('Resit'),('Repeat');

CREATE TABLE Student (
    sid         INTEGER     NOT NULL,
    titleid     INTEGER     NOT NULL,
    forename    VARCHAR(32) NOT NULL,
    familyname  VARCHAR(48) NOT NULL,
    dob         DATE        NOT NULL CHECK(dob < current_date),

    PRIMARY KEY(sid),
    FOREIGN KEY(titleid) REFERENCES Title(titleid)
        ON DELETE RESTRICT
);

CREATE TABLE Lecturer (
    lid         INTEGER     NOT NULL,
    titleid     INTEGER     NOT NULL,
    forename    VARCHAR(32) NOT NULL,
    familyname  VARCHAR(48) NOT NULL,

    PRIMARY KEY(lid),
    FOREIGN KEY(titleid) REFERENCES Title(titleid)
        ON DELETE RESTRICT
);

CREATE TABLE StudentRegistration (
    sid         INTEGER     NOT NULL,
    yearOfStudy SMALLINT    NOT NULL CHECK(yearOfStudy >= 1),
    regtypeid   INTEGER     NOT NULL,

    FOREIGN KEY(sid) REFERENCES Student(sid)
        ON DELETE CASCADE
);

CREATE TABLE StudentContact (
    sid         INTEGER     NOT NULL,
    email       VARCHAR(64) UNIQUE,
    address     VARCHAR(128),

    FOREIGN KEY(sid) REFERENCES Student(sid)
        ON DELETE CASCADE,
    CONSTRAINT contact_not_null
        CHECK(email IS NOT NULL OR address IS NOT NULL)
);

CREATE TABLE NextOfKinContact (
    sid         INTEGER     NOT NULL,
    name        VARCHAR(64) NOT NULL,
    email       VARCHAR(64),
    address     VARCHAR(128),

    FOREIGN KEY(sid) REFERENCES Student(sid)
        ON DELETE CASCADE,
    CONSTRAINT contact_not_null
        CHECK(email IS NOT NULL OR address IS NOT NULL)
);

CREATE TABLE LecturerContact (
    lid         INTEGER     NOT NULL,
    office      VARCHAR(8)  NOT NULL,
    email       VARCHAR(32) NOT NULL UNIQUE,

    FOREIGN KEY(lid) REFERENCES Lecturer(lid)
        ON DELETE CASCADE
);

CREATE TABLE Tutor (
    sid         INTEGER     NOT NULL,
    lid         INTEGER     NOT NULL,

    FOREIGN KEY(sid) REFERENCES Student(sid)
        ON DELETE CASCADE,
    FOREIGN KEY(lid) REFERENCES Lecturer(lid)
        ON DELETE RESTRICT
);

