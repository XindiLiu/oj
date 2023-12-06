package com.example.oj.constant;

public enum ProgrammingLanguage {
    CPP("C++"),
    C("C");
    public final String label;
    private ProgrammingLanguage(String label) {
        this.label = label;
    }
}
