package com.example.ui;

public class OverviewStats {
    private final long totalSimulators;
    private final long activeBorrowings;
    private final long upcomingCalibrations;

    public OverviewStats(long totalSimulators,
                         long activeBorrowings,
                         long upcomingCalibrations) {
        this.totalSimulators = totalSimulators;
        this.activeBorrowings = activeBorrowings;
        this.upcomingCalibrations = upcomingCalibrations;
    }

    public long getTotalSimulators()     { return totalSimulators; }
    public long getActiveBorrowings()    { return activeBorrowings; }
    public long getUpcomingCalibrations(){ return upcomingCalibrations; }
}
