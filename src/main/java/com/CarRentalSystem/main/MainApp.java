package com.CarRentalSystem.main;

import com.CarRentalSystem.reservation.Bill;
import com.CarRentalSystem.users.admin.Admin;
import com.CarRentalSystem.users.admin.ReceptionistsManager;
import com.CarRentalSystem.users.admin.RentalSystemManager;
import com.CarRentalSystem.users.client.*;
import com.CarRentalSystem.loginHandler.LoginPage;
import com.CarRentalSystem.loginHandler.SignUpPage;
import com.CarRentalSystem.users.admin.AdminUI;
import com.CarRentalSystem.users.member.Member;
import com.CarRentalSystem.users.member.MemberUI;
import com.CarRentalSystem.users.member.OrdersPage;
import com.CarRentalSystem.users.receptionist.Receptionist;
import com.CarRentalSystem.users.receptionist.ReceptionistUI;
import com.CarRentalSystem.users.receptionist.Reservations;
import com.CarRentalSystem.users.receptionist.VehicleManager;
import com.CarRentalSystem.vehicle.Vehicle;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.Stack;
import java.util.UUID;

public class MainApp extends Application {

    private static BorderPane mainLayout;
    private static Stage primaryStage;
    private static final Stack<javafx.scene.layout.Pane> layoutHistory = new Stack<>(); // Track history of layouts

    @Override
    public void start(Stage stage) {
        primaryStage = stage;
        mainLayout = new BorderPane();
        Scene mainScene = new Scene(mainLayout, 800, 600);
        primaryStage.setScene(mainScene);
        primaryStage.show();

        showLoginPage();
    }

    public static void showLoginPage() {
        LoginPage loginPage = new LoginPage(primaryStage);
        switchContent(loginPage.getLayout());
    }

    public static void showSignUpPage() {
        SignUpPage signupPage = new SignUpPage(primaryStage);
        switchContent(signupPage.getLayout());
    }

    public static void showClient(Client client) {
        ClientUI clientUI = new ClientUI(primaryStage, client);
        switchContent(clientUI.getLayout());
    }

    public static void showMember(Member member) {
        MemberUI memberUI = new MemberUI(primaryStage, member);
        switchContent(memberUI.getLayout());
    }

    public static void showReceptionist(Receptionist receptionist) {
        ReceptionistUI receptionistUI = new ReceptionistUI(primaryStage, receptionist);
        switchContent(receptionistUI.getLayout());
    }

    public static void showAdmin(Admin admin) {
        AdminUI adminUI = new AdminUI(primaryStage, admin);
        switchContent(adminUI.getLayout());
    }

    public static void showRentCar(UUID rental_id, Client client) {
        RentCar rentCar = new RentCar(primaryStage, rental_id, client);
        switchContent(rentCar.getLayout());
    }

    public static void showAdditionalMaterials(Vehicle vehicle, Client client) {
        AdditionalMaterials additionalMaterials = new AdditionalMaterials(primaryStage, vehicle, client);
        switchContent(additionalMaterials.getLayout());
    }

    public static void showBillPage(Bill bill, Client client) {
        BillPage billPage = new BillPage(primaryStage, bill, client);
        switchContent(billPage.getLayout());
    }

    public static void showClientInfo(Client client) throws SQLException {
        ClientInfoPage infoPage = new ClientInfoPage(primaryStage, client);
        switchContent(infoPage.getLayout());
    }

    public static void showReceptionistManager(Admin admin) {
        ReceptionistsManager receptionistsManager = new ReceptionistsManager(primaryStage, admin);
        switchContent(receptionistsManager.getLayout());
    }

    public static void showRentalSystemManager(Admin admin) {
        RentalSystemManager rentalSystemManager = new RentalSystemManager(primaryStage, admin);
        switchContent(rentalSystemManager.getLayout());
    }

    public static void showVehicleManager(Receptionist receptionist) {
        VehicleManager vehicleManager = new VehicleManager(primaryStage, receptionist);
        switchContent(vehicleManager.getLayout());
    }

    public static void showRentedCarsPage(Client client) {
        RentedCarsPage rentalCarsPage = new RentedCarsPage(primaryStage, client);
        switchContent(rentalCarsPage.getLayout());
    }

    public static void showOrdersPage(Member member) {
        OrdersPage ordersPage = new OrdersPage(primaryStage, member);
        switchContent(ordersPage.getLayout());
    }

    public static void showReservations(Receptionist receptionist) {
        Reservations reservations = new Reservations(primaryStage, receptionist);
        switchContent(reservations.getLayout());
    }

    private static void switchContent(Pane newContent) {
        if (mainLayout.getCenter() != null) {
            layoutHistory.push((Pane) mainLayout.getCenter());
        }
        mainLayout.setCenter(newContent);
    }

    public static void goBack() {
        if (!layoutHistory.isEmpty()) {
            mainLayout.setCenter(layoutHistory.pop());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
