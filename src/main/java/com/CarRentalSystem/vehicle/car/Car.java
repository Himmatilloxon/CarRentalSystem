package com.CarRentalSystem.vehicle.car;

import com.CarRentalSystem.vehicle.Vehicle;

public class Car extends Vehicle {
    private CarType carType;

    public Car() {}

    public void setCarType(CarType carType) {
        this.carType = carType;
    }

    public CarType getCarType() {
        return carType;
    }
}
