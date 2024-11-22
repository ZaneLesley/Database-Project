-- This file is based off Task 3.1, justification and reasons for certain indexes are there, otherwise Table Person is mainly the table
-- That is constrained as it's the most important table for my database.

CREATE TABLE Person
(
    SSN             CHAR(11) PRIMARY KEY CHECK (SSN LIKE '[0-9][0-9][0-9]-[0-9][0-9]-[0-9][0-9][0-9][0-9]'),
    first_name      VARCHAR(50) NOT NULL,
    last_name       VARCHAR(50) NOT NULL,
    gender          VARCHAR(10),
    profession      VARCHAR(100),
    mailing_address VARCHAR(100),
    email           VARCHAR(255) CHECK (email LIKE '%_@_%._%'),
    phone_number    VARCHAR(20) CHECK (phone_number LIKE '[0-9][0-9][0-9]-[0-9][0-9][0-9]-[0-9][0-9][0-9][0-9]'),
    mailing_list    BIT
);

CREATE TABLE Emergency_Contact
(
    SSN          CHAR(11),
    contact_name VARCHAR(255) NOT NULL,
    phone_number VARCHAR(20)  NOT NULL,
    relationship VARCHAR(20),
    PRIMARY KEY (SSN, contact_name),
    FOREIGN KEY (SSN) REFERENCES Person (SSN)
);

CREATE NONCLUSTERED INDEX idx_EmergencyContact_SSN
    ON Emergency_Contact (SSN);

CREATE TABLE Donor
(
    SSN          CHAR(11) PRIMARY KEY,
    is_anonymous BIT,
    FOREIGN KEY (SSN) REFERENCES Person (SSN)
);

CREATE TABLE Donation
(
    ID               INT IDENTITY (1,1) PRIMARY KEY,
    SSN              CHAR(11),
    date_donated     DATE,
    amount           MONEY,
    type_of_donation VARCHAR(50),
    campaign_name    VARCHAR(50),
    payment_method   VARCHAR(20),
    check_number     VARCHAR(50),
    card_number      VARCHAR(50),
    card_type        VARCHAR(50),
    expiration_date  CHAR(5) CHECK (expiration_date LIKE '[0-9][0-9]/[0-9][0-9]'),
    FOREIGN KEY (SSN) REFERENCES Donor (SSN)
);

CREATE NONCLUSTERED INDEX idx_Donation_SSN
    ON Donation (SSN);

CREATE TABLE Client
(
    SSN                 CHAR(11) PRIMARY KEY,
    doctor_name         VARCHAR(100),
    doctor_phone_number VARCHAR(20),
    date_assigned       datetime,
    FOREIGN KEY (SSN) REFERENCES Person (SSN)
);

CREATE TABLE Insurance_Policy
(
    ID                INT IDENTITY (1,1) PRIMARY KEY,
    provider_name     VARCHAR(100),
    provider_address  VARCHAR(255),
    type_of_insurance VARCHAR(20)
);

CREATE NONCLUSTERED INDEX idx_InsurancePolicy_Type
    ON Insurance_Policy (type_of_insurance);

CREATE TABLE Client_Insurance_Policy
(
    SSN CHAR(11),
    ID  INTEGER,
    PRIMARY KEY (SSN, ID),
    FOREIGN KEY (SSN) REFERENCES Client (SSN),
    FOREIGN KEY (ID) REFERENCES Insurance_Policy (ID)
);

CREATE NONCLUSTERED INDEX idx_ClientInsurancePolicy_SSN
    ON Client_Insurance_Policy (SSN);

CREATE TABLE Need
(
    name VARCHAR(50) PRIMARY KEY
);

CREATE TABLE Client_Need
(
    SSN        CHAR(11),
    need_name  VARCHAR(50),
    importance SMALLINT CHECK (importance BETWEEN 1 AND 10),
    PRIMARY KEY (SSN, need_name),
    FOREIGN KEY (SSN) REFERENCES Client (SSN),
    FOREIGN KEY (need_name) REFERENCES Need (name)
);

CREATE NONCLUSTERED INDEX idx_ClientNeed_NeedName_Importance
    ON Client_Need (need_name, importance);

CREATE TABLE Team
(
    team_name    VARCHAR(100) PRIMARY KEY,
    type         VARCHAR(100),
    created_date datetime
);

