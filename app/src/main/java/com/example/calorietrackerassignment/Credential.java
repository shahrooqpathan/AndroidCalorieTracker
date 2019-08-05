package com.example.calorietrackerassignment;

public class Credential {
    private AppUser appUser;
    private String passwordHash;
    private String signupDate;
    private String userId;
    private AppUser username;

    public Credential(AppUser appUser, String passwordHash, String signupDate, String userId, AppUser username) {
        this.appUser = appUser;
        this.passwordHash = passwordHash;
        this.signupDate = signupDate;
        this.userId = userId;
        this.username = username;
    }

    public AppUser getAppUser() {
        return appUser;
    }

    public void setAppUser(AppUser appUser) {
        this.appUser = appUser;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getSignupDate() {
        return signupDate;
    }

    public void setSignupDate(String signupDate) {
        this.signupDate = signupDate;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public AppUser getUsername() {
        return username;
    }

    public void setUsername(AppUser username) {
        this.username = username;
    }
}
