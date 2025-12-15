package com.example.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "USERS")
public class User {

    @Id
    @Column(name = "USERID", nullable = false)
    private Integer id;

    @Column(name = "USERNAME", length = 20)
    private String username;

    @Column(name = "DISPLAYNAME", length = 80)
    private String displayName;

    @Column(name = "ROLE", length = 20)
    private String role;

    @Column(name = "PASSHASH", length = 150)
    private String passHash;

    @Column(name = "IS_ACTIVE")
    private Boolean isActive;

    @Column(name = "USERMAIL", length = 120)
    private String email;

    // FK → BRANCH.BranchID
    @ManyToOne
    @JoinColumn(name = "BranchID")
    private Branch branch;

    // reset tinyint(1) NOT NULL
    @Column(name = "reset", nullable = false)
    private Boolean reset;

    @OneToMany(mappedBy = "createdBy")
    private List<Simulator> simulatorsCreated = new ArrayList<>();

    @OneToMany(mappedBy = "updatedBy")
    private List<Simulator> simulatorsUpdated = new ArrayList<>();

    @OneToMany(mappedBy = "createdBy")
    private List<Stock> stocksCreated = new ArrayList<>();

    @OneToMany(mappedBy = "updatedBy")
    private List<Stock> stocksUpdated = new ArrayList<>();

    @OneToMany(mappedBy = "createdBy")
    private List<Maintenance> maintCreated = new ArrayList<>();

    @OneToMany(mappedBy = "updatedBy")
    private List<Maintenance> maintUpdated = new ArrayList<>();

    @OneToMany(mappedBy = "createdBy")
    private List<Borrowing> borrowCreated = new ArrayList<>();

    @OneToMany(mappedBy = "updatedBy")
    private List<Borrowing> borrowUpdated = new ArrayList<>();

    // -------------------------------------------------
    // Constructors
    // -------------------------------------------------
    public User() {}

    public User(Integer id) {
        this.id = id;
    }

    // -------------------------------------------------
    // Getters / Setters (scalar fields)
    // -------------------------------------------------
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getPassHash() {
        return passHash;
    }

    public void setPassHash(String passHash) {
        this.passHash = passHash;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean active) {
        isActive = active;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Branch getBranch() {
        return branch;
    }

    public void setBranch(Branch branch) {
        this.branch = branch;
    }

    public Boolean getReset() {
        return reset;
    }

    public void setReset(Boolean reset) {
        this.reset = reset;
    }

    // -------------------------------------------------
    // Getters / Setters (collections)
    // -------------------------------------------------
    public List<Simulator> getSimulatorsCreated() {
        return simulatorsCreated;
    }

    public void setSimulatorsCreated(List<Simulator> simulatorsCreated) {
        this.simulatorsCreated = simulatorsCreated;
    }

    public List<Simulator> getSimulatorsUpdated() {
        return simulatorsUpdated;
    }

    public void setSimulatorsUpdated(List<Simulator> simulatorsUpdated) {
        this.simulatorsUpdated = simulatorsUpdated;
    }

    public List<Stock> getStocksCreated() {
        return stocksCreated;
    }

    public void setStocksCreated(List<Stock> stocksCreated) {
        this.stocksCreated = stocksCreated;
    }

    public List<Stock> getStocksUpdated() {
        return stocksUpdated;
    }

    public void setStocksUpdated(List<Stock> stocksUpdated) {
        this.stocksUpdated = stocksUpdated;
    }

    public List<Maintenance> getMaintCreated() {
        return maintCreated;
    }

    public void setMaintCreated(List<Maintenance> maintCreated) {
        this.maintCreated = maintCreated;
    }

    public List<Maintenance> getMaintUpdated() {
        return maintUpdated;
    }

    public void setMaintUpdated(List<Maintenance> maintUpdated) {
        this.maintUpdated = maintUpdated;
    }

    public List<Borrowing> getBorrowCreated() {
        return borrowCreated;
    }

    public void setBorrowCreated(List<Borrowing> borrowCreated) {
        this.borrowCreated = borrowCreated;
    }

    public List<Borrowing> getBorrowUpdated() {
        return borrowUpdated;
    }

    public void setBorrowUpdated(List<Borrowing> borrowUpdated) {
        this.borrowUpdated = borrowUpdated;
    }
}
