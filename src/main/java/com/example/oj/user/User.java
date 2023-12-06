package com.example.oj.user;

import com.example.oj.constant.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.DynamicUpdate;
import java.time.LocalDateTime;
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name="user")
@DynamicUpdate
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Long id;
    @Column(name = "username", unique = true, nullable = false, updatable = false)
    String username;
    @Column(name = "password", nullable = false)
    String password;
    @Column(name = "name")
    String name;
    @Column(name = "score")
    Long score;
    @Column(name = "create_time")
    LocalDateTime createTime;
    @Column(name = "update_time")
    LocalDateTime updateTime;
//    Role role;


//    public User(Long id, String username, String password, String name, Long score, LocalDateTime creationTime) {
//        this.id = id;
//        this.username = username;
//        this.password = password;
//        this.name = name;
//        this.score = score;
//        this.creationTime = creationTime;
//    }
//
//    public void setScore(Long score) {
//        this.score = score;
//    }
//
//    public void setCreationTime(LocalDateTime creattionTime) {
//        this.creationTime = creattionTime;
//    }
//
//    public Long getScore() {
//        return score;
//    }
//
//    public LocalDateTime getCreationTime() {
//        return creationTime;
//    }
//
//    public Long getId() {
//        return id;
//    }
//
//    public void setId(Long id) {
//        this.id = id;
//    }
//
//    public String getUsername() {
//        return username;
//    }
//
//    @Override
//    public String toString() {
//        return "User{" +
//                "id=" + id +
//                ", username=" + username +
//                ", password=" + password +
//                ", name=" + name +
//                '}';
//    }
//
//    public void setUsername(String username) {
//        this.username = username;
//    }
//
//    public String getPassword() {
//        return password;
//    }
//
//    public void setPassword(String password) {
//        this.password = password;
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }
//    public User() {
//    }
}
