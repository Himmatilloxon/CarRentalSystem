package com.CarRentalSystem.database;

import com.CarRentalSystem.reservation.Bill;
import com.CarRentalSystem.reservation.Payment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class PaymentDAO {

    public static int insertBill(Bill bill) {
        String query = "INSERT INTO \"Bill\" (\"reservation_id\", \"vehicle_bill\", \"equipment_bill\", \"service_bill\", \"total_amount\") VALUES (?, ?, ?, ?, ?) RETURNING id";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setObject(1, bill.getReservationID());
            statement.setDouble(2, bill.getVehicleBill());
            statement.setDouble(3, bill.getEquipmentBill());
            statement.setDouble(4, bill.getServiceBill());
            statement.setDouble(5, bill.getTotalBill());

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("id");
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error inserting bill: " + e.getMessage(), e);
        }
        return 0;
    }

    public static UUID insertPayment(Payment payment) {
        String query = "INSERT INTO \"Payment\" (\"bill_id\", \"amount\", \"payment_status\", \"type\") VALUES (?, ?, ?, ?) RETURNING id";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setObject(1, payment.getBill_id());
            statement.setDouble(2, payment.getAmount());
            statement.setObject(3, payment.getStatus(), java.sql.Types.OTHER);
            statement.setObject(4, payment.getType(), java.sql.Types.OTHER);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return (UUID) resultSet.getObject("id");
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error inserting bill: " + e.getMessage(), e);
        }
        return null;
    }

    public static void insertCheckTransaction(UUID id, String bankName, String checkNumber) {
        String query = "INSERT INTO \"CheckTransaction\" (\"id\", \"bank_name\", \"check_number\") VALUES (?, ?, ?)";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setObject(1, id);
            statement.setString(2, bankName);
            statement.setString(3, checkNumber);
            statement.execute();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void insertCreditCardTransaction(UUID id, String cardNumber) {
        String query = "INSERT INTO \"CreditCardTransaction\" (\"id\", \"card_number\") VALUES (?, ?)";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setObject(1, id);
            statement.setString(2, cardNumber);
            statement.execute();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void insertCashTransaction(UUID id, double amount) {
        String query = "INSERT INTO \"CashTransaction\" (\"id\", \"cash_tendered\") VALUES (?, ?)";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setObject(1, id);
            statement.setDouble(2, amount);
            statement.execute();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
