package com.example.domain;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.*;

@Entity
@Table(name = "BORROWING")
public class Borrowing extends AuditableEntity {
    @Id
    @Column(name = "BORROWID", nullable = false)
    private Integer id;

    @ManyToOne @JoinColumn(name = "BRANCHID", nullable = false)
    private Branch branch;

    @ManyToOne @JoinColumn(name = "DEPTID", nullable = false)
    private Department department;

    @Column(name = "BORROWCODE", length = 60) private String borrowCode;
    @Column(name = "STARTDATE") private LocalDate startDate;
    @Column(name = "ENDDATE")   private LocalDate endDate;
    @Column(name = "NOTES", length = 1024) private String notes;
    @Column(name = "BORROWSTATUS", length = 60) private String status;

    // Many-to-many via BorrowSim
    @OneToMany(mappedBy = "borrowing", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BorrowSim> borrowSims = new ArrayList<>();

    // Many-to-many via BorrowCons
    @OneToMany(mappedBy = "borrowing", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BorrowCons> borrowConsList = new ArrayList<>();

    public Borrowing() {}
    public Borrowing(Integer id){ this.id = id; }

    public Integer getId(){ return id; }
    public void setId(Integer id){ this.id = id; }
    public Branch getBranch(){ return branch; }
    public void setBranch(Branch branch){ this.branch = branch; }
    public Department getDepartment(){ return department; }
    public void setDepartment(Department department){ this.department = department; }
    public String getBorrowCode(){ return borrowCode; }
    public void setBorrowCode(String borrowCode){ this.borrowCode = borrowCode; }
    public LocalDate getStartDate(){ return startDate; }
    public void setStartDate(LocalDate startDate){ this.startDate = startDate; }
    public LocalDate getEndDate(){ return endDate; }
    public void setEndDate(LocalDate endDate){ this.endDate = endDate; }
    public String getNotes(){ return notes; }
    public void setNotes(String notes){ this.notes = notes; }
    public String getStatus(){ return status; }
    public void setStatus(String status){ this.status = status; }

    public List<BorrowSim> getBorrowSims(){ return borrowSims; }
    public void addBorrowSim(BorrowSim bs){ if(!borrowSims.contains(bs)){ borrowSims.add(bs); bs.setBorrowing(this);} }
    public void removeBorrowSim(BorrowSim bs){ if(borrowSims.remove(bs) && bs.getBorrowing()==this) bs.setBorrowing(null); }

    public List<BorrowCons> getBorrowConsList(){ return borrowConsList; }
    public void addBorrowCons(BorrowCons bc){ if(!borrowConsList.contains(bc)){ borrowConsList.add(bc); bc.setBorrowing(this);} }
    public void removeBorrowCons(BorrowCons bc){ if(borrowConsList.remove(bc) && bc.getBorrowing()==this) bc.setBorrowing(null); }
}
