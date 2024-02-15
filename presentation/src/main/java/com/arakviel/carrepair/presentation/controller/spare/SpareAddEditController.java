package com.arakviel.carrepair.presentation.controller.spare;

import com.arakviel.carrepair.domain.factory.RepositoryFactory;
import com.arakviel.carrepair.domain.impl.Spare;
import com.arakviel.carrepair.domain.repository.SpareRepository;
import com.arakviel.carrepair.presentation.mapper.impl.ModelMapperFactory;
import com.arakviel.carrepair.presentation.mapper.impl.SpareModelMapper;
import com.arakviel.carrepair.presentation.model.impl.MoneyModel;
import com.arakviel.carrepair.presentation.model.impl.SpareModel;
import com.arakviel.carrepair.presentation.model.impl.WorkroomModel;
import com.arakviel.carrepair.presentation.util.CrudState;
import com.arakviel.carrepair.presentation.util.UiHelpers;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXFilterComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import java.net.URL;
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
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.util.converter.IntegerStringConverter;

public class SpareAddEditController implements Initializable {

    @FXML
    private MFXTextField nameTextField;

    @FXML
    private Label nameErrorLabel;

    @FXML
    private TextArea descriptionTextArea;

    @FXML
    private Label descriptionErrorLabel;

    @FXML
    private MFXFilterComboBox<WorkroomModel> workroomComboBox;

    @FXML
    private Label workroomErrorLabel;

    @FXML
    private MFXTextField priceWholePartTextField;

    @FXML
    private MFXTextField priceDecimalPartTextField;

    @FXML
    private Label priceErrorLabel;

    @FXML
    private MFXButton photoButton;

    @FXML
    private ImageView photoImageView;

    @FXML
    private Label photoErrorLabel;

    @FXML
    private MFXTextField quantityInStockTextField;

    @FXML
    private Label quantityInStockErrorLabel;

    @FXML
    private MFXButton addEditButton;

    @FXML
    private MFXButton editResetButton;

    private MoneyModel moneyModel;
    private SpareModel spareModel;
    private SpareRepository spareRepository;
    private SpareModelMapper spareModelMapper;
    private ObservableList<SpareModel> spares;
    private List<WorkroomModel> workrooms;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.spareRepository = RepositoryFactory.getInstance().getSpareRepository();
        this.spareModelMapper = ModelMapperFactory.getInstance().getSpareModelMapper();
        Tooltip tooltip = new Tooltip("Скинути редагування");
        Tooltip.install(editResetButton, tooltip);
        initWorkroomComboBox();

        moneyModel = new MoneyModel(0, 0);
        spareModel = SpareModel.builder()
                .workroom(workrooms.get(0))
                .name("")
                .price(moneyModel)
                .quantityInStock(0)
                .build();

