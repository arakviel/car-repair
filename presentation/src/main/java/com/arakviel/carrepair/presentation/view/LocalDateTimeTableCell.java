package com.arakviel.carrepair.presentation.view;

import com.arakviel.carrepair.presentation.model.Model;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javafx.scene.control.TableCell;

public class LocalDateTimeTableCell<T extends Model> extends TableCell<T, LocalDateTime> {
    @Override
    protected void updateItem(LocalDateTime item, boolean empty) {
        super.updateItem(item, empty);
        if (item != null && !empty) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
            setText(item.format(formatter));
        } else {
            setText(null);
        }
    }
}
