package com.CarRentalSystem.users.admin;

import com.CarRentalSystem.users.Account;
import com.CarRentalSystem.users.AccountStatus;
import com.CarRentalSystem.users.UserType;

import java.time.LocalDate;
import java.util.UUID;

public class Admin extends Account {
    private String permissions; // e.g., "CREATE, DELETE, UPDATE"

    public Admin() {}

    public Admin(String name, String surname, String gender, String email, String phone, LocalDate birthday, UUID addressId, String login, String password, UserType userType, AccountStatus status, String permissions) {
        super(name, surname, gender, email, phone, birthday, addressId, login, password, userType, status);
        this.permissions = permissions;
    }

    public String getPermissions() {
        return permissions;
    }

    public void setPermissions(String permissions) {
        this.permissions = permissions;
    }
}
