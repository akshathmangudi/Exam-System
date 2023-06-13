import java.sql.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ExamSeatingArrangementGUI {
    // JDBC connection details
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/examdb";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "Chintutyagi@12";

    private JFrame frame;
    private JTextField nameField;
    private JTextField idField;
    private JButton findSeatButton;
    private JTextArea seatingTextArea;
    private JLabel errorLabel;

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                ExamSeatingArrangementGUI window = new ExamSeatingArrangementGUI();
                window.frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public ExamSeatingArrangementGUI() {
        initialize();
    }

    private void initialize() {
        frame = new JFrame();
        frame.setTitle("Exam Seating Arrangement");
        frame.setBounds(100, 100, 450, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);

        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setBounds(50, 50, 80, 25);
        frame.getContentPane().add(nameLabel);

        nameField = new JTextField();
        nameField.setBounds(140, 50, 200, 25);
        frame.getContentPane().add(nameField);
        nameField.setColumns(10);

        JLabel idLabel = new JLabel("Unique ID:");
        idLabel.setBounds(50, 90, 80, 25);
        frame.getContentPane().add(idLabel);

        idField = new JTextField();
        idField.setBounds(140, 90, 200, 25);
        frame.getContentPane().add(idField);
        idField.setColumns(10);

        findSeatButton = new JButton("Find My Seat");
        findSeatButton.setBounds(140, 130, 120, 25);
        frame.getContentPane().add(findSeatButton);

        seatingTextArea = new JTextArea();
        seatingTextArea.setEditable(false);
        seatingTextArea.setBounds(20, 180, 390, 150);
        frame.getContentPane().add(seatingTextArea);

        errorLabel = new JLabel("");
        errorLabel.setBounds(50, 165, 400, 15);
        errorLabel.setForeground(Color.RED);
        frame.getContentPane().add(errorLabel);

        findSeatButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String name = nameField.getText();
                String id = idField.getText();

                if (name.isEmpty() || id.isEmpty()) {
                    errorLabel.setText("Please enter name and ID.");
                    seatingTextArea.setText("");
                } else {
                    findSeat(name, id);
                }
            }
        });
    }

    private void findSeat(String name, String id) {
        try (Connection conn = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD)) {
            String selectQuery = "SELECT seats FROM students WHERE name = ? AND unique_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(selectQuery)) {
                stmt.setString(1, name);
                stmt.setString(2, id);
    
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    int seats = rs.getInt("seats");
    
                    seatingTextArea.setText("Name: " + name + "\n" +
                            "Unique ID: " + id + "\n" +
                            "Seat Number: " + seats);
                    errorLabel.setText("");
                } else {
                    seatingTextArea.setText("");
                    errorLabel.setText("Invalid name or ID. Please try again.");
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            errorLabel.setText("Error connecting to the database.");
            seatingTextArea.setText("");
        }
    }
    
    
}
