package com.CarRentalSystem.vehicle.suv;

import com.CarRentalSystem.vehicle.Vehicle;

import java.util.UUID;

public class SUV extends Vehicle {
    private SUVType type;

    public SUV() {}

    public void setType(SUVType type) {
        this.type = type;
    }

    public SUVType getType() {
        return type;
    }
}
