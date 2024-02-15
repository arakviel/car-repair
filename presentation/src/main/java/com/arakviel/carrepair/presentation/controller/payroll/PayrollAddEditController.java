package com.arakviel.carrepair.presentation.controller.payroll;

import com.arakviel.carrepair.domain.factory.RepositoryFactory;
import com.arakviel.carrepair.domain.impl.Payroll;
import com.arakviel.carrepair.domain.repository.PayrollRepository;
import com.arakviel.carrepair.presentation.mapper.impl.ModelMapperFactory;
import com.arakviel.carrepair.presentation.mapper.impl.PayrollModelMapper;
import com.arakviel.carrepair.presentation.model.impl.EmployeeModel;
import com.arakviel.carrepair.presentation.model.impl.MoneyModel;
import com.arakviel.carrepair.presentation.model.impl.PayrollModel;
import com.arakviel.carrepair.presentation.model.impl.PayrollModel.PeriodType;
import com.arakviel.carrepair.presentation.util.CrudState;
import com.arakviel.carrepair.presentation.util.UiHelpers;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXDatePicker;
import io.github.palexdev.materialfx.controls.MFXFilterComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import java.net.URL;
import java.time.LocalDate;
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
import javafx.scene.control.TextFormatter;
import javafx.scene.control.Tooltip;
import javafx.util.converter.IntegerStringConverter;

public class PayrollAddEditController implements Initializable {

    @FXML
    private MFXFilterComboBox<EmployeeModel> employeeComboBox;

    @FXML
    private Label employeeErrorLabel;

    @FXML
    private MFXFilterComboBox<PeriodType> periodTypeComboBox;

    @FXML
    private Label periodTypeErrorLabel;

    @FXML
    private MFXTextField hourCountTextField;

    @FXML
    private Label hourCountErrorLabel;

    @FXML
    private MFXTextField salaryWholePartTextField;

    @FXML
    private MFXTextField salaryDecimalPartTextField;

    @FXML
    private Label salaryErrorLabel;

    @FXML
    private MFXDatePicker paymentAtDatePicker;

    @FXML
    private Label paymentAtErrorLabel;

    @FXML
    private MFXButton addEditButton;

    @FXML
    private MFXButton editResetButton;

    private MoneyModel moneyModel;
    private PayrollModel payrollModel;
    private PayrollRepository payrollRepository;
    private PayrollModelMapper payrollModelMapper;
    private ObservableList<PayrollModel> payrolls;
    private List<EmployeeModel> employees;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.payrollRepository = RepositoryFactory.getInstance().getPayrollRepository();
        this.payrollModelMapper = ModelMapperFactory.getInstance().getPayrollModelMapper();
        Tooltip tooltip = new Tooltip("Скинути редагування");
        Tooltip.install(editResetButton, tooltip);
        initEmployeeComboBox();
        initPeriodTypeComboBox();
        paymentAtDatePicker.setValue(LocalDate.now());

        moneyModel = new MoneyModel(0, 0);
        payrollModel = payrollModel
                .builder()
                .employeeModel(employees.get(0))
                .periodType(null)
                .hourCount(0)
                .salary(moneyModel)
                .paymentAt(null)
                .build();

