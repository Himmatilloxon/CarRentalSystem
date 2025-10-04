package com.CarRentalSystem.reservation;

import java.util.Date;
import java.util.UUID;

public class VehicleReservation {

    private UUID id;
    private int reservationNumber;
    private ReservationStatus status;
    private Date dueDate;
    private Date returnDate;
    private UUID vehicleId;

    public VehicleReservation() {}

    public void setId(UUID id) {
        this.id = id;
    }

    public void setReservationNumber(int reservationNumber) {
        this.reservationNumber = reservationNumber;
    }

    public void setStatus(ReservationStatus status) {
        this.status = status;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public void setReturnDate(Date returnDate) {
        this.returnDate = returnDate;
    }

    public void setVehicleId(UUID vehicleId) {
        this.vehicleId = vehicleId;
    }

    public UUID getId() {
        return id;
    }

    public int getReservationNumber() {
        return reservationNumber;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public Date getReturnDate() {
        return returnDate;
    }

    public UUID getVehicleId() {
        return vehicleId;
    }
}
