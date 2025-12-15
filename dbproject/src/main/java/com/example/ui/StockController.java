package com.example.ui;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import com.example.app.JPA;
import com.example.app.ViewUtil;
import com.example.domain.Branch;
import com.example.domain.Consumable;
import com.example.domain.Stock;
import com.example.session.AppSession;
import com.example.session.LoggedInUser;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Window;

public class StockController {

    @FXML private Label userLabel;

    @FXML private TableView<Stock>          stockTable;
    @FXML private TableColumn<Stock,String> consColumn;
    @FXML private TableColumn<Stock,String> measureColumn;
    @FXML private TableColumn<Stock,String> branchColumn;
    @FXML private TableColumn<Stock,Number> availColumn;
    @FXML private TableColumn<Stock,Number> resColumn;
    @FXML private TableColumn<Stock,String> lastCountCol;

    @FXML private TextField   searchField;
    @FXML private ComboBox<String> sortByCombo;
    @FXML private Label       infoLabel;

    private final DateTimeFormatter dateFmt =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final ObservableList<Stock> masterData =
            FXCollections.observableArrayList();
    private FilteredList<Stock> filteredData;
    private SortedList<Stock>   sortedData;

    @FXML
    public void initialize() {
        // Show logged in user
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
        consColumn.setCellValueFactory(cd ->
                new SimpleStringProperty(
                        cd.getValue().getConsumable() != null &&
                        cd.getValue().getConsumable().getItemName() != null
                                ? cd.getValue().getConsumable().getItemName()
                                : ""
                ));

        measureColumn.setCellValueFactory(cd ->
                new SimpleStringProperty(
                        cd.getValue().getConsumable() != null &&
                        cd.getValue().getConsumable().getMeasure() != null
                                ? cd.getValue().getConsumable().getMeasure()
                                : ""
                ));

        branchColumn.setCellValueFactory(cd ->
                new SimpleStringProperty(
                        cd.getValue().getBranch() != null &&
                        cd.getValue().getBranch().getName() != null
                                ? cd.getValue().getBranch().getName()
                                : ""
                ));

        availColumn.setCellValueFactory(cd ->
                new SimpleIntegerProperty(
                        cd.getValue().getAvailableQuantity() != null
                                ? cd.getValue().getAvailableQuantity()
                                : 0
                ));

        resColumn.setCellValueFactory(cd ->
                new SimpleIntegerProperty(
                        cd.getValue().getReservedQuantity() != null
                                ? cd.getValue().getReservedQuantity()
                                : 0
                ));

        lastCountCol.setCellValueFactory(cd ->
                new SimpleStringProperty(
                        cd.getValue().getLastCountDate() != null
                                ? cd.getValue().getLastCountDate().format(dateFmt)
                                : ""
                ));

        // Wrappers
        filteredData = new FilteredList<>(masterData, s -> true);
        sortedData   = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(stockTable.comparatorProperty());
        stockTable.setItems(sortedData);

        setupSearch();
        setupSortCombo();
        loadStock();
    }

    private void loadStock() {
        LoggedInUser u = AppSession.getCurrentUser();

        EntityManager em = JPA.em();
        try {
            boolean filterByBranch = (u != null &&
                    u.getBranchName() != null &&
                    !u.getBranchName().isBlank() &&
                    (u.getRole() == null ||
                     !u.getRole().equalsIgnoreCase("ADMIN")));

            String jpql =
                    "SELECT s FROM Stock s " +
                    "JOIN FETCH s.consumable c " +
                    "JOIN FETCH s.branch b";
            if (filterByBranch) {
                jpql += " WHERE b.name = :branchName";
            }
            jpql += " ORDER BY c.itemName, b.name";

            TypedQuery<Stock> q = em.createQuery(jpql, Stock.class);
            if (filterByBranch) {
                q.setParameter("branchName", u.getBranchName());
            }

            List<Stock> list = q.getResultList();
            masterData.setAll(list);

            if (infoLabel != null) {
                infoLabel.setText(list.isEmpty()
                        ? "No stock rows found."
                        : "");
            }
        } catch (Exception e) {
            if (infoLabel != null) {
                infoLabel.setText("Error loading stock: " + e.getMessage());
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

            filteredData.setPredicate(st -> {
                if (needle.isEmpty()) return true;

                if (st.getConsumable() != null &&
                        st.getConsumable().getItemName() != null &&
                        st.getConsumable().getItemName().toLowerCase().contains(needle))
                    return true;

                if (st.getConsumable() != null &&
                        st.getConsumable().getMeasure() != null &&
                        st.getConsumable().getMeasure().toLowerCase().contains(needle))
                    return true;

                if (st.getBranch() != null &&
                        st.getBranch().getName() != null &&
                        st.getBranch().getName().toLowerCase().contains(needle))
                    return true;

                if (st.getAvailableQuantity() != null &&
                        st.getAvailableQuantity().toString().contains(needle))
                    return true;

                if (st.getReservedQuantity() != null &&
                        st.getReservedQuantity().toString().contains(needle))
                    return true;

                if (st.getLastCountDate() != null &&
                        st.getLastCountDate().format(dateFmt).toLowerCase().contains(needle))
                    return true;

                return false;
            });
        });
    }

