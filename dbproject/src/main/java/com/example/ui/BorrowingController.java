package com.example.ui;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import com.example.app.JPA;
import com.example.app.ViewUtil;
import com.example.domain.BorrowCons;
import com.example.domain.BorrowSim;
import com.example.domain.Borrowing;
import com.example.domain.Branch;
import com.example.domain.Department;
import com.example.domain.Simulator;
import com.example.domain.Status;
import com.example.domain.Stock;
import com.example.session.AppSession;
import com.example.session.LoggedInUser;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class BorrowingController {

    @FXML private Label userLabel;

    @FXML private TextField searchField;
    @FXML private ComboBox<String> statusCombo;
    @FXML private ComboBox<Department> deptCombo;
    @FXML private DatePicker dpFrom;
    @FXML private DatePicker dpTo;

    @FXML private TableView<Borrowing> borrowTable;
    @FXML private TableColumn<Borrowing, Number> colId;
    @FXML private TableColumn<Borrowing, String> colCode;
    @FXML private TableColumn<Borrowing, String> colBranch;
    @FXML private TableColumn<Borrowing, String> colDept;
    @FXML private TableColumn<Borrowing, String> colStart;
    @FXML private TableColumn<Borrowing, String> colEnd;
    @FXML private TableColumn<Borrowing, String> colStatus;

    @FXML private Label borrowMetaLabel;

    @FXML private TableView<BorrowSim> borrowSimsTable;
    @FXML private TableColumn<BorrowSim, String> colSimTag;
    @FXML private TableColumn<BorrowSim, String> colSimModel;
    @FXML private TableColumn<BorrowSim, String> colSimSN;
    @FXML private TableColumn<BorrowSim, String> colSimStatus;
    @FXML private TableColumn<BorrowSim, String> colSimNextCal;
    @FXML private TableColumn<BorrowSim, String> colCondOut;
    @FXML private TableColumn<BorrowSim, String> colCondIn;
    @FXML private TableColumn<BorrowSim, String> colReturnNotes;

    @FXML private TableView<BorrowCons> borrowConsTable;
    @FXML private TableColumn<BorrowCons, String> colConsName;
    @FXML private TableColumn<BorrowCons, String> colConsMeasure;
    @FXML private TableColumn<BorrowCons, Number> colConsQty;
    @FXML private TableColumn<BorrowCons, Number> colConsAvailNow;
    @FXML private TableColumn<BorrowCons, String> colConsLastCount;
    @FXML private TableColumn<BorrowCons, String> colConsBranch;
    @FXML private TableColumn<BorrowCons, Number> colConsStockId;

    private final DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @FXML
    public void initialize() {
        renderUserHeader();
        setupCombos();
        setupBorrowTableColumns();
        setupDetailsTables();

        borrowTable.getSelectionModel().selectedItemProperty().addListener((obs, old, selected) -> {
            if (selected == null) {
                borrowMetaLabel.setText("");
                borrowSimsTable.setItems(FXCollections.observableArrayList());
                borrowConsTable.setItems(FXCollections.observableArrayList());
            } else {
                updateMeta(selected);
                loadDetails(selected);
            }
        });

        loadBorrowings();
    }

    @FXML private void onNavOverview(ActionEvent e) { ViewUtil.switchScene(e, "/ui/home.fxml", "Overview"); }
    @FXML private void onNavSimulators(ActionEvent e) { ViewUtil.switchScene(e, "/ui/simulators.fxml", "Simulators"); }
    @FXML private void onNavStock(ActionEvent e) { ViewUtil.switchScene(e, "/ui/stock.fxml", "Consumables & Stock"); }
    @FXML private void onNavMaintentance(ActionEvent e) { ViewUtil.switchScene(e, "/ui/maintenance.fxml", "Maintenance"); }

    @FXML
    private void onNavUsers(ActionEvent event) {
        LoggedInUser u = AppSession.getCurrentUser();
        if (u == null || u.getRole() == null || !u.getRole().equalsIgnoreCase("ADMIN")) {
            warn("Access denied", "Only ADMIN users can manage users.");
            return;
        }
        ViewUtil.switchScene(event, "/ui/users.fxml", "Users");
    }

    @FXML private void onNavSettings(ActionEvent e) { ViewUtil.switchScene(e, "/ui/settings.fxml", "Settings"); }

    @FXML
    private void onLogout(ActionEvent e) {
        AppSession.clear();
        ViewUtil.switchScene(e, "/ui/login.fxml", "Login");
    }

    @FXML private void onSearch(ActionEvent e) { loadBorrowings(); }

    @FXML
    private void onClear(ActionEvent e) {
        searchField.clear();
        statusCombo.getSelectionModel().select("ALL");
        deptCombo.getSelectionModel().clearSelection();
        dpFrom.setValue(null);
        dpTo.setValue(null);
        loadBorrowings();
    }

    @FXML
    private void onNewBorrowing(ActionEvent e) {
        LoggedInUser u = AppSession.getCurrentUser();
        if (u == null) return;

        Dialog<ButtonType> d = new Dialog<>();
        d.setTitle("New borrowing");
        d.setHeaderText("Create a borrowing header");
        d.getDialogPane().setPrefWidth(760);
        d.getDialogPane().setPrefHeight(420);

        ButtonType createBtn = new ButtonType("Create", ButtonData.OK_DONE);
        d.getDialogPane().getButtonTypes().addAll(createBtn, ButtonType.CANCEL);

        ComboBox<Department> dep = new ComboBox<>();
        dep.setItems(loadDepartments());
        dep.setMaxWidth(Double.MAX_VALUE);

        dep.setCellFactory(cb -> new ListCell<>() {
            @Override protected void updateItem(Department item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); return; }
                String name = safeStr(item.getName());
                String contact = safeStr(item.getContactName());
                String phone = safeStr(item.getPhone1());
                String suffix = "";
                if (!contact.isBlank()) suffix += " · " + contact;
                if (!phone.isBlank()) suffix += " · " + phone;
                setText(name + suffix);
            }
        });
        dep.setButtonCell(dep.getCellFactory().call(null));

        DatePicker start = new DatePicker(LocalDate.now());
        DatePicker end = new DatePicker();

        TextArea notes = new TextArea();
        notes.setPromptText("Notes (optional)");
        notes.setPrefRowCount(3);

        GridPane gp = new GridPane();
        gp.setHgap(10);
        gp.setVgap(10);
        gp.add(new Label("Department:"), 0, 0);
        gp.add(dep, 1, 0);
        gp.add(new Label("Start date:"), 0, 1);
        gp.add(start, 1, 1);
        gp.add(new Label("End date:"), 0, 2);
        gp.add(end, 1, 2);
        gp.add(new Label("Notes:"), 0, 3);
        gp.add(notes, 1, 3);

        d.getDialogPane().setContent(gp);

        Optional<ButtonType> res = d.showAndWait();
        if (res.isEmpty() || res.get() != createBtn) return;

        if (dep.getValue() == null) { warn("Validation", "Department is required."); return; }
        if (start.getValue() == null) { warn("Validation", "Start date is required."); return; }
        if (end.getValue() != null && end.getValue().isBefore(start.getValue())) {
            warn("Validation", "End date cannot be before start date.");
            return;
        }

        EntityManager em = JPA.em();
        try {
            em.getTransaction().begin();

            Borrowing b = new Borrowing();
            b.setId(nextBorrowId(em));

            Branch branch = resolveUserBranch(em, u);
            if (branch == null) {
                em.getTransaction().rollback();
                warn("Validation", "Your account is not linked to a branch.");
                return;
            }

            b.setBranch(branch);
            b.setDepartment(em.find(Department.class, dep.getValue().getId()));

            String code = "BRW-" + LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE) + "-" + b.getId();
            b.setBorrowCode(code);

            b.setStartDate(start.getValue());
            b.setEndDate(end.getValue());
            b.setNotes(notes.getText() == null ? null : notes.getText().trim());
            b.setStatus(Status.BORROW_ACTIVE);

            em.persist(b);
            em.getTransaction().commit();

            loadBorrowings();
            selectBorrowingById(b.getId());

        } catch (Exception ex) {
            safeRollback(em);
            ex.printStackTrace();
            error("Error", "Failed to create borrowing.");
        } finally {
            em.close();
        }
    }

    @FXML
    private void onAddSimulators(ActionEvent e) {
        Borrowing b = borrowTable.getSelectionModel().getSelectedItem();
        if (b == null) { warn("Validation", "Select a borrowing first."); return; }

        if (!isEditableBorrowing(b)) {
            warn("Not allowed", "You can add items only for ACTIVE / PARTIALLY_RETURNED borrowings.");
            return;
        }

        EntityManager em = JPA.em();
        try {
            Borrowing managed = em.find(Borrowing.class, b.getId());

            List<Simulator> candidates = loadAvailableSimulatorsForBranch(em, managed.getBranch().getName());
            if (candidates.isEmpty()) {
                warn("No simulators", "No AVAILABLE simulators found for this branch.");
                return;
            }

            Dialog<ButtonType> d = new Dialog<>();
            d.setTitle("Add simulators");
            d.setHeaderText("Select one or more simulators to borrow");
            d.getDialogPane().setPrefWidth(980);
            d.getDialogPane().setPrefHeight(640);

            ButtonType addBtn = new ButtonType("Add", ButtonData.OK_DONE);
            d.getDialogPane().getButtonTypes().addAll(addBtn, ButtonType.CANCEL);

            ListView<Simulator> list = new ListView<>();
            list.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
            list.setItems(FXCollections.observableArrayList(candidates));
            list.setPrefHeight(420);
			list.setPrefWidth(600);

            list.setCellFactory(v -> new ListCell<>() {
                @Override
                protected void updateItem(Simulator item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) { setText(null); return; }

                    String tag = safeStr(item.getTag());
                    String model = (item.getModel() != null) ? safeStr(item.getModel().getModelName()) : "";
                    String sn = safeStr(item.getSerialNumber());
                    String nextCal = (item.getNextCalibrationDate() == null) ? "" : df.format(item.getNextCalibrationDate());

                    String line = tag + " · " + model;
                    if (!sn.isBlank()) line += " · SN: " + sn;
                    if (!nextCal.isBlank()) line += " · NextCal: " + nextCal;

                    setText(line);
                }
            });

            TextField condOut = new TextField();
            condOut.setPromptText("Condition out (optional)");

            GridPane gp = new GridPane();
            gp.setHgap(10); gp.setVgap(10);
            gp.add(new Label("Simulators:"), 0, 0);
            gp.add(list, 1, 0);
            gp.add(new Label("Condition out:"), 0, 1);
            gp.add(condOut, 1, 1);

            d.getDialogPane().setContent(gp);

            Optional<ButtonType> res = d.showAndWait();
            if (res.isEmpty() || res.get() != addBtn) return;

            List<Simulator> selected = list.getSelectionModel().getSelectedItems();
            if (selected == null || selected.isEmpty()) {
                warn("Validation", "Select at least one simulator.");
                return;
            }

            em.getTransaction().begin();

            for (Simulator s : selected) {
                Simulator ms = em.find(Simulator.class, s.getId());
                if (ms == null || ms.getStatus() == null || !ms.getStatus().equalsIgnoreCase(Status.SIM_AVAILABLE)) {
                    safeRollback(em);
                    warn("Validation", "One or more selected simulators are no longer AVAILABLE.");
                    return;
                }

                BorrowSim bs = new BorrowSim();
                bs.setBorrowing(managed);
                bs.setSimulator(ms);
                bs.setConditionOut(safeTrim(condOut.getText()));

                em.persist(bs);

                ms.setStatus(Status.SIM_BORROWED);
                em.merge(ms);
            }

            em.merge(managed);
            em.getTransaction().commit();

            loadBorrowings();
            selectBorrowingById(managed.getId());
            loadDetails(managed);

        } catch (Exception ex) {
            ex.printStackTrace();
            safeRollback(em);
            error("Error", "Failed to add simulators.");
        } finally {
            em.close();
        }
    }

    @FXML
    private void onAddConsumables(ActionEvent e) {
        Borrowing b = borrowTable.getSelectionModel().getSelectedItem();
        if (b == null) { warn("Validation", "Select a borrowing first."); return; }

        if (!isEditableBorrowing(b)) {
            warn("Not allowed", "You can add items only for ACTIVE / PARTIALLY_RETURNED borrowings.");
            return;
        }

        EntityManager em = JPA.em();
        try {
            Borrowing managed = em.find(Borrowing.class, b.getId());

            List<Stock> candidates = loadStockWithAvailableForBranch(em, managed.getBranch().getName());
            if (candidates.isEmpty()) {
                warn("No stock", "No stock with available quantity found for this branch.");
                return;
            }

            Dialog<ButtonType> d = new Dialog<>();
            d.setTitle("Add consumables");
            d.setHeaderText("Select a stock item and quantity");
            d.getDialogPane().setPrefWidth(900);
            d.getDialogPane().setPrefHeight(420);

            ButtonType addBtn = new ButtonType("Add", ButtonData.OK_DONE);
            d.getDialogPane().getButtonTypes().addAll(addBtn, ButtonType.CANCEL);

            ComboBox<Stock> stockCombo = new ComboBox<>();
            stockCombo.setItems(FXCollections.observableArrayList(candidates));
            stockCombo.setMaxWidth(Double.MAX_VALUE);

            stockCombo.setCellFactory(cb -> new ListCell<>() {
                @Override
                protected void updateItem(Stock item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) { setText(null); return; }

                    String name = (item.getConsumable() != null) ? safeStr(item.getConsumable().getItemName()) : "";
                    String meas = (item.getConsumable() != null) ? safeStr(item.getConsumable().getMeasure()) : "";
                    int av = (item.getAvailableQuantity() == null) ? 0 : item.getAvailableQuantity();
                    String last = (item.getLastCountDate() == null) ? "" : df.format(item.getLastCountDate());

                    String line = name + " (" + meas + ")"
                            + " · Avail: " + av;
                    if (!last.isBlank()) line += " · LastCount: " + last;

                    setText(line);
                }
            });
            stockCombo.setButtonCell(stockCombo.getCellFactory().call(null));

            TextField qtyField = new TextField();
            qtyField.setPromptText("Quantity (integer)");

            GridPane gp = new GridPane();
            gp.setHgap(10); gp.setVgap(10);
            gp.add(new Label("Stock item:"), 0, 0);
            gp.add(stockCombo, 1, 0);
            gp.add(new Label("Quantity:"), 0, 1);
            gp.add(qtyField, 1, 1);

            d.getDialogPane().setContent(gp);

            Optional<ButtonType> res = d.showAndWait();
            if (res.isEmpty() || res.get() != addBtn) return;

            if (stockCombo.getValue() == null) { warn("Validation", "Select a stock item."); return; }
            Integer qty = parsePositiveInt(qtyField.getText());
            if (qty == null) { warn("Validation", "Quantity must be a positive integer."); return; }

            em.getTransaction().begin();

            Stock st = em.find(Stock.class, stockCombo.getValue().getId());
            if (st == null) { safeRollback(em); warn("Validation", "Selected stock is not available anymore."); return; }

            int avail = (st.getAvailableQuantity() == null) ? 0 : st.getAvailableQuantity();
            if (qty > avail) { safeRollback(em); warn("Validation", "Quantity exceeds available stock (" + avail + ")."); return; }

            st.setAvailableQuantity(avail - qty);

            BorrowCons bc = new BorrowCons();
            bc.setBorrowing(managed);
            bc.setStock(st);
            bc.setQuantity(qty);

            em.persist(bc);
            em.merge(st);

            em.getTransaction().commit();

            loadBorrowings();
            selectBorrowingById(managed.getId());
            loadDetails(managed);

        } catch (Exception ex) {
            ex.printStackTrace();
            safeRollback(em);
            error("Error", "Failed to add consumables.");
        } finally {
            em.close();
        }
    }

    @FXML
    private void onReturnSimulators(ActionEvent e) {
        Borrowing b = borrowTable.getSelectionModel().getSelectedItem();
        if (b == null) { warn("Validation", "Select a borrowing first."); return; }
        if (!isEditableBorrowing(b)) { warn("Not allowed", "Returns are allowed only for ACTIVE / PARTIALLY_RETURNED."); return; }

        BorrowSim selectedLine = borrowSimsTable.getSelectionModel().getSelectedItem();
        if (selectedLine == null) { warn("Validation", "Select a simulator line from the Simulators tab."); return; }

        Dialog<ButtonType> d = new Dialog<>();
        d.setTitle("Return simulator");
        d.setHeaderText("Capture return information");
        d.getDialogPane().setPrefWidth(820);
        d.getDialogPane().setPrefHeight(420);

        ButtonType saveBtn = new ButtonType("Save", ButtonData.OK_DONE);
        d.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

        ComboBox<String> newStatus = new ComboBox<>();
        newStatus.setItems(FXCollections.observableArrayList(Status.SIM_AVAILABLE, Status.SIM_OUT_OF_SERVICE));
        newStatus.getSelectionModel().select(Status.SIM_AVAILABLE);

        TextField condIn = new TextField();
        condIn.setPromptText("Condition in (optional)");

        TextArea notes = new TextArea();
        notes.setPromptText("Return notes (optional)");
        notes.setPrefRowCount(3);

        GridPane gp = new GridPane();
        gp.setHgap(10); gp.setVgap(10);
        gp.add(new Label("Set simulator status:"), 0, 0);
        gp.add(newStatus, 1, 0);
        gp.add(new Label("Condition in:"), 0, 1);
        gp.add(condIn, 1, 1);
        gp.add(new Label("Return notes:"), 0, 2);
        gp.add(notes, 1, 2);

        d.getDialogPane().setContent(gp);

        Optional<ButtonType> res = d.showAndWait();
        if (res.isEmpty() || res.get() != saveBtn) return;

        EntityManager em = JPA.em();
        try {
            em.getTransaction().begin();

            Borrowing managedBorrow = em.find(Borrowing.class, b.getId());
            BorrowSim bs = em.find(BorrowSim.class, selectedLine.getId());
            if (bs == null) { safeRollback(em); warn("Not found", "The selected borrow simulator line was not found."); return; }

            Simulator sim = em.find(Simulator.class, bs.getSimulator().getId());
            if (sim == null) { safeRollback(em); warn("Not found", "Simulator not found."); return; }

            bs.setConditionIn(safeTrim(condIn.getText()));
            bs.setReturnNotes(safeTrim(notes.getText()));
            em.merge(bs);

            sim.setStatus(newStatus.getValue());
            em.merge(sim);

            long remainingBorrowed = countBorrowedSims(em, managedBorrow.getId());
            if (remainingBorrowed == 0) managedBorrow.setStatus(Status.BORROW_CLOSED);
            else managedBorrow.setStatus(Status.BORROW_PARTIAL_RETURN);

            em.merge(managedBorrow);
            em.getTransaction().commit();

            loadBorrowings();
            selectBorrowingById(managedBorrow.getId());
            loadDetails(managedBorrow);

        } catch (Exception ex) {
            ex.printStackTrace();
            safeRollback(em);
            error("Error", "Failed to save return.");
        } finally {
            em.close();
        }
    }

    @FXML
    private void onCloseBorrowing(ActionEvent e) {
        Borrowing b = borrowTable.getSelectionModel().getSelectedItem();
        if (b == null) { warn("Validation", "Select a borrowing first."); return; }

        if (b.getStatus() != null && b.getStatus().equalsIgnoreCase(Status.BORROW_CLOSED)) {
            warn("Not allowed", "Borrowing is already CLOSED.");
            return;
        }
        if (b.getStatus() != null && b.getStatus().equalsIgnoreCase(Status.BORROW_CANCELLED)) {
            warn("Not allowed", "Borrowing is CANCELLED.");
            return;
        }

        EntityManager em = JPA.em();
        try {
            em.getTransaction().begin();
            Borrowing managed = em.find(Borrowing.class, b.getId());

            long remainingBorrowed = countBorrowedSims(em, managed.getId());
            if (remainingBorrowed > 0) {
                safeRollback(em);
                warn("Not allowed", "You cannot close this borrowing while simulators are still BORROWED.");
                return;
            }

            managed.setStatus(Status.BORROW_CLOSED);
            em.merge(managed);

            em.getTransaction().commit();

            loadBorrowings();
            selectBorrowingById(managed.getId());
            loadDetails(managed);

        } catch (Exception ex) {
            ex.printStackTrace();
            safeRollback(em);
            error("Error", "Failed to close borrowing.");
        } finally {
            em.close();
        }
    }

    @FXML
    private void onCancelBorrowing(ActionEvent e) {
        Borrowing b = borrowTable.getSelectionModel().getSelectedItem();
        if (b == null) { warn("Validation", "Select a borrowing first."); return; }

        if (b.getStatus() == null || !b.getStatus().equalsIgnoreCase(Status.BORROW_ACTIVE)) {
            warn("Not allowed", "Only ACTIVE borrowings can be cancelled.");
            return;
        }

        Alert confirm = new Alert(AlertType.CONFIRMATION);
        confirm.setTitle("Cancel borrowing");
        confirm.setHeaderText(null);
        confirm.setContentText("Cancel borrowing and revert simulator/stock allocations?");
        Optional<ButtonType> ans = confirm.showAndWait();
        if (ans.isEmpty() || ans.get() != ButtonType.OK) return;

        EntityManager em = JPA.em();
        try {
            em.getTransaction().begin();

            Borrowing managed = em.find(Borrowing.class, b.getId());

            long returnedCount = em.createQuery(
                    "SELECT COUNT(bs) FROM BorrowSim bs WHERE bs.borrowing.id = :bid AND bs.conditionIn IS NOT NULL",
                    Long.class
            ).setParameter("bid", managed.getId()).getSingleResult();

            if (returnedCount > 0) {
                safeRollback(em);
                warn("Not allowed", "This borrowing has return data; cancel is blocked. Close it instead.");
                return;
            }

            List<BorrowSim> simLines = em.createQuery(
                    "SELECT bs FROM BorrowSim bs WHERE bs.borrowing.id = :bid", BorrowSim.class)
                    .setParameter("bid", managed.getId())
                    .getResultList();

            for (BorrowSim bs : simLines) {
                Simulator sim = em.find(Simulator.class, bs.getSimulator().getId());
                if (sim != null && Status.SIM_BORROWED.equalsIgnoreCase(sim.getStatus())) {
                    sim.setStatus(Status.SIM_AVAILABLE);
                    em.merge(sim);
                }
                em.remove(bs);
            }

            List<BorrowCons> consLines = em.createQuery(
                    "SELECT bc FROM BorrowCons bc WHERE bc.borrowing.id = :bid", BorrowCons.class)
                    .setParameter("bid", managed.getId())
                    .getResultList();

            for (BorrowCons bc : consLines) {
                Stock st = em.find(Stock.class, bc.getStock().getId());
                int qty = (bc.getQuantity() == null) ? 0 : bc.getQuantity();
                if (st != null && qty > 0) {
                    int av = (st.getAvailableQuantity() == null) ? 0 : st.getAvailableQuantity();
                    st.setAvailableQuantity(av + qty);
                    em.merge(st);
                }
                em.remove(bc);
            }

            managed.setStatus(Status.BORROW_CANCELLED);
            em.merge(managed);

            em.getTransaction().commit();

            loadBorrowings();
            borrowTable.getSelectionModel().clearSelection();
            borrowSimsTable.setItems(FXCollections.observableArrayList());
            borrowConsTable.setItems(FXCollections.observableArrayList());
            borrowMetaLabel.setText("");

        } catch (Exception ex) {
            ex.printStackTrace();
            safeRollback(em);
            error("Error", "Failed to cancel borrowing.");
        } finally {
            em.close();
        }
    }


    private void renderUserHeader() {
        LoggedInUser u = AppSession.getCurrentUser();
        if (u == null || userLabel == null) return;

        String name = (!safeStr(u.getDisplayName()).isBlank()) ? u.getDisplayName() : u.getUsername();
        String role = (!safeStr(u.getRole()).isBlank()) ? (" (" + u.getRole() + ")") : "";
        String branch = (!safeStr(u.getBranchName()).isBlank()) ? (" · " + u.getBranchName()) : "";
        userLabel.setText(name + role + branch);
    }

    private void setupCombos() {
        statusCombo.setItems(FXCollections.observableArrayList(
                "ALL",
                Status.BORROW_ACTIVE,
                Status.BORROW_PARTIAL_RETURN,
                Status.BORROW_CLOSED,
                Status.BORROW_CANCELLED
        ));
        statusCombo.getSelectionModel().select("ALL");

        deptCombo.setItems(loadDepartments());
        deptCombo.setCellFactory(cb -> new ListCell<>() {
            @Override
            protected void updateItem(Department item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : safeStr(item.getName()));
            }
        });
        deptCombo.setButtonCell(deptCombo.getCellFactory().call(null));
    }

    private void setupBorrowTableColumns() {
        colId.setCellValueFactory(cd -> new SimpleIntegerProperty(cd.getValue().getId()));
        colCode.setCellValueFactory(cd -> new SimpleStringProperty(safeStr(cd.getValue().getBorrowCode())));
        colBranch.setCellValueFactory(cd ->
                new SimpleStringProperty(cd.getValue().getBranch() != null ? safeStr(cd.getValue().getBranch().getName()) : "")
        );
        colDept.setCellValueFactory(cd ->
                new SimpleStringProperty(cd.getValue().getDepartment() != null ? safeStr(cd.getValue().getDepartment().getName()) : "")
        );
        colStart.setCellValueFactory(cd ->
                new SimpleStringProperty(cd.getValue().getStartDate() == null ? "" : df.format(cd.getValue().getStartDate()))
        );
        colEnd.setCellValueFactory(cd ->
                new SimpleStringProperty(cd.getValue().getEndDate() == null ? "" : df.format(cd.getValue().getEndDate()))
        );
        colStatus.setCellValueFactory(cd -> new SimpleStringProperty(safeStr(cd.getValue().getStatus())));
    }

    private void setupDetailsTables() {
        colSimTag.setCellValueFactory(cd ->
                new SimpleStringProperty(cd.getValue().getSimulator() != null ? safeStr(cd.getValue().getSimulator().getTag()) : "")
        );

        colSimModel.setCellValueFactory(cd -> {
            String model = "";
            if (cd.getValue().getSimulator() != null && cd.getValue().getSimulator().getModel() != null) {
                model = safeStr(cd.getValue().getSimulator().getModel().getModelName());
            }
            return new SimpleStringProperty(model);
        });

        colSimSN.setCellValueFactory(cd ->
                new SimpleStringProperty(cd.getValue().getSimulator() != null ? safeStr(cd.getValue().getSimulator().getSerialNumber()) : "")
        );

        colSimStatus.setCellValueFactory(cd ->
                new SimpleStringProperty(cd.getValue().getSimulator() != null ? safeStr(cd.getValue().getSimulator().getStatus()) : "")
        );

        colSimNextCal.setCellValueFactory(cd -> {
            if (cd.getValue().getSimulator() == null || cd.getValue().getSimulator().getNextCalibrationDate() == null) {
                return new SimpleStringProperty("");
            }
            return new SimpleStringProperty(df.format(cd.getValue().getSimulator().getNextCalibrationDate()));
        });

        colCondOut.setCellValueFactory(cd -> new SimpleStringProperty(safeStr(cd.getValue().getConditionOut())));
        colCondIn.setCellValueFactory(cd -> new SimpleStringProperty(safeStr(cd.getValue().getConditionIn())));
        colReturnNotes.setCellValueFactory(cd -> new SimpleStringProperty(safeStr(cd.getValue().getReturnNotes())));

        colConsName.setCellValueFactory(cd -> {
            String name = "";
            if (cd.getValue().getStock() != null && cd.getValue().getStock().getConsumable() != null) {
                name = safeStr(cd.getValue().getStock().getConsumable().getItemName());
            }
            return new SimpleStringProperty(name);
        });

        colConsMeasure.setCellValueFactory(cd -> {
            String meas = "";
            if (cd.getValue().getStock() != null && cd.getValue().getStock().getConsumable() != null) {
                meas = safeStr(cd.getValue().getStock().getConsumable().getMeasure());
            }
            return new SimpleStringProperty(meas);
        });

        colConsQty.setCellValueFactory(cd ->
                new SimpleIntegerProperty(cd.getValue().getQuantity() == null ? 0 : cd.getValue().getQuantity())
        );

        colConsAvailNow.setCellValueFactory(cd -> {
            if (cd.getValue().getStock() == null || cd.getValue().getStock().getAvailableQuantity() == null) {
                return new SimpleIntegerProperty(0);
            }
            return new SimpleIntegerProperty(cd.getValue().getStock().getAvailableQuantity());
        });

        colConsLastCount.setCellValueFactory(cd -> {
            if (cd.getValue().getStock() == null || cd.getValue().getStock().getLastCountDate() == null) {
                return new SimpleStringProperty("");
            }
            return new SimpleStringProperty(df.format(cd.getValue().getStock().getLastCountDate()));
        });

        colConsBranch.setCellValueFactory(cd -> {
            String br = "";
            if (cd.getValue().getStock() != null && cd.getValue().getStock().getBranch() != null) {
                br = safeStr(cd.getValue().getStock().getBranch().getName());
            }
            return new SimpleStringProperty(br);
        });

        colConsStockId.setCellValueFactory(cd ->
                new SimpleIntegerProperty(cd.getValue().getStock() == null ? 0 : cd.getValue().getStock().getId())
        );
    }

    private void loadBorrowings() {
        LoggedInUser u = AppSession.getCurrentUser();

        String needle = safeLower(searchField.getText());
        String status = statusCombo.getValue();
        Department dept = deptCombo.getValue();
        LocalDate from = dpFrom.getValue();
        LocalDate to = dpTo.getValue();

        boolean filterByBranch = (u != null
                && u.getBranchName() != null
                && !u.getBranchName().isBlank()
                && (u.getRole() == null || !u.getRole().equalsIgnoreCase("ADMIN")));

        EntityManager em = JPA.em();
        try {
            String jpql =
                    "SELECT b FROM Borrowing b " +
                    "WHERE 1=1 " +
                    (filterByBranch ? "AND b.branch.name = :branch " : "") +
                    ((status != null && !"ALL".equalsIgnoreCase(status)) ? "AND b.status = :status " : "") +
                    (dept != null ? "AND b.department.id = :deptId " : "") +
                    (from != null ? "AND b.startDate >= :from " : "") +
                    (to != null ? "AND b.startDate <= :to " : "") +
                    (!needle.isEmpty()
                            ? "AND (LOWER(b.borrowCode) LIKE :needle OR LOWER(b.notes) LIKE :needle OR LOWER(b.department.name) LIKE :needle) "
                            : "") +
                    "ORDER BY b.startDate DESC, b.id DESC";

            TypedQuery<Borrowing> q = em.createQuery(jpql, Borrowing.class);

            if (filterByBranch) q.setParameter("branch", u.getBranchName());
            if (status != null && !"ALL".equalsIgnoreCase(status)) q.setParameter("status", status);
            if (dept != null) q.setParameter("deptId", dept.getId());
            if (from != null) q.setParameter("from", from);
            if (to != null) q.setParameter("to", to);
            if (!needle.isEmpty()) q.setParameter("needle", "%" + needle + "%");

            List<Borrowing> rows = q.getResultList();
            borrowTable.setItems(FXCollections.observableArrayList(rows));

            borrowMetaLabel.setText("Loaded " + rows.size() + " borrowing record(s).");

        } finally {
            em.close();
        }
    }

    private void loadDetails(Borrowing b) {
        EntityManager em = JPA.em();
        try {
            List<BorrowSim> sims = em.createQuery(
                    "SELECT bs FROM BorrowSim bs " +
                    "JOIN FETCH bs.simulator s " +
                    "JOIN FETCH s.model m " +
                    "WHERE bs.borrowing.id = :bid " +
                    "ORDER BY s.tag", BorrowSim.class)
                    .setParameter("bid", b.getId())
                    .getResultList();

            List<BorrowCons> cons = em.createQuery(
                    "SELECT bc FROM BorrowCons bc " +
                    "JOIN FETCH bc.stock st " +
                    "JOIN FETCH st.consumable c " +
                    "JOIN FETCH st.branch br " +
                    "WHERE bc.borrowing.id = :bid " +
                    "ORDER BY c.itemName", BorrowCons.class)
                    .setParameter("bid", b.getId())
                    .getResultList();

            borrowSimsTable.setItems(FXCollections.observableArrayList(sims));
            borrowConsTable.setItems(FXCollections.observableArrayList(cons));

            updateMeta(b);
        } finally {
            em.close();
        }
    }

    private void updateMeta(Borrowing b) {
        String code = safeStr(b.getBorrowCode());
        String st = safeStr(b.getStatus());
        String notes = safeStr(b.getNotes());
        borrowMetaLabel.setText("Selected: " + code + " · Status: " + st + (notes.isBlank() ? "" : " · Notes: " + notes));
    }

    private ObservableList<Department> loadDepartments() {
        EntityManager em = JPA.em();
        try {
            List<Department> deps = em.createQuery("SELECT d FROM Department d ORDER BY d.name", Department.class)
                    .getResultList();
            return FXCollections.observableArrayList(deps);
        } finally {
            em.close();
        }
    }

    private List<Simulator> loadAvailableSimulatorsForBranch(EntityManager em, String branchName) {
        return em.createQuery(
                "SELECT s FROM Simulator s " +
                "JOIN FETCH s.model m " +
                "WHERE s.status = :st " +
                "AND (:branch IS NULL OR s.branch.name = :branch) " +
                "ORDER BY s.tag", Simulator.class)
                .setParameter("st", Status.SIM_AVAILABLE)
                .setParameter("branch", branchName)
                .getResultList();
    }

    private List<Stock> loadStockWithAvailableForBranch(EntityManager em, String branchName) {
        return em.createQuery(
                "SELECT st FROM Stock st " +
                "JOIN FETCH st.consumable c " +
                "JOIN FETCH st.branch br " +
                "WHERE (st.availableQuantity IS NOT NULL AND st.availableQuantity > 0) " +
                "AND (:branch IS NULL OR st.branch.name = :branch) " +
                "ORDER BY c.itemName", Stock.class)
                .setParameter("branch", branchName)
                .getResultList();
    }

    private long countBorrowedSims(EntityManager em, Integer borrowId) {
        return em.createQuery(
                "SELECT COUNT(bs) FROM BorrowSim bs " +
                "WHERE bs.borrowing.id = :bid " +
                "AND bs.simulator.status = :st", Long.class)
                .setParameter("bid", borrowId)
                .setParameter("st", Status.SIM_BORROWED)
                .getSingleResult();
    }

    private int nextBorrowId(EntityManager em) {
        Integer max = em.createQuery("SELECT COALESCE(MAX(b.id), 0) FROM Borrowing b", Integer.class)
                .getSingleResult();
        return (max == null ? 1 : (max + 1));
    }

    private Branch resolveUserBranch(EntityManager em, LoggedInUser u) {
        if (u == null || u.getBranchName() == null || u.getBranchName().isBlank()) return null;
        List<Branch> brs = em.createQuery("SELECT b FROM Branch b WHERE b.name = :n", Branch.class)
                .setParameter("n", u.getBranchName())
                .getResultList();
        return brs.isEmpty() ? null : brs.get(0);
    }

    private boolean isEditableBorrowing(Borrowing b) {
        if (b == null || b.getStatus() == null) return false;
        String st = b.getStatus();
        return st.equalsIgnoreCase(Status.BORROW_ACTIVE) || st.equalsIgnoreCase(Status.BORROW_PARTIAL_RETURN);
    }

    private void selectBorrowingById(Integer id) {
        if (id == null) return;
        for (Borrowing b : borrowTable.getItems()) {
            if (b.getId() != null && b.getId().equals(id)) {
                borrowTable.getSelectionModel().select(b);
                borrowTable.scrollTo(b);
                break;
            }
        }
    }

    /* ========= Small helpers ========= */
    private void warn(String title, String msg) {
        Alert a = new Alert(AlertType.WARNING);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }

    private void error(String title, String msg) {
        Alert a = new Alert(AlertType.ERROR);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }

    private void safeRollback(EntityManager em) {
        try {
            if (em != null && em.getTransaction().isActive()) em.getTransaction().rollback();
        } catch (Exception ignored) {}
    }

    private Integer parsePositiveInt(String s) {
        try {
            int v = Integer.parseInt(s.trim());
            return v > 0 ? v : null;
        } catch (Exception ex) { return null; }
    }

    private String safeStr(String s) { return s == null ? "" : s; }
    private String safeTrim(String s) { return (s == null) ? null : s.trim(); }
    private String safeLower(String s) { return (s == null) ? "" : s.trim().toLowerCase(); }
}
