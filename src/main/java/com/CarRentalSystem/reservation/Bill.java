package com.CarRentalSystem.reservation;

import java.util.UUID;

public class Bill {
    private int id;
    private UUID reservationID;
    private double vehicleBill;
    private double serviceBill;
    private double equipmentBill;
    private double totalBill;

    public Bill() {
        this.serviceBill = 0.0;
        this.equipmentBill = 0.0;
    }

    public void addEquipmentBill(double equipmentBill) {
        this.equipmentBill += equipmentBill;
    }

    public void addServiceBill(double serviceBill) {
        this.serviceBill += serviceBill;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setReservationID(UUID reservationID) {
        this.reservationID = reservationID;
    }

    public void setVehicleBill(double vehicleBill) {
        this.vehicleBill = vehicleBill;
    }

    public void setServiceBill(double serviceBill) {
        this.serviceBill = serviceBill;
    }

    public void setEquipmentBill(double equipmentBill) {
        this.equipmentBill = equipmentBill;
    }

    public void setTotalBill(double totalBill) {
        this.totalBill = totalBill;
    }

    public int getId() {
        return id;
    }

    public UUID getReservationID() {
        return reservationID;
    }

    public double getVehicleBill() {
        return vehicleBill;
    }

    public double getServiceBill() {
        return serviceBill;
    }

    public double getEquipmentBill() {
        return equipmentBill;
    }

    public double getTotalBill() {
        return totalBill;
    }
}
