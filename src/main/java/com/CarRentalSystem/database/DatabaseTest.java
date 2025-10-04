package com.CarRentalSystem.database;

import java.sql.*;

public class DatabaseTest {
    private static final String URL = "jdbc:postgresql://aws-0-eu-central-1.pooler.supabase.com:6543/postgres";
    private static final String USER = "postgres.hkdjkhiokhaqkqzuevpr";
    private static final String PASSWORD = "CarRentalSystemTeam3";

    public static void main(String[] args) throws SQLException {

        String carRentalSystemQuery = "SELECT * FROM \"CarRentalSystem\"";

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)){
            System.out.println("Connected");
            try (PreparedStatement statement = connection.prepareStatement(carRentalSystemQuery);
            ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String name = resultSet.getString("name");
                    int address_id = resultSet.getInt("address_id");

                    System.out.println(id + " " + name + " " + address_id);
                }
            }

        } catch (Exception e) {
            System.out.println("Error fetching data!");
            e.printStackTrace();
        }
    }
}
