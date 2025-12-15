package com.example.domain;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "DEPARTMENT")
public class Department {
    @Id
    @Column(name = "DEPTID", nullable = false)
    private Integer id;

    @Column(name = "DEPTNAME", length = 60)
    private String name;

    @Column(name = "CONTACTNAME", length = 60)
    private String contactName;

    @Column(name = "PHONE1", length = 12)
    private String phone1;

    @Column(name = "PHONE2", length = 12)
    private String phone2;

    @Column(name = "EMAIL", length = 120)
    private String email;

    @OneToMany(mappedBy = "department")
    private List<Borrowing> borrowings = new ArrayList<>();

    public Department() {}
    public Department(Integer id){ this.id = id; }

    public Integer getId(){ return id; }
    public void setId(Integer id){ this.id = id; }
    public String getName(){ return name; }
    public void setName(String name){ this.name = name; }
    public String getContactName(){ return contactName; }
    public void setContactName(String contactName){ this.contactName = contactName; }
    public String getPhone1(){ return phone1; }
    public void setPhone1(String phone1){ this.phone1 = phone1; }
    public String getPhone2(){ return phone2; }
    public void setPhone2(String phone2){ this.phone2 = phone2; }
    public String getEmail(){ return email; }
    public void setEmail(String email){ this.email = email; }

    public List<Borrowing> getBorrowings(){return borrowings; }
    public void addBorrowing(Borrowing b){
		if(!borrowings.contains(b)){ borrowings.add(b);
			b.setDepartment(this);
		}
	}
    public void removeBorrowing(Borrowing b){
		if(borrowings.remove(b) && b.getDepartment()==this)
			b.setDepartment(null);
	}
}
