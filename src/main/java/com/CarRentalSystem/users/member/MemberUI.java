package com.CarRentalSystem.users.member;

import com.CarRentalSystem.database.ReservationDAO;
import com.CarRentalSystem.database.VehicleDAO;
import com.CarRentalSystem.reservation.VehicleReservation;
import com.CarRentalSystem.shapes.rectangle;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.util.List;
import java.util.Map;

public class MemberUI {

    private final BorderPane layout;
    private final TilePane reservationsPane;
    private final Member member;

    public MemberUI(Stage primaryStage, Member member) {
        layout = new BorderPane();
        this.member = member;

        // Left MenuBar
        MemberMenuBar menuBar = new MemberMenuBar(member, "Home");
        layout.setLeft(menuBar.getVBox());

        // Center layout for Orders section and Reservations
        VBox centerContent = new VBox(20); // Spacing between elements
        centerContent.setPadding(new Insets(20, 10, 10, 10));
        centerContent.setAlignment(Pos.TOP_CENTER);

        // "Choose Reservation" Label
        Label chooseReservationLabel = new Label("Choose Reservation:");
        chooseReservationLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // TilePane for reservations (inside a ScrollPane)
        reservationsPane = new TilePane();
        reservationsPane.setAlignment(Pos.CENTER);
        reservationsPane.setPadding(new Insets(10, 10, 0, 0));
        reservationsPane.setHgap(10);
        reservationsPane.setVgap(10);

        // ScrollPane wrapping the TilePane
        ScrollPane scrollPane = new ScrollPane(reservationsPane);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefViewportHeight(350); // Adjust height as needed

        // Initial population of reservations
        refreshReservations();

        centerContent.getChildren().addAll(chooseReservationLabel, scrollPane);
        layout.setCenter(centerContent);
    }

    private void refreshReservations() {
        // Clear the current content in the TilePane
        reservationsPane.getChildren().clear();

        // Fetch non-accepted reservations and display them
        List<VehicleReservation> nonAcceptedReservations = ReservationDAO.getNonAcceptedReservations();
        if (!nonAcceptedReservations.isEmpty()) {
            for (VehicleReservation reservation : nonAcceptedReservations) {
                StackPane reservationBox = createReservationBox(reservation);
                reservationsPane.getChildren().add(reservationBox);
            }
        } else {
            Label noReservationsLabel = new Label("No reservations available.");
            noReservationsLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #555;");
            reservationsPane.getChildren().add(noReservationsLabel);
        }
    }

    private StackPane createReservationBox(VehicleReservation reservation) {
        Rectangle reservationRect = new rectangle(300, 150, Color.LIGHTBLUE, 2, 20);

        Map<String, String> vehicle = VehicleDAO.getModelAndMakeByVehicleId(reservation.getVehicleId());
        String make = vehicle.getOrDefault("make", "Unknown");
        String model = vehicle.getOrDefault("model", "Unknown");

        String reservationDetails = String.format(
                "Reservation #%d\nStart Date: %s\nDue Date: %s\nMake: %s\nModel: %s",
                reservation.getReservationNumber(),
                reservation.getDueDate(),
                reservation.getReturnDate(),
                make,
                model
        );

        VBox contentBox = getvBox(reservation, reservationDetails);

        StackPane stack = new StackPane(reservationRect, contentBox);
        stack.setAlignment(Pos.CENTER);

        return stack;
    }

    private VBox getvBox(VehicleReservation reservation, String reservationDetails) {
        Label reservationLabel = new Label(reservationDetails);
        reservationLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #000;");

        // "Accept" Button
        Button acceptButton = new Button("Accept");
        acceptButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px;");
        acceptButton.setOnAction(event -> {
            System.out.println("Accepted");
            ReservationDAO.updateAdditionalDriver(member.getId(), reservation.getId());
            refreshReservations();
        });

        VBox contentBox = new VBox(10, reservationLabel, acceptButton);
        contentBox.setAlignment(Pos.CENTER);
        return contentBox;
    }

    public BorderPane getLayout() {
        return layout;
    }
}
