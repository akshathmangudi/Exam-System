import java.sql.*;

public class jdbc {
    // JDBC connection details
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/examdb";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "Chintutyagi@12";

    public static void main(String[] args) {
        try (Connection conn = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD)) {
            // Create the database table if it doesn't exist
            createTable(conn);

            // Generate and insert student data into the table
            generateStudentData(conn);

            // Retrieve and display the seating arrangement
            displaySeatingArrangement(conn);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void createTable(Connection conn) throws SQLException {
        String createTableQuery = "CREATE TABLE IF NOT EXISTS students (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "name VARCHAR(100)," +
                "unique_id VARCHAR(10)," +
                "exam VARCHAR(100)," +
                "slot_number VARCHAR(2)" +
                ")";
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(createTableQuery);
            System.out.println("Table 'students' created successfully!");
        }
    }

    private static void generateStudentData(Connection conn) throws SQLException {
        String[] names = {"John Doe", "Jane Smith", "Mike Johnson", "Emily Davis", "Robert Wilson", "Olivia Brown",
                "David Taylor", "Sophia Miller", "Daniel Anderson", "Ava Garcia", "William Thomas", "Mia Martinez"};
        String[] uniqueIds = {"001", "002", "003", "004", "005", "006", "007", "008", "009", "010", "011", "012"};
        String[] exams = {"Math", "Physics", "Chemistry", "English", "History", "Biology",
                "Geography", "Computer Science", "Economics", "Art", "Music", "Physical Education"};

        String insertQuery = "INSERT INTO students (name, unique_id, exam, slot_number) VALUES (?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(insertQuery)) {
            for (int i = 0; i < names.length; i++) {
                String slot = getRandomSlot(conn);
                stmt.setString(1, names[i]);
                stmt.setString(2, uniqueIds[i]);
                stmt.setString(3, exams[i]);
                stmt.setString(4, slot);
                stmt.executeUpdate();
            }
            System.out.println("Student data inserted successfully!");
        }
    }

    private static String getRandomSlot(Connection conn) throws SQLException {
        String selectQuery = "SELECT slot FROM classes ORDER BY RAND() LIMIT 1";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(selectQuery)) {
            if (rs.next()) {
                return rs.getString("slot");
            }
        }
        throw new SQLException("Failed to retrieve a random slot from the classes table.");
    }

    private static void displaySeatingArrangement(Connection conn) throws SQLException {
        String selectQuery = "SELECT s.name, s.unique_id, s.exam, s.slot_number FROM students s ORDER BY s.slot_number, s.name";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(selectQuery)) {
            System.out.println("Seating Arrangement:");
            System.out.println("Slot\tName\t\t\t\tUnique ID\tExam");
            System.out.println("---------------------------------------------");
            while (rs.next()) {
                String name = rs.getString("name");
                String uniqueId = rs.getString("unique_id");
                String exam = rs.getString("exam");
                String slot = rs.getString("slot_number");

                System.out.printf("%s\t%-25s\t%s\t\t%s\n", slot, name, uniqueId, exam);
            }
        }
    }
}
