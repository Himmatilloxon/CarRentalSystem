package com.CarRentalSystem.users.admin;

import com.CarRentalSystem.Address;
import com.CarRentalSystem.database.AccountDAO;
import com.CarRentalSystem.database.AddressDAO;
import com.CarRentalSystem.database.ReceptionistDAO;
import com.CarRentalSystem.main.MainApp;
import com.CarRentalSystem.users.AccountStatus;
import com.CarRentalSystem.users.receptionist.Receptionist;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.List;

public class ReceptionistsManager {

    private final BorderPane layout;

    public ReceptionistsManager(Stage primaryStage, Admin admin) {
        layout = new BorderPane();

        AdminMenuBar menuBar = new AdminMenuBar(admin, "Receptionists");
        layout.setLeft(menuBar.getVBox());

        // Center: Search bar, list of receptionists, and Add button
        VBox centerVBox = new VBox(15);
        centerVBox.setAlignment(Pos.TOP_CENTER);
        centerVBox.setPadding(new Insets(10));

        // Search bar and button
        HBox searchBox = new HBox(10);
        searchBox.setAlignment(Pos.CENTER);

        TextField searchField = new TextField();
        searchField.setPromptText("Search Receptionists");
        Button searchButton = new Button("Search");
        searchBox.getChildren().addAll(searchField, searchButton);

        // Scrollable list of receptionists
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        VBox receptionistListVBox = new VBox(10);
        receptionistListVBox.setPadding(new Insets(10));
        scrollPane.setContent(receptionistListVBox);

        // Add button
        Button addButton = new Button("Add Receptionist");
        addButton.setOnAction(e ->{
            openAddReceptionistDialog();
            MainApp.showReceptionistManager(admin);
        });

        // Load all receptionists on initialization
        populateReceptionistList(receptionistListVBox, null);

        // Search functionality
        searchButton.setOnAction(e -> {
            String searchTerm = searchField.getText();
            populateReceptionistList(receptionistListVBox, searchTerm);
        });

        // Add components to the center VBox
        centerVBox.getChildren().addAll(searchBox, scrollPane, addButton);

        // Set centerVBox to the center of the BorderPane
        layout.setCenter(centerVBox);
    }

    private void populateReceptionistList(VBox listVBox, String searchTerm) {
        listVBox.getChildren().clear();

        List<Receptionist> receptionists = ReceptionistDAO.getReceptionists(searchTerm);

        if (receptionists.isEmpty()) {
            Label emptyLabel = new Label("No receptionists found.");
            listVBox.getChildren().add(emptyLabel);
        } else {
            for (Receptionist receptionist : receptionists) {
                HBox receptionistBox = new HBox(10);
                receptionistBox.setAlignment(Pos.CENTER_LEFT);
                receptionistBox.setPadding(new Insets(5));
                receptionistBox.setStyle("-fx-border-color: lightgrey; -fx-padding: 5;");

                Label nameLabel = new Label(receptionist.getLogin());
                Button deleteButton = new Button("Delete");
                deleteButton.setOnAction(e -> {
                    ReceptionistDAO.deleteReceptionist(receptionist.getId());
                    populateReceptionistList(listVBox, searchTerm); // Refresh list
                });

                receptionistBox.getChildren().addAll(nameLabel, deleteButton);
                listVBox.getChildren().add(receptionistBox);
            }
        }
    }

