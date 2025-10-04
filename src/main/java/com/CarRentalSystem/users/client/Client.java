package com.CarRentalSystem.users.client;

import com.CarRentalSystem.users.Account;
import com.CarRentalSystem.users.AccountStatus;
import com.CarRentalSystem.users.Gender;
import com.CarRentalSystem.users.UserType;

import java.time.LocalDate;
import java.util.Date;
import java.util.UUID;

public class Client extends Account {
    private LocalDate created_at;
    private LocalDate updated_at;

    public Client() {}

    public Client(String name, String surname, String gender, String email, String phone,
                  LocalDate birthday, UUID addressId, String login, String password, UserType role,
                  AccountStatus status, LocalDate created_at) {
        super(name, surname, gender, email, phone, birthday, addressId, login, password, role, status);
        this.created_at = created_at;
        this.updated_at = LocalDate.now();
    }

    public void setCreated_at(LocalDate created_at) {
        this.created_at = created_at;
    }

    public void setUpdated_at(LocalDate updated_at) {
        this.updated_at = updated_at;
    }

    public LocalDate getCreated_at() {
        return created_at;
    }

    public LocalDate getUpdated_at() {
        return updated_at;
    }
}
