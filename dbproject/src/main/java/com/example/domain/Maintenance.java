package com.example.domain;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.*;

/**
 * Bidirectional JPA entity for MAINTENANCE table.
 * - Many-to-many via link entity:
 *     Maintenance ─< Maintained >─ Simulator
 * - Read-only ManyToMany "view":
 *     maintainedSimulatorsView (via MAINTAINED)
 * - Auditable fields inherited from AuditableEntity.
 */
@Entity
@Table(name = "MAINTENANCE")
public class Maintenance extends AuditableEntity {

    // -------------------------------------------------
    // Primary Key
    // -------------------------------------------------
    @Id
    @Column(name = "EVENTID", nullable = false)
    private Integer id;

    // -------------------------------------------------
    // Columns
    // -------------------------------------------------
    @Column(name = "TYPE", length = 60)
    private String type;

    @Column(name = "EVENTSTARTDATE")
    private LocalDate eventStartDate;

    @Column(name = "EVENTENDDATE")
    private LocalDate eventEndDate;

    @Column(name = "EVENTNOTES", length = 1024)
    private String eventNotes;

    @Column(name = "VENDOR", length = 60)
    private String vendor;

    // -------------------------------------------------
    // Association entity (true many-to-many)
    // -------------------------------------------------
    @OneToMany(mappedBy = "maintenance", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Maintained> maintainedItems = new ArrayList<>();

    // -------------------------------------------------
    // Read-only convenience view (pure ManyToMany navigation)
    // NOTE: Do not mutate this set; manage links via Maintained.
    // -------------------------------------------------
    @ManyToMany
    @JoinTable(
        name = "MAINTAINED",
        joinColumns = @JoinColumn(name = "EVENTID"),
        inverseJoinColumns = @JoinColumn(name = "SIMID")
    )
    private Set<Simulator> maintainedSimulatorsView = new HashSet<>();

    // -------------------------------------------------
    // Constructors
    // -------------------------------------------------
    public Maintenance() {}
    public Maintenance(Integer id) { this.id = id; }

    // -------------------------------------------------
    // Getters / Setters
    // -------------------------------------------------
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public LocalDate getEventStartDate() { return eventStartDate; }
    public void setEventStartDate(LocalDate eventStartDate) { this.eventStartDate = eventStartDate; }

    public LocalDate getEventEndDate() { return eventEndDate; }
    public void setEventEndDate(LocalDate eventEndDate) { this.eventEndDate = eventEndDate; }

    public String getEventNotes() { return eventNotes; }
    public void setEventNotes(String eventNotes) { this.eventNotes = eventNotes; }

    public String getVendor() { return vendor; }
    public void setVendor(String vendor) { this.vendor = vendor; }

    public List<Maintained> getMaintainedItems() { return maintainedItems; }

    // Read-only view (unmodifiable)
    public Set<Simulator> getMaintainedSimulatorsView() {
        return Collections.unmodifiableSet(maintainedSimulatorsView);
    }

    // -------------------------------------------------
    // Sync helpers for association entity
    // -------------------------------------------------
    public void addMaintained(Maintained m) {
        if (m == null) return;
        if (!maintainedItems.contains(m)) {
            maintainedItems.add(m);
            m.setMaintenance(this);
        }
    }

    public void removeMaintained(Maintained m) {
        if (m == null) return;
        if (maintainedItems.remove(m) && m.getMaintenance() == this) {
            m.setMaintenance(null);
        }
    }

    // -------------------------------------------------
    // equals / hashCode / toString (by PK)
    // -------------------------------------------------
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Maintenance)) return false;
        Maintenance that = (Maintenance) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public String toString() {
        return "Maintenance{id=" + id + ", type='" + type + "'}";
    }
}
