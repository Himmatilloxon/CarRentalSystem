package com.CarRentalSystem.users.client;

import com.CarRentalSystem.components.BackButtonBox;
import com.CarRentalSystem.database.PaymentDAO;
import com.CarRentalSystem.database.ReservationDAO;
import com.CarRentalSystem.database.VehicleDAO;
import com.CarRentalSystem.main.MainApp;
import com.CarRentalSystem.reservation.*;
import com.CarRentalSystem.vehicle.Vehicle;
import javafx.beans.binding.Bindings;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class AdditionalMaterials {

    private static AdditionalMaterials instance; // Singleton instance
    private final BorderPane layout;
    private final Vehicle vehicle;
    private final VehicleReservation reservation;
    private final Bill bill = new Bill();

    private final CheckBox navigationCheckBox = new CheckBox("Navigation");
    private final CheckBox childSeatCheckBox = new CheckBox("Child Seat");
    private final Spinner<Integer> childSeatSpinner = new Spinner<>();
    private final CheckBox skiRackCheckBox = new CheckBox("Ski Rack");
    private final CheckBox driverCheckBox = new CheckBox("Driver");
    private final CheckBox roadAssistantCheckBox = new CheckBox("Road Assistant");
    private final CheckBox WiFiCheckBox = new CheckBox("WIFI");

    public AdditionalMaterials(Stage primaryStage, Vehicle vehicle, Client client) {
        layout = new BorderPane();
        this.vehicle = vehicle;
        this.reservation = new VehicleReservation();

        VBox mainContent = new VBox(20);
        mainContent.setAlignment(Pos.CENTER);

        // Equipment Section
        VBox equipmentSection = new VBox(10);
        equipmentSection.setAlignment(Pos.CENTER);
        equipmentSection.getChildren().add(new Label("Equipment:"));

        // Child Seat
        CheckBox childSeatCheckBox = new CheckBox("Child Seat");
        Spinner<Integer> childSeatSpinner = new Spinner<>(1, 1, 1);
        bindSpinnerToCheckBox(childSeatCheckBox, childSeatSpinner, vehicle.getPassengerCapacity());

        HBox childSeatSection = new HBox(10);
        childSeatSection.setAlignment(Pos.CENTER);
        childSeatSection.getChildren().addAll(childSeatCheckBox, childSeatSpinner);

        equipmentSection.getChildren().addAll(navigationCheckBox, childSeatSection, skiRackCheckBox);

        // Service Section
        VBox serviceSection = new VBox(10);
        serviceSection.setAlignment(Pos.CENTER);
        serviceSection.getChildren().add(new Label("Service:"));

        serviceSection.getChildren().addAll(driverCheckBox, roadAssistantCheckBox, WiFiCheckBox);

        VBox dateSection = new VBox(10);
        dateSection.setAlignment(Pos.CENTER);

        // Labels and DatePickers
        Label dueDateLabel = new Label("Start Date:");
        DatePicker dueDatePicker = new DatePicker();
        Label returnDateLabel = new Label("Due Date:");
        DatePicker returnDatePicker = new DatePicker();

        // Feedback label for errors
        Label feedbackLabel = new Label();
        feedbackLabel.setStyle("-fx-text-fill: red;"); // Red text for error messages

        // Add listener to returnDatePicker
        returnDatePicker.valueProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue != null && dueDatePicker.getValue() != null) {
                if (newValue.isBefore(dueDatePicker.getValue())) {
                    // Display error and reset the return date
                    feedbackLabel.setText("Return date cannot be earlier than due date.");
                    returnDatePicker.setValue(null); // Reset the return date
                } else {
                    feedbackLabel.setText(""); // Clear feedback on valid input
                }
            }
        });

        // Add listener to dueDatePicker
        dueDatePicker.valueProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue != null && returnDatePicker.getValue() != null) {
                if (returnDatePicker.getValue().isBefore(newValue)) {
                    // Display error and reset the return date
                    feedbackLabel.setText("Return date cannot be earlier than due date.");
                    returnDatePicker.setValue(null); // Reset the return date
                } else {
                    feedbackLabel.setText(""); // Clear feedback on valid input
                }
            }
        });

        dateSection.getChildren().addAll(dueDateLabel, dueDatePicker, returnDateLabel, returnDatePicker, feedbackLabel);

        Button continueButton = new Button("Continue");
        continueButton.disableProperty().bind(
                Bindings.createBooleanBinding(
                        () -> {
                            LocalDate dueDate = dueDatePicker.getValue();
                            LocalDate returnDate = returnDatePicker.getValue();
                            return (dueDate == null || returnDate == null || returnDate.isBefore(dueDate));
                        },
                        dueDatePicker.valueProperty(),
                        returnDatePicker.valueProperty()
                )
        );
        continueButton.setOnAction(e -> {
            // Store inputs in VehicleReservation
            storeReservation(dueDatePicker.getValue(), returnDatePicker.getValue());
            MainApp.showBillPage(bill, client); // Navigate to the Bill Page
        });

        mainContent.getChildren().addAll(equipmentSection, serviceSection, dateSection, continueButton);

        BackButtonBox backButtonBox = new BackButtonBox();

        layout.setCenter(mainContent);
        layout.setTop(backButtonBox.getBackButtonBox());
    }

    private void bindSpinnerToCheckBox(CheckBox checkBox, Spinner<Integer> spinner, int passengerCapacity) {
        spinner.setDisable(true); // Initially disable the spinner

        // Enable/disable spinner based on checkbox selection
        checkBox.selectedProperty().addListener((obs, oldVal, newVal) -> {
            spinner.setDisable(!newVal); // Enable spinner if checkbox is checked
            if (newVal) {
                // Set the spinner's value factory when the checkbox is checked
                spinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, passengerCapacity - 1));
            } else {
                // Reset spinner to 1 when checkbox is unchecked
                spinner.getValueFactory().setValue(1);
            }
        });

        if (checkBox.isSelected()) {
            spinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, passengerCapacity - 1));
        }
    }

    private void storeReservation(LocalDate dueDate, LocalDate returnDate) {

        reservation.setVehicleId(vehicle.getId());
        reservation.setDueDate(java.sql.Date.valueOf(dueDate));
        reservation.setReturnDate(java.sql.Date.valueOf(returnDate));
        reservation.setStatus(ReservationStatus.Completed);

        long days = ChronoUnit.DAYS.between(dueDate, returnDate);
        if (days == 0) days = 1;

        reservation.setId(ReservationDAO.addReservation(reservation));

        bill.setReservationID(reservation.getId());
        bill.setVehicleBill(VehicleDAO.getVehiclePrice(vehicle.getId()) * days);

        if (navigationCheckBox.isSelected()){
            ReservationDAO.addEquipment(reservation.getId(), EquipmentType.Navigation);
            bill.addEquipmentBill(4.5);
        }
        if (childSeatCheckBox.isSelected()){
            ReservationDAO.addEquipment(reservation.getId(), EquipmentType.ChildSeat);
            bill.addEquipmentBill(2.5 * childSeatSpinner.getValue());
        }
        if (skiRackCheckBox.isSelected()) {
            ReservationDAO.addEquipment(reservation.getId(), EquipmentType.SkiRack);
            bill.addEquipmentBill(20);
        }
        if (WiFiCheckBox.isSelected()) {
            ReservationDAO.addService(reservation.getId(), ServiceType.WIFI);
            bill.addServiceBill(4.5);
        }
        if (driverCheckBox.isSelected()) {
            ReservationDAO.addService(reservation.getId(), ServiceType.Driver);
            bill.addServiceBill(50);
        }
        if (roadAssistantCheckBox.isSelected()) {
            ReservationDAO.addService(reservation.getId(), ServiceType.RoadsideAssistance);
            bill.addServiceBill(25);
        }

        bill.setTotalBill(bill.getVehicleBill() + bill.getServiceBill() + bill.getEquipmentBill());
        bill.setId(PaymentDAO.insertBill(bill));

        // Log reservation details for debugging
        System.out.println("Reservation Details:");
        System.out.println("Vehicle ID: " + reservation.getVehicleId());
        System.out.println("Reservation Number: " + reservation.getReservationNumber());
        System.out.println("Due Date: " + reservation.getDueDate());
        System.out.println("Return Date: " + reservation.getReturnDate());
        System.out.println("Status: " + reservation.getStatus());
    }

    public static AdditionalMaterials getInstance(Stage primaryStage, Vehicle vehicle, Client client) {
        if (instance == null) {
            instance = new AdditionalMaterials(primaryStage, vehicle, client);
        }
        return instance;
    }

    public BorderPane getLayout() {
        return layout;
    }
}
