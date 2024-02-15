package com.arakviel.carrepair.presentation.controller.employee;

import com.arakviel.carrepair.domain.factory.RepositoryFactory;
import com.arakviel.carrepair.domain.impl.Employee;
import com.arakviel.carrepair.domain.impl.User;
import com.arakviel.carrepair.domain.repository.EmployeeRepository;
import com.arakviel.carrepair.domain.repository.UserRepository;
import com.arakviel.carrepair.presentation.mapper.impl.EmployeeModelMapper;
import com.arakviel.carrepair.presentation.mapper.impl.ModelMapperFactory;
import com.arakviel.carrepair.presentation.mapper.impl.UserModelMapper;
import com.arakviel.carrepair.presentation.model.impl.AddressModel;
import com.arakviel.carrepair.presentation.model.impl.EmployeeModel;
import com.arakviel.carrepair.presentation.model.impl.PositionModel;
import com.arakviel.carrepair.presentation.model.impl.UserModel;
import com.arakviel.carrepair.presentation.model.impl.WorkroomModel;
import com.arakviel.carrepair.presentation.util.CrudState;
import com.arakviel.carrepair.presentation.util.UiHelpers;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXFilterComboBox;
import io.github.palexdev.materialfx.controls.MFXPasswordField;
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
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;

public class EmployeeAddEditController implements Initializable {

    @FXML
    private MFXTextField emailTextField;

    @FXML
    private Label emailErrorLabel;

    @FXML
    private MFXTextField loginTextField;

    @FXML
    private Label loginErrorLabel;

    @FXML
    private MFXPasswordField passwordPasswordField;

    @FXML
    private Label passwordErrorLabel;

    @FXML
    private MFXFilterComboBox<AddressModel> addressComboBox;

    @FXML
    private Label addressErrorLabel;

    @FXML
    private MFXFilterComboBox<WorkroomModel> workroomComboBox;

    @FXML
    private Label workroomErrorLabel;

    @FXML
    private MFXFilterComboBox<PositionModel> positionComboBox;

    @FXML
    private Label positionErrorLabel;

    @FXML
    private MFXTextField firstNameTextField;

    @FXML
    private Label firstNameErrorLabel;

    @FXML
    private MFXTextField lastNameTextField;

    @FXML
    private Label lastNameErrorLabel;

    @FXML
    private MFXTextField middleNameTextField;

    @FXML
    private Label middleNameErrorLabel;

    @FXML
    private MFXButton photoButton;

    @FXML
    private ImageView photoImageView;

    @FXML
    private Label photoErrorLabel;

    @FXML
    private MFXButton passportDocCopyButton;

    @FXML
    private Label passportDocCopyErrorLabel;

    @FXML
    private MFXButton bankNumberDocCopyButton;

    @FXML
    private Label bankNumberDocCopyErrorLabel;

    @FXML
    private MFXButton otherDocCopyButton;

    @FXML
    private Label otherDocCopyErrorLabel;

    @FXML
    private Label passportDocCopyLabel;

    @FXML
    private Label bankNumberDocCopyLabel;

    @FXML
    private Label otherDocCopyLabel;

    @FXML
    private MFXButton addEditButton;

    @FXML
    private MFXButton editResetButton;

    private EmployeeModel employeeModel;
    private UserModel userModel;
    private EmployeeRepository employeeRepository;
    private UserRepository userRepository;
    private EmployeeModelMapper employeeModelMapper;
    private UserModelMapper userModelMapper;
    private ObservableList<EmployeeModel> staff;
    private List<AddressModel> addressModels;
    private List<WorkroomModel> workroomModels;
    private List<PositionModel> positionModels;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.employeeRepository = RepositoryFactory.getInstance().getEmployeeRepository();
        this.userRepository = RepositoryFactory.getInstance().getUserRepository();
        this.employeeModelMapper = ModelMapperFactory.getInstance().getEmployeeModelMapper();
        this.userModelMapper = ModelMapperFactory.getInstance().getUserModelMapper();

        Tooltip tooltip = new Tooltip("Скинути редагування");
        Tooltip.install(editResetButton, tooltip);

        initAddressComboBox();
        initWorkroomComboBox();
        initPositionComboBox();

        employeeModel = EmployeeModel.builder()
                .addressModel(addressModels.get(0))
                .workroomModel(workroomModels.get(0))
                .positionModel(positionModels.get(0))
                .firstName("")
                .lastName("")
                .photo(null)
                .passportDocCopy(null)
                .build();

        userModel =
                UserModel.builder().id(null).email("").login("").password("").build();
        employeeModel.setUserModel(userModel);

