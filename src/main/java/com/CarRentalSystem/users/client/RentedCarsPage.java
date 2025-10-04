package com.CarRentalSystem.users.client;

import com.CarRentalSystem.database.ReservationDAO;
import com.CarRentalSystem.database.VehicleDAO;
import com.CarRentalSystem.reservation.VehicleReservation;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.List;
import java.util.Map;

public class RentedCarsPage {
    private final BorderPane layout;

    public RentedCarsPage(Stage primaryStage, Client client) {
        layout = new BorderPane();

        // Add a menu bar on the left
        ClientMenuBar menuBar = new ClientMenuBar(client, "Rented Cars");
        layout.setLeft(menuBar.getVBox());

        // Main container for reservations
        VBox reservationsBox = new VBox(15);
        reservationsBox.setAlignment(Pos.CENTER);
        reservationsBox.setPadding(new Insets(20));

        // Fetch reservations from the database
        List<VehicleReservation> reservations = ReservationDAO.getReservationsByClientId(client.getId());

        if (reservations.isEmpty()) {
            Label noReservationsLabel = new Label("No reservations found.");
            reservationsBox.getChildren().add(noReservationsLabel);
        } else {
            for (VehicleReservation reservation : reservations) {
                Map<String, String> vehicle = VehicleDAO.getModelAndMakeByVehicleId(reservation.getVehicleId());
                String make = vehicle.getOrDefault("make", "Unknown");
                String model = vehicle.getOrDefault("model", "Unknown");

                // Create a box for each reservation
                VBox reservationBox = createReservationBox(reservation, make, model);
                reservationsBox.getChildren().add(reservationBox);
            }
        }

        // Wrap the reservations in a ScrollPane
        ScrollPane scrollPane = new ScrollPane(reservationsBox);
        scrollPane.setFitToWidth(true);

        // Set ScrollPane to center of the layout
        layout.setCenter(scrollPane);
    }

    private VBox createReservationBox(VehicleReservation reservation, String make, String model) {
        VBox box = new VBox(10);
        box.setAlignment(Pos.CENTER_LEFT);
        box.setPadding(new Insets(15));
        box.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: #cccccc; -fx-border-radius: 10; -fx-background-radius: 10;");
        box.setMaxWidth(400);

        Label reservationNumberLabel = new Label("Reservation #" + reservation.getReservationNumber());
        Label statusLabel = new Label("Status: " + reservation.getStatus());
        Label dueDateLabel = new Label("Start Date: " + reservation.getDueDate());
        Label returnDateLabel = new Label("Due Date: " + reservation.getReturnDate());
        Label makeLabel = new Label("Make: " + make);
        Label modelLabel = new Label("Model: " + model);

        reservationNumberLabel.setStyle("-fx-font-size: 16; -fx-font-weight: bold;");
        statusLabel.setStyle("-fx-font-size: 14;");
        dueDateLabel.setStyle("-fx-font-size: 14;");
        returnDateLabel.setStyle("-fx-font-size: 14;");
        makeLabel.setStyle("-fx-font-size: 14;");
        modelLabel.setStyle("-fx-font-size: 14;");

        box.getChildren().addAll(
                reservationNumberLabel,
                statusLabel,
                dueDateLabel,
                returnDateLabel,
                makeLabel,
                modelLabel
        );

        return box;
    }

    public BorderPane getLayout() {
        return layout;
    }
}
