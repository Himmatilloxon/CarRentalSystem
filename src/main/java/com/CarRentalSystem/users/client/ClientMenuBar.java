package com.CarRentalSystem.users.client;

import com.CarRentalSystem.main.MainApp;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.SQLException;

public class ClientMenuBar {

    private final VBox vbox; // VBox for the menu
    private final String currentPage; // Tracks the current page
    private final Client client; // Client instance

    public ClientMenuBar(Client client, String currentPage) {
        this.client = client;
        this.currentPage = currentPage; // Default page
        this.vbox = new VBox(15); // Initialize VBox
        this.vbox.setPadding(new Insets(20));
        this.vbox.setAlignment(Pos.TOP_CENTER);
        this.vbox.setBackground(new Background(new BackgroundFill(
                Color.LIGHTGREY, // Background color
                CornerRadii.EMPTY,
                Insets.EMPTY
        )));
        refreshMenu(); // Initial menu setup
    }

    private void refreshMenu() {
        vbox.getChildren().clear(); // Clear existing menu items

        // Add user image if available
        ImageView imageView = getUserImage();
        if (imageView != null) {
            vbox.getChildren().add(imageView);
        }

        // Add common components
        Label welcome = new Label("Welcome");
        Label nameLabel = new Label(client.getName());
        Label line = new Label("-------------");
        vbox.getChildren().addAll(welcome, nameLabel, line);

        // Page-specific buttons
        if (currentPage.equals("Home")) {
            vbox.getChildren().addAll(
                    createButton("Info", this::showInfoPage),
                    createButton("Rented Cars", this::showRentedCars),
                    createButton("Settings", this::showSettings),
                    createButton("Logout", MainApp::showLoginPage)
            );
        } else if (currentPage.equals("Info")) {
            vbox.getChildren().addAll(
                    createButton("Home", this::showHomePage),
                    createButton("Rented Cars", this::showRentedCars),
                    createButton("Settings", this::showSettings),
                    createButton("Logout", MainApp::showLoginPage)
            );
        } else if (currentPage.equals("Rented Cars")) {
            vbox.getChildren().addAll(
                    createButton("Home", this::showHomePage),
                    createButton("Info", this::showInfoPage),
                    createButton("Settings", this::showSettings),
                    createButton("Logout", MainApp::showLoginPage)
            );
        }
    }

    private ImageView getUserImage() {
        try {
            FileInputStream inputStream = new FileInputStream("src/main/resources/image/user.png");
            Image image = new Image(inputStream);
            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(100);
            imageView.setFitHeight(100);
            return imageView;
        } catch (FileNotFoundException e) {
            System.out.println("User image not found: " + e.getMessage());
            return null; // Return null if image is not found
        }
    }

    private Button createButton(String text, Runnable action) {
        Button button = new Button(text);
        button.setOnAction(e -> action.run());
        return button;
    }

    private void showInfoPage() {
        try {
            MainApp.showClientInfo(client); // Display Info page
            refreshMenu(); // Refresh menu to reflect changes
        } catch (SQLException e) {
            throw new RuntimeException("Error loading Info page", e);
        }
    }

    private void showHomePage() {
        MainApp.showClient(client); // Display Home page
        refreshMenu(); // Refresh menu to reflect changes
    }

    private void showRentedCars() {
        MainApp.showRentedCarsPage(client);
        refreshMenu();
    }

    private void showSettings() {
        System.out.println("Settings button clicked.");
        // Add settings functionality here
    }

    public VBox getVBox() {
        return vbox;
    }
}