        initBindings();
    }

    public void setStaff(ObservableList<EmployeeModel> staff) {
        this.staff = staff;
    }

    public void setEmployeeModel(EmployeeModel employeeModel) {
        this.employeeModel = employeeModel.clone();
        this.userModel = userModelMapper.toModel(userRepository.get(employeeModel.getId()));
        employeeModel.setUserModel(userModel);
        addEditButton.setText(CrudState.EDIT.getValue());
        editResetButton.setManaged(true);
        editResetButton.setVisible(true);
        initBindings();
    }

    public void onPhotoButtonClick(ActionEvent actionEvent) {
        UiHelpers.initFileChooserForPhoto(photoImageView);
    }

    public void onPassportDocCopyClick(ActionEvent actionEvent) {
        UiHelpers.initFileChooserForDoc(passportDocCopyLabel);
    }

    public void onBankNumberDocCopyClick(ActionEvent actionEvent) {
        UiHelpers.initFileChooserForDoc(bankNumberDocCopyLabel);
    }

    public void onOtherDocCopyClick(ActionEvent actionEvent) {
        UiHelpers.initFileChooserForDoc(otherDocCopyLabel);
    }

    public void onAddEditButtonClick(ActionEvent actionEvent) {
        try {
            employeeModel.setPassportDocCopy((byte[]) passportDocCopyLabel.getUserData());
            employeeModel.setBankNumberDocCopy(
                    Objects.nonNull(bankNumberDocCopyLabel.getUserData())
                            ? (byte[]) bankNumberDocCopyLabel.getUserData()
                            : null);
            employeeModel.setOtherDocCopy(
                    Objects.nonNull(otherDocCopyLabel.getUserData()) ? (byte[]) otherDocCopyLabel.getUserData() : null);

            Employee employee = employeeModelMapper.toDomain(employeeModel);
            User user = userModelMapper.toDomain(userModel);
            if (addEditButton.getText().equals(CrudState.ADD.getValue())) {
                Employee newEmployee = employeeRepository.add(employee);
                EmployeeModel newEmployeeModel = employeeModelMapper.toModel(newEmployee);

                user.setId(newEmployee.getId());
                user.setEmployee(newEmployee);
                User newUser = userRepository.add(user);
                newEmployeeModel.setUserModel(userModelMapper.toModel(newUser));

                staff.add(newEmployeeModel);
            } else {
                employeeRepository.set(employee.getId(), employee);

                user.setId(employee.getId());
                user.setEmployee(employee);
                userRepository.set(user.getId(), user);
                UserModel updatedUserModel = userModelMapper.toModel(user);
                employeeModel.setUserModel(updatedUserModel.clone());

                int index = IntStream.range(0, staff.size())
                        .filter(i -> staff.get(i).getId().equals(employeeModel.getId()))
                        .findFirst()
                        .orElse(-1);
                staff.set(index, employeeModel.clone());
                // Скидаємо все до дефолту.
                addEditButton.setText(CrudState.ADD.getValue());
                editResetButton.setVisible(false);
                editResetButton.setManaged(false);
            }
            resetModels();
        } catch (Exception e) {
            Map<String, List<String>> validationMessages = employeeRepository.getValidationMessages();
            UiHelpers.validateShow(validationMessages, "address", addressErrorLabel);
            UiHelpers.validateShow(validationMessages, "workroom", workroomErrorLabel);
            UiHelpers.validateShow(validationMessages, "position", positionErrorLabel);
            UiHelpers.validateShow(validationMessages, "firstName", firstNameErrorLabel);
            UiHelpers.validateShow(validationMessages, "lastName", lastNameErrorLabel);
            UiHelpers.validateShow(validationMessages, "middleName", middleNameErrorLabel);
            UiHelpers.validateShow(validationMessages, "photo", photoErrorLabel);
            UiHelpers.validateShow(validationMessages, "passportDocCopy", passportDocCopyErrorLabel);

            Map<String, List<String>> validationMessages2 = userRepository.getValidationMessages();
            UiHelpers.validateShow(validationMessages2, "email", emailErrorLabel);
            UiHelpers.validateShow(validationMessages2, "login", loginErrorLabel);
            UiHelpers.validateShow(validationMessages2, "password", passwordErrorLabel);
        }
    }

    public void onEditResetButtonClick(ActionEvent actionEvent) {
        addEditButton.setText(CrudState.ADD.getValue());
        editResetButton.setManaged(false);
        editResetButton.setVisible(false);
        resetModels();
    }

    private void initAddressComboBox() {
        var addressRepository = RepositoryFactory.getInstance().getAddressRepository();
        // addressRepository.getAllByWorkroom();
        var addressModelMapper = ModelMapperFactory.getInstance().getAddressModelMapper();
        addressModels = addressRepository.getAll().stream()
                .map(addressModelMapper::toModel)
                .sorted(Comparator.comparing(AddressModel::getFullAddress))
                .toList();
        addressComboBox.getItems().addAll(addressModels);
        addressComboBox.selectFirst();
        addressComboBox.setText(addressModels.get(0).toString());
    }

    private void initWorkroomComboBox() {
        var workroomRepository = RepositoryFactory.getInstance().getWorkroomRepository();
        var workroomModelMapper = ModelMapperFactory.getInstance().getWorkroomModelMapper();
        workroomModels = workroomRepository.getAll().stream()
                .map(workroomModelMapper::toModel)
                .sorted(Comparator.comparing(WorkroomModel::getName))
                .toList();
        workroomComboBox.getItems().addAll(workroomModels);
        workroomComboBox.selectFirst();
        workroomComboBox.setText(workroomModels.get(0).toString());
    }

    private void initPositionComboBox() {
        var positionRepository = RepositoryFactory.getInstance().getPositionRepository();
        var positionModelMapper = ModelMapperFactory.getInstance().getPositionModelMapper();
        positionModels = positionRepository.getAll().stream()
                .map(positionModelMapper::toModel)
                .sorted(Comparator.comparing(PositionModel::getName))
                .toList();
        positionComboBox.getItems().addAll(positionModels);
        positionComboBox.selectFirst();
        positionComboBox.setText(positionModels.get(0).toString());
    }

    private void initBindings() {
        addressComboBox.valueProperty().bindBidirectional(employeeModel.addressModelProperty());
        workroomComboBox.valueProperty().bindBidirectional(employeeModel.workroomModelProperty());
        positionComboBox.valueProperty().bindBidirectional(employeeModel.positionModelProperty());
        firstNameTextField.textProperty().bindBidirectional(employeeModel.firstNameProperty());
        lastNameTextField.textProperty().bindBidirectional(employeeModel.lastNameProperty());
        middleNameTextField.textProperty().bindBidirectional(employeeModel.middleNameProperty());
        photoImageView.imageProperty().bindBidirectional(employeeModel.photoProperty());
        photoImageView.managedProperty().bind(employeeModel.photoProperty().isNotNull());

        employeeModel.passportDocCopyProperty().addListener((observable, oldValue, newValue) -> {
            byte[] fileBytes = newValue;
            passportDocCopyLabel.setUserData(newValue);
            // Оновити текст label (назва файлу)
            if (fileBytes != null) {
                passportDocCopyLabel.setText("passport_scan.pdf");
            } else {
                passportDocCopyLabel.setText("");
            }
        });

        employeeModel.bankNumberDocCopyProperty().addListener((observable, oldValue, newValue) -> {
            byte[] fileBytes = newValue;
            bankNumberDocCopyLabel.setUserData(newValue);
            // Оновити текст label (назва файлу)
            if (fileBytes != null) {
                bankNumberDocCopyLabel.setText("bank_number_scan.pdf");
            } else {
                bankNumberDocCopyLabel.setText("");
            }
        });

        employeeModel.bankNumberDocCopyProperty().addListener((observable, oldValue, newValue) -> {
            byte[] fileBytes = newValue;
            otherDocCopyLabel.setUserData(newValue);
            // Оновити текст label (назва файлу)
            if (fileBytes != null) {
                otherDocCopyLabel.setText("other_doc_copy.pdf");
            } else {
                otherDocCopyLabel.setText("");
            }
        });

        emailTextField.textProperty().bindBidirectional(userModel.emailProperty());
        loginTextField.textProperty().bindBidirectional(userModel.loginProperty());
        passwordPasswordField.textProperty().bindBidirectional(userModel.passwordProperty());
    }

    private void resetModels() {
        employeeModel.setId(null);
        employeeModel.setAddressModel(addressModels.get(0));
        employeeModel.setWorkroomModel(workroomModels.get(0));
        employeeModel.setPositionModel(positionModels.get(0));
        employeeModel.setFirstName("");
        employeeModel.setLastName("");
        employeeModel.setMiddleName("");
        employeeModel.setPhoto(null);
        employeeModel.setPassportDocCopy(null);
        employeeModel.setBankNumberDocCopy(null);
        employeeModel.setOtherDocCopy(null);
        employeeModel.setUpdatedAt(null);
        employeeModel.setCreatedAt(null);
    }
}
