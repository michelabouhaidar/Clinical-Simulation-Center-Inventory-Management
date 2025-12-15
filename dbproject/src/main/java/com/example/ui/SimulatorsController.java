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
import com.example.domain.Simulator;
import com.example.domain.SimulatorModel;
import com.example.domain.Status;
import com.example.session.AppSession;
import com.example.session.LoggedInUser;

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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Window;

public class SimulatorsController {

    @FXML private Label userLabel;

    @FXML private TableView<Simulator> simulatorTable;
    @FXML private TableColumn<Simulator, String> tagColumn;
    @FXML private TableColumn<Simulator, String> modelColumn;
    @FXML private TableColumn<Simulator, String> modelSpecsColumn;
    @FXML private TableColumn<Simulator, String> modelCalReqColumn;
    @FXML private TableColumn<Simulator, String> modelMaxDaysColumn;
    @FXML private TableColumn<Simulator, String> branchColumn;
    @FXML private TableColumn<Simulator, String> statusColumn;
    @FXML private TableColumn<Simulator, String> nextCalColumn;

    @FXML private TextField searchField;
    @FXML private ComboBox<String> sortByCombo;

    @FXML private Label infoLabel;

    private final DateTimeFormatter dateFmt =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // backing list for the table + filtering/sorting wrappers
    private final ObservableList<Simulator> masterData =
            FXCollections.observableArrayList();
    private FilteredList<Simulator> filteredData;
    private SortedList<Simulator> sortedData;

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

