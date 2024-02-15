package com.arakviel.carrepair.presentation.view;

import com.arakviel.carrepair.presentation.model.Model;
import java.util.List;
import javafx.scene.control.TableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

public class PhotosTableCell<T extends Model> extends TableCell<T, List<Image>> {
    protected void updateItem(List<Image> images, boolean empty) {
        super.updateItem(images, empty);

        if (images != null && !empty) {
            HBox hbox = new HBox();

            for (Image image : images) {
                ImageView imageView = new ImageView(image);
                imageView.setFitHeight(50);
                imageView.setFitWidth(50);
                hbox.getChildren().add(imageView);
            }

            setGraphic(hbox);
        } else {
            setGraphic(null);
        }
    }
}
