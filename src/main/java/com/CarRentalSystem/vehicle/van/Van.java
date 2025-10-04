package com.CarRentalSystem.vehicle.van;

import com.CarRentalSystem.vehicle.Vehicle;

import java.util.UUID;

public class Van extends Vehicle {
    private VanType vanType;

    public Van() {}

    public void setVanType(VanType vanType) {
        this.vanType = vanType;
    }

    public VanType getVanType() {
        return vanType;
    }
}
