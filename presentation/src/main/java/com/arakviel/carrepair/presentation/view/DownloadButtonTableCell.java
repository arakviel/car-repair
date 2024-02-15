package com.arakviel.carrepair.presentation.view;

import com.arakviel.carrepair.presentation.model.impl.EmployeeModel;
import com.arakviel.carrepair.presentation.util.PageLoader;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXIconWrapper;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import javafx.scene.Cursor;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TableCell;
import javafx.stage.FileChooser;

public class DownloadButtonTableCell extends TableCell<EmployeeModel, Object> {
    MFXIconWrapper wrapper = new MFXIconWrapper("fas-download", 12, 20);
    private final MFXButton downloadButton = new MFXButton("", wrapper);

    public DownloadButtonTableCell() {
        downloadButton.setCursor(Cursor.HAND);
        downloadButton.setOnAction(event -> {
            EmployeeModel employeeModel = getTableView().getItems().get(getIndex());
            byte[] pdfData = employeeModel.getPassportDocCopy();

            if (pdfData != null) {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Виберіть папку для збереження");
                fileChooser.setInitialFileName("doc-%s.pdf".formatted(UUID.randomUUID()));

                File selectedDirectory = fileChooser.showSaveDialog(PageLoader.primaryStage);

                if (selectedDirectory != null) {
                    try {
                        Path filePath = Paths.get(selectedDirectory.getAbsolutePath());
                        Files.write(filePath, pdfData);
                        Alert alert = new Alert(AlertType.INFORMATION);
                        alert.setTitle("Успішне збереження");
                        alert.setHeaderText(null);
                        alert.setContentText("Файл успішно збережено.");
                        alert.showAndWait();
                    } catch (IOException e) {
                        Alert alert = new Alert(AlertType.ERROR);
                        alert.setTitle("Помилка збереження");
                        alert.setHeaderText(null);
                        alert.setContentText("Сталася помилка під час збереження файлу. Причина: " + e.getMessage());
                        alert.showAndWait();
                    }
                }
            }
        });
    }

    @Override
    protected void updateItem(Object item, boolean empty) {
        super.updateItem(item, empty);
        if (empty) {
            setGraphic(null);
        } else {
            setGraphic(downloadButton);
        }
    }
}
