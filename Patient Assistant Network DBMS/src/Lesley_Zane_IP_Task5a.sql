-- All insertions are pretty similar to where I'm just defining what variables I need and how to get them into the system.
-- Nothing really complex in the insertions, at some points I use GETDATE() to get the current date for the creation
-- To keep track with accuracy on what is happening in the system.

CREATE PROCEDURE AddTeam @team_name VARCHAR(100),
                         @type VARCHAR(100)
AS
BEGIN
    SET NOCOUNT ON;

    INSERT INTO Team (team_name, type, created_date)
    VALUES (@team_name, @type, GETDATE());
END;
GO

CREATE PROCEDURE AddTeamFromFile @team_name VARCHAR(100),
                                 @type VARCHAR(100),
                                 @created_date datetime
AS
BEGIN
    SET NOCOUNT ON;

    INSERT INTO Team (team_name, type, created_date)
    VALUES (@team_name, @type, @created_date);
END;
GO

CREATE PROCEDURE LookupTeams
AS
BEGIN
    SET NOCOUNT ON
    SELECT team_name FROM Team;
END;
GO

CREATE PROCEDURE LookupInsurance
AS
BEGIN
    SET NOCOUNT ON
    SELECT id, provider_name, type_of_insurance FROM Insurance_Policy;
END;
GO

CREATE PROCEDURE AddClientInsurance @SSN CHAR(11),
                                    @ID INT
AS
BEGIN
    SET NOCOUNT ON

    INSERT INTO Client_Insurance_Policy (SSN, ID)
    VALUES (@SSN, @ID)
END;
GO

CREATE PROCEDURE AddClientNeed @SSN CHAR(11),
                               @need_name VARCHAR(50),
                               @importance INT
AS
BEGIN
    SET NOCOUNT ON

    INSERT INTO Client_Need (SSN, need_name, importance)
    VALUES (@SSN, @need_name, @importance)
END;
GO

CREATE PROCEDURE CreatePerson @SSN CHAR(11),
                              @first_name VARCHAR(50),
                              @last_name VARCHAR(50),
                              @gender VARCHAR(10),
                              @profession VARCHAR(100),
                              @mailing_address VARCHAR(100),
                              @email VARCHAR(255),
                              @phone_number VARCHAR(50),
                              @mailing_list BIT
AS
BEGIN
    SET NOCOUNT ON

    INSERT INTO Person (SSN, first_name, last_name, gender, profession, mailing_address, email, phone_number,
                        mailing_list)
    VALUES (@SSN, @first_name, @last_name, @gender, @profession,
            @mailing_address, @email, @phone_number, @mailing_list)
END;
GO

CREATE PROCEDURE AddEmergencyContact @SSN CHAR(11),
                                     @contact_name VARCHAR(255),
                                     @phone_number VARCHAR(20),
                                     @relationship VARCHAR(20)
AS
BEGIN

    INSERT INTO Emergency_Contact (SSN, contact_name, phone_number, relationship)
    VALUES (@SSN, @contact_name, @phone_number, @relationship)
END
GO

CREATE PROCEDURE AddClient @SSN CHAR(11),
                           @doctor_name VARCHAR(100),
                           @doctor_phone_number VARCHAR(20),
                           @team_name VARCHAR(100)
AS
BEGIN
    SET NOCOUNT ON

    INSERT INTO Client (SSN, doctor_name, doctor_phone_number, date_assigned)
    VALUES (@SSN, @doctor_name, @doctor_phone_number, GETDATE())

    INSERT INTO Team_Client (team_name, SSN, active)
    VALUES (@team_name, @SSN, 1)
END;
GO

CREATE PROCEDURE AddVolunteer @SSN CHAR(11),
                              @date_joined DATE,
                              @date_latest_training DATE,
                              @location_latest_training VARCHAR(255)
AS
BEGIN
    SET NOCOUNT ON

    INSERT INTO Volunteer (SSN, date_joined, date_latest_training, location_latest_training)
    VALUES (@SSN, @date_joined, @date_latest_training, @location_latest_training)
