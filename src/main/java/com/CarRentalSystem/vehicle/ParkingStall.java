package com.CarRentalSystem.vehicle;

import java.util.UUID;

public class ParkingStall {
    private int id;
    private UUID rental_id;
    private String stallNumber;
    private boolean available;

    public ParkingStall() {}

    public void setId(int id) {
        this.id = id;
    }

    public void setStallNumber(String stallNumber) {
        this.stallNumber = stallNumber;
    }

    public int getId() {
        return id;
    }

    public String getStallNumber() {
        return stallNumber;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setRental_id(UUID rental_id) {
        this.rental_id = rental_id;
    }

    public UUID getRental_id() {
        return rental_id;
    }
}
