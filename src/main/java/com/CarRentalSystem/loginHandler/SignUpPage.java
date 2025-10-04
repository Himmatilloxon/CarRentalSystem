package com.CarRentalSystem.loginHandler;

import com.CarRentalSystem.main.MainApp;
import com.CarRentalSystem.users.Account;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

public class SignUpPage {

    private final BorderPane layout;

    // Declare fields as instance variables
    private final TextField nameField = new TextField();
    private final TextField surnameField = new TextField();
    private final DatePicker birthdayPicker = new DatePicker();
    private final ComboBox<String> genderComboBox = new ComboBox<>();
    private final TextField phoneField = new TextField();
    private final TextField emailField = new TextField();
    private final TextField streetAddressField = new TextField();
    private final TextField cityField = new TextField();
    private final TextField stateField = new TextField();
    private final TextField zipcodeField = new TextField();
    private final TextField countryField = new TextField();
    private final TextField usernameField = new TextField();
    private final PasswordField passwordField = new PasswordField();
    private final PasswordField confirmPasswordField = new PasswordField();
    private final CheckBox driverCheckbox = new CheckBox("Sign up as Driver");
    private final Button uploadLicenseButton = new Button("Upload Driver License");
    private final Text feedbackText = new Text(); // Feedback text
    private File passportFile;
    private File licenseFile;
    private final BooleanProperty passportUploaded = new SimpleBooleanProperty(false);
    private final BooleanProperty licenseUploaded = new SimpleBooleanProperty(false);

