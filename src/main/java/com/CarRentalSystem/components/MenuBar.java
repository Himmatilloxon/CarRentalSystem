package com.CarRentalSystem.components;

import com.CarRentalSystem.main.MainApp;
import com.CarRentalSystem.users.UserType;
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

public class MenuBar {

    private final VBox vbox;

    public MenuBar(UserType userType, String name) {
        vbox = new VBox(15);
        vbox.setPadding(new Insets(20));
        vbox.setAlignment(Pos.TOP_CENTER);
        vbox.setBackground(new Background(new BackgroundFill(
                Color.GREY, // Set your desired color here
                CornerRadii.EMPTY,
                Insets.EMPTY
        )));

        // Load user image with exception handling
        ImageView imageView = null;
        try {
            FileInputStream inputStream = new FileInputStream("src/main/resources/image/user.png");
            Image image = new Image(inputStream);
            imageView = new ImageView(image);
            imageView.setFitWidth(100);
            imageView.setFitHeight(100);
        } catch (FileNotFoundException e) {
            System.out.println("User image not found: " + e.getMessage());
            // Optionally set a default image or handle differently
        }

        Label welcome = new Label("Welcome");
        Label nameLabel = new Label(name);
        Label line = new Label("-------------");

        Button infoButton = new Button("Info");

        Button settingsButton = new Button("Settings");
        Button logoutButton = new Button("Logout");
        logoutButton.setOnAction(e -> MainApp.showLoginPage());

        // Define the user-specific button and set its label based on user type
        Button userSpecificButton = new Button();
        switch (userType) {
            case Admin -> userSpecificButton.setText("Receptionists");
            case Client -> userSpecificButton.setText("Rented Cars");
            case Member -> userSpecificButton.setText("Orders");
            case Receptionist -> userSpecificButton.setText("Notifications");
        }

        // Add all components to VBox, including image if successfully loaded
        if (imageView != null) {
            vbox.getChildren().add(imageView);
        }
        vbox.getChildren().addAll(welcome, nameLabel, line, infoButton, userSpecificButton, settingsButton, logoutButton);
    }

    public VBox getVBox() {
        return vbox;
    }
}
