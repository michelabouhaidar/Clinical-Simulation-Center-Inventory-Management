package com.example.ui;

import java.time.LocalDate;

public class MaintenanceRow {
    private final LocalDate date;
    private final String simulator;
    private final String type;
    private final String vendor;

    public MaintenanceRow(LocalDate date, String simulator, String type, String vendor) {
        this.date = date;
        this.simulator = simulator;
        this.type = type;
        this.vendor = vendor;
    }

    public LocalDate getDate()      { return date; }
    public String getSimulator()    { return simulator; }
    public String getType()         { return type; }
    public String getVendor()       { return vendor; }
}
