package com.shilo.golanimanage.mainactivity.model;

import com.google.firebase.firestore.FirebaseFirestore;
import com.shilo.golanimanage.model.LoggedInUser;

import java.util.List;

public class Team {
    public static final String RESHEF = "Reshef";
    public static final String KEREN = "Keren";
    public static final String DRAKON = "Drakon";
    public static final String NAMER = "Namer";


    private String id;
    private String name;
    private List<Soldier> crew;
    private LoggedInUser[] TeamLeader;

    public Team() {
    }

    public Team(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public List<Soldier> getCrew() {
        return crew;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setCrew(List<Soldier> crew) {
        this.crew = crew;
    }

    public LoggedInUser[] getTeamLeader() {
        return TeamLeader;
    }

    public void setTeamLeader(LoggedInUser[] teamLeader) {
        TeamLeader = teamLeader;
    }
}
