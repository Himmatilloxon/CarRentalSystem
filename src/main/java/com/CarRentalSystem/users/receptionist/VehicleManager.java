package com.CarRentalSystem.users.receptionist;

import com.CarRentalSystem.RentalSystem;
import com.CarRentalSystem.database.RentalSystemDAO;
import com.CarRentalSystem.database.VehicleDAO;
import com.CarRentalSystem.main.MainApp;
import com.CarRentalSystem.shapes.rectangle;
import com.CarRentalSystem.vehicle.Barcode;
import com.CarRentalSystem.vehicle.ParkingStall;
import com.CarRentalSystem.vehicle.Vehicle;
import com.CarRentalSystem.vehicle.VehicleStatus;
import com.CarRentalSystem.vehicle.car.Car;
import com.CarRentalSystem.vehicle.car.CarType;
import com.CarRentalSystem.vehicle.motorcycle.Motorcycle;
import com.CarRentalSystem.vehicle.motorcycle.MotorcycleType;
import com.CarRentalSystem.vehicle.suv.SUV;
import com.CarRentalSystem.vehicle.suv.SUVType;
import com.CarRentalSystem.vehicle.truck.Truck;
import com.CarRentalSystem.vehicle.truck.TruckType;
import com.CarRentalSystem.vehicle.van.Van;
import com.CarRentalSystem.vehicle.van.VanType;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class VehicleManager {

    private final BorderPane layout;
    private final Receptionist receptionist;

    private final TextField licenseNumberField = new TextField();
    private final TextField passengerCapacityField = new TextField();
    private final CheckBox sunroofCheckBox = new CheckBox("Has Sunroof");
    private final TextField makeField = new TextField();
    private final TextField modelField = new TextField();
    private final TextField manufactureYearField = new TextField();
    private final TextField mileageField = new TextField();
    private final ComboBox<VehicleStatus> statusComboBox = new ComboBox<>();
    private final ComboBox<String> vehicleTypeComboBox = new ComboBox<>();
    private final ComboBox<CarType> carTypeComboBox = new ComboBox<>();
    private final ComboBox<TruckType> truckTypeComboBox = new ComboBox<>();
    private final ComboBox<SUVType> suvTypeComboBox = new ComboBox<>();
    private final ComboBox<VanType> vanTypeComboBox = new ComboBox<>();
    private final ComboBox<MotorcycleType> motorcycleTypeComboBox = new ComboBox<>();
    private final TextField barcodeField = new TextField();
    private final Button chooseRentalSystem = new Button("Choose Rental System");
    private final Button chooseParkingStall = new Button("Choose Parking Stall");
    private final TextField priceField = new TextField("Price");
    private UUID rental_id;
    private int parking_id;

    public VehicleManager(Stage primaryStage, Receptionist receptionist) {
        this.receptionist = receptionist;
        layout = new BorderPane();

        ReceptionistMenuBar menuBar = new ReceptionistMenuBar(receptionist, "Vehicle Manager");
        VBox menuBarLayout = menuBar.getVBox();
        layout.setLeft(menuBarLayout);

        // Common Fields
        licenseNumberField.setPromptText("License Number");
        makeField.setPromptText("Make");
        modelField.setPromptText("Model");
        manufactureYearField.setPromptText("Manufacture Year");
        mileageField.setPromptText("Mileage");
        passengerCapacityField.setPromptText("Passenger Capacity");
        // Validation for Manufacture Year
        manufactureYearField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.matches("\\d{4}") && Integer.parseInt(newValue) <= java.time.Year.now().getValue()) {
                manufactureYearField.setStyle("-fx-background-color: white;"); // Valid input
//                feedbackText.setText(""); // Clear feedback
            } else {
                manufactureYearField.setStyle("-fx-background-color: lightcoral;"); // Invalid input
//                feedbackText.setText("Enter a valid manufacture year (e.g., 2020).");
//                feedbackText.setFill(Color.RED);
            }
        });

        mileageField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.matches("\\d+") && Integer.parseInt(newValue) >= 0) {
                mileageField.setStyle("-fx-background-color: white;"); // Valid input
//                feedbackText.setText(""); // Clear feedback
            } else {
                mileageField.setStyle("-fx-background-color: lightcoral;"); // Invalid input
//                feedbackText.setText("Mileage must be a positive number.");
//                feedbackText.setFill(Color.RED);
            }
        });

        passengerCapacityField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.matches("\\d+") && Integer.parseInt(newValue) > 0) {
                passengerCapacityField.setStyle("-fx-background-color: white;"); // Valid input
//                feedbackText.setText(""); // Clear feedback
            } else {
                passengerCapacityField.setStyle("-fx-background-color: lightcoral;"); // Invalid input
//                feedbackText.setText("Passenger capacity must be greater than 0.");
//                feedbackText.setFill(Color.RED);
            }
        });

        priceField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.matches("\\d*(\\.\\d*)?") && !newValue.isEmpty() && Float.parseFloat(newValue) > 0) {
                priceField.setStyle("-fx-background-color: white;"); // Valid input
//                feedbackText.setText(""); // Clear feedback
            } else {
                priceField.setStyle("-fx-background-color: lightcoral;"); // Invalid input
//                feedbackText.setText("Price must be greater than 0.");
//                feedbackText.setFill(Color.RED);
            }
        });

        statusComboBox.getItems().setAll(VehicleStatus.values());
        statusComboBox.setPromptText("Status");

        vehicleTypeComboBox.getItems().addAll("Car", "Truck", "SUV", "Van", "Motorcycle");

        carTypeComboBox.getItems().setAll(CarType.values());
        carTypeComboBox.setPromptText("Car Type");
        carTypeComboBox.setVisible(false);

        truckTypeComboBox.getItems().setAll(TruckType.values());
        truckTypeComboBox.setPromptText("Truck Type");
        truckTypeComboBox.setVisible(false);

        suvTypeComboBox.getItems().setAll(SUVType.values());
        suvTypeComboBox.setPromptText("SUV Type");
        suvTypeComboBox.setVisible(false);

        vanTypeComboBox.getItems().setAll(VanType.values());
        vanTypeComboBox.setPromptText("Van Type");
        vanTypeComboBox.setVisible(false);

        motorcycleTypeComboBox.getItems().setAll(MotorcycleType.values());
        motorcycleTypeComboBox.setPromptText("Motorcycle Type");
        motorcycleTypeComboBox.setVisible(false);

        // Create the main VBox to hold the dynamic ComboBox
        VBox carType = new VBox();
        carType.setSpacing(10);
        carType.setAlignment(Pos.CENTER);

        double comboBoxWidth = 150;

        carTypeComboBox.setMaxWidth(comboBoxWidth);
        truckTypeComboBox.setMaxWidth(comboBoxWidth);
        suvTypeComboBox.setMaxWidth(comboBoxWidth);
        vanTypeComboBox.setMaxWidth(comboBoxWidth);
        motorcycleTypeComboBox.setMaxWidth(comboBoxWidth);

        vehicleTypeComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            // Clear the current selection and remove all children
            carType.getChildren().clear();
            carTypeComboBox.setVisible(false);
            truckTypeComboBox.setVisible(false);
            suvTypeComboBox.setVisible(false);
            vanTypeComboBox.setVisible(false);
            motorcycleTypeComboBox.setVisible(false);

            // Show the appropriate ComboBox based on the selected vehicle type
            if (newValue != null) {
                switch (newValue) {
                    case "Car":
                        carTypeComboBox.setVisible(true);
                        carType.getChildren().add(carTypeComboBox);
                        break;
                    case "Truck":
                        truckTypeComboBox.setVisible(true);
                        carType.getChildren().add(truckTypeComboBox);
                        break;
                    case "SUV":
                        suvTypeComboBox.setVisible(true);
                        carType.getChildren().add(suvTypeComboBox);
                        break;
                    case "Van":
                        vanTypeComboBox.setVisible(true);
                        carType.getChildren().add(vanTypeComboBox);
                        break;
                    case "Motorcycle":
                        motorcycleTypeComboBox.setVisible(true);
                        carType.getChildren().add(motorcycleTypeComboBox);
                        break;
                    default:
                        // No action for unsupported vehicle types
                        break;
                }
            }
        });

        barcodeField.setPromptText("Barcode");

        chooseRentalSystem.setOnAction(e -> this.rental_id = (openChooseRentalSystem()));
        chooseParkingStall.setOnAction(e -> this.parking_id = openChooseParkingStall(this.rental_id, vehicleTypeComboBox.getValue()));

        Button saveButton = new Button("Save");
        saveButton.disableProperty().bind(
                Bindings.createBooleanBinding(() ->
                                makeField.getText().trim().isEmpty()
                                        || modelField.getText().trim().isEmpty()
                                        || mileageField.getText().trim().isEmpty()
                                        || passengerCapacityField.getText().trim().isEmpty()
                                        || licenseNumberField.getText().trim().isEmpty()
                                        || manufactureYearField.getText().trim().isEmpty()
                                        || statusComboBox.getSelectionModel().getSelectedItem() == null
                                        || vehicleTypeComboBox.getSelectionModel().getSelectedItem() == null
                                        || (vehicleTypeComboBox.getValue().equals("Car") && carTypeComboBox.getSelectionModel().getSelectedItem() == null)
                                        || (vehicleTypeComboBox.getValue().equals("Truck") && truckTypeComboBox.getSelectionModel().getSelectedItem() == null)
                                        || (vehicleTypeComboBox.getValue().equals("SUV") && suvTypeComboBox.getSelectionModel().getSelectedItem() == null)
                                        || (vehicleTypeComboBox.getValue().equals("Van") && vanTypeComboBox.getSelectionModel().getSelectedItem() == null)
                                        || (vehicleTypeComboBox.getValue().equals("Motorcycle") && motorcycleTypeComboBox.getSelectionModel().getSelectedItem() == null)
                                        || priceField.getText().trim().isEmpty(),
                        makeField.textProperty(),
                        modelField.textProperty(),
                        mileageField.textProperty(),
                        passengerCapacityField.textProperty(),
                        licenseNumberField.textProperty(),
                        manufactureYearField.textProperty(),
                        statusComboBox.getSelectionModel().selectedItemProperty(),
                        vehicleTypeComboBox.getSelectionModel().selectedItemProperty(),
                        carTypeComboBox.getSelectionModel().selectedItemProperty(),
                        truckTypeComboBox.getSelectionModel().selectedItemProperty(),
                        suvTypeComboBox.getSelectionModel().selectedItemProperty(),
                        vanTypeComboBox.getSelectionModel().selectedItemProperty(),
                        motorcycleTypeComboBox.getSelectionModel().selectedItemProperty(),
                        priceField.textProperty()
                )
        );
        saveButton.setOnAction(e -> saveVehicle());

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setVgap(10);
        grid.setHgap(10);
        grid.setPadding(new Insets(20));

        grid.add(new Label("Make: "), 0, 0);
        grid.add(makeField, 1, 0);

        grid.add(new Label("Model: "), 0, 1);
        grid.add(modelField, 1, 1);

        grid.add(new Label("Manufacture Year: "), 0, 2);
        grid.add(manufactureYearField, 1, 2);

        grid.add(new Label("License Number: "), 0, 3);
        grid.add(licenseNumberField, 1, 3);

        grid.add(new Label("Mileage: "), 0, 4);
        grid.add(mileageField, 1, 4);

        grid.add(new Label("Passenger Capacity: "), 0, 5);
        grid.add(passengerCapacityField, 1, 5);

        grid.add(new Label("Sunroof: "), 0, 6);
        grid.add(sunroofCheckBox, 1, 6);

        grid.add(new Label("Status: "), 0, 7);
        grid.add(statusComboBox, 1, 7);

        grid.add(new Label("Vehicle Type: "), 0, 8);
        grid.add(vehicleTypeComboBox, 1, 8);
        grid.add(carType, 2, 8);

        grid.add(new Label("Barcode: "), 0, 9);
        grid.add(barcodeField, 1, 9);

        grid.add(new Label("Choose Rental System: "), 0, 10);
        grid.add(chooseRentalSystem, 1, 10);

        grid.add(new Label("Parking Stall: "), 0, 11);
        grid.add(chooseParkingStall, 1, 11);

        grid.add(new Label("Price: "), 0, 12);
        grid.add(priceField, 1, 12);

        grid.add(saveButton, 1, 13);

        layout.setCenter(grid);
    }

    public void saveVehicle() {
        Vehicle vehicle = getVehicle();
        vehicle.setId(VehicleDAO.insertVehicle(vehicle));

        Barcode barcode = new Barcode();
        barcode.setId(vehicle.getId());
        barcode.setBarcode(barcodeField.getText());
        barcode.setActive(true);
        VehicleDAO.insertBarcode(barcode);

        switch (vehicleTypeComboBox.getValue()) {
            case "Car":
                Car car = new Car();
                car.setId(vehicle.getId());
                car.setCarType(carTypeComboBox.getValue());
                VehicleDAO.insertCar(car);
                break;
                case "Truck":
                    Truck truck = new Truck();
                    truck.setId(vehicle.getId());
                    truck.setType(truckTypeComboBox.getValue());
                    VehicleDAO.insertTruck(truck);
                    break;
                    case "SUV":
                        SUV suv = new SUV();
                        suv.setId(vehicle.getId());
                        suv.setType(suvTypeComboBox.getValue());
                        VehicleDAO.insertSUV(suv);
                        break;
                        case "Van":
                            Van van = new Van();
                            van.setId(vehicle.getId());
                            van.setVanType(vanTypeComboBox.getValue());
                            VehicleDAO.insertVan(van);
                            break;
                            case "Motorcycle":
                                Motorcycle motorcycle = new Motorcycle();
                                motorcycle.setId(vehicle.getId());
                                motorcycle.setMotorcycleType(motorcycleTypeComboBox.getValue());
                                VehicleDAO.insertMotorcycle(motorcycle);
                                break;
        }

        MainApp.showVehicleManager(receptionist);
    }

    private Vehicle getVehicle() {
        Vehicle vehicle = new Vehicle();
        vehicle.setMake(makeField.getText());
        vehicle.setModel(modelField.getText());
        vehicle.setLicenseNumber(licenseNumberField.getText());
        vehicle.setHasSunroof(sunroofCheckBox.isSelected());
        vehicle.setPassengerCapacity(Integer.parseInt(passengerCapacityField.getText()));
        vehicle.setStatus(statusComboBox.getSelectionModel().getSelectedItem());
        vehicle.setManufactureYear(Integer.parseInt(manufactureYearField.getText()));
        vehicle.setMileage(Integer.parseInt(mileageField.getText()));
        vehicle.setRentalId(rental_id);
        vehicle.setParkingStallId(parking_id);
        vehicle.setPrice(Float.parseFloat(priceField.getText()));
        return vehicle;
    }

    public UUID openChooseRentalSystem() {
        // Create a dialog window
        Stage dialog = new Stage();
        dialog.setTitle("Choose Rental System");

        // ComboBox for region selection
        ComboBox<String> regionComboBox = new ComboBox<>();
        regionComboBox.getItems().addAll(
                "Tashkent", "Andijan", "Bukhara", "Fergana", "Jizzakh",
                "Namangan", "Navoiy", "Qashqadaryo", "Samarqand",
                "Sirdaryo", "Surxondaryo", "Tashkent Region", "Xorazm",
                "Republic of Karakalpakstan"
        );
        regionComboBox.setPromptText("Region");

        // Label for region ComboBox
        Label regionLabel = new Label("Choose Region:");

        // Layout for region selection
        HBox regionBox = new HBox(10, regionLabel, regionComboBox);
        regionBox.setAlignment(Pos.CENTER);

        // VBox to organize content
        VBox centerContent = new VBox(regionBox);
        centerContent.setAlignment(Pos.TOP_CENTER);
        centerContent.setSpacing(20);
        centerContent.setPadding(new javafx.geometry.Insets(50, 0, 0, 0));

        // Label and TilePane for rental systems
        Label rentalLocationLabel = new Label("Choose Rental Location:");
        rentalLocationLabel.setVisible(false);

        TilePane rentalSystemsPane = new TilePane();
        rentalSystemsPane.setAlignment(Pos.CENTER);
        rentalSystemsPane.setHgap(15);
        rentalSystemsPane.setVgap(10);
        rentalSystemsPane.setVisible(false);

        // Variable to hold the selected rental system ID
        AtomicReference<UUID> selectedRentalSystemId = new AtomicReference<>(null);

        // Listener for region selection
        regionComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.isEmpty()) {
                // Fetch tiles for the selected region
                List<StackPane> tiles = createRentalSystemList(newValue, selectedRentalSystemId, dialog);

                // Update the TilePane
                rentalSystemsPane.getChildren().clear();
                rentalSystemsPane.getChildren().addAll(tiles);

                // Show rental systems
                rentalLocationLabel.setVisible(true);
                rentalSystemsPane.setVisible(true);
            } else {
                // Hide rental systems if no region is selected
                rentalSystemsPane.getChildren().clear();
                rentalSystemsPane.setVisible(false);
                rentalLocationLabel.setVisible(false);
            }
        });

        // Add components to the content
        centerContent.getChildren().addAll(rentalLocationLabel, rentalSystemsPane);

        // Set up the dialog scene
        StackPane stackPane = new StackPane(centerContent);
        Scene scene = new Scene(stackPane, 400, 600);
        dialog.setScene(scene);
        dialog.showAndWait();

        // Return the selected rental system ID
        return selectedRentalSystemId.get();
    }

    public int openChooseParkingStall(UUID rentalId, String vehicleType) {
        Stage dialog = new Stage();
        dialog.setTitle("Choose Parking Stall");

        // Label for the parking stall selection
        Label titleLabel = new Label("Choose a Parking Stall:");
        titleLabel.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");

        // Create a TilePane for displaying parking stalls
        TilePane parkingStallsPane = new TilePane();
        parkingStallsPane.setAlignment(Pos.CENTER);
        parkingStallsPane.setHgap(15);
        parkingStallsPane.setVgap(15);

        // Variable to store the selected parking stall ID
        AtomicInteger selectedParkingStallId = new AtomicInteger(-1);

        // Fetch and display parking stalls from the database
        List<ParkingStall> parkingStalls = RentalSystemDAO.getParkingStallsFromDB(rentalId);
        for (ParkingStall stall : parkingStalls) {
            // Filter parking stalls by vehicle type
            if (!stall.getStallNumber().startsWith(vehicleType.substring(0, 1))) {
                continue; // Skip stalls that don't match the vehicle type
            }

            // Create a rectangle for each parking stall
            rectangle rectangle = new rectangle(100, 60, Color.BLACK, 1, 10);

            // Set color and clickability based on availability
            if (stall.isAvailable()) {
                rectangle.setFill(Color.LIGHTGREEN);
                rectangle.setOnMouseClicked(e -> {
                    selectedParkingStallId.set(stall.getId());
                    dialog.close(); // Close the dialog after selection
                });
            } else {
                rectangle.setFill(Color.RED);
                rectangle.setDisable(true); // Make unavailable stalls unclickable
            }

            // Add a label to display stall details
            Label stallLabel = new Label(stall.getStallNumber());
            stallLabel.setStyle("-fx-font-weight: bold;");

            // Combine the rectangle and label in a StackPane
            StackPane stallPane = new StackPane(rectangle, stallLabel);
            parkingStallsPane.getChildren().add(stallPane);
        }

        // Handle empty results
        if (parkingStallsPane.getChildren().isEmpty()) {
            Label noStallsLabel = new Label("No available stalls for the selected type.");
            noStallsLabel.setStyle("-fx-font-size: 16; -fx-font-weight: bold;");
            parkingStallsPane.getChildren().add(noStallsLabel);
        }

        // Layout for the dialog
        VBox layout = new VBox(20, titleLabel, parkingStallsPane);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));
        Scene scene = new Scene(layout, 400, 600);

        // Configure and show the dialog
        dialog.setScene(scene);
        dialog.showAndWait();

        // Return the selected parking stall ID
        return selectedParkingStallId.get();
    }

    public List<StackPane> createRentalSystemList(String selectedRegion, AtomicReference<UUID> selectedRentalSystemId, Stage dialog) {
        // Retrieve the list of rental systems for the selected region
        List<RentalSystem> rentalSystems = getRentalSystemsByRegion(selectedRegion);

        // List to hold clickable tiles
        List<StackPane> tiles = new ArrayList<>();

        // Create a tile for each rental system
        for (RentalSystem rentalSystem : rentalSystems) {
            // Create a rectangle
            rectangle rectangle = new rectangle(200, 100, Color.BLUE, 2, 20);

            // Add labels for rental system details
            VBox rectangleContent = new VBox();
            rectangleContent.setAlignment(Pos.CENTER);
            rectangleContent.setSpacing(5);
            Label nameLabel = new Label("Name:");
            Label valueLabel = new Label(rentalSystem.getName());
            rectangleContent.getChildren().addAll(nameLabel, valueLabel);

            // Make the tile clickable
            StackPane clickableRectangle = new StackPane(rectangle, rectangleContent);
            clickableRectangle.setOnMouseClicked(e -> {
                // Set the selected rental system ID
                selectedRentalSystemId.set(rentalSystem.getId());
                dialog.close(); // Close the dialog
            });

            // Add the clickable tile to the list
            tiles.add(clickableRectangle);
        }

        return tiles;
    }

    private List<RentalSystem> getRentalSystemsByRegion(String region) {
        // Fetch rental systems based on the selected region
        return RentalSystemDAO.getRentalSystemsByRegion(region);
    }

    public BorderPane getLayout() {
        return layout;
    }
}

