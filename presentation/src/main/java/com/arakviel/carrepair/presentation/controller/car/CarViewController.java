package com.arakviel.carrepair.presentation.controller.car;

import com.arakviel.carrepair.domain.factory.RepositoryFactory;
import com.arakviel.carrepair.domain.impl.Car;
import com.arakviel.carrepair.domain.impl.CarPhoto;
import com.arakviel.carrepair.domain.impl.Model;
import com.arakviel.carrepair.domain.repository.CarPhotoRepository;
import com.arakviel.carrepair.domain.repository.CarRepository;
import com.arakviel.carrepair.presentation.converter.KeyValueStringConverter;
import com.arakviel.carrepair.presentation.mapper.impl.CarModelMapper;
import com.arakviel.carrepair.presentation.mapper.impl.CarPhotoModelMapper;
import com.arakviel.carrepair.presentation.mapper.impl.ModelMapperFactory;
import com.arakviel.carrepair.presentation.model.impl.CarModel;
import com.arakviel.carrepair.presentation.model.impl.CarPhotoModel;
import com.arakviel.carrepair.presentation.util.CrudState;
import com.arakviel.carrepair.presentation.util.PageLoader;
import com.arakviel.carrepair.presentation.util.UiHelpers;
import com.arakviel.carrepair.presentation.view.ButtonTableCell;
import com.arakviel.carrepair.presentation.view.KeyValue;
import com.arakviel.carrepair.presentation.view.LocalDateTimeTableCell;
import com.arakviel.carrepair.presentation.view.PhotosTableCell;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXCheckbox;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.Set;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
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

public class CarViewController implements Initializable {

    @FXML
    public MFXCheckbox showHideDateTimeFields;

    @FXML
    private TableView<CarModel> table;

    @FXML
    private Label countLabel;

    @FXML
    private MFXTextField searchTextField;

    @FXML
    private MFXButton searchButton;

    @FXML
    private MFXComboBox<KeyValue> searchComboBox;

