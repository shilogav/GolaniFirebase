package com.shilo.myloginfirebase.model;

import com.shilo.myloginfirebase.Utility;

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */


public class LoggedInUser {

    private String userId;
    private String displayName;
    private Utility.Role role;

    public LoggedInUser(String userId, String displayName, Utility.Role role) {
        this.userId = userId;
        this.displayName = displayName;
        this.role = role;
    }

    public Utility.Role getRole() {
        return role;
    }

    public String getUserId() {
        return userId;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return "userId is" + userId + '\'' +
                ", displayName is " + displayName + '\'' +
                ", role is " + role;
    }
}