package com.arakviel.carrepair.presentation.controller.position;

import com.arakviel.carrepair.domain.factory.RepositoryFactory;
import com.arakviel.carrepair.domain.impl.Position;
import com.arakviel.carrepair.domain.impl.Role;
import com.arakviel.carrepair.domain.repository.PositionRepository;
import com.arakviel.carrepair.domain.repository.RoleRepository;
import com.arakviel.carrepair.presentation.mapper.impl.ModelMapperFactory;
import com.arakviel.carrepair.presentation.mapper.impl.PositionModelMapper;
import com.arakviel.carrepair.presentation.mapper.impl.RoleModelMapper;
import com.arakviel.carrepair.presentation.model.impl.CurrencyModel;
import com.arakviel.carrepair.presentation.model.impl.MoneyModel;
import com.arakviel.carrepair.presentation.model.impl.PositionModel;
import com.arakviel.carrepair.presentation.model.impl.RoleModel;
import com.arakviel.carrepair.presentation.util.CrudState;
import com.arakviel.carrepair.presentation.util.UiHelpers;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXCheckbox;
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
import javafx.util.converter.IntegerStringConverter;

public class PositionAddEditController implements Initializable {

    @FXML
    private MFXTextField nameTextField;

    @FXML
    private Label nameErrorLabel;

    @FXML
    private TextArea descriptionTextArea;

    @FXML
    private Label descriptionErrorLabel;

    @FXML
    private MFXFilterComboBox<CurrencyModel> currencyComboBox;

    @FXML
    private Label currencyErrorLabel;

    @FXML
    private MFXTextField salaryPerHourWholePartTextField;

    @FXML
    private MFXTextField salaryPerHourDecimalPartTextField;

    @FXML
    private Label salaryPerHourErrorLabel;

    @FXML
    private MFXCheckbox canEditUsersCheckBox;

    @FXML
    private MFXCheckbox canEditSparesCheckBox;

    @FXML
    private MFXCheckbox canEditClientsCheckBox;

    @FXML
    private MFXCheckbox canEditServicesCheckBox;

    @FXML
    private MFXCheckbox canEditOrdersCheckBox;

    @FXML
    private MFXCheckbox canEditPayrollsCheckBox;

    @FXML
    private MFXButton addEditButton;

    @FXML
    private MFXButton editResetButton;

    private PositionModel positionModel;
    private RoleModel roleModel;
    private MoneyModel moneyModel;
    private PositionRepository positionRepository;
    private RoleRepository roleRepository;
    private PositionModelMapper positionModelMapper;
    private RoleModelMapper roleModelMapper;
    private ObservableList<PositionModel> positions;
    private List<CurrencyModel> currencies;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.positionRepository = RepositoryFactory.getInstance().getPositionRepository();
        this.roleRepository = RepositoryFactory.getInstance().getRoleRepository();
        this.positionModelMapper = ModelMapperFactory.getInstance().getPositionModelMapper();
        this.roleModelMapper = ModelMapperFactory.getInstance().getRoleModelMapper();
        Tooltip tooltip = new Tooltip("Скинути редагування");
        Tooltip.install(editResetButton, tooltip);
        initCurrencyComboBox();

        moneyModel = new MoneyModel(0, 0);
        positionModel = PositionModel.builder()
                .name(null)
                .currencyModel(currencies.get(0))
                .salaryPerHour(moneyModel)
                .build();
        roleModel = RoleModel.builder()
                .id(0)
                .canEditUsers(false)
                .canEditSpares(false)
                .canEditClients(false)
                .canEditServices(false)
                .canEditOrders(false)
                .canEditPayrolls(false)
                .build();
        positionModel.setRoleModel(roleModel);

