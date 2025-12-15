package com.example.ui;

public class LowStockRow {
    private final String consumable;
    private final String branch;
    private final Integer available;
    private final Integer reserved;

    public LowStockRow(String consumable, String branch,
                       Integer available, Integer reserved) {
        this.consumable = consumable;
        this.branch = branch;
        this.available = available;
        this.reserved = reserved;
    }

    public String getConsumable() { return consumable; }
    public String getBranch()     { return branch; }
    public Integer getAvailable() { return available; }
    public Integer getReserved()  { return reserved; }
}
