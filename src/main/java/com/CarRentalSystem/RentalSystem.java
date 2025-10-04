package com.CarRentalSystem;

import java.util.UUID;

public class RentalSystem {
    private UUID id;
    private String name;
    private UUID address_id;

    public RentalSystem() {}

    public RentalSystem(UUID id, String name, UUID address_id) {
        this.id = id;
        this.name = name;
        this.address_id = address_id;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UUID getAddress_id() {
        return address_id;
    }

    public void setAddress_id(UUID address_id) {
        this.address_id = address_id;
    }
}
