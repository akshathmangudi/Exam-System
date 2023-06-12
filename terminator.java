import java.sql.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class terminator {
    // JDBC connection details
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/examdb";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "Chintutyagi@12";

    private JFrame frame;
    private JButton assignSeatsButton;
    private JButton clearDatabaseButton;
    private JTextArea seatingTextArea;

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                terminator window = new terminator();
                window.frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public terminator() {
        initialize();
    }

    private void initialize() {
        frame = new JFrame();
        frame.setTitle("Exam Seating Arrangement");
        frame.setBounds(100, 100, 450, 350);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);

        assignSeatsButton = new JButton("Assign Seats");
        assignSeatsButton.setBounds(30, 20, 120, 25);
        frame.getContentPane().add(assignSeatsButton);

        clearDatabaseButton = new JButton("Clear Database");
        clearDatabaseButton.setBounds(160, 20, 120, 25);
        frame.getContentPane().add(clearDatabaseButton);

        seatingTextArea = new JTextArea();
        seatingTextArea.setEditable(false);
        seatingTextArea.setBounds(20, 60, 390, 220);
        frame.getContentPane().add(seatingTextArea);

        assignSeatsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                assignSeats();
            }
        });

        clearDatabaseButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                clearDatabase();
            }
        });
    }

    private void assignSeats() {
        try (Connection conn = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD)) {
            // Check if the 'seats' column exists in the table
            DatabaseMetaData meta = conn.getMetaData();
            ResultSet rs = meta.getColumns(null, null, "students", "seats");
            boolean seatsColumnExists = rs.next();
            rs.close();
    
            // If the 'seats' column does not exist, add it to the table
            if (!seatsColumnExists) {
                String alterTableQuery = "ALTER TABLE students ADD COLUMN seats INT";
                try (Statement stmt = conn.createStatement()) {
                    stmt.executeUpdate(alterTableQuery);
                }
            }
    
            // Retrieve the student records
            String selectQuery = "SELECT name, unique_id, exam FROM students";
            try (Statement stmt = conn.createStatement();
                 ResultSet resultSet = stmt.executeQuery(selectQuery)) {
                ArrayList<Integer> seatNumbers = new ArrayList<>();
                for (int i = 1; i <= 100; i++) {
                    seatNumbers.add(i);
                }
                Collections.shuffle(seatNumbers);
    
                String updateQuery = "UPDATE students SET seats = ? WHERE unique_id = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(updateQuery)) {
                    int index = 0;
                    while (resultSet.next()) {
                        String uniqueId = resultSet.getString("unique_id");
                        int seatNumber = seatNumbers.get(index);
    
                        pstmt.setInt(1, seatNumber);
                        pstmt.setString(2, uniqueId);
                        pstmt.executeUpdate();
    
                        index++;
                    }
                }
            }
    
            // Retrieve the updated seating arrangement
            String selectQueryWithSeats = "SELECT name, unique_id, exam, seats FROM students ORDER BY seats";
            try (Statement stmt = conn.createStatement();
                 ResultSet resultSet = stmt.executeQuery(selectQueryWithSeats)) {
                StringBuilder seatingArrangement = new StringBuilder();
                seatingArrangement.append("Seating Arrangement:\n");
                seatingArrangement.append("Name\t\t\t\tUnique ID\tExam\t\tSeat\n");
                seatingArrangement.append("---------------------------------------------------\n");
    
                while (resultSet.next()) {
                    String name = resultSet.getString("name");
                    String uniqueId = resultSet.getString("unique_id");
                    String exam = resultSet.getString("exam");
                    int seatNumber = resultSet.getInt("seats");
    
                    seatingArrangement.append(name);
                    seatingArrangement.append("\t\t");
                    seatingArrangement.append(uniqueId);
                    seatingArrangement.append("\t\t");
                    seatingArrangement.append(exam);
                    seatingArrangement.append("\t\t");
                    seatingArrangement.append(seatNumber);
                    seatingArrangement.append("\n");
                }
    
                seatingTextArea.setText(seatingArrangement.toString());
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    
    
    

    private void clearDatabase() {
        try (Connection conn = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD)) {
            String clearQuery = "TRUNCATE TABLE students";
            try (Statement stmt = conn.createStatement()) {
                stmt.executeUpdate(clearQuery);
            }

            seatingTextArea.setText("Database cleared.");
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
