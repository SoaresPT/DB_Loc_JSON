package org.example.db_gui_ex;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class UserManagementApp extends Application {

    private TextField nameField;
    private TextField ageField;
    private TextField emailField;
    private List<User> users;
    private final String DB_FILE = "users.json";

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("User Management");

        // Initialize user list
        users = loadUsers();

        // Create labels and text fields
        Label nameLabel = new Label("Name:");
        nameField = new TextField();
        Label ageLabel = new Label("Age:");
        ageField = new TextField();
        Label emailLabel = new Label("Email:");
        emailField = new TextField();

        // Create buttons
        Button addUserButton = new Button("Add User");
        addUserButton.setOnAction(e -> addUser(primaryStage));
        Button showUsersButton = new Button("Show Users");
        showUsersButton.setOnAction(e -> showUsers());

        // Create layout
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setVgap(10);
        grid.setHgap(10);
        grid.addRow(0, nameLabel, nameField);
        grid.addRow(1, ageLabel, ageField);
        grid.addRow(2, emailLabel, emailField);
        grid.addRow(3, addUserButton, showUsersButton);

        // Set up scene
        Scene scene = new Scene(grid, 300, 160);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void addUser(Stage primaryStage) {
        String name = nameField.getText();
        String ageStr = ageField.getText();
        String email = emailField.getText();

        // Validate age
        int age;
        try {
            age = Integer.parseInt(ageStr);
            if (age <= 0) {
                showAlert("Invalid Age", "Age must be a positive number.");
                return;
            }
        } catch (NumberFormatException e) {
            showAlert("Invalid Age", "Please enter a valid number for age.");
            return;
        }

        // Validate email (basic validation)
        if (!email.matches("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")) {
            showAlert("Invalid Email", "Please enter a valid email address.");
            return;
        }

        // Add user to list
        User newUser = new User(name, age, email);
        users.add(newUser);
        saveUsers();

        // Show confirmation popup
        showAlert("User Added", "User has been successfully added.");

        // Clear input fields
        nameField.clear();
        ageField.clear();
        emailField.clear();
    }

    private void showUsers() {
        // Display users in pop-up window
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle("Users");

        StringBuilder usersInfo = new StringBuilder();
        for (User user : users) {
            usersInfo.append("Name: ").append(user.getName()).append("\n")
                    .append("Age: ").append(user.getAge()).append("\n")
                    .append("Email: ").append(user.getEmail()).append("\n\n");
        }

        TextArea popupTextArea = new TextArea(usersInfo.toString());
        popupTextArea.setEditable(false);
        popupTextArea.setWrapText(true);

        Scene popupScene = new Scene(popupTextArea, 300, 200);
        popupStage.setScene(popupScene);
        popupStage.show();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private List<User> loadUsers() {
        try (Reader reader = new FileReader(DB_FILE)) {
            Gson gson = new Gson();
            Type userListType = new TypeToken<ArrayList<User>>(){}.getType();
            return gson.fromJson(reader, userListType);
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    private void saveUsers() {
        try (Writer writer = new FileWriter(DB_FILE)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(users, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}