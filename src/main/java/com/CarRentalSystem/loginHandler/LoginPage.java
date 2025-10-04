package com.CarRentalSystem.loginHandler;

import com.CarRentalSystem.database.AccountDAO;
import com.CarRentalSystem.main.MainApp;
import com.CarRentalSystem.users.Account;
import com.CarRentalSystem.users.Person;
import com.CarRentalSystem.users.UserType;
import com.CarRentalSystem.users.admin.Admin;
import com.CarRentalSystem.users.client.Client;
import com.CarRentalSystem.users.member.Member;
import com.CarRentalSystem.users.receptionist.Receptionist;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

public class LoginPage {

    private final BorderPane layout;

    private final ComboBox<String> userTypeDropdown = new ComboBox<>();
    private final TextField usernameField = new TextField();
    private final PasswordField passwordField = new PasswordField();

    public LoginPage(Stage primaryStage) {
        layout = new BorderPane();
        layout.setStyle("-fx-background-color: blue;"); // Set background color to blue

        // Create a label and ComboBox within the square
        Label loginLabel = new Label("Login as:");
        loginLabel.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        loginLabel.setTextFill(Color.WHITE);

        userTypeDropdown.getItems().addAll("Client", "Member", "Receptionist", "Admin");
        userTypeDropdown.setPromptText("Select User Type");
        userTypeDropdown.setMaxWidth(200); // Set width for ComboBox

        // Username and password fields
        usernameField.setPromptText("Username");
        usernameField.setMaxWidth(200); // Limit width

        passwordField.setPromptText("Password");
        passwordField.setMaxWidth(200); // Limit width

        // Login button to navigate to the User's page
        Button loginButton = new Button("Login");
        loginButton.setOnAction(e -> {loginHandler();});

        // Sign Up button to navigate to the SignupPage
        Button signUpButton = new Button("Sign Up");
        signUpButton.setOnAction(e -> MainApp.showSignUpPage());
        signUpButton.setVisible(false);

        // Add a listener to the ComboBox to detect selection changes
        userTypeDropdown.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                // Check if the selected item matches the specified item
                signUpButton.setVisible("Client".equals(newValue) || "Member".equals(newValue));  // Show the button
            }
        });

        // Layout setup with VBox
        VBox vbox = new VBox(20); // Space between elements
        vbox.setAlignment(Pos.CENTER);
        vbox.getChildren().addAll(loginLabel, userTypeDropdown, usernameField, passwordField, loginButton, signUpButton);

        // Create a square background for design
        Rectangle square = new Rectangle(300, 300); // Adjust dimensions as needed
        square.setFill(Color.TRANSPARENT); // Transparent fill
        square.setStroke(Color.WHITE); // White border
        square.setStrokeWidth(3); // Set border width
        square.setArcWidth(20); // Rounded corners
        square.setArcHeight(20);

        // StackPane to hold the square background and the VBox
        StackPane centerSquare = new StackPane(square, vbox);
        layout.setCenter(centerSquare);
    }

    private void loginHandler() {
        if (usernameField.getText().isEmpty() || passwordField.getText().isEmpty()) {
            showAlert("Fill all fields");
        } else {

            Optional<Account> account = AccountDAO.getAccountByLogin(usernameField.getText());

            if (account.isPresent()) {
                if (account.get().getPassword().equals(passwordField.getText()) && account.get().getUserType().equals(UserType.valueOf(userTypeDropdown.getValue()))) {
                    Person person = AccountDAO.getPersonById(account.get().getId());
                    if (person != null) {
                        String selectedUserType = userTypeDropdown.getValue();
                        switch (selectedUserType) {
                            case "Client" -> {
                                Client client = AccountDAO.getClientById(account.get().getId());
                                if (client != null) {
                                    client = new Client(person.getName(), person.getSurname(), person.getGender().toString(), person.getEmail(), person.getPhone(),
                                            person.getBirthday(), person.getAddressId(), account.get().getLogin(), account.get().getPassword(), account.get().getUserType(),
                                            account.get().getStatus(), client.getCreated_at());
                                    client.setId(person.getId());
                                    MainApp.showClient(client);
                                }
                            }
                            case "Member" -> {
                                Member member = new Member(person.getName(), person.getSurname(), person.getGender().toString(), person.getEmail(), person.getPhone(),
                                        person.getBirthday(), person.getAddressId(), account.get().getLogin(), account.get().getPassword(), account.get().getUserType(),
                                        account.get().getStatus());
                                member.setId(person.getId());
                                MainApp.showMember(member);
                            }
                            case "Receptionist" -> {
                                Receptionist receptionist = AccountDAO.getReceptionistById(account.get().getId());
                                if (receptionist != null) {
                                    receptionist = new Receptionist(person.getName(), person.getSurname(), person.getGender().toString(), person.getEmail(), person.getPhone(),
                                            person.getBirthday(), person.getAddressId(), account.get().getLogin(), account.get().getPassword(), account.get().getUserType(),
                                            account.get().getStatus(), receptionist.getDate_joined());
                                    receptionist.setId(person.getId());
                                    MainApp.showReceptionist(receptionist);
                                }
                            }
                            case "Admin" -> {
                                Admin admin = AccountDAO.getAdminById(account.get().getId());
                                if (admin != null) {
                                    admin = new Admin(person.getName(), person.getSurname(), person.getGender().toString(), person.getEmail(), person.getPhone(),
                                            person.getBirthday(), person.getAddressId(), account.get().getLogin(), account.get().getPassword(), account.get().getUserType(),
                                            account.get().getStatus(), admin.getPermissions());
                                    admin.setId(person.getId());
                                    MainApp.showAdmin(admin);
                                }
                            }
                            case null, default ->
                                // Optionally handle the case where no user type is selected
                                    showAlert("Choose login type");
                        }
                    }
                }  else {
                    showAlert("Wrong login or password");
                }
            } else {
                showAlert("Account is not found!");
            }
        }
    }

    // Helper method to show an alert if no user type is selected
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public BorderPane getLayout() {
        return layout;
    }
}
