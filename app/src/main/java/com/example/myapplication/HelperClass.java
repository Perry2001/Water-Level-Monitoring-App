package com.example.myapplication;

public class HelperClass {
    String uid, name, phone, email, password;

    public HelperClass(String uid, String name, String email, String password) {
        this.uid = uid;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.password = password;
    }



    public HelperClass() {

    }

    public String getPhone() {

        return phone;
    }

    public void setPhone(String phone) {

        this.phone = phone;
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

    public String getPassword() {

        return password;
    }

    public void setPassword(String password) {

        this.password = password;
    }
}