    private void openAddReceptionistDialog() {
        Stage dialog = new Stage();
        dialog.setTitle("Add Receptionist");

        VBox dialogVBox = new VBox(15);
        dialogVBox.setPadding(new Insets(20));
        dialogVBox.setAlignment(Pos.CENTER);

        // Input fields
        TextField nameField = new TextField();
        nameField.setPromptText("Name");

        TextField surnameField = new TextField();
        surnameField.setPromptText("Surname");
        DatePicker birthdayPicker = new DatePicker();
        birthdayPicker.setPromptText("Birthday");
        ComboBox<String> genderComboBox = new ComboBox<>();
        genderComboBox.getItems().addAll("Male", "Female");
        genderComboBox.setPromptText("Gender");
        TextField phoneField = new TextField();
        phoneField.setPromptText("Phone Number");
        phoneField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (isValidPhone(newValue)) {
                phoneField.setStyle("-fx-background-color: white;");
            } else {
                phoneField.setStyle("-fx-background-color: lightcoral;");
            }
        });
        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        emailField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (isValidEmail(newValue)) {
                emailField.setStyle("-fx-background-color: white;");
            } else {
                emailField.setStyle("-fx-background-color: lightcoral;");
            }
        });
        TextField streetAddressField = new TextField();
        TextField cityField = new TextField();
        TextField stateField = new TextField();
        TextField zipcodeField = new TextField();
        TextField countryField = new TextField();
        streetAddressField.setPromptText("Street Address");

        cityField.setPromptText("City");

        stateField.setPromptText("State");

        zipcodeField.setPromptText("Zip Code");
        zipcodeField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (isValidZipcode(newValue)) {
                zipcodeField.setStyle("-fx-background-color: white;");
            } else {
                zipcodeField.setStyle("-fx-background-color: lightcoral;");
            }
        });

        countryField.setPromptText("Country");
        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        PasswordField confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Confirm Password");

        // Feedback text for validation
        Text feedbackText = new Text();
        feedbackText.setFill(Color.RED);

        // Validate email
        emailField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (isValidEmail(newValue)) {
                emailField.setStyle("-fx-background-color: white;");
                feedbackText.setText(""); // Clear feedback
            } else {
                emailField.setStyle("-fx-background-color: lightcoral;");
                feedbackText.setText("Invalid email format!");
            }
        });

        // Validate password match
        confirmPasswordField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.equals(passwordField.getText())) {
                confirmPasswordField.setStyle("-fx-background-color: white;");
                feedbackText.setText(""); // Clear feedback
            } else {
                confirmPasswordField.setStyle("-fx-background-color: lightcoral;");
                feedbackText.setText("Passwords do not match!");
            }
        });

        // Save button
        Button saveButton = new Button("Save");
        saveButton.disableProperty().bind(
                Bindings.createBooleanBinding(() ->
                                nameField.getText().trim().isEmpty()
                                        || surnameField.getText().trim().isEmpty()
                                        || birthdayPicker.getValue() == null // Check DatePicker value
                                        || genderComboBox.getValue() == null
                                        || !isValidPhone(phoneField.getText())
                                        || !isValidEmail(emailField.getText())
                                        || streetAddressField.getText().trim().isEmpty()
                                        || cityField.getText().trim().isEmpty()
                                        || stateField.getText().trim().isEmpty()
                                        || !isValidZipcode(zipcodeField.getText())
                                        || countryField.getText().trim().isEmpty()
                                        || usernameField.getText().trim().isEmpty()
                                        || passwordField.getText().isEmpty()
                                        || confirmPasswordField.getText().isEmpty()
                                        || !isValidEmail(emailField.getText())
                                        || !passwordField.getText().equals(confirmPasswordField.getText()),
                        nameField.textProperty(),
                        surnameField.textProperty(),
                        birthdayPicker.valueProperty(),
                        genderComboBox.valueProperty(),
                        phoneField.textProperty(),
                        emailField.textProperty(),
                        streetAddressField.textProperty(),
                        cityField.textProperty(),
                        stateField.textProperty(),
                        zipcodeField.textProperty(),
                        countryField.textProperty(),
                        usernameField.textProperty(),
                        passwordField.textProperty(),
                        confirmPasswordField.textProperty()
                )
        );

        saveButton.setOnAction(e -> {
            Receptionist receptionist = new Receptionist();
            receptionist.setName(nameField.getText());
            receptionist.setSurname(surnameField.getText());
            receptionist.setBirthday(birthdayPicker.getValue());
            receptionist.setGender(genderComboBox.getValue());
            receptionist.setPhone(phoneField.getText());
            receptionist.setEmail(emailField.getText());

            Address address = new Address();
            address.setStreet(streetAddressField.getText());
            address.setCity(cityField.getText());
            address.setState(stateField.getText());
            address.setZipcode(zipcodeField.getText());
            address.setCountry(countryField.getText());
            try {
                AddressDAO.insertAddress(address);
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }

            receptionist.setAddressId(address.getId());
            receptionist.setLogin(usernameField.getText());
            receptionist.setPassword(passwordField.getText());
            receptionist.setUserType("Receptionist");
            receptionist.setStatus(AccountStatus.Active);
            try {
                AccountDAO.insertAccount(receptionist);
                ReceptionistDAO.addReceptionist(receptionist);
                dialog.close(); // Close dialog on successful save
            } catch (Exception ex) {
                feedbackText.setText("Error saving receptionist: " + ex.getMessage());
            }
        });

        dialogVBox.getChildren().addAll(
                new Label("Add New Receptionist"),
                nameField,
                surnameField,
                birthdayPicker,
                genderComboBox,
                phoneField,
                emailField,
                streetAddressField,
                cityField,
                stateField,
                zipcodeField,
                countryField,
                usernameField,
                passwordField,
                confirmPasswordField,
                feedbackText,
                saveButton
        );

        Scene scene = new Scene(dialogVBox, 400, 800);
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    boolean isValidPhone(String phone) {
        String phoneRegex = "\\d{7,15}"; // Adjust range based on your requirement
        return phone.matches(phoneRegex);
    }

    // Validation methods
    private boolean isValidEmail(String email) {
        String emailRegex = "^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
        return email.matches(emailRegex);
    }

    boolean isValidZipcode(String zipcode) {
        return zipcode.matches("\\d+"); // Zipcode must be numeric
    }


    public BorderPane getLayout() {
        return layout;
    }
}

