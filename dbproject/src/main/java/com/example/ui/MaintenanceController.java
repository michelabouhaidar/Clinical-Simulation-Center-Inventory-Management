package com.example.ui;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import com.example.app.JPA;
import com.example.app.ViewUtil;
import com.example.domain.Maintained;
import com.example.domain.Maintenance;
import com.example.domain.Simulator;
import com.example.domain.Status;
import com.example.session.AppSession;
import com.example.session.LoggedInUser;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Window;

public class MaintenanceController {

    @FXML private Label userLabel;

    @FXML private TableView<Maintenance> maintenanceTable;
	@FXML private TableColumn<Maintenance,String> idColumn;
	@FXML private TableColumn<Maintenance,String> typeColumn;
	@FXML private TableColumn<Maintenance,String> vendorColumn;
	@FXML private TableColumn<Maintenance,String> startDateColumn;
	@FXML private TableColumn<Maintenance,String> endDateColumn;
	@FXML private TableColumn<Maintenance,String> simCountColumn;
	@FXML private TableColumn<Maintenance,String> notesColumn;


    @FXML private TextField        searchField;
    @FXML private ComboBox<String> sortByCombo;
    @FXML private Label            infoLabel;

    private final DateTimeFormatter dateFmt =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final ObservableList<Maintenance> masterData =
            FXCollections.observableArrayList();
    private FilteredList<Maintenance> filteredData;
    private SortedList<Maintenance>   sortedData;

    // Helper row for detail dialog
    private static class SimStatusRow {
        final Simulator simulator;
        final ComboBox<String> combo;

        SimStatusRow(Simulator simulator, ComboBox<String> combo) {
            this.simulator = simulator;
            this.combo = combo;
        }
    }

    // =========================================================
    // Initialization
    // =========================================================
    @FXML
    public void initialize() {
        // Show logged-in user
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

        // Table columns
        idColumn.setCellValueFactory(cd ->
                new SimpleStringProperty(
                        cd.getValue().getId() != null
                                ? cd.getValue().getId().toString()
                                : ""
                ));
		notesColumn.setCellValueFactory(cd ->
        		new SimpleStringProperty(
        		        cd.getValue().getEventNotes() != null
        		                ? cd.getValue().getEventNotes()
        		                : ""
        		));


        typeColumn.setCellValueFactory(cd ->
                new SimpleStringProperty(
                        cd.getValue().getType() != null
                                ? cd.getValue().getType()
                                : ""
                ));

        vendorColumn.setCellValueFactory(cd ->
                new SimpleStringProperty(
                        cd.getValue().getVendor() != null
                                ? cd.getValue().getVendor()
                                : ""
                ));

        startDateColumn.setCellValueFactory(cd ->
                new SimpleStringProperty(
                        cd.getValue().getEventStartDate() != null
                                ? cd.getValue().getEventStartDate().format(dateFmt)
                                : ""
                ));

        endDateColumn.setCellValueFactory(cd ->
                new SimpleStringProperty(
                        cd.getValue().getEventEndDate() != null
                                ? cd.getValue().getEventEndDate().format(dateFmt)
                                : ""
                ));

        simCountColumn.setCellValueFactory(cd ->
                new SimpleStringProperty(
                        cd.getValue().getMaintainedItems() != null
                                ? Integer.toString(cd.getValue().getMaintainedItems().size())
                                : "0"
                ));

        // Wrappers
        filteredData = new FilteredList<>(masterData, m -> true);
        sortedData   = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(maintenanceTable.comparatorProperty());
        maintenanceTable.setItems(sortedData);

        // Double-click row to open details (always fresh from DB)
        maintenanceTable.setRowFactory(tv -> {
            TableRow<Maintenance> row = new TableRow<>();
            row.setOnMouseClicked(ev -> {
                if (ev.getClickCount() == 2 && !row.isEmpty()) {
                    Maintenance selected = row.getItem();
                    if (selected != null && selected.getId() != null) {
                        openMaintenanceDetailDialog(selected.getId());
                    }
                }
            });
            return row;
        });

        setupSearch();
        setupSortCombo();
        loadMaintenance();
    }