    public SignUpPage(Stage primaryStage) {
        layout = new BorderPane();
        layout.setStyle("-fx-background-color: lightblue;");

        // Fields
        nameField.setPromptText("Name");

        surnameField.setPromptText("Surname");

        birthdayPicker.setPromptText("Birthday");

        genderComboBox.getItems().addAll("Male", "Female");
        genderComboBox.setPromptText("Gender");

        phoneField.setPromptText("Phone Number");
        phoneField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (isValidPhone(newValue)) {
                phoneField.setStyle("-fx-background-color: white;");
            } else {
              phoneField.setStyle("-fx-background-color: lightcoral;");
            }
        });

        emailField.setPromptText("Email Address");
        emailField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (isValidEmail(newValue)) {
                emailField.setStyle("-fx-background-color: white;");
            } else {
                emailField.setStyle("-fx-background-color: lightcoral;");
            }
        });

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

        usernameField.setPromptText("Username");

        passwordField.setPromptText("Password");

        confirmPasswordField.setPromptText("Confirm Password");
        // Check if password match
        confirmPasswordField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.equals(passwordField.getText())) {
                confirmPasswordField.setStyle("-fx-background-color: white;"); // Reset to default
                feedbackText.setText(""); // Clear feedback text
            } else {
                confirmPasswordField.setStyle("-fx-background-color: lightcoral;"); // Highlight in red
                feedbackText.setText("Passwords do not match!");
                feedbackText.setFill(Color.RED);
            }
        });

        // File upload for passport
        Button uploadPassportButton = new Button("Upload Passport");
        uploadPassportButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select Passport File");
            File selectedFile = fileChooser.showOpenDialog(primaryStage);
            if (selectedFile != null) {
                passportFile = selectedFile; // Store the selected file
                passportUploaded.set(true); // Update the flag
            }
        });

        // File upload for driver license, shown conditionally
        uploadLicenseButton.setVisible(false);
        uploadLicenseButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select Driver License File");
            File selectedFile = fileChooser.showOpenDialog(primaryStage);
            if (selectedFile != null) {
                licenseFile = selectedFile; // Store the selected file
                licenseUploaded.set(true); // Update the flag
            }
        });

        driverCheckbox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            uploadLicenseButton.setVisible(newValue);
        });

        // Signup button
        Button signupButton = new Button("Sign Up");
        // Bind the button's disable property to form validation conditions
        signupButton.disableProperty().bind(
                Bindings.createBooleanBinding(this::isFormInvalid,
                        usernameField.textProperty(),
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
                        passwordField.textProperty(),
                        confirmPasswordField.textProperty(),
                        passportUploaded, // Include flags
                        driverCheckbox.selectedProperty(),
                        licenseUploaded
                )
        );

        signupButton.setOnAction(e -> {
            try {
                infoManager();
            } catch (SQLException | IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        // Login button to navigate to the LoginPage
        Button loginButton = new Button("Login");
        loginButton.setOnAction(e -> MainApp.showLoginPage());

        // Layout with GridPane
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setVgap(10);
        grid.setHgap(10);
        grid.setPadding(new Insets(20));

        // Add components to grid
        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);

        grid.add(new Label("Surname:"), 0, 1);
        grid.add(surnameField, 1, 1);

        grid.add(new Label("Birthday:"), 0, 2);
        grid.add(birthdayPicker, 1, 2);

        grid.add(new Label("Gender:"), 0, 3);
        grid.add(genderComboBox, 1, 3);

        grid.add(new Label("Phone:"), 0, 4);
        grid.add(phoneField, 1, 4);

        grid.add(new Label("Email:"), 0, 5);
        grid.add(emailField, 1, 5);

        grid.add(new Label("Address:"), 0, 6);
        grid.add(streetAddressField, 1, 6);
        grid.add(cityField, 2, 6);
        grid.add(stateField, 1, 7);
        grid.add(zipcodeField, 2, 7);
        grid.add(countryField, 1, 8);

        grid.add(new Label("Username:"), 0, 9);
        grid.add(usernameField, 1, 9);

        grid.add(new Label("Password:"), 0, 10);
        grid.add(passwordField, 1, 10);

        grid.add(new Label("Confirm Password:"), 0, 11);
        grid.add(confirmPasswordField, 1, 11);

        grid.add(uploadPassportButton, 1, 12);
        grid.add(driverCheckbox, 0, 13);
        grid.add(uploadLicenseButton, 1, 13);
        grid.add(signupButton, 1, 14);
        grid.add(loginButton, 1, 15);

        layout.setCenter(grid);
    }

    private boolean isFormInvalid() {
        return usernameField.getText().trim().isEmpty()
                || nameField.getText().trim().isEmpty()
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
                || passwordField.getText().isEmpty()
                || confirmPasswordField.getText().isEmpty()
//                || !passportUploaded.get() // Use the observable flag
//                || (driverCheckbox.isSelected() && !licenseUploaded.get()) // Check license flag
                || !passwordField.getText().equals(confirmPasswordField.getText())
                ;
    }

    boolean isValidEmail(String email) {
        String emailRegex = "^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
        return email.matches(emailRegex);
    }

    boolean isValidPhone(String phone) {
        String phoneRegex = "\\d{7,15}"; // Adjust range based on your requirement
        return phone.matches(phoneRegex);
    }

    boolean isValidZipcode(String zipcode) {
        return zipcode.matches("\\d+"); // Zipcode must be numeric
    }

    public void infoManager() throws SQLException, IOException {
        SignUpHandler signUpHandler = new SignUpHandler();
        Account account = signUpHandler.handleSignUp(
                usernameField.getText(),
                nameField.getText(),
                surnameField.getText(),
                birthdayPicker.getValue(),
                genderComboBox.getValue(),
                phoneField.getText(),
                emailField.getText(),
                streetAddressField.getText(),
                cityField.getText(),
                stateField.getText(),
                zipcodeField.getText(),
                confirmPasswordField.getText(),
                passwordField.getText(),
                driverCheckbox.isSelected()
                );
//        FileSaver.savePassportFile(passportFile, account.getId());
//        if (driverCheckbox.isSelected()) FileSaver.saveLicienseFile(licenseFile, account.getId());

        MainApp.showLoginPage();
    }

    public BorderPane getLayout() {
        return layout;
    }
}
