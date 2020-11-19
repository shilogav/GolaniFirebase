package com.shilo.golanimanage.model;

import com.shilo.golanimanage.mainactivity.model.Report;
import com.shilo.golanimanage.mainactivity.model.Team;

import java.util.ArrayList;

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */

public class LoggedInUser {

    public static final String ADMIN = "A";
    public static final String COMMAND = "B";
    public static final String TEAM_LEADER = "C";




    private String userId;
    private String name;
    private String role;
    private Team leaderOfTeam;
    private ArrayList<Report> reports;

    public LoggedInUser(String userId, String displayName, String role) {
        new LoggedInUser();
        this.userId = userId;
        this.name = displayName;
        this.role = role;
    }

    public LoggedInUser(String displayName) {
        new LoggedInUser();
        this.name = displayName;
    }

    public LoggedInUser() {
        reports = new ArrayList<>();
    }

    public ArrayList<Report> getReports() {
        return reports;
    }

    public void setReports(ArrayList<Report> reports) {
        this.reports = reports;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRole(String role) {
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

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "userId is" + userId + '\'' +
                ", displayName is " + name + '\'' +
                ", role is " + role;
    }
}