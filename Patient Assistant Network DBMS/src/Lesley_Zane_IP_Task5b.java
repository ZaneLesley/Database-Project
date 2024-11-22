import java.io.*;
import java.math.BigDecimal;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Lesley_Zane_IP_Task5b {
    final static String hostname = "lesl0009-sql-server.database.windows.net";
    final static String dbname = "cs-dsa-4513-sql-db";
    final static String username = "AdminUser";
    final static String password = "CS4513password!";

    public static void main(String[] args) {
        // Ensure server will properly connect and disconnect
        Connection connection = initializeServer(hostname, dbname, username, password);
        consoleWindow(connection);
        closeConnection(connection);
    }

    public static Connection initializeServer(String HOSTNAME, String DBNAME, String USERNAME, String PASSWORD) {
        Connection connection = null;

        // Database connection string
        String URL = String.format("jdbc:sqlserver://%s:1433;" + "database=%s;" + "user=%s;" + "password=%s;" + "encrypt=true;" +
                        "trustServerCertificate=false;" + "hostNameInCertificate=*.database.windows.net;" + "loginTimeout=30;", HOSTNAME, DBNAME,
                USERNAME,
                PASSWORD);

        try {

            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

            // Establish connection
            connection = DriverManager.getConnection(URL);
            System.out.println("Connected to the MySQL database successfully!");

        } catch (SQLException e) {
            // Handle SQL connection errors
            System.out.println("Failed to connect to the database!" + e.getMessage());

        } catch (ClassNotFoundException e) {
            System.out.println("Failed to connect to the database!" + e.getMessage());
        }

        return connection;
    }

    private static void closeConnection(Connection connection) {
        try {
            connection.close();
            System.out.println("Succesfully closed the connection to the database!");
        } catch (SQLException e) {
            System.out.println("Failed to close the connection!" + e.getMessage());
        }
    }

    private static void consoleWindow(Connection connection) {
        int choice;
        ArrayList<String> teamNames;
        ArrayList<Integer> insurance;

        while (true) {
            Scanner input = new Scanner(System.in);
            System.out.println();
            System.out.println("Choose an option");
            System.out.println("1. Enter a new team into the database");
            System.out.println("2. Enter a new client into the database and associate him or her with one or more teams");
            System.out.println("3. Enter a new volunteer into the database and associate him or her with one or more teams");
            System.out.println("4. Enter the number of hours a volunteer worked this month for a particular team");
            System.out.println("5. Enter a new employee into the database and associate him or her with one or more teams");
            System.out.println("6. Enter an expense charged by an employee");
            System.out.println("7. Enter a new donor and associate him or her with several donations");
            System.out.println("8. Retrieve the name and phone number of the doctor of a particular client");
            System.out.println("9. Retrieve the total amount of expenses charged by each employee for a particular period of time. The list should " +
                    "be sorted by the total amount of expenses");
            System.out.println("10. Retrieve the list of volunteers that are members of teams that support a particular client");
            System.out.println("11. Retrieve the names of all teams that were founded after a particular date");
            System.out.println("12. Retrieve the names, social security numbers, contact information, and emergency contact information of all " +
                    "people in the database");
            System.out.println("13. Retrieve the name and total amount donated by donors that are also employees. The list should be sorted by the " +
                    "total amount of the donations, and indicate if each donor wishes to remain anonymous");
            System.out.println("14. Increase the salary by 10% of all employees to whom more than one team must report");
            System.out.println("15. Delete all clients who do not have health insurance and whose value of importance for transportation is less " +
                    "than 5");
            System.out.println("16. Enter new teams from a data file until the file is empty");
            System.out.println("17. Output names and mailing addresses of all people on mailing list");
            System.out.println("18. Quit the application");

            // Variables that get used across multiple switch blocks
            String teamName;
            String ssn;

            try {
                choice = Integer.parseInt(input.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a number!");
                continue;
            }

            // Main Logic Selector
            /*
             * All of this part and the calls act close to the same, basically you'll see prints asking for input, then we just save what the user
             * gives us then we save those values. Eventually we will load the procedure and execute the query using the values we got from the user.
             * All of it pretty much works like this, and it's just a very repetitive cycle. The important parts on in the .sql file. Most of this
             * is just basic work. Most of the are put in places where intellj told me to put them (IDE throws an error if you don't do it).
             * All the switch cases correspond with a function down below. I recommend reading the .sql files to actually learn how the logic
             * behind the application works.
             */
            switch (choice) {
                case 1:
                    System.out.println("Please enter the team name:");
                    teamName = input.nextLine();
                    System.out.println("Please enter team type:");
                    String team_type = input.nextLine();
                    addTeam(connection, teamName, team_type);
                    break;
                case 2:
                    teamNames = lookupTeams(connection);
                    if (!teamNames.isEmpty()) {
                        System.out.println("Please enter the team name's number to the left:");
                        teamName = teamNames.get(Integer.parseInt(input.nextLine()));
                        System.out.println("Please enter person's SSN:");
                        ssn = input.nextLine();
                        addPerson(connection, ssn);
                        System.out.println("Please enter client's doctor's name: ");
                        String doctorName = input.nextLine();
                        System.out.println("Please enter client's doctor's phone number xxx-xxx-xxxx: ");
                        String doctorPhone = input.nextLine();
                        addClient(connection, ssn, doctorName, doctorPhone, teamName);
                        insurance = lookupInsurance(connection);
                        while (true) {
                            System.out.println("Pick which insurances to add to Client. Type -1 to stop");
                            String userInput = input.nextLine();
                            if (userInput.equals("-1")) {
                                break;
                            }
                            addInsurancePolicy(connection, ssn, insurance.get(Integer.parseInt(userInput)));
                        }
                        while (true) {
                            System.out.println("Type -1 to stop, Else enter any number");
                            String userInput = input.nextLine();
                            if (userInput.equals("-1")) {
                                break;
                            }
                            addClientNeed(connection, ssn);
                        }
                        // Need to insert teams first.
                    } else {
                        break;
                    }
                    // Clear Names for next use.
                    teamNames.clear();
                    insurance.clear();
                    System.out.println("Entry " + ssn + " Successfully added.");
                    break;
                case 3:
                    teamNames = lookupTeams(connection);
                    if (!teamNames.isEmpty()) {
                        System.out.println("Please enter the person's SSN:");
                        ssn = input.nextLine();
                        addPerson(connection, ssn);
                        addVolunteer(connection, ssn);
                        lookupTeams(connection);
                        while (true) {
                            System.out.println("Please enter the index that you the person is to be added to, type '-1' to exit:");
                            String userInput = input.nextLine();
                            if (userInput.equals("-1")) {
                                break;
                            }
                            addVolunteerTeam(connection, ssn, teamNames.get(Integer.parseInt(userInput)));
                        }

                    } else {
                        System.out.println("No data added");
                    }
                    // Clear Names for next use
                    teamNames.clear();
                    break;
                case 4:
                    addVolunteerWork(connection);
                    break;
                case 5:
                    System.out.println("Enter Employee's SSN:");
                    ssn = input.nextLine();
                    addPerson(connection, ssn);
                    addEmployee(connection, ssn);
                    teamNames = lookupTeams(connection);
                    while (true) {
                        System.out.println("Please enter the index that you the person is to be added to, type '-1' to exit:");
                        String userInput = input.nextLine();
                        if (userInput.equals("-1")) {
                            break;
                        }
                        addReport(connection, ssn, teamNames.get(Integer.parseInt(userInput)));
                    }
                    // Clear names for next use
                    teamNames.clear();
                    break;
                case 6:
                    addExpense(connection);
                    break;
                // Anonymous Donors are not required to have an Emergency Contact. So we manually enter NULLs to simulate not knowing where the
                // donation is from.
                case 7:
                    System.out.println("Enter donor's SSN:");
                    ssn = input.nextLine();
                    System.out.println("If the donor is anonymous, please select 0, else push any key to continue");
                    if (input.nextLine().equals("0")) {
                        String sql = "{Call CreatePerson(?, ?, ?, ?, ?, ?, ?, ?, ?)}";
                        try (CallableStatement stmt = connection.prepareCall(sql)) {
                            stmt.setString(1, ssn);
                            stmt.setString(2, "John");
                            stmt.setString(3, "Doe");
                            stmt.setNull(4, Types.NULL);
                            stmt.setNull(5, Types.NULL);
                            stmt.setNull(6, Types.NULL);
                            stmt.setNull(7, Types.NULL);
                            stmt.setNull(8, Types.NULL);
                            stmt.setNull(9, Types.NULL);
                            stmt.execute();
                        } catch (SQLException e) {
                            System.err.println("Error adding person: " + e.getMessage());
                        }

                        addDonor(connection, ssn, 1);
                    } else {
                        addPerson(connection, ssn);
                        addDonor(connection, ssn, 0);
                    }
                    addDonation(connection, ssn);
                    break;
                case 8:
                    System.out.println("Enter client's SSN:");
                    ssn = input.nextLine();
                    getDoctor(connection, ssn);
                    break;
                case 9:
                    getExpenses(connection);
                    break;
                case 10:
                    getVolunteers(connection);
                    break;
                case 11:
                    getTeamsAfter(connection);
                    break;
                case 12:
                    getAllInfo(connection);
                    break;
                case 13:
                    getDonorEmployees(connection);
                    break;
                case 14:
                    increaseSalary(connection);
                    break;
                case 15:
                    deleteClients(connection);
                    break;
                case 16:
                    addTeamFromFile(connection);
                    break;
                case 17:
                    getMailingList(connection);
                    break;
                case 18:
                    return;
            }
        }
    }

    /*
    As stated early, most of the code just follows this format, get the information we need for the Procedure defined in the .sql file. All of
    these simply just work to get that data. For understanding what the values are going to be used for please refer to the .sql code.
     */

    private static void addTeam(Connection connection, String teamName, String teamType) {
        String sql = "{Call AddTeam(?, ?)}";
        try (CallableStatement stmt = connection.prepareCall(sql)) {
            stmt.setString(1, teamName);
            stmt.setString(2, teamType);
            stmt.execute();
        } catch (SQLException e) {
            System.err.println("Error adding team: " + e.getMessage());
            return;
        }

        System.out.println("Team " + teamName + " added successfully!");
    }

    private static void addTeamFromFile(Connection connection) {
        Scanner input = new Scanner(System.in);
        System.out.println("Enter File Name: ");
        String fileName = input.nextLine();
        String teamName;
        String type;
        java.sql.Date createdDate;
        try {
            BufferedReader br = new BufferedReader(new FileReader(fileName));

            // Read the file and make a call to insert it. Does not delete the file afterward.
            String line;
            while ((line = br.readLine()) != null) {
                // File is .csv with no headers
                String[] teamData = line.split(",");
                teamName = teamData[0].trim();
                type = teamData[1].trim();
                createdDate = toSqlDate(teamData[2]);

                String sql = "{Call AddTeamFromFile(?, ?, ?)}";
                try (CallableStatement stmt = connection.prepareCall(sql)) {
                    stmt.setString(1, teamName); // @team_name
                    stmt.setString(2, type); // @type
                    stmt.setDate(3, createdDate);
                    stmt.execute();
                } catch (SQLException e) {
                    System.err.println("Error adding team: " + e.getMessage());
                    return;
                }

                System.out.println("Team " + teamName + " added successfully!");
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static ArrayList<String> lookupTeams(Connection connection) {
        ArrayList<String> teamNames = new ArrayList<>();
        String sql = "{Call LookupTeams()}";
        try (CallableStatement stmt = connection.prepareCall(sql)) {
            if (stmt.execute()) {
                try (ResultSet rs = stmt.getResultSet()) {
                    int index = 0;
                    // Loop through the returned results
                    while (rs.next()) {
                        String teamName = rs.getString("team_name");
                        teamNames.add(teamName);
                        System.out.println(index + ": " + teamName);
                        index++;
                    }
                }
            } else {
                System.out.println("No teams found.");
            }
        } catch (SQLException e) {
            System.err.println("Error looking up teams: " + e.getMessage());
        }
        return teamNames;
    }

    private static ArrayList<Integer> lookupInsurance(Connection connection) {
        ArrayList<Integer> insurance = new ArrayList<>();
        String sql = "{Call LookupInsurance()}";
        try (CallableStatement stmt = connection.prepareCall(sql)) {
            if (stmt.execute()) {
                try (ResultSet rs = stmt.getResultSet()) {
                    int index = 0;
                    // Loop through the returned results
                    while (rs.next()) {
                        int id = rs.getInt(1);
                        String providerName = rs.getString(2);
                        String type_of_insurance = rs.getString(3);
                        insurance.add(id);
                        System.out.println(index + ": " + providerName + " " + type_of_insurance);
                        index++;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error looking up insurances: " + e.getMessage());
        }
        return insurance;
    }

    private static void addPerson(Connection connection, String ssn) {
        Scanner input = new Scanner(System.in);
        System.out.println("Is this person new to the database? 0 for no, 1 for yes");
        String userInput = input.nextLine();
        if (userInput.equals("0")) {
            return;
        }
        System.out.println("Please enter person's first name:");
        String firstName = input.nextLine();
        System.out.println("Please enter person's last name:");
        String lastName = input.nextLine();
        System.out.println("Please enter person's gender:");
        String gender = input.nextLine();
        System.out.println("Please enter person's profession");
        String profession = input.nextLine();
        System.out.println("Please enter person's mailing address");
        String mailingAddress = input.nextLine();
        System.out.println("Please enter person's email:");
        String email = input.nextLine();
        System.out.println("Please enter person's phone number xxx-xxx-xxxx:");
        String phoneNumber = input.nextLine();
        System.out.println("Does person want to be added to mailing list? Insert 1 for yes, 0 for no:");
        int isMailingList = Integer.parseInt(input.nextLine());

        String sql = "{Call CreatePerson(?, ?, ?, ?, ?, ?, ?, ?, ?)}";
        try (CallableStatement stmt = connection.prepareCall(sql)) {
            stmt.setString(1, ssn);
            stmt.setString(2, firstName);
            stmt.setString(3, lastName);
            stmt.setString(4, gender);
            stmt.setString(5, profession);
            stmt.setString(6, mailingAddress);
            stmt.setString(7, email);
            stmt.setString(8, phoneNumber);
            stmt.setInt(9, isMailingList);
            stmt.execute();
        } catch (SQLException e) {
            System.err.println("Error adding person: " + e.getMessage());
            return;
        }
        addEmergencyContact(connection, ssn);
        System.out.println("Successfully added: " + firstName + " " + lastName + " to the database as a person");
    }

    private static void addEmergencyContact(Connection connection, String ssn) {
        Scanner input = new Scanner(System.in);
        System.out.println("Please enter the emergency contact for this person (first last):");
        String contactName = input.nextLine();
        System.out.println("Please enter the emergency contact's phone number xxx-xxx-xxxx:");
        String phoneNumber = input.nextLine();
        System.out.println("Please enter the emergency contact's relationship to the person:");
        String relationship = input.nextLine();
        String sql = "{Call AddEmergencyContact(?, ?, ?, ?)}";
        try (CallableStatement stmt = connection.prepareCall(sql)) {
            stmt.setString(1, ssn);
            stmt.setString(2, contactName);
            stmt.setString(3, phoneNumber);
            stmt.setString(4, relationship);
            stmt.execute();
        } catch (SQLException e) {
            System.err.println("Error adding client: " + e.getMessage());
            return;
        }
        System.out.println("Successfully added emergency contact: " + contactName);
    }

    private static void addClient(Connection connection, String ssn, String doctorName, String doctorPhoneNumber, String teamName) {
        String sql = "{Call AddClient(?, ?, ?, ?)}";
        try (CallableStatement stmt = connection.prepareCall(sql)) {
            stmt.setString(1, ssn);
            stmt.setString(2, doctorName);
            stmt.setString(3, doctorPhoneNumber);
            stmt.setString(4, teamName);
            stmt.execute();

        } catch (SQLException e) {
            System.err.println("Error adding client: " + e.getMessage());
        }
    }

    private static void addInsurancePolicy(Connection connection, String ssn, Integer id) {
        String sql = "{Call AddClientInsurance(?, ?)}";
        try (CallableStatement stmt = connection.prepareCall(sql)) {
            stmt.setString(1, ssn);
            stmt.setInt(2, id);
            stmt.execute();
        } catch (SQLException e) {
            System.out.println("Error adding insurance policy: " + e.getMessage());
        }
    }

    private static void addClientNeed(Connection connection, String ssn) {
        Scanner input = new Scanner(System.in);
        System.out.println("Enter the need: Day Support, In-House, Morning Support, Night Support, Transportation:");
        String name = input.nextLine();
        System.out.println("Enter the need level from 1-10");
        int level = Integer.parseInt(input.nextLine());
        String sql = "{Call AddClientNeed(?, ?, ?)}";
        try (CallableStatement stmt = connection.prepareCall(sql)) {
            stmt.setString(1, ssn);
            stmt.setString(2, name);
            stmt.setInt(3, level);
            stmt.execute();
        } catch (SQLException e) {
            System.err.println("Error adding need: " + e.getMessage());
        }
        System.out.println("Successfully added need");
    }

    private static void addVolunteer(Connection connection, String ssn) {
        Scanner input = new Scanner(System.in);
        System.out.println("Please enter the date joined YYYY-MM-DD:");
        String joinDateString = input.nextLine();

        // Converts the String into the DATE needed for the SQL constraint
        java.sql.Date joinSqlDate = toSqlDate(joinDateString);
        System.out.println("Please enter the date of latest training. Please use YYY-MM-DD:");
        String latestTrainingDateString = input.nextLine();
        java.sql.Date latestTrainingSqlDate = toSqlDate(latestTrainingDateString);

        System.out.println("Please enter the location of the latest training:");
        String latestTrainingLocation = input.nextLine();
        String sql = "{Call AddVolunteer(?, ?, ?, ?)}";
        try (CallableStatement stmt = connection.prepareCall(sql)) {
            stmt.setString(1, ssn);
            stmt.setDate(2, joinSqlDate);
            stmt.setDate(3, latestTrainingSqlDate);
            stmt.setString(4, latestTrainingLocation);
            stmt.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // Converts a date to a SQL Date to ensure Data Type consistency, specifically ensuring its a YYYY-MM-DD Format.
    private static java.sql.Date toSqlDate(String string) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            java.util.Date utilDate = dateFormat.parse(string);

            return new java.sql.Date(utilDate.getTime());
        } catch (ParseException e) {
            throw new IllegalArgumentException("Invalid date format. Please use YYYY-MM-DD.");
        }
    }

    private static void addVolunteerTeam(Connection connection, String ssn, String teamName) {
        Scanner input = new Scanner(System.in);
        System.out.println("Is the person the team leader? 0 for no, 1 for yes:");
        int isTeamLeader = Integer.parseInt(input.nextLine());
        String sql = "{Call AddVolunteerTeam(?, ?, ?)}";
        try (CallableStatement stmt = connection.prepareCall(sql)) {
            stmt.setString(1, ssn);
            stmt.setString(2, teamName);
            stmt.setInt(3, isTeamLeader);
            stmt.execute();
        } catch (SQLException e) {
            System.err.println("Error adding client: " + e.getMessage());
        }
    }

    private static void addVolunteerWork(Connection connection) {
        Scanner input = new Scanner(System.in);

        ArrayList<String> teamNames = lookupTeams(connection);
        if (!teamNames.isEmpty()) {
            System.out.println("Please enter the team name's number to the left:");
            String teamName = teamNames.get(Integer.parseInt(input.nextLine()));
            System.out.println("Enter Volunteer's SSN:");
            String ssn = input.nextLine();
            System.out.println("Enter the date for this work period YYYY-MM-DD:");
            String workDateString = input.nextLine();
            java.sql.Date workSqlDate = toSqlDate(workDateString);
            System.out.println("Enter the amount of hours worked to the nearest whole number:");
            int workHours = Integer.parseInt(input.nextLine());
            String sql = "{Call AddVolunteerWork(?, ?, ?, ?)}";
            try (CallableStatement stmt = connection.prepareCall(sql)) {
                stmt.setString(1, ssn);
                stmt.setString(2, teamName);
                stmt.setDate(3, workSqlDate);
                stmt.setInt(4, workHours);
                stmt.execute();
            } catch (SQLException e) {
                System.err.println("Error adding client: " + e.getMessage());
                return;
            }
        } else {
            System.out.println("No teams in the database");
            return;
        }
        System.out.println("Successfully added the hours worked to the database.");
    }

    private static void addEmployee(Connection connection, String ssn) {
        Scanner input = new Scanner(System.in);
        System.out.println("Enter Employee's Salary. ex: 12345.67 (no $ sign):");
        BigDecimal salary = new BigDecimal(input.nextLine());
        System.out.println("Enter Employee's marital status:");
        String maritalStatus = input.nextLine();
        String sql = "{Call AddEmployee(?, ?, ?)}";
        try (CallableStatement stmt = connection.prepareCall(sql)) {
            stmt.setString(1, ssn);
            stmt.setBigDecimal(2, salary);
            stmt.setString(3, maritalStatus);
            stmt.execute();
        } catch (SQLException e) {
            System.err.println("Error adding employee: " + e.getMessage());
            return;
        }
        System.out.println("Successfully added the employee.");
    }

    private static void addReport(Connection connection, String ssn, String teamName) {
        Scanner input = new Scanner(System.in);
        System.out.println("Enter a description for this report:");
        String description = input.nextLine();
        String sql = "{Call AddReport(?, ?, ?)}";
        try (CallableStatement stmt = connection.prepareCall(sql)) {
            stmt.setString(1, ssn);
            stmt.setString(2, teamName);
            stmt.setString(3, description);
            stmt.execute();
        } catch (SQLException e) {
            System.err.println("Error adding report: " + e.getMessage());
            return;
        }
        System.out.println("Successfully added the hours worked to the database.");
    }

    private static void addExpense(Connection connection) {
        Scanner input = new Scanner(System.in);
        System.out.println("Enter the employee's SSN number:");
        String ssn = input.nextLine();
        System.out.println("Enter the amount for the charge. ex: 12345.67 (no $ sign):");
        BigDecimal charge = new BigDecimal(input.nextLine());
        System.out.println("Enter a description for the item:");
        String description = input.nextLine();

        String sql = "{Call AddExpense(?, ?, ?)}";
        try (CallableStatement stmt = connection.prepareCall(sql)) {
            stmt.setString(1, ssn);
            stmt.setBigDecimal(2, charge);
            stmt.setString(3, description);
            stmt.execute();
        } catch (SQLException e) {
            System.err.println("Error adding expense: " + e.getMessage());
            return;
        }
        System.out.println("Successfully added the hours expense to the database.");
    }

    private static void addDonor(Connection connection, String ssn, int donorAnonymous) {
        String sql = "{Call AddDonor(?, ?)}";
        try (CallableStatement stmt = connection.prepareCall(sql)) {
            stmt.setString(1, ssn);
            stmt.setInt(2, donorAnonymous);
            stmt.execute();
        } catch (SQLException e) {
            System.out.println("Error adding donor: " + e.getMessage());
            return;
        }
        System.out.println("Successfully added the donor to the database.");
    }

    private static void addDonation(Connection connection, String ssn) {
        Scanner input = new Scanner(System.in);
        System.out.println("Enter the donation date. ex: YYYY-MM-DD:");
        String donationDateString = input.nextLine();
        java.sql.Date donationSqlDate = toSqlDate(donationDateString);
        System.out.println("Enter the donation amount. ex. 12345.67 (no $ sign):");
        BigDecimal amount = new BigDecimal(input.nextLine());
        System.out.println("Enter type of donation:");
        String typeOfDonation = input.nextLine();
        System.out.println("Enter the campaign name:");
        String campaignName = input.nextLine();

        // We make these NULL as depending on what the user picks, we want the other set to be null.
        String checkNumber = null;
        String cardNumber = null;
        String cardType = null;
        String expirationDate = null;
        String paymentMethod;
        System.out.println("Type 0 for check, 1 for card:");
        while (true) {
            String userInput = input.nextLine();
            if (userInput.equals("0")) {
                paymentMethod = "Check";
                System.out.println("Enter check number:");
                checkNumber = input.nextLine();
                break;
            } else if (userInput.equals("1")) {
                paymentMethod = "Card";
                System.out.println("Enter card number:");
                cardNumber = input.nextLine();
                System.out.println("Enter card type:");
                cardType = input.nextLine();
                System.out.println("Enter expiration date formated as MM/DD");
                expirationDate = input.nextLine();
                break;
            } else {
                System.out.println("Invalid payment method type, please insert 0 or 1.");
            }
        }

        String sql = "{Call AddDonation(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";
        try (CallableStatement stmt = connection.prepareCall(sql)) {
            stmt.setString(1, ssn);
            stmt.setDate(2, donationSqlDate);
            stmt.setBigDecimal(3, amount);
            stmt.setString(4, typeOfDonation);
            stmt.setString(5, campaignName);
            stmt.setString(6, paymentMethod);
            stmt.setString(7, checkNumber);
            stmt.setString(8, cardNumber);
            stmt.setString(9, cardType);
            stmt.setString(10, expirationDate);
            stmt.execute();
        } catch (SQLException e) {
            System.out.println("Error adding donation: " + e.getMessage());
            return;
        }
        System.out.println("Successfully added the donation to the database.");
    }

    private static void getDoctor(Connection connection, String ssn) {
        String sql = "{Call GetDoctor(?)}";
        try (CallableStatement stmt = connection.prepareCall(sql)) {
            stmt.setString(1, ssn);
            if (stmt.execute()) {
                ResultSet rs = stmt.getResultSet();
                while (rs.next()) {
                    String doctor = rs.getString(1);
                    String phoneNumber = rs.getString(2);
                    System.out.println("The doctor is " + doctor + " whose number is " + phoneNumber);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error getting doctor: " + e.getMessage());
        }
    }

    private static void getExpenses(Connection connection) {
        Scanner input = new Scanner(System.in);
        System.out.println("Enter the dates to search between, hit enter between entries (YYYY-MM-DD) x (YYYY-MM-DD:");
        String lowerDateString = input.nextLine();
        String upperDateString = input.nextLine();
        java.sql.Date lowerDateSqlDate = toSqlDate(lowerDateString);
        java.sql.Date upperDateSqlDate = toSqlDate(upperDateString);
        String sql = "{Call GetExpenses(?, ?)}";
        try (CallableStatement stmt = connection.prepareCall(sql)) {
            stmt.setDate(1, lowerDateSqlDate);
            stmt.setDate(2, upperDateSqlDate);
            if (stmt.execute()) {
                System.out.println("The expenses are as followed, if nothing followed, no expenses between these dates.");
                ResultSet rs = stmt.getResultSet();
                while (rs.next()) {
                    String ssn = rs.getString(1);
                    BigDecimal amount = new BigDecimal(rs.getString(2));
                    System.out.println(ssn + " total is " + amount);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error getting expenses: " + e.getMessage());
        }

    }

    private static void getVolunteers(Connection connection) {
        Scanner input = new Scanner(System.in);
        System.out.println("Enter client's SSN:");
        String ssn = input.nextLine();
        String sql = "{Call GetVolunteers(?)}";
        try (CallableStatement stmt = connection.prepareCall(sql)) {
            stmt.setString(1, ssn);
            if (stmt.execute()) {
                ResultSet rs = stmt.getResultSet();
                System.out.println("Volunteers assigned are: ");
                while (rs.next()) {
                    String ssn2 = rs.getString(1);
                    System.out.println(ssn2);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error getting volunteers: " + e.getMessage());
        }
    }

    private static void getTeamsAfter(Connection connection) {
        Scanner input = new Scanner(System.in);
        System.out.println("Enter the date to search after(YYYY-MM-DD):");
        String dateString = input.nextLine();
        java.sql.Date dateSqlDate = toSqlDate(dateString);
        String sql = "{Call GetTeamsAfter(?)}";
        try (CallableStatement stmt = connection.prepareCall(sql)) {
            stmt.setDate(1, dateSqlDate);
            if (stmt.execute()) {
                System.out.println("The teams are as followed, if nothing followed, no teams after the date.");
                ResultSet rs = stmt.getResultSet();
                while (rs.next()) {
                    String team = rs.getString(1);
                    System.out.println(team);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error getting teams: " + e.getMessage());
        }

    }

    private static void getAllInfo(Connection connection) {
        String sql = "{Call GetAllInfo()}";
        try (CallableStatement stmt = connection.prepareCall(sql)) {
            if (stmt.execute()) {
                ResultSet rs = stmt.getResultSet();
                while (rs.next()) {
                    String ssn = rs.getString(1);
                    String firstName = rs.getString(2);
                    String lastName = rs.getString(3);
                    String phoneNumber = rs.getString(4);
                    String contactName = rs.getString(5);
                    String Relationship = rs.getString(6);
                    String EmergencyPhoneNumber = rs.getString(7);
                    System.out.println(ssn + ": " + firstName + " " + lastName + " Number is: " + phoneNumber + " Emergency Contact: " + contactName + " " +
                            "Relationship: " + Relationship + " Phone Number: " + EmergencyPhoneNumber);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error getting info: " + e.getMessage());
        }
    }

    private static void getDonorEmployees(Connection connection) {
        String sql = "{Call GetDonorEmployees()}";
        try (CallableStatement stmt = connection.prepareCall(sql)) {
            if (stmt.execute()) {
                ResultSet rs = stmt.getResultSet();
                System.out.println("Null names indicate donor wants to remain anonymous.");
                while (rs.next()) {
                    String ssn = rs.getString(1);
                    String personName = rs.getString(2);
                    String totalDonation = rs.getString(3);
                    System.out.println(ssn + ": " + personName + ": " + totalDonation);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error getting info: " + e.getMessage());
        }
    }

    private static void increaseSalary(Connection connection) {
        String sql = "{Call IncreaseSalary()}";
        try (CallableStatement stmt = connection.prepareCall(sql)) {
            stmt.execute();
        } catch (SQLException e) {
            System.out.println("Error increasing salary: " + e.getMessage());
            return;
        }
        System.out.println("Increase salary completed.");
    }

    private static void deleteClients(Connection connection) {
        String sql = "{Call DeleteClientsWithoutHealthInsurance()}";
        try (CallableStatement stmt = connection.prepareCall(sql)) {
            if (stmt.execute()) {
                ResultSet rs = stmt.getResultSet();
                while (rs.next()) {
                    String ssn = rs.getString(1);
                    System.out.println(ssn);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error deleting clients: " + e.getMessage());
            return;
        }
        System.out.println("Clients deleted.");
    }

    private static void getMailingList(Connection connection) {
        Scanner input = new Scanner(System.in);
        System.out.println("Enter the output file path:");
        String outputFilePath = input.nextLine();
        String sql = "{Call GetMailingList()}";

        try (CallableStatement stmt = connection.prepareCall(sql)) {
            stmt.execute();
            ResultSet rs = stmt.getResultSet();
            BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath));
            writer.write("First Name,Last Name,Mailing Address");
            writer.newLine();

            while (rs.next()) {
                String firstName = rs.getString(1);
                String lastName = rs.getString(2);
                String mailingAddress = rs.getString(3);

                writer.write(firstName + "," + lastName + "," + mailingAddress);
                writer.newLine();

                System.out.println("Wrote " + firstName + " " + lastName + " To " + outputFilePath);
            }
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            System.out.println("Error getting mailing list: " + e.getMessage());
        }
    }
}