END;
GO

CREATE PROCEDURE AddVolunteerTeam @SSN CHAR(11),
                                  @team_name VARCHAR(100),
                                  @team_leader BIT
AS
BEGIN
    SET NOCOUNT ON

    INSERT INTO Volunteer_Team (SSN, team_name, active_status, team_leader)
    VALUES (@SSN, @team_name, 1, @team_leader)
END;
GO

CREATE PROCEDURE AddVolunteerWork @SSN CHAR(11),
                                  @team_name VARCHAR(100),
                                  @work_date DATE,
                                  @hours_worked TINYINT
AS
BEGIN
    SET NOCOUNT ON

    INSERT INTO Volunteer_Team_Work (SSN, team_name, work_date, hours_worked)
    VALUES (@SSN, @team_name, @work_date, @hours_worked)
end
GO

CREATE PROCEDURE AddEmployee @SSN CHAR(11),
                             @salary MONEY,
                             @marital_status VARCHAR(20)
AS
BEGIN
    SET NOCOUNT ON

    INSERT INTO Employee (SSN, salary, marital_status, date_hired)
    VALUES (@SSN, @salary, @marital_status, GETDATE())
END;

GO
CREATE PROCEDURE AddReport @SSN CHAR(11),
                           @team_name VARCHAR(100),
                           @description VARCHAR(255)
AS
BEGIN
    SET NOCOUNT ON

    INSERT INTO Report (SSN, team_name, report_date, description)
    VALUES (@SSN, @team_name, GETDATE(), @description)
END;
GO

CREATE PROCEDURE AddExpense @SSN CHAR(11),
                            @amount MONEY,
                            @description VARCHAR(255)
AS
BEGIN
    SET NOCOUNT ON

    INSERT INTO Employee_Expense (SSN, date_created, amount, description)
    VALUES (@SSN, GETDATE(), @amount, @description)
END
GO
CREATE PROCEDURE AddDonor @SSN CHAR(11),
                          @is_anonymous BIT
AS
BEGIN
    SET NOCOUNT ON

    INSERT INTO Donor (SSN, is_anonymous)
    VALUES (@SSN, @is_anonymous)
END;
GO
CREATE PROCEDURE AddDonation @SSN CHAR(11),
                             @date_donated DATE,
                             @amount money,
                             @type_of_donation VARCHAR(50),
                             @campaign_name VARCHAR(50),
                             @payment_method VARCHAR(20),
                             @check_number VARCHAR(50),
                             @card_number VARCHAR(50),
                             @card_type VARCHAR(50),
                             @expiration_date CHAR(5)
AS
BEGIN

    SET NOCOUNT ON

    INSERT INTO Donation (SSN, date_donated, amount, type_of_donation, campaign_name, payment_method, check_number,
                          card_number, card_type, expiration_date)
    VALUES (@SSN, @date_donated, @amount, @type_of_donation, @campaign_name,
            @payment_method, @check_number, @card_number, @card_type, @expiration_date)
END;
GO

-- The GetProcedures simply are getting whatever is defined as the name. They are to implement the tasks after the insertion ones.
-- Most of them operate by doing different things with the SSN, mostly just navigating from table to table with them. Again, nothing
-- Too complex here and most can be understood as it is close to plain english.

CREATE PROCEDURE GetDoctor @SSN CHAR(11)
AS
BEGIN
    SET NOCOUNT ON
    SELECT doctor_name, doctor_phone_number
    FROM Client
    WHERE SSN = @SSN
END
GO
CREATE PROCEDURE GetExpenses @lower_date DATE,
                             @upper_date DATE
AS
BEGIN
    SET NOCOUNT ON;

    SELECT SSN,
           SUM(amount) AS total_expenses
    FROM Employee_Expense
    WHERE date_created BETWEEN @lower_date AND @upper_date
    GROUP BY SSN
    ORDER BY total_expenses DESC;
