package edu.wgu.qam2schedulingapp.model;

import java.sql.ResultSet;
import java.sql.SQLException;

public class User {
    private int id;
    private String username;
    private String password;

    public static User fromResultSet(ResultSet rs) throws SQLException {
        User u = new User();
        u.setId(rs.getInt("User_ID"));
        u.setPassword(rs.getString("Password"));
        u.setUsername(rs.getString("User_Name"));
        return u;
    }

    public static User withOnlyId(int userId) {
        User u = new User();
        u.setId(userId);
        return u;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return String.valueOf(id);
    }
}
