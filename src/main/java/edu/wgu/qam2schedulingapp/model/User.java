package edu.wgu.qam2schedulingapp.model;

public class User {
    private final int id;
    private final String username;
    private final String password;

    public User(String username, String password) {
        this.id = -1;
        this.username = username;
        this.password = password;
    }

    public User(int id, String username, String password) {
        this.id = id;
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
