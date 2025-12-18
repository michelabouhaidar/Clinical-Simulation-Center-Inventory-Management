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


public class SettingsController {


    @FXML private Label userLabel;


    @FXML private TextField txtFullName;
    @FXML private TextField txtEmail;
    @FXML private ComboBox<Branch> cbDefaultBranch;


    @FXML private PasswordField pfCurrentPassword;
    @FXML private PasswordField pfNewPassword;
    @FXML private PasswordField pfConfirmPassword;


    @FXML private Label infoLabel;

    private User dbUser;

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
            for (Branch b : cbDefaultBranch.getItems()) {
                if (b.getId().equals(dbUser.getBranch().getId())) {
                    cbDefaultBranch.getSelectionModel().select(b);
                    break;
                }
            }
        }
    }

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

            User managed = em.merge(dbUser);

            managed.setDisplayName(fullName);
            managed.setEmail(email);
            if (branch != null) {
                Branch managedBranch = em.getReference(Branch.class, branch.getId());
                managed.setBranch(managedBranch);
            }

            em.getTransaction().commit();
            dbUser = managed; // refresh local ref

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
            if (managed.getReset() != null && managed.getReset()) {
                managed.setReset(false);
            }

            em.getTransaction().commit();
            dbUser = managed;

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

    private void setInfo(String msg) {
        if (infoLabel != null) {
            infoLabel.setText(msg);
        } else {
            System.out.println("[SettingsController] " + msg);
        }
    }

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