    // =========================================================
    // Data loading & filtering
    // =========================================================
    private String branchFilter(LoggedInUser user) {
        if (user == null) return null;
        if (user.getRole() != null && user.getRole().equalsIgnoreCase("ADMIN")) {
            return null;    // admin sees all branches
        }
        return user.getBranchName();      // Beirut / Byblos
    }

    private void loadMaintenance() {
        LoggedInUser u = AppSession.getCurrentUser();
        String branch = branchFilter(u);

        EntityManager em = JPA.em();
        try {
            String jpql =
                    "SELECT DISTINCT m " +
                    "FROM Maintenance m " +
                    "JOIN FETCH m.maintainedItems mt " +
                    "JOIN mt.simulator s " +
                    "WHERE (:branch IS NULL OR s.branch.name = :branch) " +
                    "ORDER BY m.eventStartDate DESC, m.id DESC";

            TypedQuery<Maintenance> q = em.createQuery(jpql, Maintenance.class);
            q.setParameter("branch", branch);

            List<Maintenance> list = q.getResultList();
            masterData.setAll(list);

            if (infoLabel != null) {
                infoLabel.setText(list.isEmpty()
                        ? "No maintenance events found."
                        : "");
            }
        } catch (Exception e) {
            if (infoLabel != null) {
                infoLabel.setText("Error loading maintenance: " + e.getMessage());
            }
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    private void setupSearch() {
        if (searchField == null) return;

        searchField.textProperty().addListener((obs, old, value) -> {
            String needle = (value == null) ? "" : value.trim().toLowerCase();

            filteredData.setPredicate(m -> {
                if (needle.isEmpty()) return true;

                if (m.getId() != null &&
                        m.getId().toString().contains(needle)) return true;

                if (m.getType() != null &&
                        m.getType().toLowerCase().contains(needle)) return true;

                if (m.getVendor() != null &&
                        m.getVendor().toLowerCase().contains(needle)) return true;

                if (m.getEventNotes() != null &&
                        m.getEventNotes().toLowerCase().contains(needle)) return true;

                if (m.getEventStartDate() != null &&
                        m.getEventStartDate().format(dateFmt).toLowerCase().contains(needle))
                    return true;

                if (m.getEventEndDate() != null &&
                        m.getEventEndDate().format(dateFmt).toLowerCase().contains(needle))
                    return true;

                if (m.getMaintainedItems() != null &&
                        Integer.toString(m.getMaintainedItems().size()).contains(needle))
                    return true;

                return false;
            });
        });
    }

    private void setupSortCombo() {
        if (sortByCombo == null) return;

        sortByCombo.setItems(FXCollections.observableArrayList(
                "Start date", "End date", "Type", "Vendor", "ID", "# Sims"
        ));
        sortByCombo.getSelectionModel().select("Start date");

        sortByCombo.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
            if (sel == null) return;

            maintenanceTable.getSortOrder().clear();
            TableColumn<Maintenance, ?> col = null;

            switch (sel) {
                case "Start date": col = startDateColumn;  break;
                case "End date":   col = endDateColumn;    break;
                case "Type":       col = typeColumn;       break;
                case "Vendor":     col = vendorColumn;     break;
                case "ID":         col = idColumn;         break;
                case "# Sims":     col = simCountColumn;   break;
            }

            if (col != null) {
                col.setSortType(TableColumn.SortType.DESCENDING);
                maintenanceTable.getSortOrder().add(col);
                maintenanceTable.sort();
            }
        });
    }

