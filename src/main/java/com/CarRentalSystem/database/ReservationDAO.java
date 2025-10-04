package com.CarRentalSystem.database;

import com.CarRentalSystem.reservation.EquipmentType;
import com.CarRentalSystem.reservation.ReservationStatus;
import com.CarRentalSystem.reservation.ServiceType;
import com.CarRentalSystem.reservation.VehicleReservation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ReservationDAO {

    public static UUID addReservation(VehicleReservation reservation) {
        String query = "INSERT INTO \"VehicleReservation\" (\"status\", \"due_time\", \"return_date\", \"vehicle_id\") " +
                "VALUES (?, ?, ?, ?) RETURNING \"id\"";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            // Set the parameters for the query
            statement.setObject(1, reservation.getStatus(), java.sql.Types.OTHER);
            statement.setDate(2, new java.sql.Date(reservation.getDueDate().getTime()));
            statement.setDate(3, new java.sql.Date(reservation.getReturnDate().getTime()));
            statement.setObject(4, reservation.getVehicleId());

            // Execute the query and fetch the generated ID
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return (UUID) resultSet.getObject("id");
                } else {
                    throw new RuntimeException("Failed to retrieve the generated reservation ID.");
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error adding reservation: " + e.getMessage(), e);
        }
    }

    public static void addService(UUID id, ServiceType serviceType) {
        String query = "INSERT INTO \"Service\" (\"id\", \"service_type\") VALUES (?, ?)";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setObject(1, id);
            statement.setObject(2, serviceType, java.sql.Types.OTHER);
            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error adding service: " + e.getMessage(), e);
        }
    }

    public static void addEquipment(UUID id, EquipmentType equipmentType) {
        String query = "INSERT INTO \"Equipment\" (\"id\", \"equipment_type\") VALUES (?, ?)";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setObject(1, id);
            statement.setObject(2, equipmentType, java.sql.Types.OTHER);
            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error adding equipment: " + e.getMessage(), e);
        }    }

    public static void insertReservations(UUID clientId, UUID reservationId) {
        String query = "INSERT INTO \"Reservations\" (\"client_id\", \"reservation_id\") VALUES (?, ?)";
        String query1 = "INSERT INTO \"AdditionalDriver\" (\"reservation_id\") VALUES (?)";

        try (Connection connection = DatabaseManager.getConnection()) {
            // Disable auto-commit to handle both queries as a single transaction
            connection.setAutoCommit(false);

            try (PreparedStatement statement1 = connection.prepareStatement(query);
                 PreparedStatement statement2 = connection.prepareStatement(query1)) {

                // Execute the first query
                statement1.setObject(1, clientId);
                statement1.setObject(2, reservationId);
                statement1.executeUpdate();

                // Execute the second query
                statement2.setObject(1, reservationId);
                statement2.executeUpdate();

                // Commit the transaction if both queries succeed
                connection.commit();

            } catch (SQLException e) {
                // Rollback the transaction in case of an error
                connection.rollback();
                throw new RuntimeException("Error adding reservations: " + e.getMessage(), e);
            } finally {
                // Restore auto-commit mode
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database connection error: " + e.getMessage(), e);
        }
    }

    public static List<VehicleReservation> getReservationsByClientId(UUID clientId) {
        List<VehicleReservation> reservations = new ArrayList<>();
        String reservationsQuery = "SELECT VR.* FROM \"Reservations\" R " +
                "JOIN \"VehicleReservation\" VR ON R.reservation_id = VR.id " +
                "WHERE R.client_id = ?";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(reservationsQuery)) {

            statement.setObject(1, clientId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    VehicleReservation reservation = new VehicleReservation();
                    reservation.setId(UUID.fromString(resultSet.getString("id")));
                    reservation.setReservationNumber(resultSet.getInt("reservation_number"));
                    reservation.setStatus(ReservationStatus.valueOf(resultSet.getString("status")));
                    reservation.setDueDate(resultSet.getDate("due_time"));
                    reservation.setReturnDate(resultSet.getDate("return_date"));
                    reservation.setVehicleId(UUID.fromString(resultSet.getString("vehicle_id")));

                    reservations.add(reservation);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error getting reservations by client id: " + e.getMessage(), e);
        }

        return reservations;
    }

    public static List<VehicleReservation> getNonAcceptedReservations() {
        List<VehicleReservation> nonAcceptedReservations = new ArrayList<>();

        String query = """
                SELECT vr.id, vr.reservation_number, vr.status, vr.due_time, vr.return_date, vr.vehicle_id
                FROM \"VehicleReservation\" vr
                JOIN \"AdditionalDriver\" ad ON vr.id = ad.reservation_id
                WHERE ad.driver_id IS NULL;
                """;

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                VehicleReservation reservation = new VehicleReservation();
                reservation.setId(UUID.fromString(resultSet.getString("id")));
                reservation.setReservationNumber(resultSet.getInt("reservation_number"));
                reservation.setStatus(ReservationStatus.valueOf(resultSet.getString("status")));
                reservation.setDueDate(resultSet.getDate("due_time"));
                reservation.setReturnDate(resultSet.getDate("return_date"));
                reservation.setVehicleId(UUID.fromString(resultSet.getString("vehicle_id")));

                nonAcceptedReservations.add(reservation);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error getting non-accepted reservations: " + e.getMessage(), e);
        }

        return nonAcceptedReservations;
    }

    public static void updateAdditionalDriver(UUID driverId, UUID reservationId) {
        String query = "UPDATE \"AdditionalDriver\" SET \"driver_id\" = ? WHERE \"reservation_id\" = ?";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            // Set the parameters for the query
            statement.setObject(1, driverId);
            statement.setObject(2, reservationId);

            // Execute the update query
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected == 0) {
                System.out.println("No rows updated. Make sure the reservation_id exists in the table.");
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error updating additional driver: " + e.getMessage(), e);
        }
    }

    public static List<VehicleReservation> getOrders(UUID memberId) {
        List<VehicleReservation> reservations = new ArrayList<>();
        String ordersQuery = "SELECT VR.* FROM \"AdditionalDriver\" R " +
                "JOIN \"VehicleReservation\" VR ON R.reservation_id = VR.id " +
                "WHERE R.driver_id = ?";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(ordersQuery)) {

            statement.setObject(1, memberId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    VehicleReservation reservation = new VehicleReservation();
                    reservation.setId(UUID.fromString(resultSet.getString("id")));
                    reservation.setReservationNumber(resultSet.getInt("reservation_number"));
                    reservation.setStatus(ReservationStatus.valueOf(resultSet.getString("status")));
                    reservation.setDueDate(resultSet.getDate("due_time"));
                    reservation.setReturnDate(resultSet.getDate("return_date"));
                    reservation.setVehicleId(UUID.fromString(resultSet.getString("vehicle_id")));

                    reservations.add(reservation);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error getting reservations by client id: " + e.getMessage(), e);
        }

        return reservations;
    }

}
