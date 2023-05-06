package edu.wgu.qam2schedulingapp.model;

import edu.wgu.qam2schedulingapp.utility.ZoneHelper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class Customer {
    private final int id;
    private final int divisionId;
    private final String name;
    private final String address;
    private final String postalCode;
    private final String phone;
    private final String createdBy;
    private final String lastUpdatedBy;
    private final Date createDate;
    private final Date lastUpdate;

    public Customer(int id,
                    String name,
                    String phone,
                    String address,
                    String postalCode,
                    String createdBy,
                    Date createDate,
                    String lastUpdatedBy,
                    Date lastUpdate,
                    int divisionId
    ) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.postalCode = postalCode;
        this.createdBy = createdBy;
        this.createDate = createDate;
        this.lastUpdatedBy = lastUpdatedBy;
        this.lastUpdate = lastUpdate;
        this.divisionId = divisionId;
    }

    public Customer(
            int id,
            String name,
            String phone,
            String address,
            String postalCode,
            int divisionId
    ) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.postalCode = postalCode;
        this.createdBy = null;
        this.createDate = null;
        this.lastUpdatedBy = null;
        this.lastUpdate = null;
        this.divisionId = divisionId;
    }


    public static Customer fromResultSet(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("Customer_ID");
        int divisionId = resultSet.getInt("Division_ID");
        String name = resultSet.getString("Customer_Name");
        String address = resultSet.getString("Address");
        String postalCode = resultSet.getString("Postal_Code");
        String phone = resultSet.getString("Phone");
        String createdBy = resultSet.getString("Created_By");
        String lastUpdatedBy = resultSet.getString("Last_Updated_By");
        Date createDate =
                ZoneHelper.utcToSystemLocalDate(resultSet.getTimestamp("Create_Date"));
        Date lastUpdate =
                ZoneHelper.utcToSystemLocalDate(resultSet.getTimestamp("Last_Update"));

        return new Customer(
                id,
                name,
                phone,
                address,
                postalCode,
                createdBy,
                createDate,
                lastUpdatedBy,
                lastUpdate,
                divisionId);
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

}
