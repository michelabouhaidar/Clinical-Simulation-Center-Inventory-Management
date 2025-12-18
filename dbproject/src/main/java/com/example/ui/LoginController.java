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

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    private final UserRepository repo = new UserRepository();

    @FXML
    public void initialize() {
        if (usernameField != null) {
            usernameField.requestFocus();
        }
    }

    @FXML
    public void onLogin(ActionEvent event) {
        errorLabel.setText("");

        String usernameRaw = usernameField.getText();
        String password = passwordField.getText();

        if (usernameRaw == null || usernameRaw.trim().isEmpty()) {
            errorLabel.setText("Enter username.");
            return;
        }

        String username = usernameRaw.trim();

        EntityManager em = JPA.em();
        try {
            User u = repo.findByUsername(em, username);

            if (u == null) {
                errorLabel.setText("Invalid username or password.");
                passwordField.clear();
                passwordField.requestFocus();
                return;
            }

            if (Boolean.FALSE.equals(u.getIsActive())) {
                errorLabel.setText("Account is inactive.");
                passwordField.clear();
                passwordField.requestFocus();
                return;
            }

            if (!PasswordUtil.verify(password, u.getPassHash())) {
                errorLabel.setText("Invalid username or password.");
                passwordField.clear();
                passwordField.requestFocus();
                return;
            }

            if (Boolean.TRUE.equals(u.getReset())) {
                try {
                    ViewUtil.switchScene(event, "/ui/set_password.fxml", loader -> {
                        SetPasswordController controller =
                                new SetPasswordController(u.getUsername());
                        loader.setController(controller);
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    errorLabel.setText("Failed to open password setup screen.");
                }
                return;
            }

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
            ex.printStackTrace();
            errorLabel.setText("Login error: " + ex.getMessage());
        } finally {
            em.close();
        }
    }
}
