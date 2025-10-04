module CarRentalSystem {
    requires javafx.controls;
    requires javafx.graphics;
    requires software.amazon.awssdk.services.s3;
    requires software.amazon.awssdk.auth;
    requires software.amazon.awssdk.regions;
    requires ch.qos.logback.core;
    requires java.sql;
    opens com.CarRentalSystem.main;
    opens com.CarRentalSystem.loginHandler;
}