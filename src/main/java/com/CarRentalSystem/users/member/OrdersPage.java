package com.CarRentalSystem.users.member;

import com.CarRentalSystem.database.ReservationDAO;
import com.CarRentalSystem.database.VehicleDAO;
import com.CarRentalSystem.reservation.VehicleReservation;
import com.CarRentalSystem.shapes.rectangle;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.util.List;
import java.util.Map;

public class OrdersPage {

    private final BorderPane layout;
    private final Member member;

    public OrdersPage(Stage primaryStage, Member member) {
        this.layout = new BorderPane();
        this.member = member;

        // Menu Bar on the left
        MemberMenuBar menuBar = new MemberMenuBar(member, "Orders");
        layout.setLeft(menuBar.getVBox());

        // Center Content - Reservations
        VBox centerContent = new VBox(20); // Spacing between elements
        centerContent.setPadding(new Insets(20, 10, 10, 10));
        centerContent.setAlignment(Pos.TOP_CENTER);

        Label pageTitle = new Label("Your Orders");
        pageTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // TilePane for reservations (inside a ScrollPane)
        TilePane ordersPane = new TilePane();
        ordersPane.setAlignment(Pos.CENTER);
        ordersPane.setPadding(new Insets(10));
        ordersPane.setHgap(10);
        ordersPane.setVgap(10);

        // ScrollPane wrapping the TilePane
        ScrollPane scrollPane = new ScrollPane(ordersPane);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefViewportHeight(400); // Adjust as needed

        // Fetch reservations for the member
        List<VehicleReservation> orders = ReservationDAO.getOrders(member.getId());
        if (!orders.isEmpty()) {
            for (VehicleReservation reservation : orders) {
                StackPane orderBox = createOrderBox(reservation);
                ordersPane.getChildren().add(orderBox);
            }
        } else {
            Label noOrdersLabel = new Label("No orders found.");
            noOrdersLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #666;");
            ordersPane.getChildren().add(noOrdersLabel);
        }

        centerContent.getChildren().addAll(pageTitle, scrollPane);
        layout.setCenter(centerContent);
    }

    private StackPane createOrderBox(VehicleReservation reservation) {
        Rectangle orderRect = new rectangle(300, 150, Color.LIGHTBLUE, 2, 20);

        // Fetch vehicle details
        Map<String, String> vehicle = VehicleDAO.getModelAndMakeByVehicleId(reservation.getVehicleId());
        String make = vehicle.getOrDefault("make", "Unknown");
        String model = vehicle.getOrDefault("model", "Unknown");

        // Create reservation details string
        String reservationDetails = String.format(
                "Reservation #%d\nStart Date: %s\nDue Date: %s\nMake: %s\nModel: %s",
                reservation.getReservationNumber(),
                reservation.getDueDate(),
                reservation.getReturnDate(),
                make,
                model
        );

        Label reservationLabel = new Label(reservationDetails);
        reservationLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #000;");

        // Create a VBox for the reservation details and status label
        VBox contentBox = new VBox(10, reservationLabel);
        contentBox.setAlignment(Pos.CENTER);

        // StackPane with rectangle and details
        StackPane stack = new StackPane(orderRect, contentBox);
        stack.setAlignment(Pos.CENTER);
        stack.setPadding(new Insets(10));
        return stack;
    }

    public BorderPane getLayout() {
        return layout;
    }
}
