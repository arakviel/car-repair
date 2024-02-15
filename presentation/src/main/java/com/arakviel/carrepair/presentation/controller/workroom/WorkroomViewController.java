package com.arakviel.carrepair.presentation.controller.workroom;

import com.arakviel.carrepair.domain.factory.RepositoryFactory;
import com.arakviel.carrepair.domain.impl.Address;
import com.arakviel.carrepair.domain.impl.Workroom;
import com.arakviel.carrepair.domain.repository.WorkroomRepository;
import com.arakviel.carrepair.presentation.converter.KeyValueStringConverter;
import com.arakviel.carrepair.presentation.mapper.impl.ModelMapperFactory;
import com.arakviel.carrepair.presentation.mapper.impl.WorkroomModelMapper;
import com.arakviel.carrepair.presentation.model.impl.WorkroomModel;
import com.arakviel.carrepair.presentation.util.CrudState;
import com.arakviel.carrepair.presentation.util.PageLoader;
import com.arakviel.carrepair.presentation.util.UiHelpers;
import com.arakviel.carrepair.presentation.view.ButtonTableCell;
import com.arakviel.carrepair.presentation.view.KeyValue;
import com.arakviel.carrepair.presentation.view.PhotoTableCell;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXCheckbox;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import java.net.URL;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.Set;
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

public class WorkroomViewController implements Initializable {

    @FXML
    private TableView<WorkroomModel> table = new TableView<>();

    @FXML
    private Label countLabel;

    @FXML
    private MFXTextField searchTextField;

    @FXML
    private MFXButton searchButton;

    @FXML
    private MFXComboBox<KeyValue> searchComboBox;

    @FXML
    private MFXCheckbox showHideDateTimeFields;

    private ObservableList<WorkroomModel> workrooms;
    private WorkroomRepository workroomRepository;
    private WorkroomModelMapper workroomModelMapper;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        workroomRepository = RepositoryFactory.getInstance().getWorkroomRepository();
        workroomModelMapper = ModelMapperFactory.getInstance().getWorkroomModelMapper();
        initSearchComboBox();
    }

    public void setupTable(ObservableList<WorkroomModel> workrooms) {
        countLabel.textProperty().bind(Bindings.size(workrooms).asString());

        TableColumn<WorkroomModel, Image> photoColumn = new TableColumn<>("Фото");
        photoColumn.setCellValueFactory(cellData -> {
            ObjectProperty<Image> photoProperty = cellData.getValue().photoProperty();
            return new SimpleObjectProperty<>(photoProperty.get());
        });
        photoColumn.setCellFactory(column -> new PhotoTableCell<>());

        TableColumn<WorkroomModel, String> nameColumn = new TableColumn<>("Назва");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<WorkroomModel, String> descriptionColumn = new TableColumn<>("Опис");
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        descriptionColumn.setPrefWidth(200);

        TableColumn<WorkroomModel, String> addressColumn = new TableColumn<>("Адреса");
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));

        // Створюємо колонку "Редагувати"
        TableColumn<WorkroomModel, Void> editColumn = new TableColumn<>("");
        editColumn.setCellFactory(column -> new ButtonTableCell<>(CrudState.EDIT.getValue(), wm -> {
            var myController = PageLoader.getController("workrooms.addedit");
            var controller = (WorkroomAddEditController) myController.controller();
            controller.setWorkroomModel(wm);
            controller.setWorkrooms(workrooms);
            PageLoader.setInnerContent(myController);
        }));

        // Створюємо колонку "Видалити"
        TableColumn<WorkroomModel, Void> deleteColumn = new TableColumn<>("");
        deleteColumn.setCellFactory(column -> new ButtonTableCell<>(CrudState.REMOVE.getValue(), wm -> {
            if (UiHelpers.showDeleteConfirmation()) {
                workroomRepository.remove(wm.getId());
                workrooms.remove(wm);
            }
        }));

        table.getColumns().addAll(photoColumn, nameColumn, descriptionColumn, addressColumn, editColumn, deleteColumn);
        table.setItems(workrooms);
    }

    private void initSearchComboBox() {
        searchComboBox.setConverter(new KeyValueStringConverter());
        searchComboBox.getItems().add(new KeyValue("country", "Країна"));
        searchComboBox.getItems().add(new KeyValue("region", "Регіон"));
        searchComboBox.getItems().add(new KeyValue("city", "Місто"));
        searchComboBox.getItems().add(new KeyValue("street", "Вулиця"));
        searchComboBox.getItems().add(new KeyValue("home", "Будинок"));
        searchComboBox.getItems().add(new KeyValue("name", "Назва філії"));
        searchComboBox.selectFirst();
    }

    @FXML
    private void onSearchButton(ActionEvent actionEvent) {
        String search = searchTextField.getText();
        String selectedKey = "";
        var selectedKeyValue = searchComboBox.getValue();
        if (Objects.nonNull(selectedKeyValue)) {
            selectedKey = selectedKeyValue.key();
        }

        var addressRepository = RepositoryFactory.getInstance().getAddressRepository();
        List<Address> addresses = addressRepository.getAllWhere(
                selectedKey.equals("country") ? search : null,
                selectedKey.equals("region") ? search : null,
                selectedKey.equals("city") ? search : null,
                selectedKey.equals("street") ? search : null,
                selectedKey.equals("home") ? search : null);
        Set<Workroom> workroomSet = new HashSet<>();
        for (var address : addresses) {
            List<Workroom> innerWorkroomList =
                    workroomRepository.getAllWhere(address, selectedKey.equals("name") ? search : null);
            workroomSet.addAll(innerWorkroomList);
        }
        var workroomModels = workroomSet.stream()
                .map(workroomModelMapper::toModel)
                .sorted(Comparator.comparing(WorkroomModel::getName))
                .toList();
        workrooms = FXCollections.observableArrayList(workroomModels);
        table.setItems(workrooms);
    }
}
