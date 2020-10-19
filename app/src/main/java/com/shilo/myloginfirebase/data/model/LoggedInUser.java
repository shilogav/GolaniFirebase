package com.shilo.myloginfirebase.data.model;

import com.shilo.myloginfirebase.data.Utility;

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
}