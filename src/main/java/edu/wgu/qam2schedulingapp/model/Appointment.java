package edu.wgu.qam2schedulingapp.model;

import edu.wgu.qam2schedulingapp.utility.TimeHelper;

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
    private Date start;
    private Date end;
    private String createdBy;
    private String lastUpdatedBy;
    private Date createDate;
    private Date lastUpdate;

    public static Appointment fromResultSet(ResultSet rs) throws SQLException {
        Appointment ap = new Appointment();
        ap.id = rs.getInt("Appointment_ID");
        ap.title = rs.getString("Title");
        ap.description = rs.getString("Description");
        ap.location = rs.getString("Location");
        ap.type = rs.getString("Type");
        ap.start = TimeHelper.utcToSystemLocalDate(rs.getTimestamp("Start"));
        ap.end = TimeHelper.utcToSystemLocalDate(rs.getTimestamp("End"));
        ap.createDate =
                TimeHelper.utcToSystemLocalDate(rs.getTimestamp("Create_Date"));
        ap.createdBy = rs.getString("Created_By");
        ap.lastUpdate =
                TimeHelper.utcToSystemLocalDate(rs.getTimestamp("Last_Update"));
        ap.lastUpdatedBy = rs.getString("Last_Updated_By");
        ap.customerId = rs.getInt("Customer_ID");
        ap.userId = rs.getInt("User_ID");
        ap.contactId = rs.getInt("Contact_ID");
        return ap;
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
        return "Appointment{" +
               "id=" + id +
               ", customerId=" + customerId +
               ", userId=" + userId +
               ", contactId=" + contactId +
               ", title='" + title + '\'' +
               ", description='" + description + '\'' +
               ", location='" + location + '\'' +
               ", type='" + type + '\'' +
               ", start=" + start +
               ", end=" + end +
               ", createdBy='" + createdBy + '\'' +
               ", lastUpdatedBy='" + lastUpdatedBy + '\'' +
               ", createDate=" + createDate +
               ", lastUpdate=" + lastUpdate +
               '}';
    }
}
