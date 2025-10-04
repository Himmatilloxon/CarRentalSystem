package com.CarRentalSystem.vehicle.motorcycle;

import com.CarRentalSystem.vehicle.Vehicle;

import java.util.UUID;

public class Motorcycle extends Vehicle {
    private MotorcycleType motorcycleType;

    public Motorcycle() {}

    public void setMotorcycleType(MotorcycleType motorcycleType) {
        this.motorcycleType = motorcycleType;
    }

    public MotorcycleType getMotorcycleType() {
        return motorcycleType;
    }
}
