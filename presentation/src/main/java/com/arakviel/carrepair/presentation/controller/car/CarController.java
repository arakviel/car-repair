package com.arakviel.carrepair.presentation.controller.car;

import com.arakviel.carrepair.domain.factory.RepositoryFactory;
import com.arakviel.carrepair.domain.repository.CarRepository;
import com.arakviel.carrepair.presentation.mapper.impl.ModelMapperFactory;
import com.arakviel.carrepair.presentation.model.impl.CarModel;
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

public class CarController implements Initializable {

    @FXML
    private final ToggleGroup toggleGroup = new ToggleGroup();

    @FXML
    private MFXRectangleToggleNode menuItemModelAddEdit;

    @FXML
    private MFXRectangleToggleNode menuItemView;

    @FXML
    private MFXRectangleToggleNode menuItemAddEdit;

    @FXML
    private AnchorPane contentPane;

    private ObservableList<CarModel> cars;
    private CarRepository carRepository;
    private final String directoryName = "cars";

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        toggleGroup.getToggles().addAll(menuItemView, menuItemAddEdit, menuItemModelAddEdit);
        menuItemView.setSelected(true);

        loadListsData();
        initSubControllers();
    }

    private void loadListsData() {
        carRepository = RepositoryFactory.getInstance().getCarRepository();
        var carList = carRepository.getAll();
        var carModelMapper = ModelMapperFactory.getInstance().getCarModelMapper();
        List<CarModel> list = carList.stream()
                .map(carModelMapper::toModel)
                .sorted(Comparator.comparing(CarModel::getNumber))
                .toList();
        cars = FXCollections.observableArrayList(list);
    }

    private void initSubControllers() {
        MyController myController1 = PageLoader.load(contentPane, directoryName, "addedit");
        var carAddEditController = (CarAddEditController) myController1.controller();
        carAddEditController.setCars(cars);

        MyController myController3 = PageLoader.load(contentPane, directoryName, "modeladdedit");
        // var modelAddEditController = (ModelAddEditController) myController3.controller();

        MyController myController2 = PageLoader.load(contentPane, directoryName, "view");
        var carViewController = (CarViewController) myController2.controller();
        carViewController.setupTable(cars);
    }

    @FXML
    private void onMenuItemViewSelected(ActionEvent actionEvent) {
        PageLoader.load(contentPane, directoryName, "view");
    }

    @FXML
    private void onMenuItemAddEditSelected(ActionEvent actionEvent) {
        PageLoader.load(contentPane, directoryName, "addedit");
    }

    @FXML
    private void onMenuItemModelAddEditSelected(ActionEvent actionEvent) {
        PageLoader.load(contentPane, directoryName, "modeladdedit");
    }
}
