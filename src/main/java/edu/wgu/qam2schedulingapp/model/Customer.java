package edu.wgu.qam2schedulingapp.model;

import edu.wgu.qam2schedulingapp.utility.ZoneHelper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class Customer {
    private int id;
    private int divisionId;
    private String name;
    private String address;
    private String postalCode;
    private String phone;
    private String createdBy;
    private String lastUpdatedBy;
    private Date createDate;
    private Date lastUpdate;

    public static Customer fromResultSet(ResultSet resultSet) throws SQLException {
        Customer customer = new Customer();
        customer.id = resultSet.getInt("Customer_ID");
        customer.divisionId = resultSet.getInt("Division_ID");
        customer.name = resultSet.getString("Customer_Name");
        customer.address = resultSet.getString("Address");
        customer.postalCode = resultSet.getString("Postal_Code");
        customer.phone = resultSet.getString("Phone");
        customer.createdBy = resultSet.getString("Created_By");
        customer.lastUpdatedBy = resultSet.getString("Last_Updated_By");
        customer.createDate =
                ZoneHelper.utcToSystemLocalDate(resultSet.getTimestamp("Create_Date"));
        customer.lastUpdate =
                ZoneHelper.utcToSystemLocalDate(resultSet.getTimestamp("Last_Update"));
        return customer;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDivisionId() {
        return divisionId;
    }

    public void setDivisionId(int divisionId) {
        this.divisionId = divisionId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    public void setLastUpdatedBy(String lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }
}
