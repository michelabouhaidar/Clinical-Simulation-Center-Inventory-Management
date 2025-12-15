package com.example.domain;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "BRANCH")
public class Branch {
    @Id
    @Column(name = "BRANCHID", nullable = false)
    private Integer id;

    @Column(name = "NAME", nullable = false, length = 120)
    private String name;

    @Column(name = "LOCATION", length = 200)
    private String location;

    @OneToMany(mappedBy = "branch")
	private List<Simulator> simulators = new ArrayList<>();

	@OneToMany(mappedBy = "branch")
	private List<Stock>     stocks     = new ArrayList<>();

	@OneToMany(mappedBy = "branch")
	private List<Borrowing> borrowings = new ArrayList<>();


    public Branch() {}
    public Branch(Integer id) { this.id = id; }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public List<Simulator> getSimulators() { return simulators; }
    public List<Stock> getStocks() { return stocks; }
    public List<Borrowing> getBorrowings() { return borrowings; }

    public void addSimulator(Simulator s){
		if(!simulators.contains(s)){
			simulators.add(s); s.setBranch(this);
		}
	}
    public void removeSimulator(Simulator s){
		if(simulators.remove(s) && s.getBranch()==this) s.setBranch(null);
		 }
    public void addStock(Stock st){
		if(!stocks.contains(st)){ stocks.add(st);
		 st.setBranch(this);
		}
	}
    public void removeStock(Stock st){
		if(stocks.remove(st) && st.getBranch()==this)
			st.setBranch(null);
	}
    public void addBorrowing(Borrowing b){
		if(!borrowings.contains(b)){ borrowings.add(b);
			b.setBranch(this);
		}
	}
    public void removeBorrowing(Borrowing b){
		if(borrowings.remove(b) && b.getBranch()==this)
			b.setBranch(null);
	}
}
