package com.CarRentalSystem.vehicle.truck;

import com.CarRentalSystem.vehicle.Vehicle;

public class Truck extends Vehicle {
    private TruckType type;

    public Truck() {}

    public void setType(TruckType type) {
        this.type = type;
    }

    public TruckType getType() {
        return type;
    }
}
