package edu.wgu.qam2schedulingapp.model;

import java.time.ZonedDateTime;

public class Customer {
    private final int id;
    private final int divisionId;
    private final String name;
    private final String address;
    private final String postalCode;
    private final String phone;
    private final String createdBy;
    private final String lastUpdatedBy;
    private final ZonedDateTime createDate;
    private final ZonedDateTime lastUpdate;

    public Customer(int id, int divisionId, String name, String address, String postalCode, String phone, String createdBy, String lastUpdatedBy, ZonedDateTime createDate, ZonedDateTime lastUpdate) {
        this.id = id;
        this.divisionId = divisionId;
        this.name = name;
        this.address = address;
        this.postalCode = postalCode;
        this.phone = phone;
        this.createdBy = createdBy;
        this.lastUpdatedBy = lastUpdatedBy;
        this.createDate = createDate;
        this.lastUpdate = lastUpdate;
    }

    public int getId() {
        return id;
    }

    public int getDivisionId() {
        return divisionId;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public String getPhone() {
        return phone;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public String getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    public ZonedDateTime getCreateDate() {
        return createDate;
    }

    public ZonedDateTime getLastUpdate() {
        return lastUpdate;
    }
}
