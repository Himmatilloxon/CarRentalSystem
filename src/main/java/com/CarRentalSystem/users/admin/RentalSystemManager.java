package com.CarRentalSystem.users.admin;

import com.CarRentalSystem.Address;
import com.CarRentalSystem.RentalSystem;
import com.CarRentalSystem.database.AddressDAO;
import com.CarRentalSystem.database.RentalSystemDAO;
import com.CarRentalSystem.main.MainApp;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.List;

public class RentalSystemManager {

    private final BorderPane layout;

    public RentalSystemManager(Stage primaryStage, Admin admin) {
        layout = new BorderPane();

        // Left: Admin menu bar
        AdminMenuBar menuBar = new AdminMenuBar(admin, "Rental Systems");
        layout.setLeft(menuBar.getVBox());

        // Center: Search bar, list of rental systems, and Add button
        VBox centerVBox = new VBox(15);
        centerVBox.setAlignment(Pos.TOP_CENTER);
        centerVBox.setPadding(new Insets(10));

        // Search bar and button
        HBox searchBox = new HBox(10);
        searchBox.setAlignment(Pos.CENTER);

        TextField searchField = new TextField();
        searchField.setPromptText("Search Rental Systems");
        Button searchButton = new Button("Search");
        searchBox.getChildren().addAll(searchField, searchButton);

        // Scrollable list of rental systems
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        VBox rentalSystemListVBox = new VBox(10);
        rentalSystemListVBox.setPadding(new Insets(10));
        scrollPane.setContent(rentalSystemListVBox);

        // Add button
        Button addButton = new Button("Add Rental System");
        addButton.setOnAction(e -> {
            openAddRentalSystemDialog();
            MainApp.showRentalSystemManager(admin); // Refresh view after adding
        });

        // Load all rental systems on initialization
        populateRentalSystemList(rentalSystemListVBox, null);

        // Search functionality
        searchButton.setOnAction(e -> {
            String searchTerm = searchField.getText();
            populateRentalSystemList(rentalSystemListVBox, searchTerm);
        });

        // Add components to the center VBox
        centerVBox.getChildren().addAll(searchBox, scrollPane, addButton);

        // Set centerVBox to the center of the BorderPane
        layout.setCenter(centerVBox);
    }

    private void populateRentalSystemList(VBox listVBox, String searchTerm) {
        listVBox.getChildren().clear();

        // Placeholder method for retrieving rental systems from the database
        List<RentalSystem> rentalSystems = RentalSystemDAO.getRentalSystems(searchTerm);

        if (rentalSystems.isEmpty()) {
            Label emptyLabel = new Label("No rental systems found.");
            listVBox.getChildren().add(emptyLabel);
        } else {
            for (RentalSystem rentalSystem : rentalSystems) {
                HBox rentalSystemBox = new HBox(10);
                rentalSystemBox.setAlignment(Pos.CENTER_LEFT);
                rentalSystemBox.setPadding(new Insets(5));
                rentalSystemBox.setStyle("-fx-border-color: lightgrey; -fx-padding: 5;");

                Label nameLabel = new Label(rentalSystem.getName());

                rentalSystemBox.getChildren().addAll(nameLabel);
                listVBox.getChildren().add(rentalSystemBox);
            }
        }
    }

    private void openAddRentalSystemDialog() {
        Stage dialog = new Stage();
        dialog.setTitle("Add Rental System");

        VBox dialogVBox = new VBox(15);
        dialogVBox.setPadding(new Insets(20));
        dialogVBox.setAlignment(Pos.CENTER);

        // Input fields for new rental system
        TextField nameField = new TextField();
        nameField.setPromptText("Rental System Name");

        TextField streetAddressField = new TextField();
        TextField zipcodeField = new TextField();
        TextField countryField = new TextField();
        streetAddressField.setPromptText("Street Address");
        TextField cityField = new TextField();
        cityField.setPromptText("City");
        TextField stateField = new TextField();
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

        // Save button
        Button saveButton = new Button("Save");
        saveButton.disableProperty().bind(
                Bindings.createBooleanBinding(() ->
                                nameField.getText().trim().isEmpty()
                                        || streetAddressField.getText().trim().isEmpty()
                                        || cityField.getText().trim().isEmpty()
                                        || stateField.getText().trim().isEmpty()
                                        || !isValidZipcode(zipcodeField.getText())
                                        || countryField.getText().trim().isEmpty(),
                        nameField.textProperty(),
                        streetAddressField.textProperty(),
                        cityField.textProperty(),
                        stateField.textProperty(),
                        zipcodeField.textProperty(),
                        countryField.textProperty()
                )
        );

        saveButton.setOnAction(e -> {
            RentalSystem rentalSystem = new RentalSystem();
            rentalSystem.setName(nameField.getText());

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

            rentalSystem.setAddress_id(address.getId());

            // Placeholder method for saving a new rental system
            RentalSystemDAO.addRentalSystem(rentalSystem);

            dialog.close(); // Close dialog after saving
        });

        dialogVBox.getChildren().addAll(
                new Label("Add New Rental System"),
                nameField,
                streetAddressField,
                cityField,
                stateField,
                zipcodeField,
                countryField,
                saveButton
        );

        Scene scene = new Scene(dialogVBox, 400, 400);
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    boolean isValidZipcode(String zipcode) {
        return zipcode.matches("\\d+"); // Zipcode must be numeric
    }

    public BorderPane getLayout() {
        return layout;
    }
}
