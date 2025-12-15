package com.example.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "CONSUMABLE")
public class Consumable {
    @Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CONSID", nullable = false)
    private Integer id;

    @Column(name = "ITEMNAME", length = 60)
    private String itemName;

    @Column(name = "MEASURE", length = 6)
    private String measure;

    @OneToMany(mappedBy = "consumable")
    private List<Stock> stocks = new ArrayList<>();

    public Consumable() {}
    public Consumable(Integer id){ this.id = id; }

    public Integer getId(){ return id; }
    public void setId(Integer id){ this.id = id; }
    public String getItemName(){ return itemName; }
    public void setItemName(String itemName){ this.itemName = itemName; }
    public String getMeasure(){ return measure; }
    public void setMeasure(String measure){ this.measure = measure; }

    public List<Stock> getStocks(){ return stocks; }
    public void addStock(Stock s){
		if(!stocks.contains(s)){ stocks.add(s);
			s.setConsumable(this);
		}
	}
    public void removeStock(Stock s){
		if(stocks.remove(s) && s.getConsumable()==this)
			s.setConsumable(null);
	}

	public String toString() {
        String name = (itemName != null ? itemName : "Consumable #" + id);
        String m    = (measure != null ? measure : "");
        return m.isEmpty() ? name : name + " (" + m + ")";
    }
}
