import java.sql.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class class_terminator {
    // JDBC connection details
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/examdb";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "Chintutyagi@12";

    private JFrame frame;
    private JButton deleteButton;
    private JLabel statusLabel;

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                class_terminator window = new class_terminator();
                window.frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public class_terminator() {
        initialize();
    }

    private void initialize() {
        frame = new JFrame();
        frame.setTitle("Delete Table Content");
        frame.setBounds(100, 100, 350, 150);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);

        deleteButton = new JButton("Delete");
        deleteButton.setBounds(120, 40, 100, 25);
        frame.getContentPane().add(deleteButton);

        statusLabel = new JLabel("");
        statusLabel.setBounds(20, 80, 300, 15);
        statusLabel.setForeground(Color.GREEN);
        frame.getContentPane().add(statusLabel);

        deleteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deleteTableContent();
            }
        });
    }

    private void deleteTableContent() {
        try (Connection conn = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD)) {
            String deleteQuery = "DELETE FROM classes";
            try (PreparedStatement stmt = conn.prepareStatement(deleteQuery)) {
                int rowsAffected = stmt.executeUpdate();

                if (rowsAffected > 0) {
                    statusLabel.setText("Table content deleted successfully.");
                } else {
                    statusLabel.setText("No records to delete.");
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            statusLabel.setText("Error connecting to the database.");
        }
    }
}