    private ObservableList<CarModel> cars;
    private CarRepository carRepository;
    private CarPhotoRepository carPhotoRepository;
    private CarModelMapper carModelMapper;
    private CarPhotoModelMapper carPhotoModelMapper;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        carRepository = RepositoryFactory.getInstance().getCarRepository();
        carModelMapper = ModelMapperFactory.getInstance().getCarModelMapper();
        carPhotoRepository = RepositoryFactory.getInstance().getCarPhotoRepository();
        carPhotoModelMapper = ModelMapperFactory.getInstance().getCarPhotoModelMapper();
        initSearchComboBox();
    }

    public void setupTable(ObservableList<CarModel> cars) {
        countLabel.textProperty().bind(Bindings.size(cars).asString());

        TableColumn<CarModel, List<Image>> photosColumn = new TableColumn<>("Фото");
        photosColumn.setCellValueFactory(cellData -> {
            CarModel carModel = cellData.getValue();
            Car car = carModelMapper.toDomain(carModel);
            List<CarPhoto> carPhotos = carPhotoRepository.getAllWhere(car);
            List<Image> images = carPhotos.stream()
                    .map(carPhotoModelMapper::toModel)
                    .map(CarPhotoModel::getPhoto)
                    .toList();

            return new SimpleObjectProperty<>(images);
        });
        photosColumn.setCellFactory(column -> new PhotosTableCell<>());

        TableColumn<CarModel, String> modelColumn = new TableColumn<>("Модель");
        modelColumn.setCellValueFactory(param -> {
            String brandName = param.getValue().getModelModel().getBrandModel().getName();
            String modelName = param.getValue().getModelModel().getName();

            return new SimpleStringProperty("%s %s".formatted(brandName, modelName));
        });

        TableColumn<CarModel, String> numberColumn = new TableColumn<>("Номер");
        numberColumn.setCellValueFactory(new PropertyValueFactory<>("number"));

        TableColumn<CarModel, Integer> yearColumn = new TableColumn<>("Рік випуску");
        yearColumn.setCellValueFactory(new PropertyValueFactory<>("year"));

        TableColumn<CarModel, String> engineTypeColumn = new TableColumn<>("Тип двигуна");
        engineTypeColumn.setCellValueFactory(param ->
                new SimpleStringProperty(param.getValue().getEngineType().getName()));

        TableColumn<CarModel, Integer> mileageColumn = new TableColumn<>("Пробіг");
        mileageColumn.setCellValueFactory(new PropertyValueFactory<>("mileage"));

        TableColumn<CarModel, String> colorColumn = new TableColumn<>("Колір");
        colorColumn.setCellValueFactory(
                param -> new SimpleStringProperty(param.getValue().getHexColor()));

        TableColumn<CarModel, LocalDateTime> updatedAtColumn = new TableColumn<>("Оновлено");
        updatedAtColumn.setCellValueFactory(new PropertyValueFactory<>("updatedAt"));
        updatedAtColumn.setCellFactory(column -> new LocalDateTimeTableCell<>());
        updatedAtColumn.visibleProperty().bind(showHideDateTimeFields.selectedProperty());

        TableColumn<CarModel, LocalDateTime> createdAtColumn = new TableColumn<>("Створено");
        createdAtColumn.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
        createdAtColumn.setCellFactory(column -> new LocalDateTimeTableCell<>());
        createdAtColumn.visibleProperty().bind(showHideDateTimeFields.selectedProperty());

        // Створюємо колонку "Редагувати"
        TableColumn<CarModel, Void> editColumn = new TableColumn<>("");
        editColumn.setCellFactory(column -> new ButtonTableCell<>(CrudState.EDIT.getValue(), cm -> {
            var myController = PageLoader.getController("cars.addedit");
            var controller = (CarAddEditController) myController.controller();
            controller.setCarModel(cm);
            controller.setCars(cars);
            PageLoader.setInnerContent(myController);
        }));

        // Створюємо колонку "Видалити"
        TableColumn<CarModel, Void> deleteColumn = new TableColumn<>("");
        deleteColumn.setCellFactory(column -> new ButtonTableCell<>(CrudState.REMOVE.getValue(), cm -> {
            if (UiHelpers.showDeleteConfirmation()) {
                carRepository.remove(cm.getId());
                cars.remove(cm);
            }
        }));

        table.getColumns()
                .addAll(
                        photosColumn,
                        modelColumn,
                        numberColumn,
                        yearColumn,
                        engineTypeColumn,
                        mileageColumn,
                        colorColumn,
                        updatedAtColumn,
                        createdAtColumn,
                        editColumn,
                        deleteColumn);
        table.setItems(cars);
    }

    private void initSearchComboBox() {
        searchComboBox.setConverter(new KeyValueStringConverter());
        searchComboBox.getItems().add(new KeyValue("model", "Модель"));
        searchComboBox.getItems().add(new KeyValue("number", "Номер"));
        searchComboBox.getItems().add(new KeyValue("year", "Рік випуску"));
        searchComboBox.getItems().add(new KeyValue("engineType", "Тип двигуна"));
        searchComboBox.getItems().add(new KeyValue("mileage", "Пробіг"));
        searchComboBox.selectFirst();
    }

    public void onSearchButton(ActionEvent actionEvent) {
        String search = searchTextField.getText();
        String selectedKey = "";
        var selectedKeyValue = searchComboBox.getValue();
        if (Objects.nonNull(selectedKeyValue)) {
            selectedKey = selectedKeyValue.key();
        }

        var modelRepository = RepositoryFactory.getInstance().getModelRepository();
        List<Model> models = modelRepository.getAllWhere(null, selectedKey.equals("model") ? search : null);
        Set<Car> carSet = new HashSet<>();
        for (var model : models) {
            List<Car> innerCarList = carRepository.getAllWhere(
                    model,
                    selectedKey.equals("number") ? search : null,
                    selectedKey.equals("year") && search.isBlank() ? Short.parseShort(search) : null,
                    selectedKey.equals("engineType") ? search : null,
                    selectedKey.equals("mileage") && search.isBlank() ? Integer.parseInt(search) : null,
                    null,
                    null,
                    null);
            carSet.addAll(innerCarList);
        }
        var carModels = carSet.stream()
                .map(carModelMapper::toModel)
                .sorted(Comparator.comparing(CarModel::getNumber))
                .toList();
        cars = FXCollections.observableArrayList(carModels);
        table.setItems(cars);
    }
}
