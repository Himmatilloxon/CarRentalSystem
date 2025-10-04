package com.CarRentalSystem.database;

import com.CarRentalSystem.RentalSystem;
import com.CarRentalSystem.vehicle.ParkingStall;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RentalSystemDAO {

    public static List<RentalSystem> getRentalSystems(String searchTerm) {
        List<RentalSystem> rentalSystems = new ArrayList<>();

        String rentalSystemQuery = "SELECT rs.\"id\", rs.\"name\", rs.\"address_id\" " +
                "FROM \"RentalSystem\" rs";

        if (searchTerm != null && !searchTerm.isEmpty()) {
            rentalSystemQuery += " WHERE LOWER(rs.\"name\") LIKE LOWER(?)";
        }

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(rentalSystemQuery)) {

            if (searchTerm != null && !searchTerm.isEmpty()) {
                statement.setString(1, "%" + searchTerm + "%");
            }

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                RentalSystem rentalSystem = new RentalSystem();
                rentalSystem.setId(resultSet.getObject("id", UUID.class));
                rentalSystem.setName(resultSet.getString("name"));
                rentalSystem.setAddress_id(resultSet.getObject("address_id", UUID.class)); // Direct from RentalSystem table

                rentalSystems.add(rentalSystem);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving rental systems", e);
        }

        return rentalSystems;
    }

    public static List<RentalSystem> getRentalSystemsByRegion(String state) {
        List<RentalSystem> rentalSystems = new ArrayList<>();

        // Updated query to join RentalSystem and Location tables and filter by state
        String rentalSystemQuery = "SELECT rs.\"id\", rs.\"name\", rs.\"address_id\" " +
                "FROM \"RentalSystem\" rs " +
                "JOIN \"Location\" l ON rs.\"address_id\" = l.\"id\"";

        if (state != null && !state.isEmpty()) {
            rentalSystemQuery += " WHERE LOWER(l.\"state\") = LOWER(?)";
        }

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(rentalSystemQuery)) {

            if (state != null && !state.isEmpty()) {
                statement.setString(1, state);
            }

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                RentalSystem rentalSystem = new RentalSystem();
                rentalSystem.setId(resultSet.getObject("id", UUID.class));
                rentalSystem.setName(resultSet.getString("name"));
                rentalSystem.setAddress_id(resultSet.getObject("address_id", UUID.class)); // From RentalSystem table

                rentalSystems.add(rentalSystem);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving rental systems by region", e);
        }

        return rentalSystems;
    }

    public static void deleteRentalSystem(UUID rentalSystemId) {
        String getLocationIdQuery = "SELECT \"address_id\" FROM \"RentalSystem\" WHERE \"id\" = ?";
        String deleteCarsQuery = "DELETE FROM \"Car\" WHERE \"vehicle_id\" IN (SELECT \"id\" FROM \"Vehicle\" WHERE \"rental_id\" = ?)";
        String deleteVehiclesQuery = "DELETE FROM \"Vehicle\" WHERE \"rental_id\" = ?";
        String deleteParkingQuery = "DELETE FROM \"ParkingStall\" WHERE \"rental_id\" = ?";
        String deleteRentalSystemQuery = "DELETE FROM \"RentalSystem\" WHERE \"id\" = ?";
        String deleteLocationQuery = "DELETE FROM \"Location\" WHERE \"id\" = ?";

        try (Connection connection = DatabaseManager.getConnection()) {
            connection.setAutoCommit(false); // Start transaction

            // Step 1: Delete cars associated with the vehicles
            try (PreparedStatement deleteCarsStmt = connection.prepareStatement(deleteCarsQuery)) {
                deleteCarsStmt.setObject(1, rentalSystemId);
                deleteCarsStmt.executeUpdate();
            }

            // Step 2: Delete vehicles associated with the rental system
            try (PreparedStatement deleteVehiclesStmt = connection.prepareStatement(deleteVehiclesQuery)) {
                deleteVehiclesStmt.setObject(1, rentalSystemId);
                deleteVehiclesStmt.executeUpdate();
            }

            // Step 3: Delete parking stalls associated with the rental system
            try (PreparedStatement deleteParkingStmt = connection.prepareStatement(deleteParkingQuery)) {
                deleteParkingStmt.setObject(1, rentalSystemId);
                deleteParkingStmt.executeUpdate();
            }

            // Step 4: Get location_id associated with the rental system
            UUID locationId = null;
            try (PreparedStatement getLocationIdStmt = connection.prepareStatement(getLocationIdQuery)) {
                getLocationIdStmt.setObject(1, rentalSystemId);
                ResultSet resultSet = getLocationIdStmt.executeQuery();
                if (resultSet.next()) {
                    locationId = resultSet.getObject("address_id", UUID.class);
                }
            }

            // Step 5: Delete the rental system
            try (PreparedStatement deleteRentalSystemStmt = connection.prepareStatement(deleteRentalSystemQuery)) {
                deleteRentalSystemStmt.setObject(1, rentalSystemId);
                deleteRentalSystemStmt.executeUpdate();
            }

            // Step 6: Delete the location if it exists
            if (locationId != null) {
                try (PreparedStatement deleteLocationStmt = connection.prepareStatement(deleteLocationQuery)) {
                    deleteLocationStmt.setObject(1, locationId);
                    deleteLocationStmt.executeUpdate();
                }
            }

            connection.commit(); // Commit transaction
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting rental system", e);
        }
    }

    public static void addRentalSystem(RentalSystem rentalSystem) {
        String queryNew = "INSERT INTO \"RentalSystem\" (\"name\", \"address_id\") VALUES (?, ?) RETURNING id";
        String parkingQuery = "INSERT INTO \"ParkingStall\" (\"stall_number\", \"rental_id\", \"available\") VALUES (?, ?, ?)";

        try (Connection connection = DatabaseManager.getConnection()) {
            // Start a transaction
            connection.setAutoCommit(false);

            UUID rental_id;
            // Insert RentalSystem and get its ID
            try (PreparedStatement statement = connection.prepareStatement(queryNew)) {
                statement.setString(1, rentalSystem.getName());
                statement.setObject(2, rentalSystem.getAddress_id());

                try (ResultSet rs = statement.executeQuery()) {
                    if (rs.next()) {
                        rental_id = rs.getObject("id", UUID.class);
                    } else {
                        throw new RuntimeException("Insertion failed, no ID obtained.");
                    }
                }
            }

            // Insert ParkingStalls
            try (PreparedStatement statement = connection.prepareStatement(parkingQuery)) {
                String s = "CMSTV";
                for (int i = 1; i <= 10; i++) { // Stalls numbered 1 to 10
                    for (char c : s.toCharArray()) { // Prefixes: C, M, S, T, V
                        String stallNumber = c + String.valueOf(i);
                        statement.setString(1, stallNumber);
                        statement.setObject(2, rental_id);
                        statement.setBoolean(3, true);
                        statement.addBatch(); // Batch for better performance
                    }
                }
                statement.executeBatch(); // Execute all inserts in a batch
            }

            // Commit the transaction
            connection.commit();
        } catch (SQLException e) {
            throw new RuntimeException("Error while adding rental system: " + e.getMessage(), e);
        }
    }

    public static List<ParkingStall> getParkingStallsFromDB(UUID rental_id) {
        List<ParkingStall> parkingStalls = new ArrayList<>();
        String query = "SELECT id, stall_number, available FROM \"ParkingStall\" WHERE \"rental_id\" = ?";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            // Set the rental_id parameter in the query
            statement.setObject(1, rental_id);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    ParkingStall stall = new ParkingStall();
                    stall.setId(resultSet.getInt("id"));
                    stall.setStallNumber(resultSet.getString("stall_number"));
                    stall.setAvailable(resultSet.getBoolean("available"));

                    parkingStalls.add(stall);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error fetching parking stalls from database", e);
        }

        return parkingStalls;
    }

    public static void reserveParkingStall(int id) {
        String query = "UPDATE \"ParkingStall\" SET \"available\" = false WHERE \"id\" = ?";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, id); // Bind the parking stall ID to the query
            statement.executeUpdate(); // Execute the update query

        } catch (SQLException e) {
            throw new RuntimeException("Error reserving parking stall: " + e.getMessage(), e);
        }
    }

}
