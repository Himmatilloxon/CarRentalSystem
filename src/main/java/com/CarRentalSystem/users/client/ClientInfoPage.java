package com.CarRentalSystem.users.client;

import com.CarRentalSystem.Address;
import com.CarRentalSystem.database.AccountDAO;
import com.CarRentalSystem.database.AddressDAO;
import com.CarRentalSystem.users.Gender;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.function.Consumer;

public class ClientInfoPage {

    private final BorderPane layout;
    private final GridPane grid;
    private boolean isEditMode = false; // Toggle between edit and read-only
    private final Text feedbackText; // Feedback text for status messages

    private final Client client;
    private final Address address;

    public ClientInfoPage(Stage primaryStage, Client client) throws SQLException {
        this.client = client;
        this.address = AddressDAO.getAddressById(client.getAddressId());
        assert address != null;

        layout = new BorderPane();
        layout.setStyle("-fx-background-color: lightblue;");

        // Add the MenuBar
        ClientMenuBar menuBar = new ClientMenuBar(client, "Info");
        VBox menuBarLayout = menuBar.getVBox();
        layout.setLeft(menuBarLayout);

        grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setVgap(10);
        grid.setHgap(10);
        grid.setPadding(new Insets(20));

        feedbackText = new Text(); // Feedback text for messages
        layout.setBottom(feedbackText);

        // Build the initial grid
        rebuildGrid();

        layout.setCenter(grid);
    }

    private void addFieldToGrid(String label, String value, Consumer<String> setter, int row) {
        Label fieldLabel = new Label(label);
        grid.add(fieldLabel, 0, row);

        if (isEditMode) {
            if (label.equals("Birthday:")) {
                // Create a DatePicker for birthday
                DatePicker datePicker = new DatePicker(LocalDate.parse(value));
                grid.add(datePicker, 1, row);

                // Update the value when saving
                datePicker.valueProperty().addListener((obs, oldVal, newVal) -> {
                    if (newVal != null) {
                        setter.accept(newVal.toString());
                    }
                });
            } else if (label.equals("Gender:")) {
                // Create a ComboBox for gender
                ComboBox<String> genderComboBox = new ComboBox<>();
                genderComboBox.getItems().addAll("Male", "Female");
                genderComboBox.setValue(value);
                grid.add(genderComboBox, 1, row);
                genderComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
                    if (newVal != null) {
                        try {
                            setter.accept(newVal);
                        } catch (IllegalArgumentException e) {
                            feedbackText.setText("Invalid gender value selected.");
                        }
                    }
                });
            } else {
                // Create a TextField for other editable fields
                TextField textField = new TextField(value);
                grid.add(textField, 1, row);

                // Update the value when saving
                textField.focusedProperty().addListener((obs, oldVal, newVal) -> {
                    if (!newVal) { // Field lost focus
                        setter.accept(textField.getText());
                    }
                });
            }
        } else {
            // Create a Label for read-only mode
            Label valueLabel = new Label(value);
            grid.add(valueLabel, 1, row);
        }
    }


    private void toggleEditMode(Button editSaveButton) {
        if (isEditMode) {
            // Save changes to the database
            try {
                System.out.println(client.getId());
                AccountDAO.updatePerson(client); // Update client in database
                AddressDAO.updateAddress(address); // Update address in database
                feedbackText.setText("Changes saved successfully!");
            } catch (SQLException ex) {
                feedbackText.setText("Error saving changes: " + ex.getMessage());
            }
            editSaveButton.setText("Edit");
        } else {
            feedbackText.setText(""); // Clear feedback
            editSaveButton.setText("Save");
        }

        isEditMode = !isEditMode;

        // Rebuild the grid with the updated mode
        grid.getChildren().clear();
        rebuildGrid();
    }

    private void rebuildGrid() {
        addFieldToGrid("Name:", client.getName(), client::setName, 0);
        addFieldToGrid("Surname:", client.getSurname(), client::setSurname, 1);
        addFieldToGrid("Birthday:", client.getBirthday().toString(), date -> client.setBirthday(LocalDate.parse(date)), 2);
        addFieldToGrid("Gender:", client.getGender().toString(), client::setGender, 3);
        addFieldToGrid("Phone:", client.getPhone(), client::setPhone, 4);
        addFieldToGrid("Email:", client.getEmail(), client::setEmail, 5);
        addFieldToGrid("Street:", address.getStreet(), address::setStreet, 6);
        addFieldToGrid("City:", address.getCity(), address::setCity, 7);
        addFieldToGrid("State:", address.getState(), address::setState, 8);
        addFieldToGrid("Zipcode:", address.getZipcode(), address::setZipcode, 9);
        addFieldToGrid("Country:", address.getCountry(), address::setCountry, 10);

        // Add the Edit/Save button
        Button editSaveButton = new Button(isEditMode ? "Save" : "Edit");
        editSaveButton.setOnAction(e -> toggleEditMode(editSaveButton));
        grid.add(editSaveButton, 0, 12, 2, 1);
    }

    public BorderPane getLayout() {
        return layout;
    }
}