        initBindings();
    }

    public void setSpares(ObservableList<SpareModel> spares) {
        this.spares = spares;
    }

    public void setSpareModel(SpareModel spareModel) {
        this.spareModel = spareModel.clone();
        this.moneyModel = this.spareModel.getPrice();
        addEditButton.setText(CrudState.EDIT.getValue());
        editResetButton.setManaged(true);
        editResetButton.setVisible(true);
        initBindings();
    }

    public void onPhotoButtonClick(ActionEvent actionEvent) {
        UiHelpers.initFileChooserForPhoto(photoImageView);
    }

    public void onAddEditButtonClick(ActionEvent actionEvent) {
        try {
            Spare spare = spareModelMapper.toDomain(spareModel);
            if (addEditButton.getText().equals(CrudState.ADD.getValue())) {
                Spare newSpare = spareRepository.add(spare);
                SpareModel newSpareModel = spareModelMapper.toModel(newSpare);
                spares.add(newSpareModel);
            } else {
                spareRepository.set(spare.getId(), spare);
                spareModel.setPrice(moneyModel.clone());
                int index = IntStream.range(0, spares.size())
                        .filter(i -> spares.get(i).getId().equals(spareModel.getId()))
                        .findFirst()
                        .orElse(-1);
                spares.set(index, spareModel.clone());
                // Скидаємо все до дефолту.
                addEditButton.setText(CrudState.ADD.getValue());
                editResetButton.setVisible(false);
                editResetButton.setManaged(false);
            }
            resetSpareModel();
        } catch (Exception e) {
            Map<String, List<String>> validationMessages = spareRepository.getValidationMessages();
            UiHelpers.validateShow(validationMessages, "photo", photoErrorLabel);
            UiHelpers.validateShow(validationMessages, "name", nameErrorLabel);
            UiHelpers.validateShow(validationMessages, "description", descriptionErrorLabel);
            UiHelpers.validateShow(validationMessages, "workroom", workroomErrorLabel);
            UiHelpers.validateShow(validationMessages, "priceWholePart", priceErrorLabel);
            UiHelpers.validateShow(validationMessages, "priceDecimalPart", priceErrorLabel);
            UiHelpers.validateShow(validationMessages, "quantityInStock", quantityInStockErrorLabel);
        }
    }

    public void onEditResetButtonClick(ActionEvent actionEvent) {
        addEditButton.setText(CrudState.ADD.getValue());
        editResetButton.setManaged(false);
        editResetButton.setVisible(false);
        resetSpareModel();
    }

    private void initWorkroomComboBox() {
        var workroomRepository = RepositoryFactory.getInstance().getWorkroomRepository();
        var workroomModelMapper = ModelMapperFactory.getInstance().getWorkroomModelMapper();
        workrooms = workroomRepository.getAll().stream()
                .map(workroomModelMapper::toModel)
                .sorted(Comparator.comparing(WorkroomModel::getName))
                .toList();
        workroomComboBox.getItems().addAll(workrooms);
        workroomComboBox.selectFirst();
        workroomComboBox.setText(workrooms.get(0).toString());
    }

    private void initBindings() {
        nameTextField.textProperty().bindBidirectional(spareModel.nameProperty());
        descriptionTextArea.textProperty().bindBidirectional(spareModel.descriptionProperty());
        workroomComboBox.valueProperty().bindBidirectional(spareModel.workroomModelProperty());
        photoImageView.imageProperty().bindBidirectional(spareModel.photoProperty());
        photoImageView.managedProperty().bind(spareModel.photoProperty().isNotNull());

        TextFormatter<Integer> textFormatterForWholePart = new TextFormatter<>(new IntegerStringConverter());
        priceWholePartTextField.setTextFormatter(textFormatterForWholePart);
        textFormatterForWholePart
                .valueProperty()
                .bindBidirectional(moneyModel.wholePartProperty().asObject());
        textFormatterForWholePart.valueProperty().addListener((obs, oldValue, newValue) -> {
            if (Objects.nonNull(newValue)) {
                moneyModel.setWholePart(newValue);
            }
        });

        TextFormatter<Integer> textFormatterForDecimalPart = new TextFormatter<>(new IntegerStringConverter());
        priceDecimalPartTextField.setTextFormatter(textFormatterForDecimalPart);
        textFormatterForDecimalPart
                .valueProperty()
                .bindBidirectional(moneyModel.decimalPartProperty().asObject());
        textFormatterForDecimalPart.valueProperty().addListener((obs, oldValue, newValue) -> {
            if (Objects.nonNull(newValue)) {
                moneyModel.setDecimalPart(newValue);
            }
        });

        TextFormatter<Integer> textFormatterForQuantityInStock = new TextFormatter<>(new IntegerStringConverter());
        quantityInStockTextField.setTextFormatter(textFormatterForQuantityInStock);
        textFormatterForQuantityInStock
                .valueProperty()
                .bindBidirectional(spareModel.quantityInStockProperty().asObject());
        textFormatterForQuantityInStock.valueProperty().addListener((obs, oldValue, newValue) -> {
            if (Objects.nonNull(newValue)) {
                spareModel.setQuantityInStock(newValue);
            }
        });
    }

    private void resetSpareModel() {
        moneyModel.setWholePart(0);
        moneyModel.setDecimalPart(0);
        priceWholePartTextField.setText("0");
        priceDecimalPartTextField.setText("0");
        quantityInStockTextField.setText("0");
        spareModel.setId(null);
        spareModel.setWorkroomModel(workrooms.get(0));
        spareModel.setName("");
        spareModel.setDescription("");
        spareModel.setPhoto(null);
        spareModel.setPrice(moneyModel);
        spareModel.setQuantityInStock(0);
    }
}
