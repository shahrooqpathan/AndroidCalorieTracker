package com.example.calorietrackerassignment;

import java.math.BigDecimal;
import java.util.Date;

public class AppUser {
    private String userId;
    private String name;
    private String surname;
    private String email;
    private String dob;
    private String height;
    private String weight;
    private String address;
    private String gender;
    private String levelOfActivity;
    private String postcode;
    private String stepsPerMile;

    public AppUser(String userId, String name, String surname, String email, String dob, String height, String weight, String address, String gender, String levelOfActivity, String postcode, String stepsPerMile) {
        this.userId = userId;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.dob = dob;
        this.height = height;
        this.weight = weight;
        this.address = address;
        this.gender = gender;
        this.levelOfActivity = levelOfActivity;
        this.postcode = postcode;
        this.stepsPerMile = stepsPerMile;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getLevelOfActivity() {
        return levelOfActivity;
    }

    public void setLevelOfActivity(String levelOfActivity) {
        this.levelOfActivity = levelOfActivity;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public String getStepsPerMile() {
        return stepsPerMile;
    }

    public void setStepsPerMile(String stepsPerMile) {
        this.stepsPerMile = stepsPerMile;
    }
}
