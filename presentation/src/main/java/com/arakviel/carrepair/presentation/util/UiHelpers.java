package com.arakviel.carrepair.presentation.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;

public final class UiHelpers {

    public static LocalDateTime stringToLocalDateTime(String input) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
        try {
            return LocalDateTime.parse(input, formatter);
        } catch (DateTimeParseException e) {
            throw new RuntimeException("Не коректний формат дати та час. Ось приклад вірного: 18.05.2023 12:34:56");
        }
    }

    public static boolean showDeleteConfirmation() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Підтвердження видалення");
        alert.setHeaderText(null);
        alert.setContentText("Ви впевнені, що хочете видалити поточний запис?");

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    public static void initFileChooserForPhoto(ImageView imageView) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Виберіть файл зображення");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Зображення", "*.jpg", "*.jpeg"));

        File selectedFile = fileChooser.showOpenDialog(PageLoader.primaryStage);
        if (selectedFile != null) {
            Image image = new Image(selectedFile.toURI().toString());
            imageView.setImage(image);
        }
    }

    public static List<ImageView> initFileChooserForPhotos(Pane photoContainer) {
        List<ImageView> imageViews = new ArrayList<>();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Виберіть файли зображень");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Зображення", "*.jpg", "*.jpeg"));

        List<File> selectedFiles = fileChooser.showOpenMultipleDialog(PageLoader.primaryStage);
        if (selectedFiles != null && !selectedFiles.isEmpty()) {
            FlowPane flowPane = new FlowPane();
            flowPane.setHgap(10);
            flowPane.setVgap(10);
            flowPane.setPadding(new Insets(10));
            for (File file : selectedFiles) {
                Image image = new Image(file.toURI().toString());
                ImageView imageView = new ImageView(image);
                imageView.setFitWidth(150);
                imageView.setFitHeight(150);
                imageView.setPreserveRatio(true);
                imageView.setSmooth(true);
                imageView.setClip(new Rectangle(imageView.getFitWidth(), imageView.getFitHeight()));
                imageViews.add(imageView);
                flowPane.getChildren().add(imageView);
                imageView.setOnMouseClicked(e -> {
                    if (e.getButton() == MouseButton.SECONDARY) {
                        if (UiHelpers.showDeleteConfirmation()) {
                            flowPane.getChildren().remove(imageView);
                            imageViews.remove(imageView);
                        }
                    }
                });
            }

            ScrollPane scrollPane = new ScrollPane(flowPane);
            scrollPane.setPrefViewportWidth(400);
            scrollPane.setPrefViewportHeight(200);
            scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
            scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

            photoContainer.getChildren().add(scrollPane);
        }
        return imageViews;
    }

    public static void initFileChooserForDoc(Label label) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Виберіть pdf файл скану документа");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Документ", "*.pdf"));

        File selectedFile = fileChooser.showOpenDialog(PageLoader.primaryStage);
        if (selectedFile != null) {
            try {
                byte[] fileBytes = Files.readAllBytes(selectedFile.toPath());
                label.setUserData(fileBytes);
                label.setText(selectedFile.getName());
            } catch (IOException e) {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Помилка збереження");
                alert.setHeaderText(null);
                alert.setContentText("Сталася помилка під час збереження файлу. Причина: " + e.getMessage());
                alert.showAndWait();
            }
        }
    }

    public static void validateShow(Map<String, List<String>> validationMessages, String keyName, Label errorLabel) {
        if (validationMessages.containsKey(keyName)) {
            errorLabel.setManaged(true);
            errorLabel.setVisible(true);
            errorLabel.setText(String.join("\n", validationMessages.get(keyName)));
        } else {
            errorLabel.setManaged(false);
            errorLabel.setVisible(false);
        }
    }

    private UiHelpers() {}
}
