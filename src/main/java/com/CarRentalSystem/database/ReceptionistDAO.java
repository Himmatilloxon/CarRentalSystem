package com.CarRentalSystem.database;

import com.CarRentalSystem.users.receptionist.Receptionist;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ReceptionistDAO {

    public static List<Receptionist> getReceptionists(String searchTerm) {
        List<Receptionist> receptionists = new ArrayList<>();

        String receptionistQuery = "SELECT r.\"id\", r.\"date_joined\" " +
                "FROM \"Receptionist\" r";

        String accountQuery = "SELECT \"login\" FROM \"Account\" WHERE \"id\" = ?";

        if (searchTerm != null && !searchTerm.isEmpty()) {
            receptionistQuery += " WHERE r.\"id\" IN " +
                    "(SELECT a.\"id\" FROM \"Account\" a WHERE LOWER(a.\"login\") LIKE LOWER(?))";
        }

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement receptionistStatement = connection.prepareStatement(receptionistQuery)) {

            if (searchTerm != null && !searchTerm.isEmpty()) {
                receptionistStatement.setString(1, "%" + searchTerm + "%");
            }

            ResultSet receptionistResult = receptionistStatement.executeQuery();
            while (receptionistResult.next()) {
                Receptionist receptionist = new Receptionist();
                receptionist.setId((UUID) receptionistResult.getObject("id"));
                receptionist.setDate_joined(receptionistResult.getDate("date_joined"));

                try (PreparedStatement accountStatement = connection.prepareStatement(accountQuery)) {
                    accountStatement.setObject(1, receptionist.getId());
                    ResultSet accountResult = accountStatement.executeQuery();
                    if (accountResult.next()) {
                        receptionist.setLogin(accountResult.getString("login"));
                    }
                }
                System.out.println(receptionist.getId());
                System.out.println(receptionist.getLogin());

                receptionists.add(receptionist);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return receptionists;
    }

    public static void deleteReceptionist(UUID receptionistId) {
        String getAddressIdQuery = "SELECT \"address_id\" FROM \"Person\" WHERE \"id\" = ?";
        String deleteFromAddressQuery = "DELETE FROM \"Location\" WHERE \"id\" = ?";
        String deleteFromPersonQuery = "DELETE FROM \"Person\" WHERE \"id\" = ?";
        String deleteFromAccountQuery = "DELETE FROM \"Account\" WHERE \"id\" = ?";
        String deleteFromReceptionistQuery = "DELETE FROM \"Receptionist\" WHERE \"id\" = ?";

        try (Connection connection = DatabaseManager.getConnection()) {
            connection.setAutoCommit(false); // Start transaction

            // Get address_id from Person table
            UUID addressId = null;
            try (PreparedStatement getAddressIdStmt = connection.prepareStatement(getAddressIdQuery)) {
                getAddressIdStmt.setObject(1, receptionistId);
                ResultSet resultSet = getAddressIdStmt.executeQuery();
                if (resultSet.next()) {
                    addressId = (UUID) resultSet.getObject("address_id");
                }
            }

            // Delete from Receptionist table
            try (PreparedStatement deleteReceptionistStmt = connection.prepareStatement(deleteFromReceptionistQuery)) {
                deleteReceptionistStmt.setObject(1, receptionistId);
                deleteReceptionistStmt.executeUpdate();
            }

            // Delete from Account table
            try (PreparedStatement deleteAccountStmt = connection.prepareStatement(deleteFromAccountQuery)) {
                deleteAccountStmt.setObject(1, receptionistId);
                deleteAccountStmt.executeUpdate();
            }

            // Delete from Person table
            try (PreparedStatement deletePersonStmt = connection.prepareStatement(deleteFromPersonQuery)) {
                deletePersonStmt.setObject(1, receptionistId);
                deletePersonStmt.executeUpdate();
            }

            // Delete from Address table if address_id exists
            if (addressId != null) {
                try (PreparedStatement deleteAddressStmt = connection.prepareStatement(deleteFromAddressQuery)) {
                    deleteAddressStmt.setObject(1, addressId);
                    deleteAddressStmt.executeUpdate();
                }
            }

            connection.commit(); // Commit transaction
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void addReceptionist(Receptionist receptionist) {
        String queryNew = "INSERT INTO \"Receptionist\" (\"id\") VALUES (?)";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(queryNew)) {

            statement.setObject(1, receptionist.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
