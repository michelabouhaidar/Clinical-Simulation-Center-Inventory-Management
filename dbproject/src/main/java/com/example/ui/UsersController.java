package com.example.ui;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import com.example.app.JPA;
import com.example.app.ViewUtil;
import com.example.domain.Branch;
import com.example.domain.User;
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
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Window;

public class UsersController {

    @FXML private Label userLabel;

    @FXML private TableView<User> userTable;
    @FXML private TableColumn<User, String> usernameColumn;
    @FXML private TableColumn<User, String> displayNameColumn;
    @FXML private TableColumn<User, String> roleColumn;
    @FXML private TableColumn<User, String> emailColumn;
    @FXML private TableColumn<User, String> branchColumn;
    @FXML private TableColumn<User, String> activeColumn;
    @FXML private TableColumn<User, String> resetColumn;

    @FXML private TextField        searchField;
    @FXML private ComboBox<String> sortByCombo;
    @FXML private Label            infoLabel;

    private final ObservableList<User> masterData =
            FXCollections.observableArrayList();
    private FilteredList<User> filteredData;
    private SortedList<User>   sortedData;

    @FXML
    public void initialize() {
        LoggedInUser loggedIn = AppSession.getCurrentUser();
        if (loggedIn != null && userLabel != null) {
            StringBuilder sb = new StringBuilder();
            if (loggedIn.getDisplayName() != null && !loggedIn.getDisplayName().isBlank())
                sb.append(loggedIn.getDisplayName());
            else if (loggedIn.getUsername() != null)
                sb.append(loggedIn.getUsername());

            if (loggedIn.getRole() != null && !loggedIn.getRole().isBlank())
                sb.append(" (").append(loggedIn.getRole()).append(")");

            if (loggedIn.getBranchName() != null && !loggedIn.getBranchName().isBlank())
                sb.append(" · ").append(loggedIn.getBranchName());

            if (loggedIn.getEmail() != null && !loggedIn.getEmail().isBlank())
                sb.append(" · ").append(loggedIn.getEmail());

            userLabel.setText(sb.toString());
        }

        // Access control: only ADMIN
        if (loggedIn == null || loggedIn.getRole() == null ||
            !loggedIn.getRole().equalsIgnoreCase("ADMIN")) {

            if (infoLabel != null) {
                infoLabel.setText("Access denied: only ADMIN can manage users.");
            }
            if (userTable != null) userTable.setDisable(true);
        }

        // Table columns
        usernameColumn.setCellValueFactory(
                cd -> new SimpleStringProperty(
                        cd.getValue().getUsername() != null ? cd.getValue().getUsername() : ""));

        displayNameColumn.setCellValueFactory(
                cd -> new SimpleStringProperty(
                        cd.getValue().getDisplayName() != null ? cd.getValue().getDisplayName() : ""));

        roleColumn.setCellValueFactory(
                cd -> new SimpleStringProperty(
                        cd.getValue().getRole() != null ? cd.getValue().getRole() : ""));

        emailColumn.setCellValueFactory(
                cd -> new SimpleStringProperty(
                        cd.getValue().getEmail() != null ? cd.getValue().getEmail() : ""));

        branchColumn.setCellValueFactory(
                cd -> new SimpleStringProperty(
                        cd.getValue().getBranch() != null
                                && cd.getValue().getBranch().getName() != null
                                ? cd.getValue().getBranch().getName()
                                : ""));

        activeColumn.setCellValueFactory(
                cd -> new SimpleStringProperty(
                        Boolean.TRUE.equals(cd.getValue().getIsActive()) ? "Yes" : "No"));

        resetColumn.setCellValueFactory(
                cd -> new SimpleStringProperty(
                        Boolean.TRUE.equals(cd.getValue().getReset()) ? "Yes" : "No"));

        filteredData = new FilteredList<>(masterData, u -> true);
        sortedData   = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(userTable.comparatorProperty());
        userTable.setItems(sortedData);

        setupSearch();
        setupSortCombo();
        loadUsers();
    }

