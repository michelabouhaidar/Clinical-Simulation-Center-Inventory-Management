package com.example.ui;

import javax.persistence.EntityManager;

import com.example.app.JPA;
import com.example.app.ViewUtil;
import com.example.domain.User;
import com.example.session.AppSession;
import com.example.session.LoggedInUser;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class SetPasswordController {

    @FXML private TextField usernameField;
    @FXML private PasswordField pass1;
    @FXML private PasswordField pass2;
    @FXML private Label errorLabel;

    private final String username;
    private final UserRepository repo = new UserRepository();

    public SetPasswordController(String username) {
        this.username = username;
    }

    @FXML
    public void initialize() {
        if (usernameField != null) {
            usernameField.setText(username);
        }
    }

    @FXML
    public void onSave(ActionEvent event) {
        errorLabel.setText("");

        String p1 = pass1.getText();
        String p2 = pass2.getText();

        if (p1 == null || p1.length() < 8) {
            errorLabel.setText("Password must be at least 8 characters.");
            return;
        }

        if (!p1.matches("(?=.*[A-Z])(?=.*[a-z])(?=.*\\d).{8,}")) {
            errorLabel.setText("Password must contain upper, lower case letters and a digit.");
            return;
        }

        if (!p1.equals(p2)) {
            errorLabel.setText("Passwords do not match.");
            return;
        }

        EntityManager em = JPA.em();
        try {
            em.getTransaction().begin();

            User u = repo.findByUsername(em, username);
            if (u == null) {
                errorLabel.setText("User disappeared!");
                em.getTransaction().rollback();
                return;
            }

            u.setPassHash(PasswordUtil.hash(p1));
            u.setReset(Boolean.FALSE);
            if (u.getIsActive() == null) {
                u.setIsActive(Boolean.TRUE);
            }

            repo.save(em, u);
            em.getTransaction().commit();

            AppSession.setCurrentUser(
                    new LoggedInUser(
                            u.getId(),
                            u.getUsername(),
                            u.getDisplayName(),
                            u.getRole(),
                            u.getEmail(),
                            (u.getBranch() != null ? u.getBranch().getName() : null)
                    )
            );

            ViewUtil.switchScene(event, "/ui/home.fxml", "Main Menu");

        } catch (Exception ex) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            errorLabel.setText("Error: " + ex.getMessage());
        } finally {
            em.close();
        }
    }
}
