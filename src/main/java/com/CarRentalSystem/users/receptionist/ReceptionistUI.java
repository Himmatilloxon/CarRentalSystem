package com.CarRentalSystem.users.receptionist;

import com.CarRentalSystem.components.MenuBar;
import com.CarRentalSystem.users.UserType;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ReceptionistUI {

    private final BorderPane layout;

    public ReceptionistUI(Stage primaryStage, Receptionist receptionist) {
        layout = new BorderPane();

        // Add the MenuBar with a specific UserType (e.g., ClientUI)
        ReceptionistMenuBar menuBar = new ReceptionistMenuBar(receptionist, "Home");
        VBox menuBarLayout = menuBar.getVBox();
        layout.setLeft(menuBarLayout);  // Place MenuBar on the left side

        // Welcome label or other client-specific content
        Label welcome = new Label("Welcome, user");
        layout.setCenter(welcome);
    }

    public BorderPane getLayout() {
        return layout;
    }
}
