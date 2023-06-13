import java.sql.*;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class ExamSeatingArrangementGUI extends Application {
    // JDBC connection details
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/examdb";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "Chintutyagi@12";

    private Stage stage;
    private TextField nameField;
    private TextField idField;
    private Button findSeatButton;
    private TextArea seatingTextArea;
    private Label errorLabel;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        stage = primaryStage;
        stage.setTitle("Exam Seating Arrangement");

        GridPane root = new GridPane();
        root.setPadding(new Insets(10));
        root.setHgap(10);
        root.setVgap(10);

        Label nameLabel = new Label("Name:");
        nameField = new TextField();

        Label idLabel = new Label("Unique ID:");
        idField = new TextField();

        findSeatButton = new Button("Find My Seat");

        seatingTextArea = new TextArea();
        seatingTextArea.setEditable(false);

        errorLabel = new Label();

        findSeatButton.setOnAction(e -> {
            String name = nameField.getText();
            String id = idField.getText();

            if (name.isEmpty() || id.isEmpty()) {
                errorLabel.setText("Please enter name and ID.");
                seatingTextArea.setText("");
            } else {
                findSeat(name, id);
            }
        });

        root.add(nameLabel, 0, 0);
        root.add(nameField, 1, 0);
        root.add(idLabel, 0, 1);
        root.add(idField, 1, 1);
        root.add(findSeatButton, 0, 2);
        root.add(seatingTextArea, 0, 3, 2, 1);
        root.add(errorLabel, 0, 4, 2, 1);

        Scene scene = new Scene(root, 450, 400);
        stage.setScene(scene);
        stage.show();
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