CREATE NONCLUSTERED INDEX idx_Team_CreatedDate
    ON Team (created_date);

CREATE TABLE Team_Client
(
    team_name VARCHAR(100),
    SSN       CHAR(11),
    active    BIT,
    PRIMARY KEY (SSN, team_name),
    FOREIGN KEY (team_name) REFERENCES Team (team_name),
    FOREIGN KEY (SSN) REFERENCES Client (SSN)
);

CREATE NONCLUSTERED INDEX idx_TeamClient_SSN
    ON Team_Client (SSN);

CREATE NONCLUSTERED INDEX idx_TeamClient_TeamName
    ON Team_Client (team_name);

CREATE TABLE Volunteer
(
    SSN                      CHAR(11) PRIMARY KEY,
    date_joined              DATE,
    date_latest_training     DATE,
    location_latest_training VARCHAR(255),
    FOREIGN KEY (SSN) REFERENCES Person (SSN)
);

CREATE TABLE Volunteer_Team_Work
(
    SSN          CHAR(11),
    team_name    VARCHAR(100),
    work_date    DATE,
    hours_worked TINYINT,
    PRIMARY KEY (SSN, team_name, work_date),
    FOREIGN KEY (SSN) REFERENCES Volunteer (SSN),
    FOREIGN KEY (team_name) REFERENCES Team (team_name)
);

CREATE TABLE Volunteer_Team
(
    SSN           CHAR(11),
    team_name     VARCHAR(100),
    active_status BIT,
    team_leader   BIT,
    PRIMARY KEY (SSN, team_name),
    FOREIGN KEY (SSN) REFERENCES Volunteer (SSN),
    FOREIGN KEY (team_name) REFERENCES Team (team_name)
);

CREATE NONCLUSTERED INDEX idx_VolunteerTeam_TeamName_SSN
    ON Volunteer_Team (team_name, SSN);

-- Enforce One Team Leader Per Team
CREATE UNIQUE INDEX IX_TeamLeader_PerTeam
    ON Volunteer_Team (team_name)
    WHERE team_leader = 1;

CREATE TABLE Employee
(
    SSN            CHAR(11) PRIMARY KEY,
    salary         MONEY,
    marital_status VARCHAR(20),
    date_hired     DATETIME,
    FOREIGN KEY (SSN) REFERENCES Person (SSN)
);

CREATE TABLE Employee_Expense
(
    ID           INT IDENTITY (1,1) PRIMARY KEY,
    SSN          CHAR(11),
    date_created DATETIME,
    amount       MONEY,
    description  VARCHAR(255),
    FOREIGN KEY (SSN) REFERENCES Employee (SSN)
);

CREATE NONCLUSTERED INDEX idx_EmployeeExpense_DateCreated_SSN
    ON Employee_Expense (date_created, SSN);

CREATE TABLE Report
(
    ID          INT IDENTITY (1,1) PRIMARY KEY,
    SSN         CHAR(11),
    team_name   VARCHAR(100),
    report_date DATE,
    description VARCHAR(255),
    FOREIGN KEY (SSN) REFERENCES Employee (SSN),
    FOREIGN KEY (team_name) REFERENCES Team (team_name)
);

CREATE NONCLUSTERED INDEX idx_Report_EmployeeSSN
    ON Report (SSN);

-- We Prefill some tables with values here, as the user has no need to enter them according to the project requirements.
INSERT INTO Insurance_Policy (provider_name, provider_address, type_of_insurance)
VALUES ('Progressive', '1231 Progressive Drive', 'Health');
INSERT INTO Insurance_Policy (provider_name, provider_address, type_of_insurance)
VALUES ('Progressive', '1231 Progressive Drive', 'Life');
INSERT INTO Insurance_Policy (provider_name, provider_address, type_of_insurance)
VALUES ('Progressive', '1231 Progressive Drive', 'Car');
INSERT INTO Insurance_Policy (provider_name, provider_address, type_of_insurance)
VALUES ('Progressive', '1231 Progressive Drive', 'Home');

INSERT INTO Need (name)
VALUES ('Transportation');

INSERT INTO Need (name)
VALUES ('In-House');

INSERT INTO Need (name)
VALUES ('Morning Support');

INSERT INTO Need (name)
VALUES ('Day Support');

INSERT INTO Need (name)
VALUES ('Night Support');