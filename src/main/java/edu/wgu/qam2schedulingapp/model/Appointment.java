package edu.wgu.qam2schedulingapp.model;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class Appointment {
    private int id;
    private int customerId;
    private int userId;
    private int contactId;
    private String title;
    private String description;
    private String location;
    private String type;
    private String createdBy;
    private String lastUpdatedBy;
    private Date start;
    private Date end;
    private Date createdDate;
    private Date lastUpdate;

    public static Appointment fromResultSet(ResultSet resultSet) throws SQLException {
        Appointment appointment = new Appointment();
        appointment.id = resultSet.getInt("Appointment_ID");
        appointment.title = resultSet.getString("Title");
        appointment.description = resultSet.getString("Description");
        appointment.location = resultSet.getString("Location");
        appointment.type = resultSet.getString("Type");
        appointment.start = resultSet.getTimestamp("Start");
        appointment.end = resultSet.getTimestamp("End");
        appointment.createdDate = resultSet.getTimestamp("Create_Date");
        appointment.createdBy = resultSet.getString("Created_By");
        appointment.lastUpdate = resultSet.getTimestamp("Last_Update");
        appointment.lastUpdatedBy = resultSet.getString("Last_Updated_By");
        appointment.customerId = resultSet.getInt("Customer_ID");
        appointment.userId = resultSet.getInt("User_ID");
        appointment.contactId = resultSet.getInt("Contact_ID");
        return appointment;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getContactId() {
        return contactId;
    }

    public void setContactId(int contactId) {
        this.contactId = contactId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }
}
