package com.arakviel.carrepair.presentation.controller.employee;

import com.arakviel.carrepair.domain.factory.RepositoryFactory;
import com.arakviel.carrepair.domain.impl.Employee;
import com.arakviel.carrepair.domain.impl.Position;
import com.arakviel.carrepair.domain.repository.EmployeeRepository;
import com.arakviel.carrepair.domain.repository.UserRepository;
import com.arakviel.carrepair.presentation.converter.KeyValueStringConverter;
import com.arakviel.carrepair.presentation.mapper.impl.EmployeeModelMapper;
import com.arakviel.carrepair.presentation.mapper.impl.ModelMapperFactory;
import com.arakviel.carrepair.presentation.mapper.impl.UserModelMapper;
import com.arakviel.carrepair.presentation.model.impl.EmployeeModel;
import com.arakviel.carrepair.presentation.model.impl.UserModel;
import com.arakviel.carrepair.presentation.util.CrudState;
import com.arakviel.carrepair.presentation.util.PageLoader;
import com.arakviel.carrepair.presentation.util.UiHelpers;
import com.arakviel.carrepair.presentation.view.ButtonTableCell;
import com.arakviel.carrepair.presentation.view.DownloadButtonTableCell;
import com.arakviel.carrepair.presentation.view.KeyValue;
import com.arakviel.carrepair.presentation.view.LocalDateTimeTableCell;
import com.arakviel.carrepair.presentation.view.PhotoTableCell;
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

public class EmployeeViewController implements Initializable {

    @FXML
    private TableView<EmployeeModel> table = new TableView<>();

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

