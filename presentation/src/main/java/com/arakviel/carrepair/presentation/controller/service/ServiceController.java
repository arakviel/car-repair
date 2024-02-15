package com.arakviel.carrepair.presentation.controller.service;

import com.arakviel.carrepair.domain.factory.RepositoryFactory;
import com.arakviel.carrepair.domain.repository.ServiceRepository;
import com.arakviel.carrepair.presentation.mapper.impl.ModelMapperFactory;
import com.arakviel.carrepair.presentation.model.impl.ServiceModel;
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

public class ServiceController implements Initializable {

    @FXML
    private final ToggleGroup toggleGroup = new ToggleGroup();

    @FXML
    private MFXRectangleToggleNode menuItemView;

    @FXML
    private MFXRectangleToggleNode menuItemAddEdit;

    @FXML
    private AnchorPane contentPane;

    private ObservableList<ServiceModel> services;
    private ServiceRepository serviceRepository;
    private final String directoryName = "services";

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        toggleGroup.getToggles().addAll(menuItemView, menuItemAddEdit);
        menuItemView.setSelected(true);

        loadListsData();
        initSubControllers();
    }

    private void loadListsData() {
        serviceRepository = RepositoryFactory.getInstance().getServiceRepository();
        var serviceList = serviceRepository.getAll();
        var serviceModelMapper = ModelMapperFactory.getInstance().getServiceModelMapper();
        List<ServiceModel> list = serviceList.stream()
                .map(serviceModelMapper::toModel)
                .sorted(Comparator.comparing(ServiceModel::getName))
                .toList();
        services = FXCollections.observableArrayList(list);
    }

    private void initSubControllers() {
        MyController myController1 = PageLoader.load(contentPane, directoryName, "addedit");
        var serviceAddEditController = (ServiceAddEditController) myController1.controller();
        serviceAddEditController.setServices(services);
        MyController myController2 = PageLoader.load(contentPane, directoryName, "view");
        var serviceViewController = (ServiceViewController) myController2.controller();
        serviceViewController.setupTable(services);
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
