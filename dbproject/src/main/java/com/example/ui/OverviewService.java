package com.example.ui;

import java.time.LocalDate;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import com.example.domain.Status;
import com.example.session.LoggedInUser;

public class OverviewService {


    private String branchFilter(LoggedInUser user) {
        if (user == null) return null;
        if (user.getRole() != null && user.getRole().equalsIgnoreCase("ADMIN")) {
            return null;
        }
        return user.getBranchName();
    }

    public OverviewStats loadStats(EntityManager em, LoggedInUser user) {
        String branch = branchFilter(user);

		TypedQuery<Long> q1 = em.createQuery(
		        "SELECT COUNT(s) FROM Simulator s " +
		        "WHERE s.status = :availStatus " +
		        "AND (:branch IS NULL OR s.branch.name = :branch)",
		        Long.class);
		q1.setParameter("availStatus", Status.SIM_AVAILABLE);
		q1.setParameter("branch", branch);
		long totalSims = q1.getSingleResult();


        TypedQuery<Long> q2 = em.createQuery(
                "SELECT COUNT(b) FROM Borrowing b " +
                "WHERE b.status IN (:s1, :s2) " +
                "AND (:branch IS NULL OR b.branch.name = :branch)",
                Long.class);
        q2.setParameter("s1", Status.BORROW_ACTIVE);
        q2.setParameter("s2", Status.BORROW_PARTIAL_RETURN);
        q2.setParameter("branch", branch);
        long activeBorrows = q2.getSingleResult();

        LocalDate today = LocalDate.now();
        LocalDate limit = today.plusDays(30);

        TypedQuery<Long> q3 = em.createQuery(
                "SELECT COUNT(s) FROM Simulator s " +
                "WHERE s.nextCalibrationDate BETWEEN :start AND :end " +
                "AND (:branch IS NULL OR s.branch.name = :branch)",
                Long.class);
        q3.setParameter("start", today);
        q3.setParameter("end",   limit);
        q3.setParameter("branch", branch);
        long upcomingCal = q3.getSingleResult();

        return new OverviewStats(totalSims, activeBorrows, upcomingCal);
    }

    public List<MaintenanceRow> loadRecentMaintenance(EntityManager em, LoggedInUser user) {
        String branch = branchFilter(user);

        TypedQuery<MaintenanceRow> q = em.createQuery(
            "SELECT new com.example.ui.MaintenanceRow(" +
            "   m.eventStartDate, s.tag, m.type, m.vendor) " +
            "FROM Maintained mt " +
            "JOIN mt.maintenance m " +
            "JOIN mt.simulator s " +
            "WHERE (:branch IS NULL OR s.branch.name = :branch) " +
            "ORDER BY m.eventStartDate DESC",
            MaintenanceRow.class
        );
        q.setParameter("branch", branch);
        return q.getResultList();
    }

    public List<LowStockRow> loadLowStock(EntityManager em, LoggedInUser user) {
        String branch = branchFilter(user);

        int threshold = 5;

        TypedQuery<LowStockRow> q = em.createQuery(
            "SELECT new com.example.ui.LowStockRow(" +
            "   c.itemName, b.name, s.availableQuantity, s.reservedQuantity) " +
            "FROM Stock s " +
            "JOIN s.consumable c " +
            "JOIN s.branch b " +
            "WHERE (:branch IS NULL OR b.name = :branch) " +
            "AND s.availableQuantity IS NOT NULL " +
            "AND s.availableQuantity <= :th " +
            "ORDER BY s.availableQuantity ASC",
            LowStockRow.class
        );
        q.setParameter("branch", branch);
        q.setParameter("th", threshold);
        return q.getResultList();
    }
}
