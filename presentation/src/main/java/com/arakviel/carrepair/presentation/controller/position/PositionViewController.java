package com.arakviel.carrepair.presentation.controller.position;

import com.arakviel.carrepair.domain.factory.RepositoryFactory;
import com.arakviel.carrepair.domain.impl.Currency;
import com.arakviel.carrepair.domain.impl.Position;
import com.arakviel.carrepair.domain.repository.PositionRepository;
import com.arakviel.carrepair.presentation.converter.KeyValueStringConverter;
import com.arakviel.carrepair.presentation.mapper.impl.ModelMapperFactory;
import com.arakviel.carrepair.presentation.mapper.impl.PositionModelMapper;
import com.arakviel.carrepair.presentation.model.impl.PositionModel;
import com.arakviel.carrepair.presentation.model.impl.RoleModel;
import com.arakviel.carrepair.presentation.util.CrudState;
import com.arakviel.carrepair.presentation.util.PageLoader;
import com.arakviel.carrepair.presentation.util.UiHelpers;
import com.arakviel.carrepair.presentation.view.ButtonTableCell;
import com.arakviel.carrepair.presentation.view.KeyValue;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXCheckbox;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import java.net.URL;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;

public class PositionViewController implements Initializable {

    @FXML
    private MFXCheckbox roleShowHideCheckBox;

    @FXML
    private TableView<PositionModel> table;

    @FXML
    private Label countLabel;

    @FXML
    private MFXTextField searchTextField;

    @FXML
    private MFXButton searchButton;

    @FXML
    private MFXComboBox<KeyValue> searchComboBox;

