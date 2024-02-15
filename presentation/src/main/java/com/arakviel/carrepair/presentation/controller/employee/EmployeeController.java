package com.arakviel.carrepair.presentation.controller.employee;

import com.arakviel.carrepair.domain.factory.RepositoryFactory;
import com.arakviel.carrepair.domain.repository.EmployeeRepository;
import com.arakviel.carrepair.domain.repository.UserRepository;
import com.arakviel.carrepair.presentation.mapper.impl.ModelMapperFactory;
import com.arakviel.carrepair.presentation.mapper.impl.UserModelMapper;
import com.arakviel.carrepair.presentation.model.impl.EmployeeModel;
import com.arakviel.carrepair.presentation.model.impl.UserModel;
import com.arakviel.carrepair.presentation.util.MyController;
import com.arakviel.carrepair.presentation.util.PageLoader;
import io.github.palexdev.materialfx.controls.MFXRectangleToggleNode;
import java.net.URL;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.UUID;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;

public class EmployeeController implements Initializable {

    @FXML
    private final ToggleGroup toggleGroup = new ToggleGroup();

    @FXML
    private MFXRectangleToggleNode menuItemView;

    @FXML
    private MFXRectangleToggleNode menuItemAddEdit;

    @FXML
    private AnchorPane contentPane;

    private ObservableList<EmployeeModel> employees;
    private EmployeeRepository employeeRepository;
    private UserRepository userRepository;
    private UserModelMapper userModelMapper;
    private final String directoryName = "staff";

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        toggleGroup.getToggles().addAll(menuItemView, menuItemAddEdit);
        menuItemView.setSelected(true);

        loadListsData();
        initSubControllers();
    }

    private void loadListsData() {
        employeeRepository = RepositoryFactory.getInstance().getEmployeeRepository();
        userRepository = RepositoryFactory.getInstance().getUserRepository();
        userModelMapper = ModelMapperFactory.getInstance().getUserModelMapper();
        var employeeList = employeeRepository.getAll();
        var employeeModelMapper = ModelMapperFactory.getInstance().getEmployeeModelMapper();
        List<EmployeeModel> list = employeeList.stream()
                .map(employeeModelMapper::toModel)
                .sorted(Comparator.comparing(EmployeeModel::getLastName))
                .toList();

        list.forEach(e -> {
            employeeRepository = RepositoryFactory.getInstance().getEmployeeRepository();
            UUID employeeId = e.getId();
            UserModel userModel = userModelMapper.toModel(userRepository.get(employeeId));
            userModel.setId(e.getId());
            userModel.setEmployeeModel(e);
            e.setUserModel(userModel);
        });
        employees = FXCollections.observableArrayList(list);
    }

    private void initSubControllers() {
        MyController myController1 = PageLoader.load(contentPane, directoryName, "addedit");
        var employeeAddEditController = (EmployeeAddEditController) myController1.controller();
        employeeAddEditController.setStaff(employees);
        MyController myController2 = PageLoader.load(contentPane, directoryName, "view");
        var employeeViewController = (EmployeeViewController) myController2.controller();
        employeeViewController.setupTable(employees);
    }

    @FXML
    private void onMenuItemViewSelected(ActionEvent actionEvent) {
        PageLoader.load(contentPane, directoryName, "view");
    }

    @FXML
    private void onMenuItemAddEditSelected(ActionEvent actionEvent) {
        PageLoader.load(contentPane, directoryName, "addedit");
    }
}
