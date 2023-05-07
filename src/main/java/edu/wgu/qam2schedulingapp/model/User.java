package edu.wgu.qam2schedulingapp.model;

import java.sql.ResultSet;
import java.sql.SQLException;

public class User {
    private int id;
    private String username;
    private String password;

    public static User fromResultSet(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("User_ID"));
        user.setPassword(rs.getString("Password"));
        user.setUsername(rs.getString("User_Name"));
        return user;
    }

//    public User(String username, String password) {
//        this.id = -1;
//        this.username = username;
//        this.password = password;
//    }
//
//    public User(int id, String username, String password) {
//        this.id = id;
//        this.username = username;
//        this.password = password;
//    }

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
