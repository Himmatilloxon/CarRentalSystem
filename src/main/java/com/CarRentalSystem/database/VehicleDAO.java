package com.CarRentalSystem.database;

import com.CarRentalSystem.vehicle.Barcode;
import com.CarRentalSystem.vehicle.Vehicle;
import com.CarRentalSystem.vehicle.VehicleStatus;
import com.CarRentalSystem.vehicle.car.Car;
import com.CarRentalSystem.vehicle.motorcycle.Motorcycle;
import com.CarRentalSystem.vehicle.suv.SUV;
import com.CarRentalSystem.vehicle.truck.Truck;
import com.CarRentalSystem.vehicle.van.Van;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class VehicleDAO {

    public static UUID insertVehicle(Vehicle vehicle) {

        RentalSystemDAO.reserveParkingStall(vehicle.getParkingStallId());

        String query = "INSERT INTO \"Vehicle\" (\"license_number\", \"passenger_capacity\", \"has_sunroof\", \"status\", \"model\", \"make\", \"manufacture_year\", \"mileage\", \"rental_id\", \"parking_id\", \"price\") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING id";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            // Setting parameters for the query
            statement.setString(1, vehicle.getLicenseNumber());
            statement.setInt(2, vehicle.getPassengerCapacity());
            statement.setBoolean(3, vehicle.isHasSunroof());
            statement.setObject(4, vehicle.getStatus(), java.sql.Types.OTHER); // Assuming status is an enum
            statement.setString(5, vehicle.getModel());
            statement.setString(6, vehicle.getMake());
            statement.setInt(7, vehicle.getManufactureYear());
            statement.setInt(8, vehicle.getMileage());
            statement.setObject(9, vehicle.getRentalId()); // Assuming rental_id is UUID
            statement.setInt(10, vehicle.getParkingStallId());
            statement.setDouble(11, vehicle.getPrice());

            // Execute the query and retrieve the generated id
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return (UUID) resultSet.getObject("id"); // Assuming the Vehicle class has a setId method
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error inserting vehicle: " + e.getMessage(), e);
        }

        return null;
    }

    public static void insertCar(Car car) {
        String query = "INSERT INTO \"Car\" (\"id\", \"type\") VALUES (?, ?)";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setObject(1, car.getId());
            statement.setObject(2, car.getCarType(), java.sql.Types.OTHER);
            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error inserting car: " + e.getMessage(), e);
        }
    }

    public static void insertTruck(Truck truck) {
        String query = "INSERT INTO \"Truck\" (\"id\", \"type\") VALUES (?, ?)";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setObject(1, truck.getId());
            statement.setObject(2, truck.getType(), java.sql.Types.OTHER);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error inserting truck: " + e.getMessage(), e);
        }
    }

    public static void insertSUV(SUV suv) {
        String query = "INSERT INTO \"SUV\" (\"id\", \"type\") VALUES (?, ?)";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setObject(1, suv.getId());
            statement.setObject(2, suv.getType(), java.sql.Types.OTHER);
            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error inserting truck: " + e.getMessage(), e);
        }
    }

    public static void insertVan(Van van) {
        String query = "INSERT INTO \"Van\" (\"id\", \"type\") VALUES (?, ?)";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setObject(1, van.getId());
            statement.setObject(2, van.getVanType(), java.sql.Types.OTHER);
            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error inserting truck: " + e.getMessage(), e);
        }
    }

    public static void insertMotorcycle(Motorcycle motorcycle) {
        String query = "INSERT INTO \"Motorcycle\" (\"id\", \"type\") VALUES (?, ?)";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setObject(1, motorcycle.getId());
            statement.setObject(2, motorcycle.getMotorcycleType(), java.sql.Types.OTHER);
            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error inserting truck: " + e.getMessage(), e);
        }
    }

    public static List<Vehicle> fetchVehicles(String vehicleType, String specificType, UUID rentalId) {
        List<Vehicle> vehicles = new ArrayList<>();
        String specificTable = getSpecificTableName(vehicleType); // Get the specific table name

        if (specificTable == null) {
            throw new IllegalArgumentException("Invalid vehicle type: " + vehicleType);
        }

        // Define the enum type name based on the specific table
        String enumType = getEnumType(vehicleType);

        // SQL query to join Vehicle with the specific vehicle type table
        String query = "SELECT v.\"id\", v.\"rental_id\", v.\"license_number\", v.\"passenger_capacity\", " +
                "v.\"has_sunroof\", v.\"status\", v.\"make\", v.\"model\", v.\"manufacture_year\", " +
                "v.\"mileage\", v.\"parking_id\", v.\"price\", s.\"type\" " +
                "FROM \"Vehicle\" v " +
                "JOIN \"" + specificTable + "\" s ON v.\"id\" = s.\"id\" " +
                "WHERE v.\"rental_id\" = ? " +
                (specificType != null ? "AND s.\"type\" = ?::" + enumType + " " : "") + // Explicit cast to enum type
                "ORDER BY v.\"model\", v.\"make\"";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            // Set rental_id and specificType if provided
            statement.setObject(1, rentalId);
            if (specificType != null) {
                statement.setObject(2, specificType);
            }

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    // Create a new Vehicle object and populate its fields
                    Vehicle vehicle = new Vehicle();
                    vehicle.setId(UUID.fromString(resultSet.getString("id")));
                    vehicle.setRentalId(UUID.fromString(resultSet.getString("rental_id")));
                    vehicle.setLicenseNumber(resultSet.getString("license_number"));
                    vehicle.setPassengerCapacity(resultSet.getInt("passenger_capacity"));
                    vehicle.setHasSunroof(resultSet.getBoolean("has_sunroof"));
                    vehicle.setStatus(VehicleStatus.valueOf(resultSet.getString("status")));
                    vehicle.setMake(resultSet.getString("make"));
                    vehicle.setModel(resultSet.getString("model"));
                    vehicle.setManufactureYear(resultSet.getInt("manufacture_year"));
                    vehicle.setMileage(resultSet.getInt("mileage"));
                    vehicle.setParkingStallId(resultSet.getInt("parking_id"));
                    vehicle.setPrice(resultSet.getFloat("price"));

                    // Add the vehicle to the list
                    vehicles.add(vehicle);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching vehicles: " + e.getMessage(), e);
        }

        return vehicles;
    }

    public static String getSpecificTableName(String vehicleType) {
        return switch (vehicleType) {
            case "Car" -> "Car";
            case "Truck" -> "Truck";
            case "SUV" -> "SUV";
            case "Van" -> "Van";
            case "Motorcycle" -> "Motorcycle";
            default -> null;
        };
    }

    public static String getEnumType(String vehicleType) {
        return switch (vehicleType) {
            case "Car" -> "CarType";
            case "Truck" -> "TruckType";
            case "SUV" -> "SUVType";
            case "Van" -> "VanType";
            case "Motorcycle" -> "MotorcycleType";
            default -> throw new IllegalArgumentException("Invalid vehicle type: " + vehicleType);
        };
    }

    public static void insertBarcode(Barcode barcode) {
        String query = "INSERT INTO \"Barcode\" (\"id\", \"barcode\", \"active\") VALUES (?, ?, ?)";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setObject(1, barcode.getId());
            statement.setString(2, barcode.getBarcode());
            statement.setBoolean(3, barcode.isActive());
            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error inserting barcode: " + e.getMessage(), e);
        }
    }

    public static Double getVehiclePrice(UUID vehicleId) {
        String query = "SELECT \"price\" FROM \"Vehicle\" WHERE id = ?";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setObject(1, vehicleId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getDouble("price");
            } else {
                System.out.println("No vehicle found with the given ID.");
                return 0.0; // Vehicle not found
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching vehicle price.", e);
        }
    }

    public static Map<String, String> getModelAndMakeByVehicleId(UUID vehicleId) {
        Map<String, String> modelAndMake = new HashMap<>();
        String query = "SELECT model, make FROM \"Vehicle\" WHERE id = ?";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setObject(1, vehicleId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    modelAndMake.put("model", resultSet.getString("model"));
                    modelAndMake.put("make", resultSet.getString("make"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching model and make for vehicle: " + e.getMessage(), e);
        }

        return modelAndMake;
    }
}
