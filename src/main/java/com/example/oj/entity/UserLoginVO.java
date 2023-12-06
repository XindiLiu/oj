package com.example.oj.entity;

public class UserLoginVO {
    Long id;
    String name;

    public UserLoginVO(Long id, String name, String token) {
        this.id = id;
        this.name = name;
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    String token;

    @Override
    public String toString() {
        return "UserVO{" +
                "id=" + id +
                ", name=" + name +
                '}';
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UserLoginVO(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public UserLoginVO() {
    }
}