    // =========================================================
    // Actions – creation
    // =========================================================
    @FXML
    private void onAddMaintenance() {
        LoggedInUser u = AppSession.getCurrentUser();
        if (u == null) {
            if (infoLabel != null)
                infoLabel.setText("You must be logged in to add maintenance.");
            return;
        }

        EntityManager em = JPA.em();
        try {
            // Load simulators (AVAILABLE / OUT_OF_SERVICE) for user branch (or all for admin)
            boolean filterByBranch = (u.getBranchName() != null &&
                    !u.getBranchName().isBlank() &&
                    (u.getRole() == null ||
                     !u.getRole().equalsIgnoreCase("ADMIN")));

            String jpql = "SELECT s FROM Simulator s " +
                          "WHERE s.status IN (:avail, :oos)";
            if (filterByBranch) {
                jpql += " AND s.branch.name = :branchName";
            }
            jpql += " ORDER BY s.tag";

            TypedQuery<Simulator> q = em.createQuery(jpql, Simulator.class);
            q.setParameter("avail", Status.SIM_AVAILABLE);
            q.setParameter("oos",   Status.SIM_OUT_OF_SERVICE);
            if (filterByBranch) {
                q.setParameter("branchName", u.getBranchName());
            }

            List<Simulator> sims = q.getResultList();
            if (sims.isEmpty()) {
                if (infoLabel != null)
                    infoLabel.setText(
                            "No simulators with status AVAILABLE or OUT_OF_SERVICE " +
                            "found for your branch.");
                return;
            }

            Dialog<NewMaintenanceData> dialog = new Dialog<>();
            Window owner = maintenanceTable.getScene() != null
                    ? maintenanceTable.getScene().getWindow()
                    : null;
            if (owner != null) dialog.initOwner(owner);
            dialog.initModality(Modality.WINDOW_MODAL);

            dialog.setTitle("New maintenance event");
            dialog.setHeaderText("Create a maintenance event and choose one or more simulators.");

            ButtonType saveButtonType =
                    new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

            TextField typeField = new TextField();
            typeField.setPromptText("Type (e.g. Preventive, Calibration, Repair)");

            DatePicker startPicker = new DatePicker(LocalDate.now());
            startPicker.setPromptText("Start date (required)");

            TextField vendorField = new TextField();
            vendorField.setPromptText("Vendor / contractor (optional)");

            TextArea notesArea = new TextArea();
            notesArea.setPromptText("Notes (optional)");
            notesArea.setPrefRowCount(3);

            ListView<Simulator> simList = new ListView<>();
            simList.getItems().addAll(sims);
            simList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
            simList.setPrefHeight(220);

            // Rich info for each simulator (from SIMULATOR + SIMULATOR_MODEL)
            simList.setCellFactory(list -> new ListCell<>() {
                @Override
                protected void updateItem(Simulator s, boolean empty) {
                    super.updateItem(s, empty);
                    if (empty || s == null) {
                        setText(null);
                    } else {
                        String tag   = s.getTag() != null ? s.getTag() : "SIM#" + s.getId();
                        String model = (s.getModel() != null && s.getModel().getModelName() != null)
                                ? s.getModel().getModelName()
                                : "No model";
                        String branch = (s.getBranch() != null && s.getBranch().getName() != null)
                                ? s.getBranch().getName()
                                : "No branch";
                        String status = s.getStatus() != null ? s.getStatus() : "UNKNOWN";
                        String sn     = (s.getSerialNumber() != null && !s.getSerialNumber().isBlank())
                                ? s.getSerialNumber()
                                : "n/a";
                        String cal    = s.getCalibrationDate() != null
                                ? s.getCalibrationDate().toString()
                                : "n/a";
                        String nextCal = s.getNextCalibrationDate() != null
                                ? s.getNextCalibrationDate().toString()
                                : "n/a";

                        String calReq = "n/a";
                        String maxDays = "n/a";
                        if (s.getModel() != null) {
                            if (s.getModel().getCalReq() != null) {
                                calReq = s.getModel().getCalReq() ? "Yes" : "No";
                            }
                            if (s.getModel().getMaxDays() != null) {
                                maxDays = s.getModel().getMaxDays().toString();
                            }
                        }

                        String cond = (s.getConditionNotes() != null && !s.getConditionNotes().isBlank())
                                ? s.getConditionNotes()
                                : "";

                        StringBuilder sb = new StringBuilder();
                        sb.append(tag).append(" · ").append(model).append(" · ").append(branch).append("\n");
                        sb.append("SN: ").append(sn)
                          .append(" | Status: ").append(status).append("\n");
                        sb.append("Cal: ").append(cal)
                          .append(" | Next cal: ").append(nextCal).append("\n");
                        sb.append("Model cal required: ").append(calReq)
                          .append(" | Max days: ").append(maxDays);
                        if (!cond.isEmpty()) {
                            sb.append("\nCond notes: ").append(cond);
                        }

                        setText(sb.toString());
                    }
                }
            });

            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(8);
            grid.setPadding(new Insets(10, 10, 10, 10));

            int row = 0;
            grid.add(new Label("Type:"), 0, row);
            grid.add(typeField,        1, row++);

            grid.add(new Label("Start date:"), 0, row);
            grid.add(startPicker,             1, row++);

            grid.add(new Label("Vendor:"), 0, row);
            grid.add(vendorField,          1, row++);

            grid.add(new Label("Notes:"), 0, row);
            grid.add(notesArea,           1, row++);

            grid.add(new Label("Simulators (multi-select):"), 0, row);
            grid.add(simList,                           1, row++);

            Label hint = new Label(
                    "Tip: hold Ctrl (or Cmd on macOS) to select multiple simulators.");
            hint.setStyle("-fx-text-fill: #6b7280; -fx-font-size: 10px;");
            grid.add(hint, 1, row);

            dialog.getDialogPane().setContent(grid);

            Node saveButton = dialog.getDialogPane().lookupButton(saveButtonType);
            saveButton.setDisable(true);

            Runnable validate = () -> {
                String type = typeField.getText() == null ? "" : typeField.getText().trim();
                LocalDate sd = startPicker.getValue();
                boolean hasSims = !simList.getSelectionModel().getSelectedItems().isEmpty();
                saveButton.setDisable(type.isEmpty() || sd == null || !hasSims);
            };

            typeField.textProperty().addListener((o, ov, nv) -> validate.run());
            startPicker.valueProperty().addListener((o, ov, nv) -> validate.run());
            simList.getSelectionModel().getSelectedItems().addListener(
                    (ListChangeListener<Simulator>) change -> validate.run()
            );

            dialog.setResultConverter(btn -> {
                if (btn == saveButtonType) {
                    String type  = typeField.getText().trim();
                    LocalDate sd = startPicker.getValue();

                    String vendor = vendorField.getText() == null
                            ? ""
                            : vendorField.getText().trim();
                    String notes  = notesArea.getText() == null
                            ? ""
                            : notesArea.getText().trim();

                    List<Simulator> selected =
                            List.copyOf(simList.getSelectionModel().getSelectedItems());

                    return new NewMaintenanceData(type, sd, vendor, notes, selected);
                }
                return null;
            });

            Optional<NewMaintenanceData> result = dialog.showAndWait();
            if (result.isEmpty()) return; // cancelled

            NewMaintenanceData data = result.get();

            em.getTransaction().begin();

            Integer nextId = ((Number) em.createQuery(
                    "SELECT COALESCE(MAX(m.id), 0) FROM Maintenance m",
                    Number.class
            ).getSingleResult()).intValue() + 1;

            Maintenance m = new Maintenance();
            m.setId(nextId);
            m.setType(data.type());
            m.setEventStartDate(data.startDate());
            // End date stays null; it will be set when all simulators are returned

            String v  = data.vendor() == null ? "" : data.vendor().trim();
            String nt = data.notes()  == null ? "" : data.notes().trim();
            m.setVendor(v.isEmpty() ? null : v);
            m.setEventNotes(nt.isEmpty() ? null : nt);

            // Link simulators + move them to SIM_MAINTENANCE
            for (Simulator sForm : data.simulators()) {
                if (sForm.getId() == null) continue;

                Simulator managedSim = em.getReference(Simulator.class, sForm.getId());
                managedSim.setStatus(Status.SIM_MAINTENANCE);

                Maintained link = new Maintained();
                link.setSimulator(managedSim);
                m.addMaintained(link);
            }

            em.persist(m);
            em.getTransaction().commit();

            loadMaintenance();
            maintenanceTable.refresh();

            if (infoLabel != null) {
                infoLabel.setText("Maintenance event #" + m.getId() +
                        " created for " + data.simulators().size() + " simulator(s).");
            }

        } catch (Exception e) {
            e.printStackTrace();
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            if (infoLabel != null) {
                infoLabel.setText("Error creating maintenance event: " + e.getMessage());
            }
        } finally {
            em.close();
        }
    }

