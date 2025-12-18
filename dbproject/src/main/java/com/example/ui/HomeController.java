package com.example.ui;

import java.time.LocalDate;

import javax.persistence.EntityManager;

import com.example.app.JPA;
import com.example.app.ViewUtil;
import com.example.session.AppSession;
import com.example.session.LoggedInUser;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class HomeController {

    @FXML private Label subtitleLabel;
    @FXML private Label userLabel;

    @FXML private Label totalSimulatorsLabel;
    @FXML private Label activeBorrowingsLabel;
    @FXML private Label upcomingCalibrationsLabel;

    @FXML private TableView<MaintenanceRow> maintenanceTable;
    @FXML private TableColumn<MaintenanceRow, LocalDate> maintDateCol;
    @FXML private TableColumn<MaintenanceRow, String>    maintSimCol;
    @FXML private TableColumn<MaintenanceRow, String>    maintTypeCol;
    @FXML private TableColumn<MaintenanceRow, String>    maintVendorCol;

    @FXML private TableView<LowStockRow> stockTable;
    @FXML private TableColumn<LowStockRow, String>  stockConsCol;
    @FXML private TableColumn<LowStockRow, String>  stockBranchCol;
    @FXML private TableColumn<LowStockRow, Integer> stockAvailCol;
    @FXML private TableColumn<LowStockRow, Integer> stockResCol;

    @FXML private Button logoutButton;

    private final OverviewService overviewService = new OverviewService();

    @FXML
    public void initialize() {
        LoggedInUser u = AppSession.getCurrentUser();
        if (u != null && userLabel != null) {
            StringBuilder sb = new StringBuilder();

            if (u.getDisplayName() != null && !u.getDisplayName().isBlank()) {
                sb.append(u.getDisplayName());
            } else if (u.getUsername() != null) {
                sb.append(u.getUsername());
            }

            if (u.getRole() != null && !u.getRole().isBlank()) {
                sb.append(" (").append(u.getRole()).append(")");
            }

            if (u.getBranchName() != null && !u.getBranchName().isBlank()) {
                sb.append(" · ").append(u.getBranchName());
            }

            if (u.getEmail() != null && !u.getEmail().isBlank()) {
                sb.append(" · ").append(u.getEmail());
            }

            userLabel.setText(sb.toString());
        }

        if (maintDateCol != null)   maintDateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        if (maintSimCol != null)    maintSimCol.setCellValueFactory(new PropertyValueFactory<>("simulator"));
        if (maintTypeCol != null)   maintTypeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        if (maintVendorCol != null) maintVendorCol.setCellValueFactory(new PropertyValueFactory<>("vendor"));

        if (stockConsCol != null)   stockConsCol.setCellValueFactory(new PropertyValueFactory<>("consumable"));
        if (stockBranchCol != null) stockBranchCol.setCellValueFactory(new PropertyValueFactory<>("branch"));
        if (stockAvailCol != null)  stockAvailCol.setCellValueFactory(new PropertyValueFactory<>("available"));
        if (stockResCol != null)    stockResCol.setCellValueFactory(new PropertyValueFactory<>("reserved"));

        loadOverviewData();
    }

    private void loadOverviewData() {
        LoggedInUser u = AppSession.getCurrentUser();
        if (u == null) return;

        EntityManager em = JPA.em();
        try {
            OverviewStats stats = overviewService.loadStats(em, u);
            if (totalSimulatorsLabel != null)
                totalSimulatorsLabel.setText(String.valueOf(stats.getTotalSimulators()));
            if (activeBorrowingsLabel != null)
                activeBorrowingsLabel.setText(String.valueOf(stats.getActiveBorrowings()));
            if (upcomingCalibrationsLabel != null)
                upcomingCalibrationsLabel.setText(String.valueOf(stats.getUpcomingCalibrations()));

            if (maintenanceTable != null) {
                maintenanceTable.getItems().setAll(
                        overviewService.loadRecentMaintenance(em, u)
                );
            }

            if (stockTable != null) {
                stockTable.getItems().setAll(
                        overviewService.loadLowStock(em, u)
                );
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            em.close();
        }
    }


	@FXML
	private void onNavSimulators(ActionEvent event) {
	    try {
	        ViewUtil.switchScene(event, "/ui/simulators.fxml", "Simulators");
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}

	@FXML
	private void onNavStock(ActionEvent event) {
	    try {
	        ViewUtil.switchScene(event, "/ui/stock.fxml", "Stock");
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	@FXML
	private void onNavBorrowing(ActionEvent event) {
	    ViewUtil.switchScene(event, "/ui/borrowing.fxml", "Borrowing");
	}
	

	@FXML
    private void onNavMaintentance(ActionEvent event) {
        try {
            ViewUtil.switchScene(event, "/ui/maintenance.fxml", "Maintenance");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

	@FXML
	private void onNavUsers(ActionEvent event) {
	    LoggedInUser u = AppSession.getCurrentUser();
	    if (u == null || u.getRole() == null || !u.getRole().equalsIgnoreCase("ADMIN")) {
	        Alert a = new Alert(Alert.AlertType.WARNING);
	        a.setTitle("Access denied");
	        a.setHeaderText(null);
	        a.setContentText("Only ADMIN users can manage users.");
	        a.showAndWait();
	        return;
	    }
	
	    try {
	        ViewUtil.switchScene(event, "/ui/users.fxml", "Users");
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}

	@FXML
	private void onNavSettings(ActionEvent event) {
	    try {
	        ViewUtil.switchScene(event, "/ui/settings.fxml", "Settings");
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}

	@FXML
    private void onLogout(ActionEvent event) {
        try {
            ViewUtil.switchScene(event, "/ui/login.fxml", "Login");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
