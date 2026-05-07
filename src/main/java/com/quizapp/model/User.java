package com.quizapp.model;

import java.time.LocalDateTime;

public class User extends Person {

    private String passwordHash;
    private LocalDateTime createdAt;
    private SkillProfile skillProfile;

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public SkillProfile getSkillProfile() {
        return skillProfile;
    }

    public void setSkillProfile(SkillProfile skillProfile) {
        this.skillProfile = skillProfile;
    }
}
