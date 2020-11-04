package com.shilo.golanimanage.model;

import com.shilo.golanimanage.mainactivity.model.Team;

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
    private Team leaderOfTeam;

    public LoggedInUser(String userId, String displayName, String role) {
        this.userId = userId;
        this.displayName = displayName;
        this.role = role;
    }

    public Team getLeaderOfTeam() {
        return leaderOfTeam;
    }

    public void setLeaderOfTeam(Team leaderOfTeam) {
        this.leaderOfTeam = leaderOfTeam;
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