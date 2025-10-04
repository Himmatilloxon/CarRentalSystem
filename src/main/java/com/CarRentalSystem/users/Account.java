package com.CarRentalSystem.users;

import java.time.LocalDate;
import java.util.UUID;

public class Account extends Person{
    private String login;
    private String password;
    private UserType userType; // MemberUI, ReceptionistUI, or ClientUI
    private AccountStatus status; // Active, Closed, etc.

    public Account() {
        super();
    }

    public Account(String name, String surname, String gender, String email,
                   String phone, LocalDate birthday, UUID addressId, String login, String password,
                   UserType userType, AccountStatus status) {
        super(name, surname, gender, email, phone, birthday, addressId);
        this.login = login;
        this.password = password;
        this.userType = userType;
        this.status = status;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = UserType.valueOf(userType);
    }

    public AccountStatus getStatus() {
        return status;
    }

    public void setStatus(AccountStatus status) {
        this.status = AccountStatus.valueOf(String.valueOf(status));
    }
}
