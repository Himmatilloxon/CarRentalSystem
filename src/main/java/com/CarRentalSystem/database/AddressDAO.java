package com.CarRentalSystem.database;

import com.CarRentalSystem.Address;

import java.sql.*;
import java.util.UUID;

public class AddressDAO {

    // Insert an Address into the database and set its ID
    public static void insertAddress(Address address) throws SQLException {
        String query = "INSERT INTO \"Location\" (street_address, city, state, zipcode, country) VALUES (?, ?, ?, ?, ?) RETURNING id";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, address.getStreet());
            statement.setString(2, address.getCity());
            statement.setString(3, address.getState());
            statement.setString(4, address.getZipcode());
            statement.setString(5, address.getCountry());

            // Execute the insert and fetch the generated ID
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    UUID id = (UUID) resultSet.getObject("id");
                    address.setId(id);
                }
            }
        }
    }


    // Fetch an Address by ID
    public static Address getAddressById(UUID id) throws SQLException {
        String query = "SELECT street_address, city, state, zipcode, country FROM \"Location\" WHERE id = ?";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setObject(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    String street = resultSet.getString("street_address");
                    String city = resultSet.getString("city");
                    String state = resultSet.getString("state");
                    String zipcode = resultSet.getString("zipcode");
                    String country = resultSet.getString("country");

                    // Create and return an Address object
                    Address address = new Address(street, city, state, zipcode, country);
                    address.setId(id);
                    return address;
                }
            }
        }
        return null; // Return null if no address is found
    }

    // Update an Address in the database
    public static void updateAddress(Address address) throws SQLException {
        String query = "UPDATE \"Location\" SET street_address = ?, city = ?, state = ?, zipcode = ?, country = ? WHERE id = ?";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, address.getStreet());
            statement.setString(2, address.getCity());
            statement.setString(3, address.getState());
            statement.setString(4, address.getZipcode());
            statement.setString(5, address.getCountry());
            statement.setObject(6, address.getId());

            // Execute the update query
            statement.executeUpdate();
        }
    }

    // Delete an Address from the database
    public boolean deleteAddress(UUID id) throws SQLException {
        String query = "DELETE FROM \"Location\" WHERE id = ?";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setObject(1, id);
            return statement.executeUpdate() > 0; // Return true if at least one row was deleted
        }
    }
}