    private record NewMaintenanceData(
            String type,
            LocalDate startDate,
            String vendor,
            String notes,
            List<Simulator> simulators
    ) {}

    // =========================================================
    // Actions – detail dialog (returns & end date)
    // =========================================================
    private void openMaintenanceDetailDialog(Integer maintenanceId) {
        EntityManager em = JPA.em();
        try {
            Maintenance m = em.find(Maintenance.class, maintenanceId);
            if (m == null) {
                if (infoLabel != null) {
                    infoLabel.setText("Maintenance event no longer exists.");
                }
                return;
            }

            // Force load of associations
            m.getMaintainedItems().size();

            final boolean isClosed = (m.getEventEndDate() != null);

            Dialog<ButtonType> dialog = new Dialog<>();
            Window owner = maintenanceTable.getScene() != null
                    ? maintenanceTable.getScene().getWindow()
                    : null;
            if (owner != null) dialog.initOwner(owner);
            dialog.initModality(Modality.WINDOW_MODAL);

            dialog.setTitle("Maintenance #" + m.getId());
            dialog.setHeaderText(isClosed
                    ? "Maintenance event is closed (end date: " + m.getEventEndDate() + ")."
                    : "Set return status for simulators in this maintenance event.");

            VBox content = new VBox(10);
            content.setPadding(new Insets(10));

            List<SimStatusRow> rows = new java.util.ArrayList<>();

            for (Maintained link : m.getMaintainedItems()) {
                Simulator s = link.getSimulator();

                HBox line = new HBox(10);

                // Build rich info block
                VBox simBox = new VBox(2);

                String tag   = s.getTag() != null ? s.getTag() : "SIM#" + s.getId();
                String model = (s.getModel() != null && s.getModel().getModelName() != null)
                        ? s.getModel().getModelName()
                        : "No model";
                String branch = (s.getBranch() != null && s.getBranch().getName() != null)
                        ? s.getBranch().getName()
                        : "No branch";
                String status = s.getStatus() != null ? s.getStatus() : "UNKNOWN";
                String sn     = (s.getSerialNumber() != null && !s.getSerialNumber().isBlank())
                        ? s.getSerialNumber()
                        : "n/a";
                String cal    = s.getCalibrationDate() != null
                        ? s.getCalibrationDate().toString()
                        : "n/a";
                String nextCal = s.getNextCalibrationDate() != null
                        ? s.getNextCalibrationDate().toString()
                        : "n/a";

                String calReq = "n/a";
                String maxDays = "n/a";
                if (s.getModel() != null) {
                    if (s.getModel().getCalReq() != null) {
                        calReq = s.getModel().getCalReq() ? "Yes" : "No";
                    }
                    if (s.getModel().getMaxDays() != null) {
                        maxDays = s.getModel().getMaxDays().toString();
                    }
                }

                String cond = (s.getConditionNotes() != null && !s.getConditionNotes().isBlank())
                        ? s.getConditionNotes()
                        : "";

                Label l1 = new Label(tag + " · " + model + " · " + branch + " | SN: " + sn);
                Label l2 = new Label("Status: " + status +
                        " | Cal: " + cal + " | Next cal: " + nextCal);
                Label l3 = new Label("Model cal required: " + calReq +
                        " | Max days: " + maxDays);
                simBox.getChildren().addAll(l1, l2, l3);
                if (!cond.isEmpty()) {
                    Label l4 = new Label("Cond notes: " + cond);
                    simBox.getChildren().add(l4);
                }
                simBox.setMinWidth(420);

                line.getChildren().add(simBox);

                if (!isClosed && Status.SIM_MAINTENANCE.equals(status)) {
                    ComboBox<String> combo = new ComboBox<>();
                    combo.getItems().addAll(
                            "Keep in maintenance",
                            Status.SIM_AVAILABLE,
                            Status.SIM_OUT_OF_SERVICE
                    );
                    combo.setValue("Keep in maintenance");
                    line.getChildren().add(new Label("New status:"));
                    line.getChildren().add(combo);
                    rows.add(new SimStatusRow(s, combo));
                } else {
                    String msg = isClosed
                            ? "Event closed"
                            : "Already returned (" + status + ")";
                    Label done = new Label(msg);
                    done.setStyle("-fx-text-fill: #16a34a;");
                    line.getChildren().add(done);
                }

                content.getChildren().add(line);
            }

            if (isClosed) {
                dialog.getDialogPane().getButtonTypes().setAll(ButtonType.CLOSE);
            } else {
                ButtonType saveButtonType =
                        new ButtonType("Save changes", ButtonBar.ButtonData.OK_DONE);
                dialog.getDialogPane().getButtonTypes().setAll(saveButtonType, ButtonType.CANCEL);

                dialog.setResultConverter(btn -> btn);
            }

            dialog.getDialogPane().setContent(content);

            Optional<ButtonType> result = dialog.showAndWait();
            if (isClosed || result.isEmpty()) {
                return; // closed event is read-only OR user cancelled
            }
            if (result.get().getButtonData() != ButtonBar.ButtonData.OK_DONE) {
                return;
            }

            // Apply changes for open events
            em.getTransaction().begin();

            for (SimStatusRow row : rows) {
                String choice = row.combo.getValue();
                if (choice == null || "Keep in maintenance".equals(choice)) {
                    continue; // no change
                }

                Simulator managedSim = em.getReference(Simulator.class, row.simulator.getId());
                managedSim.setStatus(choice);
            }

            // Check if any simulator remains SIM_MAINTENANCE
            Long countStillMaint = em.createQuery(
                    "SELECT COUNT(mt) FROM Maintained mt " +
                    "JOIN mt.simulator s " +
                    "WHERE mt.maintenance.id = :mid " +
                    "AND s.status = :stat", Long.class)
                    .setParameter("mid", m.getId())
                    .setParameter("stat", Status.SIM_MAINTENANCE)
                    .getSingleResult();

            boolean anyStillMaintenance =
                    (countStillMaint != null && countStillMaint > 0);

            // Only set end date if it is currently null
            if (!anyStillMaintenance && m.getEventEndDate() == null) {
                m.setEventEndDate(LocalDate.now());
            }

            em.getTransaction().commit();

            // Reload fresh data so the next dialog is always correct
            loadMaintenance();
            maintenanceTable.refresh();

            if (infoLabel != null) {
                if (!anyStillMaintenance && m.getEventEndDate() != null) {
                    infoLabel.setText("All simulators returned; maintenance #" + m.getId() +
                            " closed with end date " + m.getEventEndDate());
                } else {
                    infoLabel.setText("Maintenance #" + m.getId() + " updated.");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            if (infoLabel != null) {
                infoLabel.setText("Error updating maintenance: " + e.getMessage());
            }
        } finally {
            em.close();
        }
    }

    // =========================================================
    // Navigation
    // =========================================================
    @FXML
    private void onNavOverview(ActionEvent event) {
        try {
            ViewUtil.switchScene(event, "/ui/home.fxml", "Main Menu");
        } catch (Exception e) {
            e.printStackTrace();
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
