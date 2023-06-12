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
    private JButton displayButton;
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
        frame.setTitle("Exam Seating Display");
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

        displayButton = new JButton("Display");
        displayButton.setBounds(140, 130, 120, 25);
        frame.getContentPane().add(displayButton);

        seatingTextArea = new JTextArea();
        seatingTextArea.setEditable(false);
        seatingTextArea.setBounds(20, 180, 390, 150);
        frame.getContentPane().add(seatingTextArea);

        errorLabel = new JLabel("");
        errorLabel.setBounds(50, 165, 400, 15);
        errorLabel.setForeground(Color.RED);
        frame.getContentPane().add(errorLabel);

        displayButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String name = nameField.getText();
                String id = idField.getText();

                if (name.isEmpty() || id.isEmpty()) {
                    errorLabel.setText("Please enter name and ID.");
                    seatingTextArea.setText("");
                } else {
                    displaySeatInfo(name, id);
                }
            }
        });
    }

    private void displaySeatInfo(String name, String id) {
        try (Connection conn = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD)) {
            String query = "SELECT s.slot_number, c.class, s.seats " +
                    "FROM students s " +
                    "JOIN classes c ON s.slot_number = c.slot " +
                    "WHERE s.name = ? AND s.unique_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, name);
                stmt.setString(2, id);

                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        String slot = rs.getString("slot_number");
                        String studentClass = rs.getString("class");
                        int seats = rs.getInt("seats");

                        String seatingInfo = "Name: " + name + "\n" +
                                "Unique ID: " + id + "\n" +
                                "Slot: " + slot + "\n" +
                                "Class: " + studentClass + "\n" +
                                "Seats: " + seats;

                        seatingTextArea.setText(seatingInfo);
                        errorLabel.setText("");
                    } else {
                        seatingTextArea.setText("");
                        errorLabel.setText("No seat found for the given name and ID.");
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
