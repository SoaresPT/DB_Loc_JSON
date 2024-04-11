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
import java.util.Optional;

public class UserManagementApp extends Application {

    private List<User> users;
    private final String DB_FILE = "users.json";
    private Stage popupStage;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("User Management");

        // Initialize user list
        users = loadUsers();

        // Create buttons
        Button addUserButton = new Button("Add User");
        addUserButton.setOnAction(e -> addUser(primaryStage));
        Button showUsersButton = new Button("Show Users");
        showUsersButton.setOnAction(e -> showUsers(primaryStage));

        // Create layout
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setVgap(10);
        grid.setHgap(10);
        grid.addRow(0, addUserButton, showUsersButton);

        // Set up scene
        Scene scene = new Scene(grid, 270, 80);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void addUser(Stage primaryStage) {
        // Create labels and text fields for user input
        Label nameLabel = new Label("Name:");
        TextField nameField = new TextField();
        Label ageLabel = new Label("Age:");
        TextField ageField = new TextField();
        Label emailLabel = new Label("Email:");
        TextField emailField = new TextField();

        // Create 'Add' button
        Button addButton = new Button("Add");
        addButton.setOnAction(e -> {
            String name = nameField.getText();
            String ageStr = ageField.getText();
            String email = emailField.getText();

            // Validate input
            if (name.isEmpty() || ageStr.isEmpty() || email.isEmpty()) {
                showAlert("Error", "Please fill in all fields.");
                return;
            }

            int age;
            try {
                age = Integer.parseInt(ageStr);
            } catch (NumberFormatException ex) {
                showAlert("Error", "Please enter a valid age.");
                return;
            }

            // Add new user to the list
            users.add(new User(name, age, email));
            saveUsers();

            // Show confirmation message
            showAlert("Success", "User added successfully.");

            // Clear input fields
            nameField.clear();
            ageField.clear();
            emailField.clear();
        });

        // Create layout for user input
        GridPane inputGrid = new GridPane();
        inputGrid.setPadding(new Insets(20));
        inputGrid.setVgap(10);
        inputGrid.setHgap(10);
        inputGrid.addRow(0, nameLabel, nameField);
        inputGrid.addRow(1, ageLabel, ageField);
        inputGrid.addRow(2, emailLabel, emailField);
        inputGrid.addRow(3, addButton);

        // Set up scene for adding user
        Scene addUserScene = new Scene(inputGrid, 300, 200);
        Stage addUserStage = new Stage();
        addUserStage.initOwner(primaryStage);
        addUserStage.setScene(addUserScene);
        addUserStage.setTitle("Add User");
        addUserStage.show();
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
    private void showUsers(Stage primaryStage) {
        // Close the previous popup stage if it's open
        if (popupStage != null && popupStage.isShowing()) {
            popupStage.close();
        }

        // Display users in pop-up window
        popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle("Users");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setVgap(10);
        grid.setHgap(10);

        int rowIndex = 0;
        for (User user : users) {
            Label nameLabel = new Label("Name: " + user.getName());
            Label ageLabel = new Label("Age: " + user.getAge());
            Label emailLabel = new Label("Email: " + user.getEmail());

            Button updateButton = new Button("Update");
            updateButton.setOnAction(e -> updateUser(primaryStage, user));

            Button deleteButton = new Button("Delete");
            deleteButton.setOnAction(e -> deleteUser(user, primaryStage)); // Pass primaryStage to deleteUser

            grid.addRow(rowIndex++, nameLabel, ageLabel, emailLabel, updateButton, deleteButton);
        }

        Scene popupScene = new Scene(grid);
        popupStage.setScene(popupScene);
        popupStage.show();
    }

    private void updateUser(Stage primaryStage, User user) {
        // Create labels and text fields for user input
        Label nameLabel = new Label("Name:");
        TextField nameField = new TextField(user.getName());
        Label ageLabel = new Label("Age:");
        TextField ageField = new TextField(String.valueOf(user.getAge()));
        Label emailLabel = new Label("Email:");
        TextField emailField = new TextField(user.getEmail());

        // Create 'Update' button
        Button updateButton = new Button("Update");
        updateButton.setOnAction(e -> {
            String name = nameField.getText();
            String ageStr = ageField.getText();
            String email = emailField.getText();

            // Validate input
            if (name.isEmpty() || ageStr.isEmpty() || email.isEmpty()) {
                showAlert("Error", "Please fill in all fields.");
                return;
            }

            int age;
            try {
                age = Integer.parseInt(ageStr);
            } catch (NumberFormatException ex) {
                showAlert("Error", "Please enter a valid age.");
                return;
            }

            // Update user details
            user.setName(name);
            user.setAge(age);
            user.setEmail(email);
            saveUsers();

            // Show confirmation message
            showAlert("Success", "User updated successfully.");

            // Close the 'Update User' window
            ((Stage) updateButton.getScene().getWindow()).close();

            // Refresh the data in the 'Show Users' window
            showUsers(primaryStage);
        });

        // Create layout for user input
        GridPane inputGrid = new GridPane();
        inputGrid.setPadding(new Insets(20));
        inputGrid.setVgap(10);
        inputGrid.setHgap(10);
        inputGrid.addRow(0, nameLabel, nameField);
        inputGrid.addRow(1, ageLabel, ageField);
        inputGrid.addRow(2, emailLabel, emailField);
        inputGrid.addRow(3, updateButton);

        // Set up scene for updating user
        Scene updateUserScene = new Scene(inputGrid, 280, 180);
        Stage updateUserStage = new Stage();
        updateUserStage.initOwner(primaryStage);
        updateUserStage.setScene(updateUserScene);
        updateUserStage.setTitle("Update User");
        updateUserStage.show();
    }

    private void deleteUser(User user, Stage primaryStage) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Delete User");
        alert.setContentText("Are you sure you want to delete this user?\nName: " + user.getName());

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            users.remove(user);
            saveUsers();
            showAlert("Success", "User deleted successfully.");

            // Refresh the data in the 'Show Users' window
            showUsers(primaryStage);
        }
    }


    public static void main(String[] args) {
        launch(args);
    }
}