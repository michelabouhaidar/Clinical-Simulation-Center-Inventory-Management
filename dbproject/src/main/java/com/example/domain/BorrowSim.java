package com.example.domain;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;

@Entity
@Table(name = "BORROW_SIM")
public class BorrowSim {

    @Embeddable
    public static class Id implements Serializable {

        @Column(name = "SIMID")
        private Integer simId;

        @Column(name = "BORROWID")
        private Integer borrowId;

        public Id() {}

        public Id(Integer simId, Integer borrowId) {
            this.simId = simId;
            this.borrowId = borrowId;
        }

        public Integer getSimId() { return simId; }
        public void setSimId(Integer simId) { this.simId = simId; }

        public Integer getBorrowId() { return borrowId; }
        public void setBorrowId(Integer borrowId) { this.borrowId = borrowId; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Id)) return false;
            Id that = (Id) o;
            return Objects.equals(simId, that.simId)
                    && Objects.equals(borrowId, that.borrowId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(simId, borrowId);
        }
    }

    @EmbeddedId
    private Id id = new Id();

    @ManyToOne
    @MapsId("borrowId")                   // BORROWID part of PK
    @JoinColumn(name = "BORROWID", nullable = false)
    private Borrowing borrowing;

    @ManyToOne
    @MapsId("simId")                      // SIMID part of PK
    @JoinColumn(name = "SIMID", nullable = false)
    private Simulator simulator;

    @Column(name = "CONDOUT", length = 60)
    private String conditionOut;

    @Column(name = "CONDIN", length = 60)
    private String conditionIn;

    @Column(name = "RETURNNOTES", length = 1024)
    private String returnNotes;

    public BorrowSim() {}

    public BorrowSim(Borrowing borrowing, Simulator simulator) {
        setBorrowing(borrowing);
        setSimulator(simulator);
    }

    // ----- getters/setters -----

    public Id getId() { return id; }
    public void setId(Id id) { this.id = id; }

    public Borrowing getBorrowing() { return borrowing; }
    public void setBorrowing(Borrowing borrowing) {
        this.borrowing = borrowing;
        if (borrowing != null) {
            if (this.id == null) this.id = new Id();
            this.id.setBorrowId(borrowing.getId());
        }
    }

    public Simulator getSimulator() { return simulator; }
    public void setSimulator(Simulator simulator) {
        this.simulator = simulator;
        if (simulator != null) {
            if (this.id == null) this.id = new Id();
            this.id.setSimId(simulator.getId());
        }
    }

    public String getConditionOut() { return conditionOut; }
    public void setConditionOut(String conditionOut) { this.conditionOut = conditionOut; }

    public String getConditionIn() { return conditionIn; }
    public void setConditionIn(String conditionIn) { this.conditionIn = conditionIn; }

    public String getReturnNotes() { return returnNotes; }
    public void setReturnNotes(String returnNotes) { this.returnNotes = returnNotes; }
}
