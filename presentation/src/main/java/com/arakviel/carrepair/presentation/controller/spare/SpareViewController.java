package com.arakviel.carrepair.presentation.controller.spare;

import com.arakviel.carrepair.domain.factory.RepositoryFactory;
import com.arakviel.carrepair.domain.impl.Money;
import com.arakviel.carrepair.domain.impl.Spare;
import com.arakviel.carrepair.domain.repository.SpareRepository;
import com.arakviel.carrepair.presentation.converter.KeyValueStringConverter;
import com.arakviel.carrepair.presentation.mapper.impl.ModelMapperFactory;
import com.arakviel.carrepair.presentation.mapper.impl.SpareModelMapper;
import com.arakviel.carrepair.presentation.model.impl.SpareModel;
import com.arakviel.carrepair.presentation.util.CrudState;
import com.arakviel.carrepair.presentation.util.PageLoader;
import com.arakviel.carrepair.presentation.util.UiHelpers;
import com.arakviel.carrepair.presentation.view.ButtonTableCell;
import com.arakviel.carrepair.presentation.view.KeyValue;
import com.arakviel.carrepair.presentation.view.PhotoTableCell;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import java.net.URL;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;

public class SpareViewController implements Initializable {

    @FXML
    private Label countLabel;

    @FXML
    private TableView<SpareModel> table = new TableView<>();

    @FXML
    private MFXTextField searchTextField;

    @FXML
    private MFXButton searchButton;

    @FXML
    private MFXComboBox<KeyValue> searchComboBox;

    private ObservableList<SpareModel> spares;
    private SpareRepository spareRepository;
    private SpareModelMapper spareModelMapper;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        spareRepository = RepositoryFactory.getInstance().getSpareRepository();
        spareModelMapper = ModelMapperFactory.getInstance().getSpareModelMapper();
        initSearchComboBox();
    }

    public void setupTable(ObservableList<SpareModel> spares) {
        countLabel.textProperty().bind(Bindings.size(spares).asString());

        TableColumn<SpareModel, Image> photoColumn = new TableColumn<>("Фото");
        photoColumn.setCellValueFactory(cellData -> {
            ObjectProperty<Image> photoProperty = cellData.getValue().photoProperty();
            return new SimpleObjectProperty<>(photoProperty.get());
        });
        photoColumn.setCellFactory(column -> new PhotoTableCell<>());

        TableColumn<SpareModel, String> workroomColumn = new TableColumn<>("Відділення");
        workroomColumn.setCellValueFactory(param -> {
            var workroomModel = param.getValue().getWorkroomModel();
            return workroomModel.nameProperty();
        });

        TableColumn<SpareModel, String> nameColumn = new TableColumn<>("Назва");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<SpareModel, String> descriptionColumn = new TableColumn<>("Опис");
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        descriptionColumn.setPrefWidth(200);

        TableColumn<SpareModel, String> priceColumn = new TableColumn<>("Ціна");
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));

        TableColumn<SpareModel, String> quantityInStockColumn = new TableColumn<>("Кількість на складі");
        quantityInStockColumn.setCellValueFactory(new PropertyValueFactory<>("quantityInStock"));

        // Створюємо колонку "Редагувати"
        TableColumn<SpareModel, Void> editColumn = new TableColumn<>("");
        editColumn.setCellFactory(column -> new ButtonTableCell<>(CrudState.EDIT.getValue(), sm -> {
            var myController = PageLoader.getController("spares.addedit");
            var controller = (SpareAddEditController) myController.controller();
            controller.setSpareModel(sm);
            controller.setSpares(spares);
            PageLoader.setInnerContent(myController);
        }));

        // Створюємо колонку "Видалити"
        TableColumn<SpareModel, Void> deleteColumn = new TableColumn<>("");
        deleteColumn.setCellFactory(column -> new ButtonTableCell<>(CrudState.REMOVE.getValue(), sm -> {
            if (UiHelpers.showDeleteConfirmation()) {
                spareRepository.remove(sm.getId());
                spares.remove(sm);
            }
        }));

        table.getColumns()
                .addAll(
                        photoColumn,
                        workroomColumn,
                        nameColumn,
                        descriptionColumn,
                        priceColumn,
                        quantityInStockColumn,
                        editColumn,
                        deleteColumn);
        table.setItems(spares);
    }

    public void onSearchButton(ActionEvent actionEvent) {
        String search = searchTextField.getText();
        String selectedKey = "";
        var selectedKeyValue = searchComboBox.getValue();
        if (Objects.nonNull(selectedKeyValue)) {
            selectedKey = selectedKeyValue.key();
        }

        List<Spare> spareList = spareRepository.getAllWhere(
                null,
                selectedKey.equals("name") ? search : null,
                selectedKey.equals("price") && !search.isBlank() ? new Money(Integer.parseInt(search), 0) : null,
                selectedKey.equals("quantityInStock") && !search.isBlank() ? Integer.parseInt(search) : null);

        var spareModels = spareList.stream()
                .map(spareModelMapper::toModel)
                .sorted(Comparator.comparing(SpareModel::getName))
                .toList();

        spares = FXCollections.observableArrayList(spareModels);
        table.setItems(spares);
    }

    private void initSearchComboBox() {
        searchComboBox.setConverter(new KeyValueStringConverter());
        searchComboBox.getItems().add(new KeyValue("name", "Назва"));
        searchComboBox.getItems().add(new KeyValue("price", "Ціна"));
        searchComboBox.getItems().add(new KeyValue("quantityInStock", "Кількість в складі"));
        searchComboBox.selectFirst();
    }
}
