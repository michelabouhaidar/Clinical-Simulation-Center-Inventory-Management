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
@Table(name = "BORROW_CONS")
public class BorrowCons {

    // ============ Composite primary key: (BORROWID, STOCKID) ============
    @Embeddable
    public static class Id implements Serializable {

        @Column(name = "BORROWID")
        private Integer borrowId;

        @Column(name = "STOCKID")
        private Integer stockId;

        public Id() {}

        public Id(Integer borrowId, Integer stockId) {
            this.borrowId = borrowId;
            this.stockId = stockId;
        }

        public Integer getBorrowId() { return borrowId; }
        public void setBorrowId(Integer borrowId) { this.borrowId = borrowId; }

        public Integer getStockId() { return stockId; }
        public void setStockId(Integer stockId) { this.stockId = stockId; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Id)) return false;
            Id that = (Id) o;
            return Objects.equals(borrowId, that.borrowId)
                    && Objects.equals(stockId, that.stockId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(borrowId, stockId);
        }
    }

    @EmbeddedId
    private Id id = new Id();

    @ManyToOne
    @MapsId("borrowId")               // maps BORROWID part of PK
    @JoinColumn(name = "BORROWID", nullable = false)
    private Borrowing borrowing;

    @ManyToOne
    @MapsId("stockId")                // maps STOCKID part of PK
    @JoinColumn(name = "STOCKID", nullable = false)
    private Stock stock;

    @Column(name = "QUANTITY")
    private Integer quantity;

    public BorrowCons() {}

    public BorrowCons(Borrowing borrowing, Stock stock) {
        setBorrowing(borrowing);
        setStock(stock);
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

    public Stock getStock() { return stock; }
    public void setStock(Stock stock) {
        this.stock = stock;
        if (stock != null) {
            if (this.id == null) this.id = new Id();
            this.id.setStockId(stock.getId());
        }
    }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
}
