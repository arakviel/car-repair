package com.arakviel.carrepair.presentation.controller.payroll;

import com.arakviel.carrepair.domain.factory.RepositoryFactory;
import com.arakviel.carrepair.domain.repository.PayrollRepository;
import com.arakviel.carrepair.presentation.mapper.impl.ModelMapperFactory;
import com.arakviel.carrepair.presentation.model.impl.PayrollModel;
import com.arakviel.carrepair.presentation.util.MyController;
import com.arakviel.carrepair.presentation.util.PageLoader;
import io.github.palexdev.materialfx.controls.MFXRectangleToggleNode;
import java.net.URL;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;

public class PayrollController implements Initializable {

    @FXML
    private final ToggleGroup toggleGroup = new ToggleGroup();

    @FXML
    private MFXRectangleToggleNode menuItemView;

    @FXML
    private MFXRectangleToggleNode menuItemAddEdit;

    @FXML
    private AnchorPane contentPane;

    private ObservableList<PayrollModel> payrolls;
    private PayrollRepository payrollRepository;
    private final String directoryName = "payrolls";

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        toggleGroup.getToggles().addAll(menuItemView, menuItemAddEdit);
        menuItemView.setSelected(true);

        loadListsData();
        initSubControllers();
    }

    private void loadListsData() {
        payrollRepository = RepositoryFactory.getInstance().getPayrollRepository();
        var payrollList = payrollRepository.getAll();
        var payrollModelMapper = ModelMapperFactory.getInstance().getPayrollModelMapper();
        List<PayrollModel> list = payrollList.stream()
                .map(payrollModelMapper::toModel)
                .sorted(Comparator.comparing(PayrollModel::getCreatedAt))
                .toList();
        payrolls = FXCollections.observableArrayList(list);
    }

    private void initSubControllers() {
        MyController myController1 = PageLoader.load(contentPane, directoryName, "addedit");
        var payrollAddEditController = (PayrollAddEditController) myController1.controller();
        payrollAddEditController.setPayrolls(payrolls);
        MyController myController2 = PageLoader.load(contentPane, directoryName, "view");
        var payrollViewController = (PayrollViewController) myController2.controller();
        payrollViewController.setupTable(payrolls);
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
