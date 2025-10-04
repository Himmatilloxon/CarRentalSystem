package com.CarRentalSystem.users.member;

import com.CarRentalSystem.users.Account;
import com.CarRentalSystem.users.AccountStatus;
import com.CarRentalSystem.users.UserType;

import java.time.LocalDate;
import java.util.UUID;

public class Member extends Account {
    private String driverLicensePath;

    public Member(String name, String surname, String gender, String email, String phone,
                  LocalDate birthday, UUID addressId, String login, String password, UserType role,
                  AccountStatus status) {
        super(name, surname, gender, email, phone, birthday, addressId, login, password, role, status);
    }

    public Member() {}

    public void setDriverLicensePath(String driverLicensePath) {
        this.driverLicensePath = driverLicensePath;
    }

    public String getDriverLicensePath() {
        return driverLicensePath;
    }
}
