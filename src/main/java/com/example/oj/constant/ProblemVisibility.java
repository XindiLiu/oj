package com.example.oj.constant;

public enum ProblemVisibility {
    PUBLIC("public"),
    PRIVATE("private");
    private String value;
    public String value() {
        return value;
    }
    // 构造方法不能是公共的
    ProblemVisibility(String value) {
        this.value = value;
    }

}
