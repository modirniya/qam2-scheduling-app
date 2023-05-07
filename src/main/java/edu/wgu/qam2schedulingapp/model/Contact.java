package edu.wgu.qam2schedulingapp.model;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Contact {
    private int id;
    private String name;
    private String email;

    public static Contact fromResultSet(ResultSet rs) throws SQLException {
        Contact contact = new Contact();
        contact.id = rs.getInt("Contact_ID");
        contact.name = rs.getString("Contact_Name");
        contact.email = rs.getString("Email");
        return contact;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
