package com.example.domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "STOCK")
public class Stock extends AuditableEntity {
    @Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "STOCKID", nullable = false)
    private Integer id;

    @ManyToOne @JoinColumn(name = "BRANCHID", nullable = false)
    private Branch branch;

    @ManyToOne @JoinColumn(name = "CONSID", nullable = false)
    private Consumable consumable;

    @Column(name = "AVAILABLEQ")  private Integer availableQuantity;
    @Column(name = "RESERVEDQ")   private Integer reservedQuantity;
    @Column(name = "LASTCOUNTDATE") private LocalDate lastCountDate;

    // Many-to-many via link entity BorrowCons
    @OneToMany(mappedBy = "stock", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BorrowCons> borrowConsList = new ArrayList<>();

    public Stock() {}
    public Stock(Integer id){ this.id = id; }

    public Integer getId(){ return id; }
    public void setId(Integer id){ this.id = id; }
    public Branch getBranch(){ return branch; }
    public void setBranch(Branch branch){ this.branch = branch; }
    public Consumable getConsumable(){ return consumable; }
    public void setConsumable(Consumable consumable){ this.consumable = consumable; }
    public Integer getAvailableQuantity(){ return availableQuantity; }
    public void setAvailableQuantity(Integer availableQuantity){ this.availableQuantity = availableQuantity; }
    public Integer getReservedQuantity(){ return reservedQuantity; }
    public void setReservedQuantity(Integer reservedQuantity){ this.reservedQuantity = reservedQuantity; }
    public LocalDate getLastCountDate(){ return lastCountDate; }
    public void setLastCountDate(LocalDate lastCountDate){ this.lastCountDate = lastCountDate; }

    public List<BorrowCons> getBorrowConsList(){ return borrowConsList; }
    public void addBorrowCons(BorrowCons bc){ if(!borrowConsList.contains(bc)){ borrowConsList.add(bc); bc.setStock(this);} }
    public void removeBorrowCons(BorrowCons bc){ if(borrowConsList.remove(bc) && bc.getStock()==this) bc.setStock(null); }
}
