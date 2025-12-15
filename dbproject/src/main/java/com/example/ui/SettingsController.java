package com.example.ui;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import com.example.app.JPA;
import com.example.app.ViewUtil;
import com.example.domain.Branch;
import com.example.domain.User;
import com.example.session.AppSession;
import com.example.session.LoggedInUser;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ListCell;

/**
 * Controller for settings.fxml.
 * Persists changes to USERS via JPA.
 */
public class SettingsController {

    /* --------- Header --------- */

    @FXML private Label userLabel;

    /* --------- Profile section --------- */

    @FXML private TextField txtFullName;
    @FXML private TextField txtEmail;
    @FXML private ComboBox<Branch> cbDefaultBranch;

    /* --------- Password section --------- */

    @FXML private PasswordField pfCurrentPassword;
    @FXML private PasswordField pfNewPassword;
    @FXML private PasswordField pfConfirmPassword;

    /* --------- Info label --------- */

    @FXML private Label infoLabel;

    // cached current DB user (loaded lazily)
    private User dbUser;

    /* =========================================================
     *  Initialization
     * ========================================================= */

    @FXML
    public void initialize() {
        initUserHeader();
        initBranches();
        loadUserFromDatabase();
        initProfileFieldsFromUser();
    }

    private void initUserHeader() {
        LoggedInUser u = AppSession.getCurrentUser();
        if (u == null || userLabel == null) {
            return;
        }

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

    /**
     * Load all branches and configure ComboBox display.
     */
    private void initBranches() {
        if (cbDefaultBranch == null) return;

        EntityManager em = JPA.em();
        try {
            TypedQuery<Branch> q = em.createQuery(
                    "SELECT b FROM Branch b ORDER BY b.name",
                    Branch.class
            );
            List<Branch> branches = q.getResultList();
            cbDefaultBranch.setItems(FXCollections.observableArrayList(branches));

            // cell factory to show branch name
            cbDefaultBranch.setCellFactory(listView ->
                    new ListCell<Branch>() {
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

            cbDefaultBranch.setButtonCell(new ListCell<Branch>() {
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

        } catch (Exception e) {
            if (infoLabel != null) {
                infoLabel.setText("Error loading branches: " + e.getMessage());
            }
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    /**
     * Load the real User entity from DB based on LoggedInUser.username.
     */
    private void loadUserFromDatabase() {
        LoggedInUser sessionUser = AppSession.getCurrentUser();
        if (sessionUser == null || sessionUser.getUsername() == null) {
            return;
        }

        EntityManager em = JPA.em();
        try {
            TypedQuery<User> q = em.createQuery(
                    "SELECT u FROM User u WHERE u.username = :uname",
                    User.class
            );
            q.setParameter("uname", sessionUser.getUsername());
            User u = q.getSingleResult();

            // detach or keep reference; we will re-attach with merge when saving
            dbUser = u;

        } catch (NoResultException nre) {
            dbUser = null;
            if (infoLabel != null) {
                infoLabel.setText("User not found in database for username: "
                        + sessionUser.getUsername());
            }
        } catch (Exception e) {
            dbUser = null;
            if (infoLabel != null) {
                infoLabel.setText("Error loading user: " + e.getMessage());
            }
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    private void initProfileFieldsFromUser() {
        if (dbUser == null) {
            // fallback from session only
            LoggedInUser u = AppSession.getCurrentUser();
            if (u != null) {
                if (txtFullName != null) {
                    txtFullName.setText(
                            (u.getDisplayName() != null && !u.getDisplayName().isBlank())
                                    ? u.getDisplayName()
                                    : u.getUsername()
                    );
                }
                if (txtEmail != null) {
                    txtEmail.setText(u.getEmail());
                }
            }
            return;
        }

        if (txtFullName != null) {
            String name = (dbUser.getDisplayName() != null && !dbUser.getDisplayName().isBlank())
                    ? dbUser.getDisplayName()
                    : dbUser.getUsername();
            txtFullName.setText(name);
        }

        if (txtEmail != null) {
            txtEmail.setText(dbUser.getEmail());
        }

        if (cbDefaultBranch != null && dbUser.getBranch() != null) {
            // select the user's branch in the combo
            for (Branch b : cbDefaultBranch.getItems()) {
                if (b.getId().equals(dbUser.getBranch().getId())) {
                    cbDefaultBranch.getSelectionModel().select(b);
                    break;
                }
            }
        }
    }

    /* =========================================================
     *  Actions: Profile
     * ========================================================= */

    @FXML
    private void onSaveProfile(ActionEvent event) {
        String fullName = txtFullName != null ? txtFullName.getText().trim() : "";
        String email    = txtEmail    != null ? txtEmail.getText().trim()    : "";
        Branch branch   = cbDefaultBranch != null
                ? cbDefaultBranch.getSelectionModel().getSelectedItem()
                : null;

        if (fullName.isEmpty() || email.isEmpty()) {
            setInfo("Full name and email are required.");
            return;
        }

        // If we still don't have dbUser, try to load now
        if (dbUser == null) {
            loadUserFromDatabase();
            if (dbUser == null) {
                setInfo("Cannot save profile: user not found in database.");
                return;
            }
        }

        EntityManager em = JPA.em();
        try {
            em.getTransaction().begin();

            // re-attach
            User managed = em.merge(dbUser);

            // split fullName into displayName only (you can change this logic if needed)
            managed.setDisplayName(fullName);
            managed.setEmail(email);
            if (branch != null) {
                Branch managedBranch = em.getReference(Branch.class, branch.getId());
                managed.setBranch(managedBranch);
            }

            em.getTransaction().commit();
            dbUser = managed; // refresh local ref

            // update session object
            LoggedInUser sessionUser = AppSession.getCurrentUser();
            if (sessionUser != null) {
                sessionUser.setDisplayName(fullName);
                sessionUser.setEmail(email);
                if (branch != null) {
                    sessionUser.setBranchName(branch.getName());
                }
            }

            // update header
            initUserHeader();

            setInfo("Profile saved successfully.");

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            setInfo("Error saving profile: " + e.getMessage());
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    /* =========================================================
     *  Actions: Password
     * ========================================================= */

    @FXML
    private void onChangePassword(ActionEvent event) {
        String current = pfCurrentPassword != null ? pfCurrentPassword.getText() : "";
        String neu     = pfNewPassword     != null ? pfNewPassword.getText()     : "";
        String confirm = pfConfirmPassword != null ? pfConfirmPassword.getText() : "";

        if (neu.isEmpty() || confirm.isEmpty()) {
            setInfo("New password and confirmation are required.");
            return;
        }

        if (!neu.equals(confirm)) {
            setInfo("New password and confirmation do not match.");
            return;
        }

        if (neu.length() < 6) {
            setInfo("New password should be at least 6 characters.");
            return;
        }

        if (dbUser == null) {
            loadUserFromDatabase();
            if (dbUser == null) {
                setInfo("Cannot change password: user not found in database.");
                return;
            }
        }

        EntityManager em = JPA.em();
        try {
            em.getTransaction().begin();

            User managed = em.merge(dbUser);

            // verify current password with BCrypt
            // Make sure you have BCrypt in your dependencies.
            boolean okCurrent;
            if (current == null || current.isEmpty()) {
                okCurrent = false;
            } else {
                okCurrent = org.mindrot.jbcrypt.BCrypt.checkpw(
                        current, managed.getPassHash());
            }

            if (!okCurrent) {
                em.getTransaction().rollback();
                setInfo("Current password is incorrect.");
                return;
            }

            String newHash = org.mindrot.jbcrypt.BCrypt.hashpw(
                    neu, org.mindrot.jbcrypt.BCrypt.gensalt(12));
            managed.setPassHash(newHash);
            // Optionally clear reset flag:
            if (managed.getReset() != null && managed.getReset()) {
                managed.setReset(false);
            }

            em.getTransaction().commit();
            dbUser = managed;

            // clear fields
            if (pfCurrentPassword != null) pfCurrentPassword.clear();
            if (pfNewPassword != null) pfNewPassword.clear();
            if (pfConfirmPassword != null) pfConfirmPassword.clear();

            setInfo("Password changed successfully.");

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            setInfo("Error changing password: " + e.getMessage());
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    /* =========================================================
     *  Helpers
     * ========================================================= */

    private void setInfo(String msg) {
        if (infoLabel != null) {
            infoLabel.setText(msg);
        } else {
            System.out.println("[SettingsController] " + msg);
        }
    }

    /* =========================================================
     *  Navigation
     * ========================================================= */

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
            ViewUtil.switchScene(event, "/ui/stock.fxml", "Consumables & Stock");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

	
    @FXML
    private void onNavBorrowing(ActionEvent event) {
        try {
            ViewUtil.switchScene(event, "/ui/borrowing.fxml", "Borrowing");
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
            Alert a = new Alert(AlertType.WARNING);
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
    private void onLogout(ActionEvent event) {
        try {
            ViewUtil.switchScene(event, "/ui/login.fxml", "Login");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
