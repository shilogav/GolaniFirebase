package com.shilo.myloginfirebase.model;

import com.shilo.myloginfirebase.Utility;

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */

public class LoggedInUser {

    public static final String ADMIN = "A";
    public static final String COMMAND = "B";
    public static final String TEAM_LEADER = "C";




    private String userId;
    private String displayName;
    private String role;
    private String leaderOfTeam;

    public LoggedInUser(String userId, String displayName, String role) {
        this.userId = userId;
        this.displayName = displayName;
        this.role = role;
    }

    public String getRole() {
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