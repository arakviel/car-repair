package com.arakviel.carrepair.presentation.view;

import com.arakviel.carrepair.presentation.model.Model;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javafx.scene.control.TableCell;

public class LocalDateTableCell<T extends Model> extends TableCell<T, LocalDate> {
    @Override
    protected void updateItem(LocalDate item, boolean empty) {
        super.updateItem(item, empty);
        if (item != null && !empty) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            setText(item.format(formatter));
        } else {
            setText(null);
        }
    }
}
