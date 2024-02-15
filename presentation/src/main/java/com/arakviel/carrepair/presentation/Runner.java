package com.arakviel.carrepair.presentation;

import com.arakviel.carrepair.domain.factory.RepositoryFactory;
import javafx.scene.control.Alert;

public class Runner {

    public static void main(String[] args) {
        try {
            Main.main(args);
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Помилка");
            alert.setHeaderText(null);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        } finally {
            RepositoryFactory.getInstance().closePool();
        }
    }
}