    private ObservableList<PositionModel> positions;
    private PositionRepository positionRepository;
    private PositionModelMapper positionModelMapper;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        positionRepository = RepositoryFactory.getInstance().getPositionRepository();
        positionModelMapper = ModelMapperFactory.getInstance().getPositionModelMapper();
        initSearchComboBox();
    }

    public void setupTable(ObservableList<PositionModel> positions) {
        countLabel.textProperty().bind(Bindings.size(positions).asString());

        TableColumn<PositionModel, String> nameColumn = new TableColumn<>("Назва");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<PositionModel, String> descriptionColumn = new TableColumn<>("Опис");
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        descriptionColumn.setPrefWidth(200);

        TableColumn<PositionModel, String> currencyColumn = new TableColumn<>("Валюта");
        currencyColumn.setCellValueFactory(new PropertyValueFactory<>("currencyModel"));

        TableColumn<PositionModel, String> salaryPerHourColumn = new TableColumn<>("ЗП в час");
        salaryPerHourColumn.setCellValueFactory(new PropertyValueFactory<>("salaryPerHour"));

        TableColumn<PositionModel, Boolean> canEditUsersColumn = new TableColumn<>("Редагування користувачів");
        canEditUsersColumn.setCellValueFactory(param -> {
            RoleModel roleModel = param.getValue().getRoleModel();
            return roleModel.canEditUsersProperty();
        });
        canEditUsersColumn.setCellFactory(CheckBoxTableCell.forTableColumn(canEditUsersColumn));
        canEditUsersColumn.visibleProperty().bind(roleShowHideCheckBox.selectedProperty());

        TableColumn<PositionModel, Boolean> canEditSparesColumn = new TableColumn<>("Редагування запчастин");
        canEditSparesColumn.setCellValueFactory(param -> {
            RoleModel roleModel = param.getValue().getRoleModel();
            return roleModel.canEditSparesProperty();
        });
        canEditSparesColumn.setCellFactory(CheckBoxTableCell.forTableColumn(canEditSparesColumn));
        canEditSparesColumn.visibleProperty().bind(roleShowHideCheckBox.selectedProperty());

        TableColumn<PositionModel, Boolean> canEditClientsColumn = new TableColumn<>("Редагування клієнтів");
        canEditClientsColumn.setCellValueFactory(param -> {
            RoleModel roleModel = param.getValue().getRoleModel();
            return roleModel.canEditClientsProperty();
        });
        canEditClientsColumn.setCellFactory(CheckBoxTableCell.forTableColumn(canEditClientsColumn));
        canEditClientsColumn.visibleProperty().bind(roleShowHideCheckBox.selectedProperty());

        TableColumn<PositionModel, Boolean> canEditServicesColumn = new TableColumn<>("Редагування послуг");
        canEditServicesColumn.setCellValueFactory(param -> {
            RoleModel roleModel = param.getValue().getRoleModel();
            return roleModel.canEditServicesProperty();
        });
        canEditServicesColumn.setCellFactory(CheckBoxTableCell.forTableColumn(canEditServicesColumn));
        canEditServicesColumn.visibleProperty().bind(roleShowHideCheckBox.selectedProperty());

        TableColumn<PositionModel, Boolean> canEditOrdersColumn = new TableColumn<>("Редагування замовлень");
        canEditOrdersColumn.setCellValueFactory(param -> {
            RoleModel roleModel = param.getValue().getRoleModel();
            return roleModel.canEditOrdersProperty();
        });
        canEditOrdersColumn.setCellFactory(CheckBoxTableCell.forTableColumn(canEditOrdersColumn));
        canEditOrdersColumn.visibleProperty().bind(roleShowHideCheckBox.selectedProperty());

        TableColumn<PositionModel, Boolean> canEditPayrollsColumn = new TableColumn<>("Редагування заробітніх плат");
        canEditPayrollsColumn.setCellValueFactory(param -> {
            RoleModel roleModel = param.getValue().getRoleModel();
            return roleModel.canEditPayrollsProperty();
        });
        canEditPayrollsColumn.setCellFactory(CheckBoxTableCell.forTableColumn(canEditPayrollsColumn));
        canEditPayrollsColumn.visibleProperty().bind(roleShowHideCheckBox.selectedProperty());

        // Створюємо колонку "Редагувати"
        TableColumn<PositionModel, Void> editColumn = new TableColumn<>("");
        editColumn.setCellFactory(column -> new ButtonTableCell<>(CrudState.EDIT.getValue(), pm -> {
            var myController = PageLoader.getController("positions.addedit");
            var controller = (PositionAddEditController) myController.controller();
            controller.setPositionModel(pm);
            controller.setPositions(positions);
            PageLoader.setInnerContent(myController);
        }));

        // Створюємо колонку "Видалити"
        TableColumn<PositionModel, Void> deleteColumn = new TableColumn<>("");
        deleteColumn.setCellFactory(column -> new ButtonTableCell<>(CrudState.REMOVE.getValue(), pm -> {
            if (UiHelpers.showDeleteConfirmation()) {
                positionRepository.remove(pm.getId());
                positions.remove(pm);
            }
        }));

        table.getColumns()
                .addAll(
                        nameColumn,
                        descriptionColumn,
                        currencyColumn,
                        salaryPerHourColumn,
                        canEditUsersColumn,
                        canEditSparesColumn,
                        canEditClientsColumn,
                        canEditServicesColumn,
                        canEditOrdersColumn,
                        canEditPayrollsColumn,
                        editColumn,
                        deleteColumn);
        table.setItems(positions);
    }

    @FXML
    private void onSearchButton(ActionEvent actionEvent) {
        String search = searchTextField.getText();
        String selectedKey = "";
        var selectedKeyValue = searchComboBox.getValue();
        if (Objects.nonNull(selectedKeyValue)) {
            selectedKey = selectedKeyValue.key();
        }

        Currency currency = null;
        if (selectedKey.equals("currency") && !search.isBlank()) {
            currency = RepositoryFactory.getInstance()
                    .getCurrencyRepository()
                    .getAllWhere(search, null)
                    .get(0);
        }

        List<Position> positionList = positionRepository.getAllWhere(
                selectedKey.equals("name") ? search : null, Objects.nonNull(currency) ? currency : null, null);
        List<PositionModel> positionModels = positionList.stream()
                .map(positionModelMapper::toModel)
                .sorted(Comparator.comparing(PositionModel::getName))
                .toList();
        positions = FXCollections.observableArrayList(positionModels);
        table.setItems(positions);
    }

    private void initSearchComboBox() {
        searchComboBox.setConverter(new KeyValueStringConverter());
        searchComboBox.getItems().add(new KeyValue("name", "Назва"));
        searchComboBox.getItems().add(new KeyValue("currency", "Валюта"));
        searchComboBox.selectFirst();
    }
}
