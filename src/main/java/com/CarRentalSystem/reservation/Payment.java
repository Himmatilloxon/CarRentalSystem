package com.CarRentalSystem.reservation;

import java.util.UUID;

public class Payment {
    private UUID id;
    private int bill_id;
    private double amount;
    private PaymentStatus status;
    private PaymentType type;

    public Payment() {}

    public void setId(UUID id) {
        this.id = id;
    }

    public void setBill_id(int bill_id) {
        this.bill_id = bill_id;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public void setType(PaymentType type) {
        this.type = type;
    }

    public UUID getId() {
        return id;
    }

    public int getBill_id() {
        return bill_id;
    }

    public double getAmount() {
        return amount;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public PaymentType getType() {
        return type;
    }
}
