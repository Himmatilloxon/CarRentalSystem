package com.CarRentalSystem.loginHandler;

import com.CarRentalSystem.Address;
import com.CarRentalSystem.database.AccountDAO;
import com.CarRentalSystem.database.AddressDAO;
import com.CarRentalSystem.users.Account;
import com.CarRentalSystem.users.AccountStatus;
import com.CarRentalSystem.users.Gender;
import com.CarRentalSystem.users.UserType;
import com.CarRentalSystem.users.client.Client;
import com.CarRentalSystem.users.member.Member;

import java.sql.SQLException;
import java.time.LocalDate;

public class SignUpHandler {

    public Account handleSignUp(
            String username,
            String name,
            String surname,
            LocalDate birthday,
            String gender,
            String phone,
            String email,
            String streetAddress,
            String city,
            String state,
            String zipcode,
            String country,
            String password,
            boolean isDriver
    ) throws SQLException {

        // Create and store the Address
        Address address = new Address(streetAddress, city, state, zipcode, country);

        AddressDAO.insertAddress(address);

        // Create and return the appropriate user object
        if (isDriver) {
            Member member = new Member(name, surname, gender, email, phone, birthday, address.getId(), username, password, UserType.Member, AccountStatus.Active);
            AccountDAO.insertAccount(member);
            AccountDAO.insertMember(member);
            return member;
        } else {
            Client client = new Client(name, surname, gender, email, phone, birthday, address.getId(), username, password, UserType.Client, AccountStatus.Active, LocalDate.now());
            AccountDAO.insertAccount(client);
            AccountDAO.insertClient(client);
            return client;
        }
    }
}
