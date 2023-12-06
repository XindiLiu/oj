package com.example.oj.constant;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Judgement {

    PD,
    CE,
    RE,
    TLE,
    MLE,
    WA,
    AC;
//    @JsonValue
//    public String getJSON() {
//        return this.name();
//    }
//    PD("Pending"),
//    CE("Compile Error"),
//    RE("Runtime Error"),
//    TLE("Time Limit Exceeded"),
//    MLE("Memory Limit Exceeded"),
//    WA("Wrong Answer"),
//    AC("Accepted");
//    public final String label;
//    private Judgement(String label) {
//        this.label = label;
//    }
}
