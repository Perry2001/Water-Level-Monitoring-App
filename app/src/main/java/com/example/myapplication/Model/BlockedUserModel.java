package com.example.myapplication.Model;

public class BlockedUserModel {
    String name;
    String email;
    String userUID;

    public BlockedUserModel(String name, String email, String userUID) {
        this.name = name;
        this.email = email;
        this.userUID = userUID;
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

    public String getUserUID() {
        return userUID;
    }

    public void setUserUID(String userUID) {
        this.userUID = userUID;
    }
}
