package com.CarRentalSystem.users;

import java.time.LocalDate;
import java.util.UUID;

public class Person {
    private UUID id;
    private String name;
    private String surname;
    private Gender gender;
    private String email;
    private String phone;
    private LocalDate birthday;
    private UUID addressId; // Foreign key to Location table

    // Default constructor (required for frameworks like Hibernate or Jackson)
    public Person() {}

    // Constructor with all fields
    public Person(String name, String surname, String gender, String email, String phone, LocalDate birthday, UUID addressId) {
        this.name = name;
        this.surname = surname;
        this.gender = Gender.valueOf(gender);
        this.email = email;
        this.phone = phone;
        this.birthday = birthday;
        this.addressId = addressId;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public UUID getAddressId() {
        return addressId;
    }

    public void setAddressId(UUID addressId) {
        this.addressId = addressId;
    }

    public void setGender(String gender) {
        this.gender = Gender.valueOf(gender);
    }

    public Gender getGender() {
        return gender;
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }

    public LocalDate getBirthday() {
        return birthday;
    }
}