    private ObservableList<EmployeeModel> employees;
    private EmployeeRepository employeeRepository;
    private UserRepository userRepository;
    private EmployeeModelMapper employeeModelMapper;
    private UserModelMapper userModelMapper;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        employeeRepository = RepositoryFactory.getInstance().getEmployeeRepository();
        userRepository = RepositoryFactory.getInstance().getUserRepository();
        employeeModelMapper = ModelMapperFactory.getInstance().getEmployeeModelMapper();
        userModelMapper = ModelMapperFactory.getInstance().getUserModelMapper();
        initSearchComboBox();
    }

    public void setupTable(ObservableList<EmployeeModel> staff) {
        countLabel.textProperty().bind(Bindings.size(staff).asString());

        TableColumn<EmployeeModel, Image> photoColumn = new TableColumn<>("Фото");
        photoColumn.setCellValueFactory(cellData -> {
            ObjectProperty<Image> photoProperty = cellData.getValue().photoProperty();
            return new SimpleObjectProperty<>(photoProperty.get());
        });
        photoColumn.setCellFactory(column -> new PhotoTableCell<>());

        TableColumn<EmployeeModel, String> loginColumn = new TableColumn<>("Логін");
        loginColumn.setCellValueFactory(param -> {
            UserModel userModel = param.getValue().getUserModel();
            return userModel.loginProperty();
        });

        TableColumn<EmployeeModel, String> emailColumn = new TableColumn<>("Email");
        emailColumn.setCellValueFactory(param -> {
            UserModel userModel = param.getValue().getUserModel();
            return userModel.emailProperty();
        });

        TableColumn<EmployeeModel, String> firstNameColumn = new TableColumn<>("Ім'я");
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));

        TableColumn<EmployeeModel, String> lastNameColumn = new TableColumn<>("Прізвище");
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));

        TableColumn<EmployeeModel, String> middleNameColumn = new TableColumn<>("По батькові");
        middleNameColumn.setCellValueFactory(new PropertyValueFactory<>("middleName"));

        TableColumn<EmployeeModel, String> positionColumn = new TableColumn<>("Посада");
        positionColumn.setCellValueFactory(new PropertyValueFactory<>("positionModel"));

        TableColumn<EmployeeModel, String> workroomColumn = new TableColumn<>("Відділ");
        workroomColumn.setCellValueFactory(new PropertyValueFactory<>("workroomModel"));

        TableColumn<EmployeeModel, String> addressColumn = new TableColumn<>("Адреса");
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("addressModel"));

        TableColumn<EmployeeModel, Object> passportDocCopyColumn = new TableColumn<>("Копія паспорта");
        passportDocCopyColumn.setCellValueFactory(new PropertyValueFactory<>("passportDocCopy"));
        passportDocCopyColumn.setCellFactory(column -> new DownloadButtonTableCell());

        TableColumn<EmployeeModel, Object> bankNumberDocCopyColumn = new TableColumn<>("Копія РНКП");
        bankNumberDocCopyColumn.setCellValueFactory(new PropertyValueFactory<>("bankNumberDocCopy"));
        bankNumberDocCopyColumn.setCellFactory(column -> new DownloadButtonTableCell());

        TableColumn<EmployeeModel, Object> otherDocCopyColumn = new TableColumn<>("Копія додаткового документу");
        otherDocCopyColumn.setCellValueFactory(new PropertyValueFactory<>("otherDocCopy"));
        otherDocCopyColumn.setCellFactory(column -> new DownloadButtonTableCell());

        TableColumn<EmployeeModel, LocalDateTime> updatedAtColumn = new TableColumn<>("Оновлено");
        updatedAtColumn.setCellValueFactory(new PropertyValueFactory<>("updatedAt"));
        updatedAtColumn.setCellFactory(column -> new LocalDateTimeTableCell<>());
        updatedAtColumn.visibleProperty().bind(showHideDateTimeFields.selectedProperty());

        TableColumn<EmployeeModel, LocalDateTime> createdAtColumn = new TableColumn<>("Створено");
        createdAtColumn.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
        createdAtColumn.setCellFactory(column -> new LocalDateTimeTableCell<>());
        createdAtColumn.visibleProperty().bind(showHideDateTimeFields.selectedProperty());

        // Створюємо колонку "Редагувати"
        TableColumn<EmployeeModel, Void> editColumn = new TableColumn<>("");
        editColumn.setCellFactory(column -> new ButtonTableCell<>(CrudState.EDIT.getValue(), em -> {
            var myController = PageLoader.getController("staff.addedit");
            var controller = (EmployeeAddEditController) myController.controller();
            controller.setEmployeeModel(em);
            controller.setStaff(staff);
            PageLoader.setInnerContent(myController);
        }));

        // Створюємо колонку "Видалити"
        TableColumn<EmployeeModel, Void> deleteColumn = new TableColumn<>("");
        deleteColumn.setCellFactory(column -> new ButtonTableCell<>(CrudState.REMOVE.getValue(), wm -> {
            if (UiHelpers.showDeleteConfirmation()) {
                employeeRepository.remove(wm.getId());
                staff.remove(wm);
            }
        }));

        table.getColumns()
                .addAll(
                        photoColumn,
                        loginColumn,
                        emailColumn,
                        firstNameColumn,
                        lastNameColumn,
                        middleNameColumn,
                        positionColumn,
                        workroomColumn,
                        addressColumn,
                        passportDocCopyColumn,
                        bankNumberDocCopyColumn,
                        otherDocCopyColumn,
                        updatedAtColumn,
                        createdAtColumn,
                        editColumn,
                        deleteColumn);
        table.setItems(staff);
    }

    private void initSearchComboBox() {
        searchComboBox.setConverter(new KeyValueStringConverter());
        searchComboBox.getItems().add(new KeyValue("position", "Посада"));
        searchComboBox.getItems().add(new KeyValue("firstName", "Ім'я"));
        searchComboBox.getItems().add(new KeyValue("lastName", "Фамілія"));
        searchComboBox.getItems().add(new KeyValue("middleName", "По батькові"));
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

        var positionRepository = RepositoryFactory.getInstance().getPositionRepository();
        List<Position> positions =
                positionRepository.getAllWhere(selectedKey.equals("position") ? search : null, null, null);
        Set<Employee> employeeSet = new HashSet<>();
        for (var position : positions) {
            List<Employee> innerEmployeeList = employeeRepository.getAllWhere(
                    null,
                    null,
                    position,
                    selectedKey.equals("firstName") ? search : null,
                    selectedKey.equals("lastName") ? search : null,
                    selectedKey.equals("middleName") ? search : null,
                    null,
                    null);
            employeeSet.addAll(innerEmployeeList);
        }
        var employeeModels = employeeSet.stream()
                .map(employeeModelMapper::toModel)
                .sorted(Comparator.comparing(EmployeeModel::getFirstName))
                .toList();
        employees = FXCollections.observableArrayList(employeeModels);
        table.setItems(employees);
    }
}
