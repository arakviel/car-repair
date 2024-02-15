package com.arakviel.carrepair.presentation.view;

import com.arakviel.carrepair.presentation.Main;
import com.arakviel.carrepair.presentation.model.Model;
import javafx.scene.control.TableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class PhotoTableCell<T extends Model> extends TableCell<T, Image> {
    private final ImageView imageView = new ImageView();

    @Override
    protected void updateItem(Image item, boolean empty) {
        super.updateItem(item, empty);
        if (item != null) {
            imageView.setFitWidth(50);
            imageView.setFitHeight(50);
            imageView.setImage(item);
        } else {
            imageView.setFitWidth(50);
            imageView.setFitHeight(50);
            Image image = new Image(Main.class.getResourceAsStream("view/empty.png"));
            imageView.setImage(image);
        }

        if (!empty) {
            setGraphic(imageView);
        }
    }

    @Override
    public void updateIndex(int index) {
        super.updateIndex(index);

        if (isEmpty()) {
            setGraphic(null);
        }
    }
}
