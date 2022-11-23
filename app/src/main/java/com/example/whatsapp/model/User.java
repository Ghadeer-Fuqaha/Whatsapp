package com.example.whatsapp.model;

public class User {

    String id;
    String username;
    String email;
    String status;
    String password;
    String imgUrl;
    String state;

    public User(){

    }
    public User(String id, String username, String email, String status, String password, String imgUrl, String state) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.status = status;
        this.password = password;
        this.imgUrl = imgUrl;
        this.state = state;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