        tagColumn.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getTag()));

        modelColumn.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().getModel() != null ? cd.getValue().getModel().getModelName() : ""));

        modelSpecsColumn.setCellValueFactory(cd ->
                new SimpleStringProperty(
                        cd.getValue().getModel() != null &&
                        cd.getValue().getModel().getSpecs() != null
                                ? cd.getValue().getModel().getSpecs()
                                : ""
                ));

        modelCalReqColumn.setCellValueFactory(cd -> {
            if (cd.getValue().getModel() != null &&
                cd.getValue().getModel().getCalReq() != null) {
                return new SimpleStringProperty(
                        cd.getValue().getModel().getCalReq() ? "Yes" : "No"
                );
            }
            return new SimpleStringProperty("");
        });

        modelMaxDaysColumn.setCellValueFactory(cd -> {
            if (cd.getValue().getModel() != null &&
                cd.getValue().getModel().getMaxDays() != null) {
                return new SimpleStringProperty(
                        cd.getValue().getModel().getMaxDays().toString()
                );
            }
            return new SimpleStringProperty("");
        });

        branchColumn.setCellValueFactory(cd ->
                new SimpleStringProperty(
                        cd.getValue().getBranch() != null
                                ? cd.getValue().getBranch().getName()
                                : ""
                ));

        statusColumn.setCellValueFactory(cd ->
                new SimpleStringProperty(
                        cd.getValue().getStatus() != null
                                ? cd.getValue().getStatus()
                                : ""
                ));

        nextCalColumn.setCellValueFactory(cd ->
                new SimpleStringProperty(
                        cd.getValue().getNextCalibrationDate() != null
                                ? cd.getValue().getNextCalibrationDate().format(dateFmt)
                                : ""
                ));

        // Wrap master data with Filtered + Sorted lists
        filteredData = new FilteredList<>(masterData, s -> true);
        sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(simulatorTable.comparatorProperty());
        simulatorTable.setItems(sortedData);

        setupSearch();
        setupSortCombo();

        loadSimulators();
    }

    /** Load simulators from DB (filtered by user's branch if not admin). */
    private void loadSimulators() {
        LoggedInUser u = AppSession.getCurrentUser();

        EntityManager em = JPA.em();
        try {
            String jpql = "SELECT s FROM Simulator s";
            boolean filterByBranch =
                    (u != null &&
                     u.getBranchName() != null &&
                     !u.getBranchName().isBlank() &&
                     (u.getRole() == null ||
                      !u.getRole().equalsIgnoreCase("ADMIN")));

            if (filterByBranch) {
                // Non-admin: only their own branch
                jpql += " WHERE s.branch.name = :branchName";
            }

            jpql += " ORDER BY s.tag";

            TypedQuery<Simulator> q = em.createQuery(jpql, Simulator.class);
            if (filterByBranch) {
                q.setParameter("branchName", u.getBranchName());
            }

            List<Simulator> sims = q.getResultList();
            masterData.setAll(sims);

            if (infoLabel != null) {
                if (sims.isEmpty()) {
                    infoLabel.setText("No simulators found for your branch.");
                } else {
                    infoLabel.setText("");  // clear
                }
            }
        } catch (Exception e) {
            if (infoLabel != null) {
                infoLabel.setText("Error loading simulators: " + e.getMessage());
            }
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    /** Live search on main fields. */
    private void setupSearch() {
        if (searchField == null) return;

        searchField.textProperty().addListener((obs, old, value) -> {
            String needle = (value == null) ? "" : value.trim().toLowerCase();

            filteredData.setPredicate(sim -> {
                if (needle.isEmpty()) return true;

                if (sim.getTag() != null &&
                        sim.getTag().toLowerCase().contains(needle)) return true;

                if (sim.getModel() != null &&
                        sim.getModel().getModelName() != null &&
                        sim.getModel().getModelName().toLowerCase().contains(needle)) return true;

                if (sim.getModel() != null &&
                        sim.getModel().getSpecs() != null &&
                        sim.getModel().getSpecs().toLowerCase().contains(needle)) return true;

                if (sim.getBranch() != null &&
                        sim.getBranch().getName() != null &&
                        sim.getBranch().getName().toLowerCase().contains(needle)) return true;

                if (sim.getStatus() != null &&
                        sim.getStatus().toLowerCase().contains(needle)) return true;

                if (sim.getNextCalibrationDate() != null &&
                        sim.getNextCalibrationDate().format(dateFmt).toLowerCase().contains(needle))
                    return true;

                return false;
            });
        });
    }

    /** Sort combo that drives TableView sort order. */
    private void setupSortCombo() {
        if (sortByCombo == null) return;

        sortByCombo.setItems(FXCollections.observableArrayList(
                "Tag", "Model", "Branch", "Status", "Next calibration"
        ));
        sortByCombo.getSelectionModel().select("Tag");

        sortByCombo.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
            if (sel == null) return;

            simulatorTable.getSortOrder().clear();
            TableColumn<Simulator, ?> col = null;

            switch (sel) {
                case "Tag":              col = tagColumn;     break;
                case "Model":            col = modelColumn;   break;
                case "Branch":           col = branchColumn;  break;
                case "Status":           col = statusColumn;  break;
                case "Next calibration": col = nextCalColumn; break;
                default: break;
            }

            if (col != null) {
                col.setSortType(TableColumn.SortType.ASCENDING);
                simulatorTable.getSortOrder().add(col);
                simulatorTable.sort();
            }
        });
    }

    // -------- Actions from right panel --------

    /** Mark selected simulator as OUT OF SERVICE. */
	@FXML
	private void onMarkOutOfService() {
	    Simulator selected = simulatorTable.getSelectionModel().getSelectedItem();
	    if (selected == null) {
	        if (infoLabel != null)
	            infoLabel.setText("Select a simulator first.");
	        return;
	    }

	    EntityManager em = JPA.em();
	    try {
	        em.getTransaction().begin();

	        // Re-attach the entity from DB (managed instance)
	        Simulator managed = em.merge(selected);

	        // Change the mapped field
	        managed.setStatus(Status.SIM_OUT_OF_SERVICE);

	        // Force SQL generation *now* (before commit)
	        em.flush();

	        em.getTransaction().commit();

	        // Reload table from DB so UI reflects real state
	        loadSimulators();

	        if (infoLabel != null)
	            infoLabel.setText("Simulator marked as OUT OF SERVICE.");
	    } catch (Exception e) {
	        if (em.getTransaction().isActive())
	            em.getTransaction().rollback();
	        if (infoLabel != null)
	            infoLabel.setText("Error updating status: " + e.getMessage());
	        e.printStackTrace();
	    } finally {
	        em.close();
	    }
	}

    @FXML
    private void onAddSimulator() {
        LoggedInUser u = AppSession.getCurrentUser();
        if (u == null || u.getBranchName() == null || u.getBranchName().isBlank()) {
            if (infoLabel != null)
                infoLabel.setText("Cannot determine branch for new simulator.");
            return;
        }

        EntityManager em = JPA.em();
        try {
            // 1) Load ALL models (no branch filter)
            List<SimulatorModel> models = em.createQuery(
                    "SELECT m FROM SimulatorModel m ORDER BY m.modelName",
                    SimulatorModel.class
            ).getResultList();

            if (models.isEmpty()) {
                if (infoLabel != null)
                    infoLabel.setText("No simulator models found. Create a model first.");
                return;
            }

            // 2) Dialog with SIMULATOR fields
            Dialog<Simulator> dialog = new Dialog<>();

            Window owner = simulatorTable.getScene() != null
                    ? simulatorTable.getScene().getWindow()
                    : null;
            if (owner != null) {
                dialog.initOwner(owner);
            }
            dialog.initModality(Modality.WINDOW_MODAL);

            dialog.setTitle("New simulator");
            dialog.setHeaderText("Enter simulator details");

            ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

            ComboBox<SimulatorModel> modelBox = new ComboBox<>();
            modelBox.getItems().addAll(models);
            modelBox.setPrefWidth(260);
            modelBox.getSelectionModel().selectFirst(); // default

            TextField tagField = new TextField();
            tagField.setPromptText("Tag (required)");

            TextField snField = new TextField();
            snField.setPromptText("Serial number (optional)");

            TextField condNotesField = new TextField();
            condNotesField.setPromptText("Condition notes (optional)");

            DatePicker nextCalPicker = new DatePicker();
            nextCalPicker.setPromptText("Next calibration date (optional)");

            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(8);
            grid.setPadding(new Insets(10, 10, 10, 10));

            int row = 0;
            grid.add(new Label("Model:"), 0, row);
            grid.add(modelBox, 1, row++);

            grid.add(new Label("Tag:"), 0, row);
            grid.add(tagField, 1, row++);

            grid.add(new Label("SN:"), 0, row);
            grid.add(snField, 1, row++);

            grid.add(new Label("Condition notes:"), 0, row);
            grid.add(condNotesField, 1, row++);

            grid.add(new Label("Next calibration date:"), 0, row);
            grid.add(nextCalPicker, 1, row++);

            dialog.getDialogPane().setContent(grid);

            // Disable Save until required fields are present
            Node saveButton = dialog.getDialogPane().lookupButton(saveButtonType);
            saveButton.setDisable(true);

            Runnable validate = () -> {
                boolean ok =
                        modelBox.getValue() != null &&
                        tagField.getText() != null &&
                        !tagField.getText().trim().isEmpty();
                saveButton.setDisable(!ok);
            };

            modelBox.valueProperty().addListener((obs, ov, nv) -> validate.run());
            tagField.textProperty().addListener((obs, ov, nv) -> validate.run());

            dialog.setResultConverter(btn -> {
                if (btn == saveButtonType) {
                    SimulatorModel model = modelBox.getValue();
                    if (model == null) return null;

                    Simulator s = new Simulator();
                    s.setModel(model);
                    s.setTag(tagField.getText().trim());

                    String sn = snField.getText() == null ? "" : snField.getText().trim();
                    s.setSerialNumber(sn.isEmpty() ? null : sn);

                    String notes = condNotesField.getText() == null ? "" : condNotesField.getText().trim();
                    s.setConditionNotes(notes.isEmpty() ? null : notes);

                    LocalDate ncd = nextCalPicker.getValue();   // optional
                    s.setNextCalibrationDate(ncd);

                    // Default status
                    s.setStatus(Status.SIM_AVAILABLE);
                    return s;
                }
                return null;
            });

            Optional<Simulator> result = dialog.showAndWait();
            if (result.isEmpty()) {
                return; // cancelled
            }

            Simulator formSim = result.get();

            // 3) Persist to DB
            em.getTransaction().begin();

            // get branch entity for current user
            Branch branch = em.createQuery(
                    "SELECT b FROM Branch b WHERE b.name = :name",
                    Branch.class
            ).setParameter("name", u.getBranchName())
             .getSingleResult();

            Simulator s = new Simulator();
            s.setTag(formSim.getTag());
            s.setModel(em.getReference(SimulatorModel.class, formSim.getModel().getId()));
            s.setBranch(branch);
            s.setStatus(formSim.getStatus());
            s.setSerialNumber(formSim.getSerialNumber());
            s.setConditionNotes(formSim.getConditionNotes());
            s.setNextCalibrationDate(formSim.getNextCalibrationDate());
            // CALDATE can stay null for a new simulator

            em.persist(s);
            em.getTransaction().commit();

            masterData.add(s);

            if (infoLabel != null)
                infoLabel.setText("Simulator added.");

        } catch (Exception e) {
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
            if (infoLabel != null)
                infoLabel.setText("Error adding simulator: " + e.getMessage());
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    /**
     * Add a new SIMULATOR_MODEL.
     *
     * Business rule:
     *   - MODEL NAME, SPECS, and MAXDAYS are mandatory.
     *   - CALREQ is optional (checkbox).
     */
    @FXML
    private void onAddModel() {
        Dialog<SimulatorModel> dialog = new Dialog<>();

        Window owner = simulatorTable.getScene() != null
                ? simulatorTable.getScene().getWindow()
                : null;
        if (owner != null) {
            dialog.initOwner(owner);
        }
        dialog.initModality(Modality.WINDOW_MODAL);

        dialog.setTitle("New simulator model");
        dialog.setHeaderText("Create a new simulator model");

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        TextField nameField = new TextField();
        nameField.setPromptText("Model name (required)");

        TextField specsField = new TextField();
        specsField.setPromptText("Specs / notes (required)");

        javafx.scene.control.CheckBox calReqBox =
                new javafx.scene.control.CheckBox("Requires calibration?");
        calReqBox.setSelected(false);

        TextField maxDaysField = new TextField();
        maxDaysField.setPromptText("Max days (integer, required)");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(8);
        grid.setPadding(new Insets(10, 10, 10, 10));

        int row = 0;
        grid.add(new Label("Model name:"), 0, row);
        grid.add(nameField, 1, row++);

        grid.add(new Label("Specs:"), 0, row);
        grid.add(specsField, 1, row++);

        grid.add(new Label("Calibration required:"), 0, row);
        grid.add(calReqBox, 1, row++);

        grid.add(new Label("Max days:"), 0, row);
        grid.add(maxDaysField, 1, row++);

        dialog.getDialogPane().setContent(grid);

        Node saveButton = dialog.getDialogPane().lookupButton(saveButtonType);
        saveButton.setDisable(true);

        // Enforce: MODELNAME, SPECS and MAXDAYS (valid int) are mandatory
        Runnable validate = () -> {
            String name  = nameField.getText()  == null ? "" : nameField.getText().trim();
            String specs = specsField.getText() == null ? "" : specsField.getText().trim();
            String max   = maxDaysField.getText() == null ? "" : maxDaysField.getText().trim();

            boolean ok = !name.isEmpty() && !specs.isEmpty();

            if (max.isEmpty()) {
                ok = false;
            } else {
                try {
                    Integer.parseInt(max);
                } catch (NumberFormatException e) {
                    ok = false;
                }
            }

            saveButton.setDisable(!ok);
        };

        nameField.textProperty().addListener((obs, ov, nv) -> validate.run());
        specsField.textProperty().addListener((obs, ov, nv) -> validate.run());
        maxDaysField.textProperty().addListener((obs, ov, nv) -> validate.run());

        dialog.setResultConverter(btn -> {
            if (btn == saveButtonType) {
                SimulatorModel m = new SimulatorModel();
                m.setModelName(nameField.getText().trim());
                m.setSpecs(specsField.getText().trim());
                m.setCalReq(calReqBox.isSelected());

                String maxText = maxDaysField.getText().trim();
                m.setMaxDays(Integer.parseInt(maxText));  // safe thanks to validate()

                return m;
            }
            return null;
        });

        Optional<SimulatorModel> result = dialog.showAndWait();
        if (result.isEmpty()) {
            return; // cancelled
        }

        SimulatorModel formModel = result.get();

        EntityManager em = JPA.em();
        try {
            em.getTransaction().begin();

            SimulatorModel model = new SimulatorModel();
            model.setModelName(formModel.getModelName());
            model.setSpecs(formModel.getSpecs());
            model.setCalReq(formModel.getCalReq());
            model.setMaxDays(formModel.getMaxDays());

            em.persist(model);
            em.getTransaction().commit();

            if (infoLabel != null)
                infoLabel.setText("Simulator model created: " + model.getModelName());
        } catch (Exception e) {
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
            if (infoLabel != null)
                infoLabel.setText("Error creating model: " + e.getMessage());
            e.printStackTrace();
        } finally {
            em.close();
        }
    }


    @FXML
    private void onNavOverview(ActionEvent event) {
        try {
            ViewUtil.switchScene(event, "/ui/home.fxml", "Main Mennu");
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