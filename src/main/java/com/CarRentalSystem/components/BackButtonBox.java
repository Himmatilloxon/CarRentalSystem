package com.CarRentalSystem.components;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.geometry.Insets;

public class BackButtonBox {

    private final HBox backButtonBox;

    public BackButtonBox() {
        // Create the back button and its layout
        Button backButton = new Button("Back");
        backButton.setOnAction(e -> com.CarRentalSystem.main.MainApp.goBack()); // Ensure correct import

        backButtonBox = new HBox(backButton);
        backButtonBox.setAlignment(Pos.TOP_LEFT);
        backButtonBox.setPadding(new Insets(10));
    }

    // Method to get the HBox layout containing the back button
    public HBox getBackButtonBox() {
        return backButtonBox;
    }
}
