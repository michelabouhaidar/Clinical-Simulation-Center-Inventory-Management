package com.example.domain;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import com.example.session.AppSession;
import com.example.session.LoggedInUser;

@MappedSuperclass
public abstract class AuditableEntity {

    @ManyToOne
    @JoinColumn(name = "CREATED_BY", nullable = false)
    private User createdBy;

    @Column(name = "CREATED_ON", nullable = false)
    private LocalDateTime createdOn;

    @ManyToOne
    @JoinColumn(name = "UPDATED_BY", nullable = false)
    private User updatedBy;

    @Column(name = "UPDATED_ON", nullable = false)
    private LocalDateTime updatedOn;

    // ===== Getters & Setters =====

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(LocalDateTime createdOn) {
        this.createdOn = createdOn;
    }

    public User getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(User updatedBy) {
        this.updatedBy = updatedBy;
    }

    public LocalDateTime getUpdatedOn() {
        return updatedOn;
    }

    public void setUpdatedOn(LocalDateTime updatedOn) {
        this.updatedOn = updatedOn;
    }

    // ===== JPA lifecycle callbacks =====

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();

        LoggedInUser loggedIn = null;
        try {
            // Use your real AppSession
            loggedIn = AppSession.getCurrentUser();
        } catch (Exception ignored) {
            // In case AppSession is not initialized for some reason
        }

        if (loggedIn != null) {
            Integer userId = loggedIn.getId();

            if (createdBy == null && userId != null) {
                User u = new User();
                u.setId(userId);
                this.createdBy = u;
            }

            if (updatedBy == null && userId != null) {
                User u2 = new User();
                u2.setId(userId);
                this.updatedBy = u2;
            }
        }

        if (createdOn == null) {
            this.createdOn = now;
        }

        if (updatedOn == null) {
            this.updatedOn = now;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        LocalDateTime now = LocalDateTime.now();

        LoggedInUser loggedIn = null;
        try {
            // Use your real AppSession
            loggedIn = AppSession.getCurrentUser();
        } catch (Exception ignored) {
        }

        if (loggedIn != null) {
            Integer userId = loggedIn.getId();
            if (userId != null) {
                User u = new User();
                u.setId(userId);
                this.updatedBy = u;
            }
        }

        this.updatedOn = now;
    }
}
