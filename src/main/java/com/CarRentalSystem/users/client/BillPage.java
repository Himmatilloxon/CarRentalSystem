package com.CarRentalSystem.users.client;

import com.CarRentalSystem.components.BackButtonBox;
import com.CarRentalSystem.database.PaymentDAO;
import com.CarRentalSystem.database.ReservationDAO;
import com.CarRentalSystem.main.MainApp;
import com.CarRentalSystem.reservation.Bill;
import com.CarRentalSystem.reservation.Payment;
import com.CarRentalSystem.reservation.PaymentStatus;
import com.CarRentalSystem.reservation.PaymentType;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class BillPage {

    private static BillPage instance; // Singleton instance
    private final BorderPane layout; // Main layout changed to BorderPane
    private final VBox billItemsBox;
    private final Bill bill;

    private final Label totalLabel;
    private final TextField cardNumberField;
    private final TextField bankNameField;
    private final TextField checkNumberField;
    private final TextField cashAmountField;
    private final VBox paymentFieldBox; // Container for dynamic payment fields
    private final Button submitButton; // Submit button to process input

    public BillPage(Stage primaryStage, Bill bill, Client client) {
        layout = new BorderPane();
        this.bill = bill;

        // Main content layout
        VBox mainContent = new VBox(20);
        mainContent.setAlignment(Pos.CENTER);
        mainContent.setPadding(new javafx.geometry.Insets(20));

        // Title
        Label billTitle = new Label("Bill");

        // VBox for bill items
        billItemsBox = new VBox(10);
        billItemsBox.setAlignment(Pos.CENTER);
        addBillItem("Vehicle", bill.getVehicleBill());
        if (bill.getEquipmentBill() > 0) addBillItem("Equipment", bill.getEquipmentBill());
        if (bill.getServiceBill() > 0) addBillItem("Service", bill.getServiceBill());

        // Total label
        totalLabel = new Label("Total: $0"); // Update based on actual total
        updateTotal();

        // Payment section
        HBox paymentButtons = new HBox(10);
        paymentButtons.setAlignment(Pos.CENTER);
        Button creditCardButton = new Button("Credit Card");
        Button checkButton = new Button("Check");
        Button cashButton = new Button("Cash");

        // Payment input fields
        cardNumberField = new TextField();
        cardNumberField.setPromptText("Card Number");
        cardNumberField.setMaxWidth(200);

        bankNameField = new TextField();
        bankNameField.setPromptText("Bank Name");
        bankNameField.setMaxWidth(200);

        checkNumberField = new TextField();
        checkNumberField.setPromptText("Check Number");
        checkNumberField.setMaxWidth(200);

        cashAmountField = new TextField();
        cashAmountField.setPromptText("Amount");
        cashAmountField.setMaxWidth(200);

        // Payment field box to hold the input fields dynamically
        paymentFieldBox = new VBox(10);
        paymentFieldBox.setAlignment(Pos.CENTER);

        // Submit button for processing input
        submitButton = new Button("Submit");
        submitButton.setDisable(true); // Initially disabled

        // Button actions
        creditCardButton.setOnAction(e -> {
            showPaymentFields("card");
            enableSubmitButton(); // Enable after selecting a payment method
        });
        checkButton.setOnAction(e -> {
            showPaymentFields("check");
            enableSubmitButton();
        });
        cashButton.setOnAction(e -> {
            showPaymentFields("cash");
            enableSubmitButton();
        });

        paymentButtons.getChildren().addAll(creditCardButton, checkButton, cashButton);

        // Submit button action
        submitButton.setOnAction(e -> {
            if (processInput()) {
                ReservationDAO.insertReservations(client.getId(), bill.getReservationID());
                MainApp.showClient(client);
            }
        });

        // Add main content to the VBox
        mainContent.getChildren().addAll(billTitle, billItemsBox, totalLabel, paymentButtons, paymentFieldBox, submitButton);

        // Add back button to the top-left corner
        BackButtonBox backButtonBox = new BackButtonBox();

        // Set elements in the BorderPane layout
        layout.setTop(backButtonBox.getBackButtonBox());  // Back button in the top left
        layout.setCenter(mainContent); // Main content in the center
    }

    private void addBillItem(String description, double amount) {
        Label billItem = new Label(description + " ---- $" + Math.round(amount * 100.0) / 100.0);
        billItemsBox.getChildren().add(billItem);
    }

    private void updateTotal() {
        double totalAmount = bill.getTotalBill();
        totalAmount = Math.round(totalAmount * 100.0) / 100.0;
        totalLabel.setText("Total: $" + totalAmount);
    }

    private void showPaymentFields(String paymentType) {
        paymentFieldBox.getChildren().clear();

        switch (paymentType) {
            case "card":
                paymentFieldBox.getChildren().add(new Label("Enter Card Details:"));
                paymentFieldBox.getChildren().add(cardNumberField);
                break;
            case "check":
                paymentFieldBox.getChildren().add(new Label("Enter Check Details:"));
                paymentFieldBox.getChildren().addAll(bankNameField, checkNumberField);
                break;
            case "cash":
                paymentFieldBox.getChildren().add(new Label("Enter Cash Amount:"));
                paymentFieldBox.getChildren().add(cashAmountField);
                break;
        }
    }

    private void enableSubmitButton() {
        submitButton.setDisable(false);
    }

    private boolean processInput() {
        Payment payment = new Payment();
        payment.setBill_id(bill.getId());
        payment.setAmount(bill.getTotalBill());

        // Process Credit Card Payment
        if (!cardNumberField.getText().isEmpty()) {
            if (ValidCardNumber(cardNumberField.getText())) {
                payment.setStatus(PaymentStatus.Completed);
                payment.setType(PaymentType.Card);
                payment.setId(PaymentDAO.insertPayment(payment));

                PaymentDAO.insertCreditCardTransaction(payment.getId(), cardNumberField.getText());

                showAlert("Payment Successful", "Your payment via Credit Card was successful.", Alert.AlertType.INFORMATION);
                return true;
            } else {
                showAlert("Invalid Card Number", "Please enter a valid credit card number.", Alert.AlertType.ERROR);
            }
        }
        // Process Check Payment
        else if (!bankNameField.getText().isEmpty() && !checkNumberField.getText().isEmpty()) {
            payment.setStatus(PaymentStatus.Completed);
            payment.setType(PaymentType.Check);
            payment.setId(PaymentDAO.insertPayment(payment));

            PaymentDAO.insertCheckTransaction(payment.getId(), bankNameField.getText(), checkNumberField.getText());

            showAlert("Payment Successful", "Your payment via Check was successful.", Alert.AlertType.INFORMATION);
            return true;
        }
        // Process Cash Payment
        else if (!cashAmountField.getText().isEmpty()) {
            try {
                double cashAmount = Double.parseDouble(cashAmountField.getText());
                if (cashAmount >= bill.getTotalBill()) {
                    payment.setStatus(PaymentStatus.Completed);
                    payment.setType(PaymentType.Cash);
                    payment.setId(PaymentDAO.insertPayment(payment));

                    PaymentDAO.insertCashTransaction(payment.getId(), cashAmount);

                    showAlert("Payment Successful", "Your cash payment was successful.", Alert.AlertType.INFORMATION);
                    return true;
                } else {
                    payment.setStatus(PaymentStatus.Failed);
                    showAlert("Insufficient Cash", "The cash amount is less than the total bill.", Alert.AlertType.ERROR);
                }
            } catch (NumberFormatException e) {
                showAlert("Invalid Amount", "Please enter a valid numeric amount.", Alert.AlertType.ERROR);
            }
        } else {
            showAlert("Missing Payment Details", "Please select a payment method and provide the necessary details.", Alert.AlertType.ERROR);
        }
        return false;
    }

    // Validate the card number (simplified for demonstration purposes)
    public boolean ValidCardNumber(String cardNumber) {
        return cardNumber.matches("\\d{16}"); // Simple validation: must be 16 digits
    }

    // Alert function to show messages
    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static BillPage getInstance(Stage primaryStage, Bill bill, Client client) {
        if (instance == null) {
            instance = new BillPage(primaryStage, bill, client);
        }
        return instance;
    }

    public BorderPane getLayout() {
        return layout;
    }
}
