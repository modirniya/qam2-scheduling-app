package edu.wgu.qam2schedulingapp.model;

import edu.wgu.qam2schedulingapp.helper.TimeHelper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

/**
 * @author Parham Modirniya
 */

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

    public static Customer fromResultSet(ResultSet rs) throws SQLException {
        Customer c = new Customer();
        c.id = rs.getInt("Customer_ID");
        c.divisionId = rs.getInt("Division_ID");
        c.name = rs.getString("Customer_Name");
        c.address = rs.getString("Address");
        c.postalCode = rs.getString("Postal_Code");
        c.phone = rs.getString("Phone");
        c.createdBy = rs.getString("Created_By");
        c.lastUpdatedBy = rs.getString("Last_Updated_By");
        c.createDate =
                TimeHelper.utcToLocalDate(rs.getTimestamp("Create_Date"));
        c.lastUpdate =
                TimeHelper.utcToLocalDate(rs.getTimestamp("Last_Update"));
        return c;
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

    @Override
    public String toString() {
        return id + " - " + name;
    }
}