        initBindings();
    }

    public void setPayrolls(ObservableList<PayrollModel> payrolls) {
        this.payrolls = payrolls;
    }

    public void setPayrollModel(PayrollModel payrollModel) {
        this.payrollModel = payrollModel.clone();
        this.moneyModel = this.payrollModel.getSalary();
        addEditButton.setText(CrudState.EDIT.getValue());
        editResetButton.setManaged(true);
        editResetButton.setVisible(true);
        initBindings();
    }

    public void onAddEditButtonClick(ActionEvent actionEvent) {
        try {
            Payroll payroll = payrollModelMapper.toDomain(payrollModel);
            if (addEditButton.getText().equals(CrudState.ADD.getValue())) {
                Payroll newPayroll = payrollRepository.add(payroll);
                PayrollModel newPayrollModel = payrollModelMapper.toModel(newPayroll);
                payrolls.add(newPayrollModel);
            } else {
                payrollRepository.set(payroll.getId(), payroll);
                payrollModel.setSalary(moneyModel.clone());
                int index = IntStream.range(0, payrolls.size())
                        .filter(i -> payrolls.get(i).getId().equals(payrollModel.getId()))
                        .findFirst()
                        .orElse(-1);
                payrolls.set(index, payrollModel.clone());
                // Скидаємо все до дефолту.
                addEditButton.setText(CrudState.ADD.getValue());
                editResetButton.setVisible(false);
                editResetButton.setManaged(false);
            }
            resetPayrollModel();
        } catch (Exception e) {
            Map<String, List<String>> validationMessages = payrollRepository.getValidationMessages();
            UiHelpers.validateShow(validationMessages, "employee", employeeErrorLabel);
            UiHelpers.validateShow(validationMessages, "periodType", periodTypeErrorLabel);
            UiHelpers.validateShow(validationMessages, "hourCount", hourCountErrorLabel);
            UiHelpers.validateShow(validationMessages, "salaryWholePart", salaryErrorLabel);
            UiHelpers.validateShow(validationMessages, "salaryDecimalPart", salaryErrorLabel);
            UiHelpers.validateShow(validationMessages, "paymentAt", paymentAtErrorLabel);
        }
    }

    public void onEditResetButtonClick(ActionEvent actionEvent) {
        addEditButton.setText(CrudState.ADD.getValue());
        editResetButton.setManaged(false);
        editResetButton.setVisible(false);
        resetPayrollModel();
    }

    private void initBindings() {
        employeeComboBox.valueProperty().bindBidirectional(payrollModel.employeeModelProperty());
        periodTypeComboBox.valueProperty().bindBidirectional(payrollModel.periodTypeProperty());

        TextFormatter<Integer> textFormatterForPeriodType = new TextFormatter<>(new IntegerStringConverter());
        hourCountTextField.setTextFormatter(textFormatterForPeriodType);
        textFormatterForPeriodType
                .valueProperty()
                .bindBidirectional(payrollModel.hourCountProperty().asObject());
        textFormatterForPeriodType.valueProperty().addListener((obs, oldValue, newValue) -> {
            if (Objects.nonNull(newValue)) {
                payrollModel.setHourCount(newValue);
            }
        });

        TextFormatter<Integer> textFormatterForWholePart = new TextFormatter<>(new IntegerStringConverter());
        salaryWholePartTextField.setTextFormatter(textFormatterForWholePart);
        textFormatterForWholePart
                .valueProperty()
                .bindBidirectional(moneyModel.wholePartProperty().asObject());
        textFormatterForWholePart.valueProperty().addListener((obs, oldValue, newValue) -> {
            if (Objects.nonNull(newValue)) {
                moneyModel.setWholePart(newValue);
            }
        });

        TextFormatter<Integer> textFormatterForDecimalPart = new TextFormatter<>(new IntegerStringConverter());
        salaryDecimalPartTextField.setTextFormatter(textFormatterForDecimalPart);
        textFormatterForDecimalPart
                .valueProperty()
                .bindBidirectional(moneyModel.decimalPartProperty().asObject());
        textFormatterForDecimalPart.valueProperty().addListener((obs, oldValue, newValue) -> {
            if (Objects.nonNull(newValue)) {
                moneyModel.setDecimalPart(newValue);
            }
        });

        paymentAtDatePicker.valueProperty().bindBidirectional(payrollModel.paymentAtProperty());
    }

    private void initPeriodTypeComboBox() {
        periodTypeComboBox.getItems().setAll(PeriodType.values());
        periodTypeComboBox.selectIndex(1);
        periodTypeComboBox.setText(PeriodType.MONTHLY.toString());
    }

    private void initEmployeeComboBox() {
        var employeeRepository = RepositoryFactory.getInstance().getEmployeeRepository();
        var employeeModelMapper = ModelMapperFactory.getInstance().getEmployeeModelMapper();
        employees = employeeRepository.getAll().stream()
                .map(employeeModelMapper::toModel)
                .sorted(Comparator.comparing(EmployeeModel::getLastName))
                .toList();
        employeeComboBox.getItems().addAll(employees);
        employeeComboBox.selectFirst();
        employeeComboBox.setText(employees.get(0).toString());
    }

    private void resetPayrollModel() {
        moneyModel.setWholePart(0);
        moneyModel.setDecimalPart(0);
        payrollModel.setId(null);
        payrollModel.setHourCount(0);
        payrollModel.setSalary(moneyModel);
        payrollModel.setPaymentAt(null);
        salaryDecimalPartTextField.setText("0");
        salaryWholePartTextField.setText("0");
        hourCountTextField.setText("0");
    }
}
