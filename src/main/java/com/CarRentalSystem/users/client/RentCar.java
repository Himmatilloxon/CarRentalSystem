package com.CarRentalSystem.users.client;

import com.CarRentalSystem.components.BackButtonBox;
import com.CarRentalSystem.database.VehicleDAO;
import com.CarRentalSystem.main.MainApp;
import com.CarRentalSystem.vehicle.Vehicle;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.util.List;
import java.util.UUID;

public class RentCar {
    private final TilePane vehiclePane; // Class-level variable for the TilePane
    private final BorderPane layout;
    private final ComboBox<String> vehicleDropdown;
    private final ComboBox<String> typeDropdown;
    private final UUID rental_id;
    private final Client client;

    public RentCar(Stage primaryStage, UUID rental_id, Client client) {
        this.rental_id = rental_id;
        this.client = client;
        layout = new BorderPane();

        VBox mainContent = new VBox(15);
        mainContent.setAlignment(Pos.TOP_CENTER);
        mainContent.setPadding(new Insets(50, 0, 0, 0));

        // Vehicle Dropdown
        vehicleDropdown = new ComboBox<>();
        vehicleDropdown.getItems().addAll("Car", "Truck", "SUV", "Van", "Motorcycle");

        // Type Dropdown
        typeDropdown = new ComboBox<>();
        typeDropdown.setVisible(false);

        // Initialize TilePane
        vehiclePane = new TilePane();
        vehiclePane.setAlignment(Pos.CENTER);
        vehiclePane.setHgap(15);
        vehiclePane.setVgap(10);
        vehiclePane.setPadding(new Insets(10));

        vehicleDropdown.setOnAction(e -> {
            typeDropdown.getItems().clear();
            populateTypeDropdown(vehicleDropdown.getValue(), typeDropdown);
            typeDropdown.setVisible(true);

            // Update vehicles based on vehicle type
            updateVehicles(vehicleDropdown.getValue(), null);
        });

        typeDropdown.setOnAction(e -> {
            // Update vehicles based on vehicle type and specific type
            updateVehicles(vehicleDropdown.getValue(), typeDropdown.getValue());
        });

        ScrollPane scrollPane = new ScrollPane(vehiclePane);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefViewportHeight(400);

        mainContent.getChildren().addAll(new Label("Choose Vehicle type"), vehicleDropdown, typeDropdown, scrollPane);

        BackButtonBox backButtonBox = new BackButtonBox();

        layout.setCenter(mainContent);
        layout.setTop(backButtonBox.getBackButtonBox());
    }

    public void updateVehicles(String vehicleType, String specificType) {
        if (vehiclePane == null) {
            throw new IllegalStateException("TilePane is not initialized.");
        }

        vehiclePane.getChildren().clear(); // Clear previous items

        // Fetch vehicles from the database
        List<Vehicle> vehicleList = VehicleDAO.fetchVehicles(vehicleType, specificType, rental_id);

        for (Vehicle vehicle : vehicleList) {
            // Create a rectangle with labels for each vehicle
            Rectangle rectShape = new Rectangle(200, 100, Color.LIGHTGRAY);
            rectShape.setArcWidth(20);
            rectShape.setArcHeight(20);

            VBox rectangleContent = new VBox();
            rectangleContent.setAlignment(Pos.CENTER);

            Label makeLabel = new Label(vehicle.getMake());
            Label modelLabel = new Label(vehicle.getModel());
            Label priceLabel = new Label(String.valueOf(Math.round(vehicle.getPrice() * 100.0) / 100.0));
            rectangleContent.getChildren().addAll(makeLabel, modelLabel, priceLabel);

            StackPane clickableRectangle = new StackPane(rectShape, rectangleContent);
            clickableRectangle.setOnMouseClicked(e -> MainApp.showAdditionalMaterials(vehicle, client));
            vehiclePane.getChildren().add(clickableRectangle);
        }

        vehiclePane.requestLayout(); // Ensure layout updates
    }

    public void populateTypeDropdown(String vehicle, ComboBox<String> typeDropdown) {
        switch (vehicle) {
            case "Car" -> typeDropdown.getItems().addAll("Economy", "Compact", "Intermediate", "Standard", "FullSize", "Premium", "Luxury");
            case "Truck" -> typeDropdown.getItems().addAll("Pickup", "Refrigerated", "Dump", "Semitrailer");
            case "SUV" -> typeDropdown.getItems().addAll("Micro", "Compact", "Luxury");
            case "Van" -> typeDropdown.getItems().addAll("Passenger", "Cargo");
            case "Motorcycle" -> typeDropdown.getItems().addAll("Cruiser", "Touring", "Moped");
        }
    }

    public BorderPane getLayout() {
        return layout;
    }
}
