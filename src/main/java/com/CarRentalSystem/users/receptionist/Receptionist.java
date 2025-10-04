package com.CarRentalSystem.users.receptionist;

import com.CarRentalSystem.users.Account;
import com.CarRentalSystem.users.AccountStatus;
import com.CarRentalSystem.users.UserType;

import java.time.LocalDate;
import java.util.Date;
import java.util.UUID;

public class Receptionist extends Account {
    private Date date_joined;

    public Receptionist(String name, String surname, String gender, String email, String phone,
                        LocalDate birthday, UUID addressId, String login, String password, UserType role,
                        AccountStatus status, Date dateJoined) {
        super(name, surname, gender, email, phone, birthday, addressId, login, password, role, status);
        this.date_joined = dateJoined;
    }

    public Receptionist() {}

    public void setDate_joined(Date date_joined) {
        this.date_joined = date_joined;
    }

    public Date getDate_joined() {
        return date_joined;
    }
}