END;
GO

CREATE PROCEDURE GetVolunteers @SSN CHAR(11)
AS
BEGIN
    SET NOCOUNT ON

    SELECT vt.SSN AS volunteer_ssn
    FROM Team_Client tc
             JOIN Volunteer_Team vt ON tc.team_name = vt.team_name
    WHERE tc.SSN = @SSN;
END;
GO

CREATE PROCEDURE getTeamsAfter @after_date DATE
AS
BEGIN
    SET NOCOUNT ON;

    SELECT team_name
    FROM Team
    WHERE created_date >= @after_date
END;
GO

CREATE PROCEDURE GetAllInfo
AS
BEGIN
    SET NOCOUNT ON;

    SELECT p.SSN           AS "SSN",
           p.first_name    AS "First Name",
           p.last_name     AS "Last Name",
           p.phone_number  AS "Phone Number",
           ec.contact_name AS "Contact Name",
           ec.relationship AS "Relationship",
           ec.phone_number AS "Emergency Phone"
    FROM Person p
             LEFT JOIN Emergency_Contact ec ON p.SSN = ec.SSN
    ORDER BY p.SSN;


END;
GO

CREATE PROCEDURE GetDonorEmployees
AS
BEGIN
    SET NOCOUNT ON;

    SELECT p.SSN         AS person_ssn,
           CASE
               WHEN d.is_anonymous = 1 THEN NULL
               ELSE CONCAT(p.first_name, ' ', p.last_name)
               END       AS person_name,
           SUM(m.amount) AS total_donation
    FROM Person p
             JOIN Employee e ON p.SSN = e.SSN
             JOIN Donor d ON e.SSN = d.SSN
             JOIN Donation m ON d.SSN = m.SSN
    GROUP BY p.SSN, p.first_name, p.last_name, d.is_anonymous
    ORDER BY total_donation DESC;
END;
GO

CREATE PROCEDURE IncreaseSalary
AS
BEGIN
    SET NOCOUNT ON

    UPDATE Employee
    SET salary = salary * 1.10
    WHERE SSN IN (SELECT r.ssn
                  FROM Report r
                  GROUP BY r.ssn
                  HAVING COUNT(DISTINCT r.team_name) > 1);
END;
GO

-- Note here, This procedure deletes for no health insurance OR < 5 in transportation need. It
-- is NOT an XOR procedure.
CREATE PROCEDURE DeleteClientsWithoutHealthInsurance
AS
BEGIN
    SET NOCOUNT ON;

    DECLARE @ClientsToDelete TABLE
                             (
                                 SSN CHAR(11)
                             );

    INSERT INTO @ClientsToDelete (SSN)
    SELECT DISTINCT c.SSN
    FROM Client c
             LEFT JOIN Client_Insurance_Policy cip ON c.SSN = cip.SSN
             LEFT JOIN Insurance_Policy ip ON cip.ID = ip.ID AND ip.type_of_insurance = 'Health'
             LEFT JOIN Client_Need cn ON c.SSN = cn.SSN AND cn.need_name = 'Transportation'
    WHERE ip.ID IS NULL
       OR (cn.importance < 5 AND cn.importance IS NOT NULL);

    SELECT * FROM @ClientsToDelete;

    DELETE
    FROM Team_Client
    WHERE SSN IN (SELECT SSN FROM @ClientsToDelete);

    DELETE
    FROM Client_Insurance_Policy
    WHERE SSN IN (SELECT SSN FROM @ClientsToDelete);

    DELETE
    FROM Client_Need
    WHERE SSN IN (SELECT SSN FROM @ClientsToDelete);

    DELETE
    FROM Client
    WHERE SSN IN (SELECT SSN FROM @ClientsToDelete);
END;
GO

CREATE PROCEDURE GetMailingList
AS
BEGIN
    SET NOCOUNT ON
    SELECT first_name, last_name, mailing_address
    FROM Person
    WHERE mailing_list = 1;
END;