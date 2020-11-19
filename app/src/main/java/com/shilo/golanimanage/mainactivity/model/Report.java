package com.shilo.golanimanage.mainactivity.model;

import java.util.List;

import androidx.annotation.Nullable;

public class Report implements Comparable<Report> {
    private String description;
    private String id;
    private List<Question> questionList;
    private String idLeader;
    //private boolean lockToEdit;
    private String idSoldier;

    public Report() {
    }

    public Report(String description, String id, List<Question> questionList, String idLeader, String idSoldier) {
        this.description = description;
        this.id = id;
        this.questionList = questionList;
        this.idLeader = idLeader;
        this.idSoldier = idSoldier;
    }

    /*public boolean isLockToEdit() {
        return lockToEdit;
    }

    public void setLockToEdit(boolean lockToEdit) {
        this.lockToEdit = lockToEdit;
    }*/

    public String getIdLeader() {
        return idLeader;
    }

    public void setIdLeader(String idLeader) {
        this.idLeader = idLeader;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdSoldier() {
        return idSoldier;
    }

    public void setIdSoldier(String idSoldier) {
        this.idSoldier = idSoldier;
    }

    public List<Question> getQuestionList() {
        return questionList;
    }

    public void setQuestionList(List<Question> questionList) {
        this.questionList = questionList;
    }

    @Override
    public int compareTo(Report o) {
        if (id.equals(o.getId())){
              return 0;
        }
        return -1;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return id.equals(((Report) obj).getId());
    }
}
