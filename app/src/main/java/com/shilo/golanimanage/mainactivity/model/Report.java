package com.shilo.golanimanage.mainactivity.model;

import java.util.List;

public class Report {
    private String description;
    private String id;
    private List<String> questionList;

    public Report(List<String> questionList) {
        this.questionList = questionList;
    }

    public List<String> getQuestionList() {
        return questionList;
    }
}