        initBindings();
    }

    public void setPositions(ObservableList<PositionModel> positions) {
        this.positions = positions;
    }

    public void setPositionModel(PositionModel positionModel) {
        this.positionModel = positionModel.clone();
        this.moneyModel = this.positionModel.getSalaryPerHour();
        this.roleModel = this.positionModel.getRoleModel();
        addEditButton.setText(CrudState.EDIT.getValue());
        editResetButton.setManaged(true);
        editResetButton.setVisible(true);
        initBindings();
    }

    public void onAddEditButtonClick(ActionEvent actionEvent) {
        try {
            Position position = positionModelMapper.toDomain(positionModel);
            Role role = roleModelMapper.toDomain(roleModel);
            if (addEditButton.getText().equals(CrudState.ADD.getValue())) {
                Position newPosition = positionRepository.add(position);
                PositionModel newPositionModel = positionModelMapper.toModel(newPosition);

                role.setId(newPosition.getId());
                Role newRole = roleRepository.add(role);
                newPositionModel.setRoleModel(roleModelMapper.toModel(newRole));

                positions.add(newPositionModel);
            } else {
                positionRepository.set(position.getId(), position);
                role.setId(position.getId());
                roleRepository.set(role.getId(), role);
                RoleModel updatedRoleModel = roleModelMapper.toModel(role);
                positionModel.setRoleModel(updatedRoleModel.clone());
                positionModel.setSalaryPerHour(moneyModel.clone());

                int index = IntStream.range(0, positions.size())
                        .filter(i -> positions.get(i).getId().equals(positionModel.getId()))
                        .findFirst()
                        .orElse(-1);
                positions.set(index, positionModel.clone());
                // Скидаємо все до дефолту.
                addEditButton.setText(CrudState.ADD.getValue());
                editResetButton.setVisible(false);
                editResetButton.setManaged(false);
            }
            resetModels();
        } catch (Exception e) {
            Map<String, List<String>> validationMessages = positionRepository.getValidationMessages();
            UiHelpers.validateShow(validationMessages, "name", nameErrorLabel);
            UiHelpers.validateShow(validationMessages, "description", descriptionErrorLabel);
            UiHelpers.validateShow(validationMessages, "currency", currencyErrorLabel);
            UiHelpers.validateShow(validationMessages, "salaryPerHourWholePart", salaryPerHourErrorLabel);
            UiHelpers.validateShow(validationMessages, "salaryPerHourDecimalPart", salaryPerHourErrorLabel);
        }
    }

    public void onEditResetButtonClick(ActionEvent actionEvent) {
        addEditButton.setText(CrudState.ADD.getValue());
        editResetButton.setManaged(false);
        editResetButton.setVisible(false);
        resetModels();
    }

    private void initCurrencyComboBox() {
        var currencyRepository = RepositoryFactory.getInstance().getCurrencyRepository();
        var currencyModelMapper = ModelMapperFactory.getInstance().getCurrencyModelMapper();
        currencies = currencyRepository.getAll().stream()
                .map(currencyModelMapper::toModel)
                .sorted(Comparator.comparing(CurrencyModel::getName))
                .toList();
        currencyComboBox.getItems().addAll(currencies);
        currencyComboBox.selectFirst();
        currencyComboBox.setText(currencies.get(0).toString());
    }

    private void initBindings() {
        nameTextField.textProperty().bindBidirectional(positionModel.nameProperty());
        descriptionTextArea.textProperty().bindBidirectional(positionModel.descriptionProperty());
        currencyComboBox.valueProperty().bindBidirectional(positionModel.currencyModelProperty());

        TextFormatter<Integer> textFormatterForWholePart = new TextFormatter<>(new IntegerStringConverter());
        salaryPerHourWholePartTextField.setTextFormatter(textFormatterForWholePart);
        textFormatterForWholePart
                .valueProperty()
                .bindBidirectional(moneyModel.wholePartProperty().asObject());
        textFormatterForWholePart.valueProperty().addListener((obs, oldValue, newValue) -> {
            if (Objects.nonNull(newValue)) {
                moneyModel.setWholePart(newValue);
            }
        });

        TextFormatter<Integer> textFormatterForDecimalPart = new TextFormatter<>(new IntegerStringConverter());
        salaryPerHourDecimalPartTextField.setTextFormatter(textFormatterForDecimalPart);
        textFormatterForDecimalPart
                .valueProperty()
                .bindBidirectional(moneyModel.decimalPartProperty().asObject());
        textFormatterForDecimalPart.valueProperty().addListener((obs, oldValue, newValue) -> {
            if (Objects.nonNull(newValue)) {
                moneyModel.setDecimalPart(newValue);
            }
        });

        canEditUsersCheckBox.selectedProperty().bindBidirectional(roleModel.canEditUsersProperty());
        canEditSparesCheckBox.selectedProperty().bindBidirectional(roleModel.canEditSparesProperty());
        canEditClientsCheckBox.selectedProperty().bindBidirectional(roleModel.canEditClientsProperty());
        canEditServicesCheckBox.selectedProperty().bindBidirectional(roleModel.canEditServicesProperty());
        canEditOrdersCheckBox.selectedProperty().bindBidirectional(roleModel.canEditOrdersProperty());
        canEditPayrollsCheckBox.selectedProperty().bindBidirectional(roleModel.canEditPayrollsProperty());
    }

    private void resetModels() {
        moneyModel.setWholePart(0);
        moneyModel.setDecimalPart(0);
        positionModel.setId(null);
        positionModel.setName("");
        positionModel.setDescription("");
        positionModel.setCurrencyModel(currencies.get(0));
        positionModel.setSalaryPerHour(moneyModel);
        roleModel.setId(null);
        roleModel.setCanEditUsers(false);
        roleModel.setCanEditSpares(false);
        roleModel.setCanEditClients(false);
        roleModel.setCanEditServices(false);
        roleModel.setCanEditOrders(false);
        roleModel.setCanEditPayrolls(false);
    }
}
