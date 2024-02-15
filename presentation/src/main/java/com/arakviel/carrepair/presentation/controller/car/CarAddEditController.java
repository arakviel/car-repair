package com.arakviel.carrepair.presentation.controller.car;

import com.arakviel.carrepair.domain.factory.RepositoryFactory;
import com.arakviel.carrepair.domain.impl.Car;
import com.arakviel.carrepair.domain.repository.CarPhotoRepository;
import com.arakviel.carrepair.domain.repository.CarRepository;
import com.arakviel.carrepair.domain.repository.ModelRepository;
import com.arakviel.carrepair.presentation.mapper.impl.CarModelMapper;
import com.arakviel.carrepair.presentation.mapper.impl.CarPhotoModelMapper;
import com.arakviel.carrepair.presentation.mapper.impl.ModelMapperFactory;
import com.arakviel.carrepair.presentation.mapper.impl.ModelModelMapper;
import com.arakviel.carrepair.presentation.model.impl.CarModel;
import com.arakviel.carrepair.presentation.model.impl.CarModel.EngineType;
import com.arakviel.carrepair.presentation.model.impl.CarPhotoModel;
import com.arakviel.carrepair.presentation.model.impl.ModelModel;
import com.arakviel.carrepair.presentation.util.CrudState;
import com.arakviel.carrepair.presentation.util.UiHelpers;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXFilterComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.stream.IntStream;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.converter.IntegerStringConverter;

public class CarAddEditController implements Initializable {

    @FXML
    private Pane imagesContainer;

    @FXML
    private MFXTextField numberTextField;

    @FXML
    private Label numberErrorLabel;

    @FXML
    private MFXTextField yearTextField;

    @FXML
    private Label yearErrorLabel;

    @FXML
    private MFXFilterComboBox<ModelModel> modelComboBox;

    @FXML
    private Label modelErrorLabel;

    @FXML
    private MFXFilterComboBox<EngineType> engineTypeComboBox;

    @FXML
    private Label engineTypeErrorLabel;

    @FXML
    private MFXTextField mileageTextField;

    @FXML
    private Label mileageErrorLabel;

    @FXML
    private ColorPicker colorPicker;

    @FXML
    private Label colorErrorLabel;

    @FXML
    private MFXButton photoButton;

    @FXML
    private Label photoErrorLabel;

    @FXML
    private MFXButton addEditButton;

    @FXML
    private MFXButton editResetButton;

    private CarModel carModel;
    private CarRepository carRepository;
    private ModelRepository modelRepository;
    private CarPhotoRepository carPhotoRepository;
    private CarModelMapper carModelMapper;
    private CarPhotoModelMapper carPhotoModelMapper;
    private ModelModelMapper modelModelMapper;
    private ObservableList<CarModel> cars;
    private List<ModelModel> modelModels;
    private List<ImageView> imageViews;
    private List<CarPhotoModel> carPhotoModels;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        imageViews = new ArrayList<>();
        // carPhotoModels = new ArrayList<>();
        this.carRepository = RepositoryFactory.getInstance().getCarRepository();
        this.carPhotoRepository = RepositoryFactory.getInstance().getCarPhotoRepository();
        this.modelRepository = RepositoryFactory.getInstance().getModelRepository();
        this.carModelMapper = ModelMapperFactory.getInstance().getCarModelMapper();
        this.carPhotoModelMapper = ModelMapperFactory.getInstance().getCarPhotoModelMapper();
        this.modelModelMapper = ModelMapperFactory.getInstance().getModelModelMapper();
        Tooltip tooltip = new Tooltip("Скинути редагування");
        Tooltip.install(editResetButton, tooltip);
        initModelComboBox();
        initEngineTypeComboBox();

        carModel = CarModel.builder()
                .modelModel(modelModels.get(0))
                .number("")
                .year((short) 0)
                .engineType(null)
                .mileage(0)
                .build();

