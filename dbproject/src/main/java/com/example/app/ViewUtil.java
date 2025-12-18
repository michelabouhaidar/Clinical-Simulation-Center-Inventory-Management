package com.example.app;

import java.io.IOException;
import java.util.function.Consumer;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public final class ViewUtil {

    private ViewUtil() {
    }
    private static Stage getStageFromEvent(ActionEvent event) {
        if (event == null) {
            throw new IllegalArgumentException("Event cannot be null");
        }
        Object source = event.getSource();
        if (!(source instanceof Node)) {
            throw new IllegalStateException("Event source is not a JavaFX Node");
        }
        Node node = (Node) source;
        return (Stage) node.getScene().getWindow();
    }

    private static FXMLLoader createLoader(String fxmlPath) {
        if (fxmlPath == null || fxmlPath.isBlank()) {
            throw new IllegalArgumentException("FXML path cannot be null or blank");
        }
        FXMLLoader loader = new FXMLLoader(ViewUtil.class.getResource(fxmlPath));
        if (loader.getLocation() == null) {
            throw new IllegalStateException("Cannot resolve FXML resource: " + fxmlPath);
        }
        return loader;
    }

    public static void switchScene(Stage stage, String fxmlPath, String title) {
        if (stage == null) {
            throw new IllegalArgumentException("Stage cannot be null");
        }

        try {
            FXMLLoader loader = createLoader(fxmlPath);
            Parent root = loader.load();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            if (title != null && !title.isBlank()) {
                stage.setTitle(title);
            }
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load FXML: " + fxmlPath, e);
        }
    }

    public static void switchScene(Stage stage,
                                   String fxmlPath,
                                   Consumer<FXMLLoader> loaderConfigurator) {
        if (stage == null) {
            throw new IllegalArgumentException("Stage cannot be null");
        }

        try {
            FXMLLoader loader = createLoader(fxmlPath);
            if (loaderConfigurator != null) {
                loaderConfigurator.accept(loader);
            }
            Parent root = loader.load();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load FXML: " + fxmlPath, e);
        }
    }

    public static void switchScene(ActionEvent event,
                                   String fxmlPath,
                                   String title) {
        Stage stage = getStageFromEvent(event);
        switchScene(stage, fxmlPath, title);
    }

    public static void switchScene(ActionEvent event,
                                   String fxmlPath,
                                   Consumer<FXMLLoader> loaderConfigurator) {
        Stage stage = getStageFromEvent(event);
        switchScene(stage, fxmlPath, loaderConfigurator);
    }
}
