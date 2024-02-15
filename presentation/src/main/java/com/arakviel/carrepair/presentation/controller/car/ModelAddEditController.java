package com.arakviel.carrepair.presentation.controller.car;

import com.arakviel.carrepair.domain.factory.RepositoryFactory;
import com.arakviel.carrepair.domain.impl.Model;
import com.arakviel.carrepair.domain.repository.BrandRepository;
import com.arakviel.carrepair.domain.repository.ModelRepository;
import com.arakviel.carrepair.presentation.mapper.impl.BrandModelMapper;
import com.arakviel.carrepair.presentation.mapper.impl.ModelMapperFactory;
import com.arakviel.carrepair.presentation.mapper.impl.ModelModelMapper;
import com.arakviel.carrepair.presentation.model.impl.BrandModel;
import com.arakviel.carrepair.presentation.model.impl.ModelModel;
import com.arakviel.carrepair.presentation.util.CrudState;
import com.arakviel.carrepair.presentation.util.PageLoader;
import com.arakviel.carrepair.presentation.util.UiHelpers;
import com.arakviel.carrepair.presentation.view.ButtonTableCell;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXFilterComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.IntStream;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;

public class ModelAddEditController implements Initializable {

    @FXML
    private MFXButton removeBrandButton;

    @FXML
    private TableView<ModelModel> table;

    @FXML
    private MFXFilterComboBox<BrandModel> brandComboBox;

    @FXML
    private Label brandErrorLabel;

    @FXML
    private MFXTextField modelTextField;

    @FXML
    private Label modelErrorLabel;

    @FXML
    private MFXButton addEditButton;

    @FXML
    private MFXButton editResetButton;

    private ModelModel modelModel;
    private ModelRepository modelRepository;
    private BrandRepository brandRepository;
    private ModelModelMapper modelModelMapper;
    private BrandModelMapper brandModelMapper;
    private ObservableList<ModelModel> models;
    private ObservableList<BrandModel> brands;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.modelRepository = RepositoryFactory.getInstance().getModelRepository();
        this.brandRepository = RepositoryFactory.getInstance().getBrandRepository();
        this.modelModelMapper = ModelMapperFactory.getInstance().getModelModelMapper();
        this.brandModelMapper = ModelMapperFactory.getInstance().getBrandModelMapper();
        Tooltip tooltip = new Tooltip("Скинути редагування");
        Tooltip.install(editResetButton, tooltip);

        List<ModelModel> modelModels =
                modelRepository.getAll().stream().map(modelModelMapper::toModel).toList();
        models = FXCollections.observableArrayList(modelModels);

        List<BrandModel> brandModels =
                brandRepository.getAll().stream().map(brandModelMapper::toModel).toList();
        brands = FXCollections.observableArrayList(brandModels);
        brandComboBox.getItems().addAll(brands);
        brandComboBox.selectFirst();
        brandComboBox.setText(brands.get(0).getName());

        modelModel = ModelModel.builder().brandModel(brands.get(0)).name("").build();

        brandComboBox.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                String enteredText = brandComboBox.getText();
                boolean isNewElement =
                        brands.stream().noneMatch(brand -> brand.getName().equalsIgnoreCase(enteredText));

                if (isNewElement && !enteredText.isBlank()) {
                    BrandModel newBrand = BrandModel.builder().name(enteredText).build();
                    BrandModel brandModel =
                            brandModelMapper.toModel(brandRepository.add(brandModelMapper.toDomain(newBrand)));
                    brands.add(brandModel);
                    modelModel.setBrandModel(brandModel);
                    brandComboBox.getItems().clear();
                    brandComboBox.getItems().addAll(brands);
                    brandComboBox.getSelectionModel().selectItem(brandModel);
                }
            }
        });

        initBindings();
        setupTable();
    }

    public void setupTable() {
        TableColumn<ModelModel, String> brandColumn = new TableColumn<>("Бренд");
        brandColumn.setCellValueFactory(new PropertyValueFactory<>("brandModel"));

        TableColumn<ModelModel, String> nameColumn = new TableColumn<>("Назва");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        // Створюємо колонку "Редагувати"
        TableColumn<ModelModel, Void> editColumn = new TableColumn<>("");
        editColumn.setCellFactory(column -> new ButtonTableCell<>(CrudState.EDIT.getValue(), mm -> {
            modelModel = mm.clone();
            addEditButton.setText(CrudState.EDIT.getValue());
            editResetButton.setManaged(true);
            editResetButton.setVisible(true);
            initBindings();
        }));

        // Створюємо колонку "Видалити"
        TableColumn<ModelModel, Void> deleteColumn = new TableColumn<>("");
        deleteColumn.setCellFactory(column -> new ButtonTableCell<>(CrudState.REMOVE.getValue(), mm -> {
            if (UiHelpers.showDeleteConfirmation()) {
                modelRepository.remove(mm.getId());
                models.remove(mm);
                refreshModelComboBoxInOtherController();
            }
        }));

        table.getColumns().addAll(brandColumn, nameColumn, editColumn, deleteColumn);
        table.setItems(models);
    }

    public void onAddEditButtonClick(ActionEvent actionEvent) {
        try {
            Model model = modelModelMapper.toDomain(modelModel);
            if (addEditButton.getText().equals(CrudState.ADD.getValue())) {
                Model newModel = modelRepository.add(model);
                ModelModel newModelModel = modelModelMapper.toModel(newModel);
                models.add(newModelModel);
            } else {
                modelRepository.set(model.getId(), model);
                int index = IntStream.range(0, models.size())
                        .filter(i -> models.get(i).getId().equals(modelModel.getId()))
                        .findFirst()
                        .orElse(-1);
                models.set(index, modelModel.clone());
                // Скидаємо все до дефолту.
                addEditButton.setText(CrudState.ADD.getValue());
                editResetButton.setVisible(false);
                editResetButton.setManaged(false);
            }
            resetModelModel();
            refreshModelComboBoxInOtherController();

        } catch (Exception e) {
            Map<String, List<String>> validationMessages = modelRepository.getValidationMessages();
            UiHelpers.validateShow(validationMessages, "brand", brandErrorLabel);
            UiHelpers.validateShow(validationMessages, "name", modelErrorLabel);
        }
    }

    private static void refreshModelComboBoxInOtherController() {
        var myController = PageLoader.getController("cars.addedit");
        var controller = (CarAddEditController) myController.controller();
        controller.initModelComboBox();
    }

    public void onEditResetButtonClick(ActionEvent actionEvent) {
        addEditButton.setText(CrudState.ADD.getValue());
        editResetButton.setManaged(false);
        editResetButton.setVisible(false);
        resetModelModel();
    }

    public void onRemoveBrandButton(ActionEvent actionEvent) {
        BrandModel brandModel = brandComboBox.getValue();
        brandRepository.remove(brandModel.getId());
        brandComboBox.getItems().remove(brandModel);
    }

    private void initBindings() {
        modelTextField.textProperty().bindBidirectional(modelModel.nameProperty());
        brandComboBox.valueProperty().bindBidirectional(modelModel.brandModelProperty());
    }

    private void resetModelModel() {
        modelModel.setId(null);
        modelModel.setBrandModel(brands.get(0));
        modelModel.setName("");
    }
}
