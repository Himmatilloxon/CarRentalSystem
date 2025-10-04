package com.CarRentalSystem.users.admin;

import com.CarRentalSystem.components.MenuBar;
import com.CarRentalSystem.users.UserType;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class AdminUI {
    private final BorderPane layout;

    public AdminUI(Stage primaryStage, Admin admin) {
        layout = new BorderPane();

        // Left menu bar and top welcome label
        AdminMenuBar menuBar = new AdminMenuBar(admin, "Home");
        layout.setLeft(menuBar.getVBox());

    }

    public BorderPane getLayout() {
        return layout;
    }
}
