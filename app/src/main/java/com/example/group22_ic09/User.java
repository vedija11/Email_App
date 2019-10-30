package com.example.group22_ic09;

public class User {
    String status, token, user_id, user_email, user_fname, user_lname,user_role;

    public User() {
    }

    @Override
    public String toString() {
        return "User{" +
                "status='" + status + '\'' +
                ", token='" + token + '\'' +
                ", user_id='" + user_id + '\'' +
                ", user_email='" + user_email + '\'' +
                ", user_fname='" + user_fname + '\'' +
                ", user_lname='" + user_lname + '\'' +
                ", user_role='" + user_role + '\'' +
                '}';
    }
}