        initBindings();
    }

    public void setCars(ObservableList<CarModel> cars) {
        this.cars = cars;
    }

    public void onAddEditButtonClick(ActionEvent actionEvent) {
        try {
            Car car = carModelMapper.toDomain(carModel);
            if (addEditButton.getText().equals(CrudState.ADD.getValue())) {
                Car newCar = carRepository.add(car);
                CarModel newCarModel = carModelMapper.toModel(newCar);
                cars.add(newCarModel);

                for (var imageView : imageViews) {
                    CarPhotoModel newCarPhotoModel = CarPhotoModel.builder()
                            .carModel(newCarModel)
                            .photo(imageView.getImage())
                            .build();

                    carPhotoRepository.add(carPhotoModelMapper.toDomain(newCarPhotoModel));
                }
            } else {
                carRepository.set(car.getId(), car);
                int index = IntStream.range(0, cars.size())
                        .filter(i -> cars.get(i).getId().equals(carModel.getId()))
                        .findFirst()
                        .orElse(-1);
                cars.set(index, carModel.clone());

                for (int i = 0; i < carPhotoModels.size(); i++) {
                    CarPhotoModel carPhotoModel = carPhotoModels.get(0);
                    carPhotoModel.setPhoto(imageViews.get(0).getImage());
                    carPhotoRepository.set(carPhotoModel.getId(), carPhotoModelMapper.toDomain(carPhotoModel));
                }

                // Скидаємо все до дефолту.
                addEditButton.setText(CrudState.ADD.getValue());
                editResetButton.setVisible(false);
                editResetButton.setManaged(false);
            }
            resetCarModel();
        } catch (Exception e) {
            Map<String, List<String>> validationMessages = carRepository.getValidationMessages();
            UiHelpers.validateShow(validationMessages, "model", modelErrorLabel);
            UiHelpers.validateShow(validationMessages, "number", numberErrorLabel);
            UiHelpers.validateShow(validationMessages, "year", yearErrorLabel);
            UiHelpers.validateShow(validationMessages, "engineType", engineTypeErrorLabel);
            UiHelpers.validateShow(validationMessages, "mileage", mileageErrorLabel);
            UiHelpers.validateShow(validationMessages, "color", colorErrorLabel);
        }
    }

    public void onPhotoButtonClick(ActionEvent actionEvent) {
        imagesContainer.getChildren().clear();
        this.imageViews = UiHelpers.initFileChooserForPhotos(imagesContainer);
    }

    public void setCarModel(CarModel carModel) {
        this.carModel = carModel.clone();
        initPhotosForEdit(carModel);

        addEditButton.setText(CrudState.EDIT.getValue());
        editResetButton.setManaged(true);
        editResetButton.setVisible(true);
        initBindings();
    }

    private void initPhotosForEdit(CarModel carModel) {
        this.imagesContainer.getChildren().clear();
        this.imageViews.clear();

        this.carPhotoModels = carPhotoRepository.getAllWhere(carModelMapper.toDomain(carModel)).stream()
                .map(carPhotoModelMapper::toModel)
                .toList();
        FlowPane flowPane = new FlowPane();
        flowPane.setHgap(10);
        flowPane.setVgap(10);
        flowPane.setPadding(new Insets(10));
        for (var carPhotoModel : carPhotoModels) {
            ImageView imageView = new ImageView(carPhotoModel.getPhoto());
            imageView.setFitWidth(150);
            imageView.setFitHeight(150);
            imageView.setPreserveRatio(true);
            imageView.setSmooth(true);
            imageView.setClip(new Rectangle(imageView.getFitWidth(), imageView.getFitHeight()));
            imageView.setOnMouseClicked(e -> {
                if (e.getButton() == MouseButton.SECONDARY) {
                    if (UiHelpers.showDeleteConfirmation()) {
                        carPhotoRepository.remove(carPhotoModel.getId());
                        flowPane.getChildren().remove(imageView);
                        imageViews.remove(imageView);
                    }
                }
            });

            this.imageViews.add(imageView);
            flowPane.getChildren().add(imageView);
        }

        ScrollPane scrollPane = new ScrollPane(flowPane);
        scrollPane.setPrefViewportWidth(400);
        scrollPane.setPrefViewportHeight(200);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        this.imagesContainer.getChildren().add(scrollPane);
    }

    public void onEditResetButtonClick(ActionEvent actionEvent) {
        addEditButton.setText(CrudState.ADD.getValue());
        editResetButton.setManaged(false);
        editResetButton.setVisible(false);
        resetCarModel();
    }

    public void initModelComboBox() {
        modelModels = modelRepository.getAll().stream()
                .map(modelModelMapper::toModel)
                .sorted(Comparator.comparing(ModelModel::getName))
                .toList();
        modelComboBox.getItems().addAll(modelModels);
        modelComboBox.selectFirst();
        modelComboBox.setText(modelModels.get(0).toString());
    }

    private void initEngineTypeComboBox() {
        engineTypeComboBox.getItems().addAll(CarModel.EngineType.values());
        engineTypeComboBox.selectFirst();
        engineTypeComboBox.setText(EngineType.BENZINE.toString());
    }

    private void initBindings() {
        modelComboBox.valueProperty().bindBidirectional(carModel.modelModelProperty());
        numberTextField.textProperty().bindBidirectional(carModel.numberProperty());

        TextFormatter<Integer> textFormatterForYear = new TextFormatter<>(new IntegerStringConverter());
        yearTextField.setTextFormatter(textFormatterForYear);
        textFormatterForYear
                .valueProperty()
                .bindBidirectional(carModel.yearProperty().asObject());
        textFormatterForYear.valueProperty().addListener((obs, oldValue, newValue) -> {
            if (Objects.nonNull(newValue)) {
                carModel.setYear(newValue);
            }
        });

        engineTypeComboBox.valueProperty().bindBidirectional(carModel.engineTypeProperty());

        TextFormatter<Integer> textFormatterForMileage = new TextFormatter<>(new IntegerStringConverter());
        mileageTextField.setTextFormatter(textFormatterForMileage);
        textFormatterForMileage
                .valueProperty()
                .bindBidirectional(carModel.mileageProperty().asObject());
        textFormatterForMileage.valueProperty().addListener((obs, oldValue, newValue) -> {
            if (Objects.nonNull(newValue)) {
                carModel.setMileage(newValue);
            }
        });

        colorPicker.valueProperty().bindBidirectional(carModel.colorProperty());
    }

    private void resetCarModel() {
        carModel.setId(null);
        carModel.setModelModel(modelModels.get(0));
        carModel.setNumber("");
        carModel.setYear(0);
        carModel.setEngineType(engineTypeComboBox.getItems().get(0));
        carModel.setColor(Color.BLACK);
    }
}
