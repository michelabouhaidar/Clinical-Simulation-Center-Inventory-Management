package com.example.domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;


@Entity
@Table(name = "SIMULATOR")
public class Simulator extends AuditableEntity {

    @Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SIMID", nullable = false)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "BRANCHID", nullable = false)
    private Branch branch;

    @ManyToOne
    @JoinColumn(name = "MODELID", nullable = false)
    private SimulatorModel model;

    @Column(name = "TAG", nullable = false, length = 60)
    private String tag;

    @Column(name = "SN", length = 60)
    private String serialNumber;

    @Column(name = "SIMSTATUS", length = 60)
    private String status;

    @Column(name = "CONDNOTES", length = 1024)
    private String conditionNotes;

    @Column(name = "CALDATE")
    private LocalDate calibrationDate;

    @Column(name = "NEXTCALDATE")
    private LocalDate nextCalibrationDate;

    @OneToMany(mappedBy = "simulator", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BorrowSim> borrowSims = new ArrayList<>();

    @OneToMany(mappedBy = "simulator", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Maintained> maintainedEvents = new ArrayList<>();

    @ManyToMany
    @JoinTable(
        name = "BORROW_SIM",
        joinColumns = @JoinColumn(name = "SIMID"),
        inverseJoinColumns = @JoinColumn(name = "BORROWID")
    )
    private Set<Borrowing> borrowingsView = new HashSet<>();

    @ManyToMany
    @JoinTable(
        name = "MAINTAINED",
        joinColumns = @JoinColumn(name = "SIMID"),
        inverseJoinColumns = @JoinColumn(name = "EVENTID")
    )
    private Set<Maintenance> maintenancesView = new HashSet<>();

    public Simulator() {}
    public Simulator(Integer id) { this.id = id; }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Branch getBranch() { return branch; }
    public void setBranch(Branch branch) { this.branch = branch; }

    public SimulatorModel getModel() { return model; }
    public void setModel(SimulatorModel model) { this.model = model; }

    public String getTag() { return tag; }
    public void setTag(String tag) { this.tag = tag; }

    public String getSerialNumber() { return serialNumber; }
    public void setSerialNumber(String serialNumber) { this.serialNumber = serialNumber; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getConditionNotes() { return conditionNotes; }
    public void setConditionNotes(String conditionNotes) { this.conditionNotes = conditionNotes; }

    public LocalDate getCalibrationDate() { return calibrationDate; }
    public void setCalibrationDate(LocalDate calibrationDate) { this.calibrationDate = calibrationDate; }

    public LocalDate getNextCalibrationDate() { return nextCalibrationDate; }
    public void setNextCalibrationDate(LocalDate nextCalibrationDate) { this.nextCalibrationDate = nextCalibrationDate; }

    public List<BorrowSim> getBorrowSims() { return borrowSims; }
    public List<Maintained> getMaintainedEvents() { return maintainedEvents; }

    public Set<Borrowing> getBorrowingsView() { return Collections.unmodifiableSet(borrowingsView); }
    public Set<Maintenance> getMaintenancesView() { return Collections.unmodifiableSet(maintenancesView); }

    public void addBorrowSim(BorrowSim bs) {
        if (bs == null) return;
        if (!borrowSims.contains(bs)) {
            borrowSims.add(bs);
            bs.setSimulator(this);
        }
    }
    public void removeBorrowSim(BorrowSim bs) {
        if (bs == null) return;
        if (borrowSims.remove(bs) && bs.getSimulator() == this) {
            bs.setSimulator(null);
        }
    }

    public void addMaintained(Maintained m) {
        if (m == null) return;
        if (!maintainedEvents.contains(m)) {
            maintainedEvents.add(m);
            m.setSimulator(this);
        }
    }
    public void removeMaintained(Maintained m) {
        if (m == null) return;
        if (maintainedEvents.remove(m) && m.getSimulator() == this) {
            m.setSimulator(null);
        }
    }
	
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Simulator)) return false;
        Simulator that = (Simulator) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public String toString() {
        return "Simulator{id=" + id + ", tag='" + tag + "'}";
    }
}
