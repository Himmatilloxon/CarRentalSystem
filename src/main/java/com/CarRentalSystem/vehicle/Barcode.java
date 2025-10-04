package com.CarRentalSystem.vehicle;

import java.util.UUID;

public class Barcode {
    private UUID id;
    private String barcode;
    private boolean active;

    public Barcode() {}

    public Barcode(String barcode, boolean active) {
        this.barcode = barcode;
        this.active = active;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