    private boolean isAdmin() {
        LoggedInUser u = AppSession.getCurrentUser();
        return u != null &&
               u.getRole() != null &&
               u.getRole().equalsIgnoreCase("ADMIN");
    }

    private void loadUsers() {
        if (!isAdmin()) return;

        EntityManager em = JPA.em();
        try {
            // fetch branch to avoid N+1
            TypedQuery<User> q = em.createQuery(
                    "SELECT u FROM User u LEFT JOIN FETCH u.branch ORDER BY u.username",
                    User.class
            );
            List<User> list = q.getResultList();
            masterData.setAll(list);

            if (infoLabel != null) {
                infoLabel.setText(list.isEmpty()
                        ? "No users found."
                        : "");
            }
        } catch (Exception e) {
            if (infoLabel != null) {
                infoLabel.setText("Error loading users: " + e.getMessage());
            }
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    private void setupSearch() {
        if (searchField == null) return;

        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            String needle = (newVal == null) ? "" : newVal.trim().toLowerCase();

            filteredData.setPredicate(u -> {
                if (needle.isEmpty()) return true;

                if (u.getUsername() != null &&
                    u.getUsername().toLowerCase().contains(needle)) return true;

                if (u.getDisplayName() != null &&
                    u.getDisplayName().toLowerCase().contains(needle)) return true;

                if (u.getRole() != null &&
                    u.getRole().toLowerCase().contains(needle)) return true;

                if (u.getEmail() != null &&
                    u.getEmail().toLowerCase().contains(needle)) return true;

                if (u.getBranch() != null &&
                    u.getBranch().getName() != null &&
                    u.getBranch().getName().toLowerCase().contains(needle)) return true;

                return false;
            });
        });
    }

