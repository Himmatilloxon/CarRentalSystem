package com.CarRentalSystem.users.client;

import com.CarRentalSystem.RentalSystem;
import com.CarRentalSystem.database.RentalSystemDAO;
import com.CarRentalSystem.main.MainApp;
import com.CarRentalSystem.shapes.rectangle;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class ClientUI {

    private final BorderPane layout;
    private final Client client;

    public ClientUI(Stage primaryStage, Client client) {
        layout = new BorderPane();
        this.client = client;

        ClientMenuBar menuBar = new ClientMenuBar(client, "Home");
        VBox menuBarLayout = menuBar.getVBox();
        layout.setLeft(menuBarLayout);  // Place MenuBar on the left side

        ComboBox<String> Region = new ComboBox<>();
        Region.getItems().addAll("Tashkent", "Andijan", "Bukhara", "Fergana", "Jizzakh", "Namangan", "Navoiy", "Qashqadaryo", "Samarqand", "Sirdaryo", "Sirdaryo", "Surxondaryo", "Tashkent Region", "Xorazm", "Republic of  Karakalpakstan");
        Region.setPromptText("Region");

        // Create the Label
        Label regionLabel = new Label("Choose Region:");

        // Use an HBox to place the Label to the left of the ComboBox
        HBox regionBox = new HBox(10, regionLabel, Region); // 10 is the spacing between Label and ComboBox
        regionBox.setAlignment(Pos.CENTER); // Center align within the HBox

        // Use a VBox to position the HBox towards the top in the center
        VBox centerContent = new VBox(regionBox);
        centerContent.setAlignment(Pos.TOP_CENTER); // Align at the top center of the VBox
        centerContent.setSpacing(20); // Optional: Add some space if needed
        centerContent.setPadding(new javafx.geometry.Insets(50, 0, 0, 0)); // Push down from top if desired

        Label RentalLocation = new Label("Choose Rental Location:");
        RentalLocation.setVisible(false);

        // TilePane to hold rental options (hidden initially)
        TilePane rentalSystems = new TilePane();
        rentalSystems.setAlignment(Pos.CENTER); // Optional: Align content to center
        rentalSystems.setHgap(15);
        rentalSystems.setVgap(10);
        rentalSystems.setVisible(false);
        rentalSystems.setMouseTransparent(true); // Initially hidden

        Region.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.isEmpty()) {
                // Fetch tiles for the selected region
                List<StackPane> tiles = createRentalSystemList(newValue);

                // Clear and update the TilePane
                rentalSystems.getChildren().clear();
                rentalSystems.getChildren().addAll(tiles);

                // Show the TilePane and enable interaction
                RentalLocation.setVisible(true);
                rentalSystems.setVisible(true);
                rentalSystems.setMouseTransparent(false);
            } else {
                // Hide the TilePane if no region is selected
                rentalSystems.setVisible(false);
                rentalSystems.setMouseTransparent(true);
                rentalSystems.getChildren().clear();
            }
        });

        centerContent.getChildren().addAll(RentalLocation, rentalSystems);

        // Place the VBox in the center of the BorderPane within a StackPane
        StackPane stackPane = new StackPane(centerContent);
        layout.setCenter(stackPane);
    }

    public List<StackPane> createRentalSystemList(String selectedRegion) {
        // Retrieve the list of rental systems based on selected region
        List<RentalSystem> rentalSystems = getRentalSystemsByRegion(selectedRegion);

        // Create a list to hold the clickable rectangles (tiles)
        List<StackPane> tiles = new ArrayList<>();

        // Iterate through rental systems and create UI elements
        for (RentalSystem rentalSystem : rentalSystems) {
            // Create a custom rectangle
            rectangle rectangle = new rectangle(200, 100, Color.BLUE, 2, 20);

            // Add labels inside the rectangle
            VBox rectangleContent = new VBox();
            rectangleContent.setAlignment(Pos.CENTER);
            rectangleContent.setSpacing(5); // Optional spacing between labels
            Label label1 = new Label("Name:");
            Label label2 = new Label(rentalSystem.getName());
            rectangleContent.getChildren().addAll(label1, label2);

            // Make the rectangle clickable and navigate to RentCar page
            StackPane clickableRectangle = new StackPane(rectangle, rectangleContent);
            clickableRectangle.setOnMouseClicked(e -> MainApp.showRentCar(rentalSystem.getId(), client));

            // Add the clickable rectangle to the list
            tiles.add(clickableRectangle);
        }

        return tiles;
    }

    // Method to get RentalSystems by region (you can implement this based on your data source)
    private List<RentalSystem> getRentalSystemsByRegion(String region) {
        // Assuming that the `RentalSystemDAO` can handle fetching rental systems by region
        if (region == null || region.isEmpty()) {
            return RentalSystemDAO.getRentalSystemsByRegion(null); // No filter if no region is selected
        }

        return RentalSystemDAO.getRentalSystemsByRegion(region); // Fetch by selected region
    }

    public BorderPane getLayout() {
        return layout;
    }
}
