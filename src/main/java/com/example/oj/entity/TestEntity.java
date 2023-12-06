package com.example.oj.entity;

public class TestEntity {
    @Override
    public String toString() {
        return "TestEntity{" +
                "t1=" + t1 +
                ", t2='" + t2 + '\'' +
                ", t3=" + t3 +
                '}';
    }

    public TestEntity() {
    }

    public TestEntity(int t1, String t2, double t3) {
        this.t1 = t1;
        this.t2 = t2;
        this.t3 = t3;
    }

    public int getT1() {
        return t1;
    }

    public void setT1(int t1) {
        this.t1 = t1;
    }

    public String getT2() {
        return t2;
    }

    public void setT2(String t2) {
        this.t2 = t2;
    }

    public double getT3() {
        return t3;
    }

    public void setT3(double t3) {
        this.t3 = t3;
    }

    int t1;
    String t2;
    double t3;
}
