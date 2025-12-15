package com.example.session;

public final class LoggedInUser {

    private Integer id;
    private String username;
    private String displayName;
    private String role;
    private String email;
    private String branchName;

    public LoggedInUser(Integer id,
                        String username,
                        String displayName,
                        String role,
                        String email,
                        String branchName) {
        this.id = id;
        this.username = username;
        this.displayName = displayName;
        this.role = role;
        this.email = email;
        this.branchName = branchName;
    }

    // ----- getters -----
    public Integer getId()        { return id; }
    public String getUsername()   { return username; }
    public String getDisplayName(){ return displayName; }
    public String getRole()       { return role; }
    public String getEmail()      { return email; }
    public String getBranchName() { return branchName; }

    // ----- setters (only what you really need) -----
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
