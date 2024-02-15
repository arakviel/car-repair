package com.arakviel.carrepair.presentation.controller.payroll;

import com.arakviel.carrepair.domain.factory.RepositoryFactory;
import com.arakviel.carrepair.domain.impl.Employee;
import com.arakviel.carrepair.domain.impl.Payroll;
import com.arakviel.carrepair.domain.repository.PayrollRepository;
import com.arakviel.carrepair.presentation.converter.KeyValueStringConverter;
import com.arakviel.carrepair.presentation.mapper.impl.ModelMapperFactory;
import com.arakviel.carrepair.presentation.mapper.impl.PayrollModelMapper;
import com.arakviel.carrepair.presentation.model.impl.EmployeeModel;
import com.arakviel.carrepair.presentation.model.impl.PayrollModel;
import com.arakviel.carrepair.presentation.util.CrudState;
import com.arakviel.carrepair.presentation.util.PageLoader;
import com.arakviel.carrepair.presentation.util.UiHelpers;
import com.arakviel.carrepair.presentation.view.ButtonTableCell;
import com.arakviel.carrepair.presentation.view.KeyValue;
import com.arakviel.carrepair.presentation.view.LocalDateTableCell;
import com.arakviel.carrepair.presentation.view.LocalDateTimeTableCell;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXCheckbox;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.Set;
import javafx.beans.binding.Bindings;
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

public class PayrollViewController implements Initializable {

    @FXML
    private TableView<PayrollModel> table = new TableView<>();

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

    private ObservableList<PayrollModel> payrolls;
    private PayrollRepository payrollRepository;
    private PayrollModelMapper payrollModelMapper;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        payrollRepository = RepositoryFactory.getInstance().getPayrollRepository();
        payrollModelMapper = ModelMapperFactory.getInstance().getPayrollModelMapper();
        initSearchComboBox();
    }

    public void setupTable(ObservableList<PayrollModel> payrolls) {
        countLabel.textProperty().bind(Bindings.size(payrolls).asString());

        TableColumn<PayrollModel, String> employeeColumn = new TableColumn<>("Працівник");
        employeeColumn.setCellValueFactory(param -> {
            EmployeeModel employeeModel = param.getValue().getEmployeeModel();
            return new SimpleStringProperty(employeeModel.getFirstName() + " " + employeeModel.getLastName() + " "
                    + employeeModel.getMiddleName());
        });

        TableColumn<PayrollModel, String> periodTypeColumn = new TableColumn<>("Період");
        periodTypeColumn.setCellValueFactory(param ->
                new SimpleStringProperty(param.getValue().getPeriodType().getValue()));

        TableColumn<PayrollModel, Integer> hourCountColumn = new TableColumn<>("Кількість годин");
        hourCountColumn.setCellValueFactory(new PropertyValueFactory<>("hourCount"));

        TableColumn<PayrollModel, String> salaryColumn = new TableColumn<>("Зарплата");
        salaryColumn.setCellValueFactory(new PropertyValueFactory<>("salary"));

        TableColumn<PayrollModel, LocalDate> paymentAtColumn = new TableColumn<>("Дата оплати");
        paymentAtColumn.setCellValueFactory(new PropertyValueFactory<>("paymentAt"));
        paymentAtColumn.setCellFactory(column -> new LocalDateTableCell<>());

        TableColumn<PayrollModel, LocalDateTime> updatedAtColumn = new TableColumn<>("Оновлено");
        updatedAtColumn.setCellValueFactory(new PropertyValueFactory<>("updatedAt"));
        updatedAtColumn.setCellFactory(column -> new LocalDateTimeTableCell<>());
        updatedAtColumn.visibleProperty().bind(showHideDateTimeFields.selectedProperty());

        TableColumn<PayrollModel, LocalDateTime> createdAtColumn = new TableColumn<>("Створено");
        createdAtColumn.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
        createdAtColumn.setCellFactory(column -> new LocalDateTimeTableCell<>());
        createdAtColumn.visibleProperty().bind(showHideDateTimeFields.selectedProperty());

        // Створюємо колонку "Редагувати"
        TableColumn<PayrollModel, Void> editColumn = new TableColumn<>("");
        editColumn.setCellFactory(column -> new ButtonTableCell<>(CrudState.EDIT.getValue(), pm -> {
            var myController = PageLoader.getController("payrolls.addedit");
            var controller = (PayrollAddEditController) myController.controller();
            controller.setPayrollModel(pm);
            controller.setPayrolls(payrolls);
            PageLoader.setInnerContent(myController);
        }));

        // Створюємо колонку "Видалити"
        TableColumn<PayrollModel, Void> deleteColumn = new TableColumn<>("");
        deleteColumn.setCellFactory(column -> new ButtonTableCell<>(CrudState.REMOVE.getValue(), pm -> {
            if (UiHelpers.showDeleteConfirmation()) {
                payrollRepository.remove(pm.getId());
                payrolls.remove(pm);
            }
        }));

        table.getColumns()
                .addAll(
                        employeeColumn,
                        periodTypeColumn,
                        hourCountColumn,
                        salaryColumn,
                        paymentAtColumn,
                        updatedAtColumn,
                        createdAtColumn,
                        editColumn,
                        deleteColumn);
        table.setItems(payrolls);
    }

    private void initSearchComboBox() {
        searchComboBox.setConverter(new KeyValueStringConverter());
        searchComboBox.getItems().add(new KeyValue("employeeName", "Ім'я працівника"));
        searchComboBox.getItems().add(new KeyValue("employeeLastName", "Прізвище працівника"));
        searchComboBox.getItems().add(new KeyValue("employeeMiddleName", "По батькові працівника"));
        searchComboBox.getItems().add(new KeyValue("periodType", "Період"));
        searchComboBox.getItems().add(new KeyValue("hourCount", "Кількість годин"));
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

        var employeeRepository = RepositoryFactory.getInstance().getEmployeeRepository();
        List<Employee> employees = employeeRepository.getAllWhere(
                null,
                null,
                null,
                selectedKey.equals("employeeName") ? search : null,
                selectedKey.equals("employeeLastName") ? search : null,
                selectedKey.equals("employeeMiddleName") ? search : null,
                null,
                null);
        Set<Payroll> payrollSet = new HashSet<>();
        for (var employee : employees) {
            List<Payroll> innerPayrollList = payrollRepository.getAllWhere(
                    employee,
                    selectedKey.equals("periodType") ? search : null,
                    selectedKey.equals("hourCount") && !search.isBlank() ? Integer.parseInt(search) : null,
                    null,
                    null,
                    null,
                    null);
            payrollSet.addAll(innerPayrollList);
        }
        var payrollModels = payrollSet.stream()
                .map(payrollModelMapper::toModel)
                .sorted(Comparator.comparing(PayrollModel::getCreatedAt))
                .toList();
        payrolls = FXCollections.observableArrayList(payrollModels);
        table.setItems(payrolls);
    }
}