    private void setupSortCombo() {
        if (sortByCombo == null) return;

        sortByCombo.setItems(FXCollections.observableArrayList(
                "Consumable", "Branch", "Available", "Reserved", "Last count"
        ));
        sortByCombo.getSelectionModel().select("Consumable");

        sortByCombo.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
            if (sel == null) return;

            stockTable.getSortOrder().clear();
            TableColumn<Stock, ?> col = null;

            switch (sel) {
                case "Consumable": col = consColumn;   break;
                case "Branch":     col = branchColumn; break;
                case "Available":  col = availColumn;  break;
                case "Reserved":   col = resColumn;    break;
                case "Last count": col = lastCountCol; break;
            }

            if (col != null) {
                col.setSortType(TableColumn.SortType.ASCENDING);
                stockTable.getSortOrder().add(col);
                stockTable.sort();
            }
        });
    }

    @FXML
    private void onTopUpStock() {
        Stock selected = stockTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            if (infoLabel != null)
                infoLabel.setText("Select a stock row first.");
            return;
        }

        Dialog<TopUpData> dialog = new Dialog<>();

        Window owner = stockTable.getScene() != null
                ? stockTable.getScene().getWindow()
                : null;
        if (owner != null) {
            dialog.initOwner(owner);
        }
        dialog.initModality(Modality.WINDOW_MODAL);

        dialog.setTitle("Top up stock");
        dialog.setHeaderText("Top up available quantity for: " +
                (selected.getConsumable() != null ? selected.getConsumable().getItemName() : "???") +
                " @ " +
                (selected.getBranch() != null ? selected.getBranch().getName() : "???"));

        ButtonType saveButtonType =
                new ButtonType("Apply", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        TextField amountField = new TextField();
        amountField.setPromptText("Amount to add (integer > 0)");

        DatePicker countDatePicker = new DatePicker(LocalDate.now());
        countDatePicker.setPromptText("New last count date (optional)");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(8);
        grid.setPadding(new Insets(10, 10, 10, 10));

        int row = 0;
        grid.add(new Label("Top-up amount:"), 0, row);
        grid.add(amountField,               1, row++);

        grid.add(new Label("Last count date:"), 0, row);
        grid.add(countDatePicker,              1, row++);

        dialog.getDialogPane().setContent(grid);

        Node saveButton = dialog.getDialogPane().lookupButton(saveButtonType);
        saveButton.setDisable(true);

        Runnable validate = () -> {
            String text = amountField.getText() == null ? "" : amountField.getText().trim();
            boolean ok = false;
            if (!text.isEmpty()) {
                try {
                    int val = Integer.parseInt(text);
                    ok = val > 0;
                } catch (NumberFormatException ignored) {}
            }
            saveButton.setDisable(!ok);
        };

        amountField.textProperty().addListener((obs, ov, nv) -> validate.run());

        dialog.setResultConverter(btn -> {
            if (btn == saveButtonType) {
                int amount = Integer.parseInt(amountField.getText().trim());
                LocalDate date = countDatePicker.getValue();  // can be null
                return new TopUpData(amount, date);
            }
            return null;
        });

        Optional<TopUpData> result = dialog.showAndWait();
        if (result.isEmpty()) {
            return; // cancelled
        }

        TopUpData data   = result.get();
        int       toAdd  = data.topUpAmount();
        LocalDate newDate = data.lastCountDate();

        EntityManager em = JPA.em();
        try {
            em.getTransaction().begin();

            Stock managed = em.merge(selected);

            int currentAvail = managed.getAvailableQuantity() != null
                    ? managed.getAvailableQuantity()
                    : 0;
            managed.setAvailableQuantity(currentAvail + toAdd);

            if (newDate != null) {
                managed.setLastCountDate(newDate);
            }

            em.getTransaction().commit();

            // update UI copy
            selected.setAvailableQuantity(currentAvail + toAdd);
            if (newDate != null) {
                selected.setLastCountDate(newDate);
            }
            stockTable.refresh();

            if (infoLabel != null) {
                infoLabel.setText("Stock topped up by " + toAdd + " units.");
            }

        } catch (Exception e) {
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
            if (infoLabel != null)
                infoLabel.setText("Error topping up stock: " + e.getMessage());
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

	@FXML
	private void onTopUpReserved() {
	    Stock selected = stockTable.getSelectionModel().getSelectedItem();
	    if (selected == null) {
	        if (infoLabel != null)
	            infoLabel.setText("Select a stock row first.");
	        return;
	    }

	    Dialog<TopUpData> dialog = new Dialog<>();

	    Window owner = stockTable.getScene() != null
	            ? stockTable.getScene().getWindow()
	            : null;
	    if (owner != null) {
	        dialog.initOwner(owner);
	    }
	    dialog.initModality(Modality.WINDOW_MODAL);

	    dialog.setTitle("Top up reserved");
	    dialog.setHeaderText("Top up RESERVED quantity for: " +
	            (selected.getConsumable() != null ? selected.getConsumable().getItemName() : "???") +
	            " @ " +
	            (selected.getBranch() != null ? selected.getBranch().getName() : "???"));

	    ButtonType saveButtonType =
	            new ButtonType("Apply", ButtonBar.ButtonData.OK_DONE);
	    dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

	    TextField amountField = new TextField();
	    amountField.setPromptText("Amount to add (integer > 0)");

	    DatePicker countDatePicker = new DatePicker(LocalDate.now());
	    countDatePicker.setPromptText("New last count date (optional)");

	    GridPane grid = new GridPane();
	    grid.setHgap(10);
	    grid.setVgap(8);
	    grid.setPadding(new Insets(10, 10, 10, 10));

	    int row = 0;
	    grid.add(new Label("Top-up amount:"), 0, row);
	    grid.add(amountField,               1, row++);

	    grid.add(new Label("Last count date:"), 0, row);
	    grid.add(countDatePicker,              1, row++);

	    dialog.getDialogPane().setContent(grid);

	    Node saveButton = dialog.getDialogPane().lookupButton(saveButtonType);
	    saveButton.setDisable(true);

	    Runnable validate = () -> {
	        String text = amountField.getText() == null ? "" : amountField.getText().trim();
	        boolean ok = false;
	        if (!text.isEmpty()) {
	            try {
	                int val = Integer.parseInt(text);
	                ok = val > 0;
	            } catch (NumberFormatException ignored) {}
	        }
	        saveButton.setDisable(!ok);
	    };

	    amountField.textProperty().addListener((obs, ov, nv) -> validate.run());

	    dialog.setResultConverter(btn -> {
	        if (btn == saveButtonType) {
	            int amount = Integer.parseInt(amountField.getText().trim());
	            LocalDate date = countDatePicker.getValue();  // can be null
	            return new TopUpData(amount, date);
	        }
	        return null;
	    });

	    Optional<TopUpData> result = dialog.showAndWait();
	    if (result.isEmpty()) {
	        return; // cancelled
	    }

	    TopUpData data   = result.get();
	    int       toAdd  = data.topUpAmount();
	    LocalDate newDate = data.lastCountDate();

	    EntityManager em = JPA.em();
	    try {
	        em.getTransaction().begin();

	        // Reattach with merge
	        Stock managed = em.merge(selected);

	        int currentReserved = managed.getReservedQuantity() != null
	                ? managed.getReservedQuantity()
	                : 0;
	        managed.setReservedQuantity(currentReserved + toAdd);

	        if (newDate != null) {
	            managed.setLastCountDate(newDate);
	        }

	        em.getTransaction().commit();

	        // update UI copy
	        selected.setReservedQuantity(currentReserved + toAdd);
	        if (newDate != null) {
	            selected.setLastCountDate(newDate);
	        }
	        stockTable.refresh();

	        if (infoLabel != null) {
	            infoLabel.setText("Reserved quantity topped up by " + toAdd + " units.");
	        }

	    } catch (Exception e) {
	        if (em.getTransaction().isActive())
	            em.getTransaction().rollback();
	        if (infoLabel != null)
	            infoLabel.setText("Error topping up reserved: " + e.getMessage());
	        e.printStackTrace();
	    } finally {
	        em.close();
	    }
	}

	@FXML
	private void onTopUpFromReserved() {
	    Stock selected = stockTable.getSelectionModel().getSelectedItem();
	    if (selected == null) {
	        if (infoLabel != null)
	            infoLabel.setText("Select a stock row first.");
	        return;
	    }

	    Dialog<TopUpData> dialog = new Dialog<>();

	    Window owner = stockTable.getScene() != null
	            ? stockTable.getScene().getWindow()
	            : null;
	    if (owner != null) {
	        dialog.initOwner(owner);
	    }
	    dialog.initModality(Modality.WINDOW_MODAL);

	    dialog.setTitle("Top up from reserved");
	    dialog.setHeaderText("Move quantity FROM RESERVED to AVAILABLE for: " +
	            (selected.getConsumable() != null ? selected.getConsumable().getItemName() : "???") +
	            " @ " +
	            (selected.getBranch() != null ? selected.getBranch().getName() : "???"));

	    ButtonType saveButtonType =
	            new ButtonType("Apply", ButtonBar.ButtonData.OK_DONE);
	    dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

	    TextField amountField = new TextField();
	    amountField.setPromptText("Amount to move (integer > 0)");

	    DatePicker countDatePicker = new DatePicker(LocalDate.now());
	    countDatePicker.setPromptText("New last count date (optional)");

	    GridPane grid = new GridPane();
	    grid.setHgap(10);
	    grid.setVgap(8);
	    grid.setPadding(new Insets(10, 10, 10, 10));

	    int row = 0;
	    grid.add(new Label("Amount to move:"), 0, row);
	    grid.add(amountField,                 1, row++);

	    grid.add(new Label("Last count date:"), 0, row);
	    grid.add(countDatePicker,              1, row++);

	    dialog.getDialogPane().setContent(grid);

	    Node saveButton = dialog.getDialogPane().lookupButton(saveButtonType);
	    saveButton.setDisable(true);

	    Runnable validate = () -> {
	        String text = amountField.getText() == null ? "" : amountField.getText().trim();
	        boolean ok = false;
	        if (!text.isEmpty()) {
	            try {
	                int val = Integer.parseInt(text);
	                ok = val > 0;
	            } catch (NumberFormatException ignored) {}
	        }
	        saveButton.setDisable(!ok);
	    };

	    amountField.textProperty().addListener((obs, ov, nv) -> validate.run());

	    dialog.setResultConverter(btn -> {
	        if (btn == saveButtonType) {
	            int amount = Integer.parseInt(amountField.getText().trim());
	            LocalDate date = countDatePicker.getValue();  // can be null
	            return new TopUpData(amount, date);
	        }
	        return null;
	    });

	    Optional<TopUpData> result = dialog.showAndWait();
	    if (result.isEmpty()) {
	        return; // cancelled
	    }

	    TopUpData data   = result.get();
	    int       toMove = data.topUpAmount();
	    LocalDate newDate = data.lastCountDate();

	    EntityManager em = JPA.em();
	    try {
	        em.getTransaction().begin();

	        // Reattach with merge
	        Stock managed = em.merge(selected);

	        int currentReserved = managed.getReservedQuantity() != null
	                ? managed.getReservedQuantity()
	                : 0;
	        int currentAvail = managed.getAvailableQuantity() != null
	                ? managed.getAvailableQuantity()
	                : 0;

	        if (toMove > currentReserved) {
	            throw new IllegalArgumentException(
	                    "Cannot move " + toMove + " from reserved; only " + currentReserved + " reserved.");
	        }

	        managed.setReservedQuantity(currentReserved - toMove);
	        managed.setAvailableQuantity(currentAvail + toMove);

	        if (newDate != null) {
	            managed.setLastCountDate(newDate);
	        }

	        em.getTransaction().commit();

	        // update UI copy
	        selected.setReservedQuantity(currentReserved - toMove);
	        selected.setAvailableQuantity(currentAvail + toMove);
	        if (newDate != null) {
	            selected.setLastCountDate(newDate);
	        }
	        stockTable.refresh();

	        if (infoLabel != null) {
	            infoLabel.setText("Moved " + toMove + " units from reserved to available.");
	        }

	    } catch (Exception e) {
	        if (em.getTransaction().isActive())
	            em.getTransaction().rollback();
	        if (infoLabel != null)
	            infoLabel.setText("Error moving from reserved: " + e.getMessage());
	        e.printStackTrace();
	    } finally {
	        em.close();
	    }
	}

    private record TopUpData(int topUpAmount, LocalDate lastCountDate) {}

    @FXML
    private void onAddConsumable() {
        Dialog<Consumable> dialog = new Dialog<>();

        Window owner = stockTable.getScene() != null
                ? stockTable.getScene().getWindow()
                : null;
        if (owner != null) {
            dialog.initOwner(owner);
        }
        dialog.initModality(Modality.WINDOW_MODAL);

        dialog.setTitle("New consumable");
        dialog.setHeaderText("Create a new consumable item");

        ButtonType saveButtonType =
                new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        TextField nameField = new TextField();
        nameField.setPromptText("Item name (required)");

        TextField measureField = new TextField();
        measureField.setPromptText("Measure (EA, BOX, etc.) (required)");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(8);
        grid.setPadding(new Insets(10, 10, 10, 10));

        int row = 0;
        grid.add(new Label("Item name:"), 0, row);
        grid.add(nameField,              1, row++);
        grid.add(new Label("Measure:"),  0, row);
        grid.add(measureField,           1, row++);

        dialog.getDialogPane().setContent(grid);

        Node saveButton = dialog.getDialogPane().lookupButton(saveButtonType);
        saveButton.setDisable(true);

        Runnable validate = () -> {
            String n = nameField.getText() == null ? "" : nameField.getText().trim();
            String m = measureField.getText() == null ? "" : measureField.getText().trim();
            saveButton.setDisable(n.isEmpty() || m.isEmpty());
        };

        nameField.textProperty().addListener((o, ov, nv) -> validate.run());
        measureField.textProperty().addListener((o, ov, nv) -> validate.run());

        dialog.setResultConverter(btn -> {
            if (btn == saveButtonType) {
                Consumable c = new Consumable();
                c.setItemName(nameField.getText().trim());
                c.setMeasure(measureField.getText().trim());
                return c;
            }
            return null;
        });

        Optional<Consumable> result = dialog.showAndWait();
        if (result.isEmpty()) return;

        Consumable form = result.get();

        EntityManager em = JPA.em();
        try {
            em.getTransaction().begin();
            Consumable c = new Consumable();
            c.setItemName(form.getItemName());
            c.setMeasure(form.getMeasure());
            em.persist(c);
            em.getTransaction().commit();

            if (infoLabel != null)
                infoLabel.setText("Consumable created: " + c.getItemName());
            // no need to reload stock; we haven’t created a stock row yet
        } catch (Exception e) {
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
            if (infoLabel != null)
                infoLabel.setText("Error creating consumable: " + e.getMessage());
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    @FXML
    private void onAddStock() {
        LoggedInUser u = AppSession.getCurrentUser();
        if (u == null || u.getBranchName() == null || u.getBranchName().isBlank()) {
            if (infoLabel != null)
                infoLabel.setText("Cannot determine your branch for new stock row.");
            return;
        }

        EntityManager em = JPA.em();
        try {
            // Load consumables
            List<Consumable> consumables = em.createQuery(
                    "SELECT c FROM Consumable c ORDER BY c.itemName",
                    Consumable.class
            ).getResultList();

            if (consumables.isEmpty()) {
                if (infoLabel != null)
                    infoLabel.setText("No consumables found. Create a consumable first.");
                return;
            }

            // Load branch for current user
            Branch branch = em.createQuery(
                    "SELECT b FROM Branch b WHERE b.name = :name",
                    Branch.class
            ).setParameter("name", u.getBranchName())
             .getSingleResult();

            // Dialog UI
            Dialog<NewStockData> dialog = new Dialog<>();

            Window owner = stockTable.getScene() != null
                    ? stockTable.getScene().getWindow()
                    : null;
            if (owner != null) dialog.initOwner(owner);
            dialog.initModality(Modality.WINDOW_MODAL);

            dialog.setTitle("New stock row");
            dialog.setHeaderText("Create stock for branch: " + branch.getName());

            ButtonType saveButtonType =
                    new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

            ComboBox<Consumable> consBox = new ComboBox<>();
            consBox.getItems().addAll(consumables);
            consBox.getSelectionModel().selectFirst();
            consBox.setPrefWidth(260);

            // Friendly display in LOV
            consBox.setCellFactory(cb -> new ListCell<>() {
                @Override
                protected void updateItem(Consumable item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        String name = item.getItemName() != null
                                ? item.getItemName()
                                : "Consumable #" + item.getId();
                        String m = item.getMeasure() != null ? item.getMeasure() : "";
                        setText(m.isEmpty() ? name : name + " (" + m + ")");
                    }
                }
            });
            consBox.setButtonCell(new ListCell<>() {
                @Override
                protected void updateItem(Consumable item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        String name = item.getItemName() != null
                                ? item.getItemName()
                                : "Consumable #" + item.getId();
                        String m = item.getMeasure() != null ? item.getMeasure() : "";
                        setText(m.isEmpty() ? name : name + " (" + m + ")");
                    }
                }
            });

            TextField availField = new TextField();
            availField.setPromptText("Available quantity (>= 0)");

            TextField resField = new TextField();
            resField.setPromptText("Reserved quantity (>= 0, optional)");

            DatePicker datePicker = new DatePicker(LocalDate.now());
            datePicker.setPromptText("Last count date");

            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(8);
            grid.setPadding(new Insets(10, 10, 10, 10));

            int row = 0;
            grid.add(new Label("Consumable:"), 0, row);
            grid.add(consBox,                1, row++);
            grid.add(new Label("Available:"), 0, row);
            grid.add(availField,             1, row++);
            grid.add(new Label("Reserved:"), 0, row);
            grid.add(resField,               1, row++);
            grid.add(new Label("Last count:"), 0, row);
            grid.add(datePicker,             1, row++);

            dialog.getDialogPane().setContent(grid);

            Node saveButton = dialog.getDialogPane().lookupButton(saveButtonType);
            saveButton.setDisable(true);

            Runnable validate = () -> {
                boolean ok = consBox.getValue() != null;
                String a = availField.getText() == null ? "" : availField.getText().trim();
                if (a.isEmpty()) {
                    ok = false;
                } else {
                    try {
                        int v = Integer.parseInt(a);
                        if (v < 0) ok = false;
                    } catch (NumberFormatException ex) {
                        ok = false;
                    }
                }
                String r = resField.getText() == null ? "" : resField.getText().trim();
                if (!r.isEmpty()) {
                    try {
                        int v = Integer.parseInt(r);
                        if (v < 0) ok = false;
                    } catch (NumberFormatException ex) {
                        ok = false;
                    }
                }
                saveButton.setDisable(!ok);
            };

            consBox.valueProperty().addListener((o, ov, nv) -> validate.run());
            availField.textProperty().addListener((o, ov, nv) -> validate.run());
            resField.textProperty().addListener((o, ov, nv) -> validate.run());

            dialog.setResultConverter(btn -> {
                if (btn == saveButtonType) {
                    Consumable c = consBox.getValue();
                    int avail = Integer.parseInt(availField.getText().trim());
                    String resText = resField.getText() == null ? "" : resField.getText().trim();
                    Integer reserved = resText.isEmpty()
                            ? 0
                            : Integer.parseInt(resText);
                    LocalDate d = datePicker.getValue();
                    return new NewStockData(c, avail, reserved, d);
                }
                return null;
            });

            Optional<NewStockData> result = dialog.showAndWait();
            if (result.isEmpty()) {
                return; // cancelled
            }

            NewStockData data = result.get();

            // Persist new Stock
            em.getTransaction().begin();

            Stock s = new Stock();
            s.setBranch(branch);
            s.setConsumable(em.getReference(Consumable.class, data.consumable().getId()));
            s.setAvailableQuantity(data.available());
            s.setReservedQuantity(data.reserved());
            s.setLastCountDate(data.lastCountDate());

            em.persist(s);
            em.getTransaction().commit();

            // update UI
            masterData.add(s);
            stockTable.refresh();

            if (infoLabel != null)
                infoLabel.setText("Stock row created for " +
                        data.consumable().getItemName() +
                        " @ " + branch.getName());

        } catch (Exception e) {
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
            if (infoLabel != null)
                infoLabel.setText("Error creating stock row: " + e.getMessage());
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    private record NewStockData(Consumable consumable,
                                int available,
                                int reserved,
                                LocalDate lastCountDate) {}

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