    private void setupSortCombo() {
        if (sortByCombo == null) return;

        sortByCombo.setItems(FXCollections.observableArrayList(
                "Username", "Role", "Branch", "Active"
        ));
        sortByCombo.getSelectionModel().select("Username");

        sortByCombo.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
            if (sel == null) return;

            userTable.getSortOrder().clear();
            TableColumn<User, ?> col = null;

            switch (sel) {
                case "Username": col = usernameColumn; break;
                case "Role":     col = roleColumn;     break;
                case "Branch":   col = branchColumn;   break;
                case "Active":   col = activeColumn;   break;
            }

            if (col != null) {
                col.setSortType(TableColumn.SortType.ASCENDING);
                userTable.getSortOrder().add(col);
                userTable.sort();
            }
        });
    }

    // =====================================================
    // Actions
    // =====================================================

    @FXML
    private void onAddUser() {
        if (!isAdmin()) {
            if (infoLabel != null)
                infoLabel.setText("Only ADMIN can create users.");
            return;
        }

        EntityManager em = JPA.em();
        try {
            List<Branch> branches = em.createQuery(
                    "SELECT b FROM Branch b ORDER BY b.name",
                    Branch.class
            ).getResultList();

            Dialog<NewUserData> dialog = new Dialog<>();
            Window owner = userTable.getScene() != null
                    ? userTable.getScene().getWindow()
                    : null;
            if (owner != null) dialog.initOwner(owner);
            dialog.initModality(Modality.WINDOW_MODAL);

            dialog.setTitle("New user");
            dialog.setHeaderText("Create a new user (ADMIN or STAFF)");

            ButtonType saveButtonType =
                    new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

            TextField usernameField = new TextField();
            usernameField.setPromptText("Username (required)");

            TextField displayNameField = new TextField();
            displayNameField.setPromptText("Display name (optional)");

            TextField emailField = new TextField();
            emailField.setPromptText("Email (optional)");

            ComboBox<String> roleBox = new ComboBox<>();
            roleBox.getItems().addAll("ADMIN", "STAFF");
            roleBox.getSelectionModel().select("STAFF");

            ComboBox<Branch> branchBox = new ComboBox<>();
            branchBox.getItems().addAll(branches);
            branchBox.setCellFactory(cb -> new ListCell<>() {
                @Override
                protected void updateItem(Branch item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item.getName());
                    }
                }
            });
            branchBox.setButtonCell(new ListCell<>() {
                @Override
                protected void updateItem(Branch item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item.getName());
                    }
                }
            });

            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(8);
            grid.setPadding(new Insets(10, 10, 10, 10));

            int row = 0;
            grid.add(new Label("Username:"), 0, row);
            grid.add(usernameField,         1, row++);

            grid.add(new Label("Display name:"), 0, row);
            grid.add(displayNameField,          1, row++);

            grid.add(new Label("Email:"), 0, row);
            grid.add(emailField,         1, row++);

            grid.add(new Label("Role:"), 0, row);
            grid.add(roleBox,            1, row++);

            grid.add(new Label("Branch (for STAFF):"), 0, row);
            grid.add(branchBox,                     1, row++);

            dialog.getDialogPane().setContent(grid);

            Node saveButton = dialog.getDialogPane().lookupButton(saveButtonType);
            saveButton.setDisable(true);

            Runnable validate = () -> {
                String username = usernameField.getText() == null
                        ? ""
                        : usernameField.getText().trim();
                String role = roleBox.getValue();
                Branch b = branchBox.getValue();

                boolean ok = !username.isEmpty() && role != null;

                // STAFF must have branch
                if ("STAFF".equals(role) && b == null) {
                    ok = false;
                }
                saveButton.setDisable(!ok);
            };

            usernameField.textProperty().addListener((o, ov, nv) -> validate.run());
            roleBox.valueProperty().addListener((o, ov, nv) -> validate.run());
            branchBox.valueProperty().addListener((o, ov, nv) -> validate.run());

            dialog.setResultConverter(btn -> {
                if (btn == saveButtonType) {
                    String username   = usernameField.getText().trim();
                    String display    = displayNameField.getText() == null
                            ? null
                            : displayNameField.getText().trim();
                    String email      = emailField.getText() == null
                            ? null
                            : emailField.getText().trim();
                    String role       = roleBox.getValue();
                    Branch branch     = branchBox.getValue();
                    return new NewUserData(username, display, email, role, branch);
                }
                return null;
            });

            Optional<NewUserData> result = dialog.showAndWait();
            if (result.isEmpty()) return;

            NewUserData form = result.get();

            // Check username uniqueness
            Long countExisting = em.createQuery(
                    "SELECT COUNT(u) FROM User u WHERE u.username = :un", Long.class)
                    .setParameter("un", form.username())
                    .getSingleResult();
            if (countExisting != null && countExisting > 0L) {
                showAlert(Alert.AlertType.ERROR,
                        "Username already exists",
                        "The username '" + form.username() + "' is already in use.");
                return;
            }

            // Generate temp password
            String tempPassword = PasswordUtil.generateStrongPassword(10);

            em.getTransaction().begin();

            Integer nextId = ((Number) em.createQuery(
                    "SELECT COALESCE(MAX(u.id), 0) FROM User u",
                    Number.class
            ).getSingleResult()).intValue() + 1;

            User u = new User();
            u.setId(nextId);
            u.setUsername(form.username());
            u.setDisplayName(form.displayName());
            u.setEmail(form.email());
            u.setRole(form.role());
            u.setIsActive(Boolean.TRUE);
            u.setReset(Boolean.TRUE); // force change at first login
            if (form.branch() != null) {
                Branch managedBranch = em.getReference(Branch.class, form.branch().getId());
                u.setBranch(managedBranch);
            } else {
                u.setBranch(null);
            }

            u.setPassHash(PasswordUtil.hash(tempPassword));

            em.persist(u);
            em.getTransaction().commit();

            masterData.add(u);
            userTable.refresh();

            if (infoLabel != null) {
                infoLabel.setText("User created: " + u.getUsername());
            }

            showAlert(Alert.AlertType.INFORMATION,
                    "User created",
                    "Username: " + u.getUsername() + "\n" +
                    "Temporary password (share with user):\n\n" + tempPassword);

        } catch (Exception e) {
            e.printStackTrace();
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
            if (infoLabel != null) {
                infoLabel.setText("Error creating user: " + e.getMessage());
            }
        } finally {
            em.close();
        }
    }

    private record NewUserData(
            String username,
            String displayName,
            String email,
            String role,
            Branch branch
    ) {}

    @FXML
    private void onResetPassword() {
        if (!isAdmin()) {
            if (infoLabel != null)
                infoLabel.setText("Only ADMIN can reset passwords.");
            return;
        }

        User selected = userTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            if (infoLabel != null)
                infoLabel.setText("Select a user first.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Reset password");
        confirm.setHeaderText("Reset password for user: " + selected.getUsername());
        confirm.setContentText("This will generate a new temporary password "
                + "and force the user to change it at next login.");
        Optional<ButtonType> confRes = confirm.showAndWait();
        if (confRes.isEmpty() || confRes.get() != ButtonType.OK) {
            return;
        }

        String tempPassword = PasswordUtil.generateStrongPassword(10);

        EntityManager em = JPA.em();
        try {
            em.getTransaction().begin();
            User managed = em.find(User.class, selected.getId());
            if (managed == null) {
                em.getTransaction().rollback();
                if (infoLabel != null)
                    infoLabel.setText("User no longer exists.");
                return;
            }

            managed.setPassHash(PasswordUtil.hash(tempPassword));
            managed.setReset(Boolean.TRUE);

            em.getTransaction().commit();

            selected.setReset(Boolean.TRUE);
            userTable.refresh();

            if (infoLabel != null) {
                infoLabel.setText("Password reset for " + managed.getUsername());
            }

            showAlert(Alert.AlertType.INFORMATION,
                    "Password reset",
                    "Username: " + managed.getUsername() + "\n" +
                    "New temporary password:\n\n" + tempPassword);

        } catch (Exception e) {
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
            if (infoLabel != null)
                infoLabel.setText("Error resetting password: " + e.getMessage());
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    @FXML
    private void onToggleActive() {
        if (!isAdmin()) {
            if (infoLabel != null)
                infoLabel.setText("Only ADMIN can deactivate/activate users.");
            return;
        }

        User selected = userTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            if (infoLabel != null)
                infoLabel.setText("Select a user first.");
            return;
        }

        boolean newActive = !Boolean.TRUE.equals(selected.getIsActive());

        EntityManager em = JPA.em();
        try {
            em.getTransaction().begin();
            User managed = em.find(User.class, selected.getId());
            if (managed == null) {
                em.getTransaction().rollback();
                if (infoLabel != null)
                    infoLabel.setText("User no longer exists.");
                return;
            }

            managed.setIsActive(newActive);
            em.getTransaction().commit();

            selected.setIsActive(newActive);
            userTable.refresh();

            if (infoLabel != null) {
                infoLabel.setText("User " + managed.getUsername() +
                        (newActive ? " activated." : " deactivated."));
            }

        } catch (Exception e) {
            if (em.getTransaction().isActive())
                em.getTransaction().rollback();
            if (infoLabel != null)
                infoLabel.setText("Error updating user: " + e.getMessage());
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert a = new Alert(type);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(message);
        a.showAndWait();
    }

    // =====================================================
    // Navigation
    // =====================================================

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
    private void onNavMaintentance(ActionEvent event) {
        try {
            ViewUtil.switchScene(event, "/ui/maintenance.fxml", "Maintenance");
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
