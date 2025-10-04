package com.CarRentalSystem.users.receptionist;

import com.CarRentalSystem.database.AccountDAO;
import com.CarRentalSystem.database.ReservationDAO;
import com.CarRentalSystem.database.VehicleDAO;
import com.CarRentalSystem.reservation.VehicleReservation;
import com.CarRentalSystem.users.client.Client;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.util.List;
import java.util.Map;

public class Reservations {

    private BorderPane layout;
    private final Receptionist receptionist;

    public Reservations(Stage primaryStage, Receptionist receptionist) {
        layout = new BorderPane();
        this.receptionist = receptionist;

        // Menu bar on the left
        ReceptionistMenuBar menuBar = new ReceptionistMenuBar(receptionist, "Reservations");
        layout.setLeft(menuBar.getVBox());

        // Center content
        VBox centerContent = new VBox(20);
        centerContent.setPadding(new Insets(20));
        centerContent.setAlignment(Pos.TOP_CENTER);

        Label titleLabel = new Label("Client Reservations");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // TilePane for clients
        TilePane clientsPane = new TilePane();
        clientsPane.setAlignment(Pos.CENTER);
        clientsPane.setPadding(new Insets(10));
        clientsPane.setHgap(10);
        clientsPane.setVgap(10);

        // ScrollPane wrapping the TilePane
        ScrollPane scrollPane = new ScrollPane(clientsPane);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefViewportHeight(400);

        // Fetch clients and display details
        List<Client> clients = AccountDAO.getClients();
        if (!clients.isEmpty()) {
            for (Client client : clients) {
                StackPane clientBox = createClientBox(client);
                clientsPane.getChildren().add(clientBox);
            }
        } else {
            Label noClientsLabel = new Label("No clients found.");
            clientsPane.getChildren().add(noClientsLabel);
        }

        centerContent.getChildren().addAll(titleLabel, scrollPane);
        layout.setCenter(centerContent);
    }

    private StackPane createClientBox(Client client) {
        // Rectangle for styling
        Rectangle clientRect = new Rectangle(300, 150);
        clientRect.setFill(Color.LIGHTBLUE);
        clientRect.setArcWidth(20);
        clientRect.setArcHeight(20);
        clientRect.setStroke(Color.DARKBLUE);
        clientRect.setStrokeWidth(2);

        // Client details
        String clientDetails = String.format(
                "Name: %s %s\nEmail: %s\nPhone: %s",
                client.getName(),
                client.getSurname(),
                client.getEmail(),
                client.getPhone()
        );

        Label clientLabel = new Label(clientDetails);
        clientLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #000;");

        // Click event for the rectangle
        StackPane stack = new StackPane(clientRect, clientLabel);
        stack.setAlignment(Pos.CENTER);
        stack.setOnMouseClicked(event -> {
            List<VehicleReservation> reservations = ReservationDAO.getReservationsByClientId(client.getId());
            if (!reservations.isEmpty()) {
                showReservationsDialog(client, reservations);
            } else {
                showNoReservationsDialog(client);
            }
        });

        stack.setPadding(new Insets(10));
        return stack;
    }

    private void showReservationsDialog(Client client, List<VehicleReservation> reservations) {
        Stage dialog = new Stage();
        dialog.setTitle("Reservations for " + client.getName() + " " + client.getSurname());

        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        content.setAlignment(Pos.TOP_CENTER);

        for (VehicleReservation reservation : reservations) {
            Map<String, String> vehicle = VehicleDAO.getModelAndMakeByVehicleId(reservation.getVehicleId());
            String make = vehicle.getOrDefault("make", "Unknown");
            String model = vehicle.getOrDefault("model", "Unknown");

            VBox reservationBox = createReservationBox(reservation, make, model);
            content.getChildren().add(reservationBox);
        }

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);

        VBox dialogLayout = new VBox(scrollPane);
        dialogLayout.setPadding(new Insets(10));

        Scene dialogScene = new Scene(dialogLayout, 500, 400);
        dialog.setScene(dialogScene);
        dialog.show();
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

    private void showNoReservationsDialog(Client client) {
        Stage dialog = new Stage();
        dialog.setTitle("No Reservations");

        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        content.setAlignment(Pos.CENTER);

        Label message = new Label("No reservations found for " + client.getName() + " " + client.getSurname() + ".");
        message.setStyle("-fx-font-size: 14px; -fx-text-fill: #333;");
        content.getChildren().add(message);

        Scene dialogScene = new Scene(content, 300, 200);
        dialog.setScene(dialogScene);
        dialog.show();
    }

    public BorderPane getLayout() {
        return layout;
    }
}
