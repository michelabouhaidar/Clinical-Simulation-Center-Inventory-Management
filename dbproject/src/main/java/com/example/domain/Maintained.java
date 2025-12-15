package com.example.domain;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;

@Entity
@Table(name = "MAINTAINED")
public class Maintained {

    // ============ Composite primary key: (SIMID, EVENTID) ============
    @Embeddable
    public static class Id implements Serializable {

        @Column(name = "SIMID")
        private Integer simId;

        @Column(name = "EVENTID")
        private Integer eventId;

        public Id() {}

        public Id(Integer simId, Integer eventId) {
            this.simId = simId;
            this.eventId = eventId;
        }

        public Integer getSimId() { return simId; }
        public void setSimId(Integer simId) { this.simId = simId; }

        public Integer getEventId() { return eventId; }
        public void setEventId(Integer eventId) { this.eventId = eventId; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Id)) return false;
            Id that = (Id) o;
            return Objects.equals(simId, that.simId)
                    && Objects.equals(eventId, that.eventId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(simId, eventId);
        }
    }

    @EmbeddedId
    private Id id = new Id();

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @MapsId("eventId")                      // EVENTID part of PK
    @JoinColumn(name = "EVENTID", nullable = false)
    private Maintenance maintenance;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @MapsId("simId")                        // SIMID part of PK
    @JoinColumn(name = "SIMID", nullable = false)
    private Simulator simulator;

    public Maintained() {}

    public Maintained(Maintenance maintenance, Simulator simulator) {
        setMaintenance(maintenance);
        setSimulator(simulator);
    }

    public Id getId() { return id; }
    public void setId(Id id) { this.id = id; }

    public Maintenance getMaintenance() { return maintenance; }
    public void setMaintenance(Maintenance maintenance) {
        this.maintenance = maintenance;
        if (maintenance != null) {
            if (this.id == null) this.id = new Id();
            this.id.setEventId(maintenance.getId());
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Maintained)) return false;
        Maintained that = (Maintained) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
