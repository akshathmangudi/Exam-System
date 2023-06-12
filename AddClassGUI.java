import java.sql.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class AddClassGUI {
    // JDBC connection details
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/examdb";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "Chintutyagi@12";

    private JFrame frame;
    private JTextField classField;
    private JComboBox<String> slotsComboBox;
    private JButton addButton;
    private JLabel statusLabel;

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                AddClassGUI window = new AddClassGUI();
                window.frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public AddClassGUI() {
        initialize();
    }

    private void initialize() {
        frame = new JFrame();
        frame.setTitle("Add Class");
        frame.setBounds(100, 100, 450, 200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);

        JLabel classLabel = new JLabel("Class:");
        classLabel.setBounds(50, 50, 80, 25);
        frame.getContentPane().add(classLabel);

        classField = new JTextField();
        classField.setBounds(140, 50, 200, 25);
        frame.getContentPane().add(classField);
        classField.setColumns(10);

        JLabel slotsLabel = new JLabel("Slots:");
        slotsLabel.setBounds(50, 90, 80, 25);
        frame.getContentPane().add(slotsLabel);

        slotsComboBox = new JComboBox<>();
        slotsComboBox.setBounds(140, 90, 200, 25);
        frame.getContentPane().add(slotsComboBox);

        // Add the slots to the combo box
        String[] slots = {
            "A1", "A2", "A3", "A4", "B1", "B2", "B3", "B4",
            "C1", "C2", "C3", "C4", "D1", "D2", "D3", "D4",
            "E1", "E2", "E3", "E4", "F1", "F2", "F3", "F4",
            "G1", "G2", "G3", "G4"
        };
        slotsComboBox.setModel(new DefaultComboBoxModel<>(slots));

        addButton = new JButton("Add");
        addButton.setBounds(140, 130, 120, 25);
        frame.getContentPane().add(addButton);

        statusLabel = new JLabel("");
        statusLabel.setBounds(50, 165, 400, 15);
        statusLabel.setForeground(Color.GREEN);
        frame.getContentPane().add(statusLabel);

        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String className = classField.getText();
                String slot = (String) slotsComboBox.getSelectedItem();

                if (className.isEmpty() || slot.isEmpty()) {
                    statusLabel.setText("Please enter class and select a slot.");
                } else {
                    addClass(className, slot);
                }
            }
        });
    }

    private void addClass(String className, String slot) {
        try (Connection conn = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD)) {
            String insertQuery = "INSERT INTO classes (class, slot) VALUES (?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(insertQuery)) {
                stmt.setString(1, className);
                stmt.setString(2, slot);
                int rowsAffected = stmt.executeUpdate();

                if (rowsAffected > 0) {
                    statusLabel.setText("Class added successfully.");
                    classField.setText("");
                    slotsComboBox.setSelectedIndex(0);
                } else {
                    statusLabel.setText("Failed to add class. Please try again.");
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            statusLabel.setText("Error connecting to the database.");
        }
    }
}
