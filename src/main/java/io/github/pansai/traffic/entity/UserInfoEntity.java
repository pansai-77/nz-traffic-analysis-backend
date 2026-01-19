package io.github.pansai.traffic.entity;

import io.github.pansai.traffic.enums.UserStatus;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_info", schema = "public")
public class UserInfoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "user_name", nullable = false, length = 100)
    private String userName;

    /**
     * Important: store BCrypt hash only.
     */
    @Column(name = "user_pwd", nullable = false, length = 128)
    private String userPwdHash;

    @Column(name = "user_email", nullable = false, unique = true, length = 100)
    private String userEmail;

    @Column(name = "user_birthdate")
    private LocalDate userBirthdate;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_status", nullable = false,length = 20)
    private UserStatus userStatus; // status:PENDING or ACTIVE

    @Column(name = "create_time")
    private LocalDateTime createTime;

    @Column(name = "update_time")
    private LocalDateTime updateTime;

    @PrePersist
    public void prePersist(){
        var now = LocalDateTime.now();
        this.createTime = now;
        this.updateTime = now;
    }

    @PreUpdate
    public void preUpdate(){
        this.updateTime = LocalDateTime.now();
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPwdHash() {
        return userPwdHash;
    }

    public void setUserPwdHash(String userPwdHash) {
        this.userPwdHash = userPwdHash;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public LocalDate getUserBirthdate() {
        return userBirthdate;
    }

    public void setUserBirthdate(LocalDate userBirthdate) {
        this.userBirthdate = userBirthdate;
    }

    public UserStatus getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(UserStatus userStatus) {
        this.userStatus = userStatus;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }
}
