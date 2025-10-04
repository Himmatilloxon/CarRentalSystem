package com.CarRentalSystem.vehicle;

import java.util.UUID;

public class Vehicle {
    private UUID id;
    private UUID rentalId;
    private String licenseNumber;
    private int passengerCapacity;
    private boolean hasSunroof;
    private VehicleStatus status;
    private String make;
    private String model;
    private int manufactureYear;
    private int mileage;
    private int parkingStallId;
    private double price;

    public Vehicle() {}

    public void setId(UUID id) {
        this.id = id;
    }

    public void setRentalId(UUID rentalId) {
        this.rentalId = rentalId;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public void setPassengerCapacity(int passengerCapacity) {
        this.passengerCapacity = passengerCapacity;
    }

    public void setHasSunroof(boolean hasSunroof) {
        this.hasSunroof = hasSunroof;
    }

    public void setStatus(VehicleStatus status) {
        this.status = status;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public void setManufactureYear(int manufactureYear) {
        this.manufactureYear = manufactureYear;
    }

    public void setMileage(int mileage) {
        this.mileage = mileage;
    }

    public void setParkingStallId(int parkingStallId) {
        this.parkingStallId = parkingStallId;
    }

    public UUID getId() {
        return id;
    }

    public UUID getRentalId() {
        return rentalId;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public int getPassengerCapacity() {
        return passengerCapacity;
    }

    public boolean isHasSunroof() {
        return hasSunroof;
    }

    public VehicleStatus getStatus() {
        return status;
    }

    public String getMake() {
        return make;
    }

    public String getModel() {
        return model;
    }

    public int getManufactureYear() {
        return manufactureYear;
    }

    public int getMileage() {
        return mileage;
    }

    public int getParkingStallId() {
        return parkingStallId;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
