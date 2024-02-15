package com.arakviel.carrepair.presentation.view;

import com.arakviel.carrepair.presentation.model.Model;
import io.github.palexdev.materialfx.controls.MFXButton;
import java.util.function.Consumer;
import javafx.scene.Cursor;
import javafx.scene.control.TableCell;

public class ButtonTableCell<T extends Model> extends TableCell<T, Void> {
    private final MFXButton mfxButton;

    public ButtonTableCell(String name, Consumer<T> createConsumer) {
        mfxButton = new MFXButton(name);
        mfxButton.setCursor(Cursor.HAND);
        mfxButton.setOnAction(event -> {
            T model = getTableRow().getItem();
            if (model != null) {
                createConsumer.accept(model);
            }
        });
    }

    @Override
    protected void updateItem(Void item, boolean empty) {
        super.updateItem(item, empty);
        if (empty) {
            setGraphic(null);
        } else {
            setGraphic(mfxButton);
        }
    }
}
