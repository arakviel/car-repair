package com.arakviel.carrepair.presentation.controller.spare;

import com.arakviel.carrepair.domain.factory.RepositoryFactory;
import com.arakviel.carrepair.domain.repository.SpareRepository;
import com.arakviel.carrepair.presentation.mapper.impl.ModelMapperFactory;
import com.arakviel.carrepair.presentation.model.impl.SpareModel;
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
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;

public class SpareController implements Initializable {

    @FXML
    private final ToggleGroup toggleGroup = new ToggleGroup();

    public TableView<SpareModel> table;

    @FXML
    private MFXRectangleToggleNode menuItemView;

    @FXML
    private MFXRectangleToggleNode menuItemAddEdit;

    @FXML
    private AnchorPane contentPane;

    private ObservableList<SpareModel> spares;
    private SpareRepository spareRepository;
    private final String directoryName = "spares";

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        toggleGroup.getToggles().addAll(menuItemView, menuItemAddEdit);
        menuItemView.setSelected(true);

        loadListsData();
        initSubControllers();
    }

    private void loadListsData() {
        spareRepository = RepositoryFactory.getInstance().getSpareRepository();
        var spareList = spareRepository.getAll();
        var spareModelMapper = ModelMapperFactory.getInstance().getSpareModelMapper();
        List<SpareModel> list = spareList.stream()
                .map(spareModelMapper::toModel)
                .sorted(Comparator.comparing(SpareModel::getName))
                .toList();
        spares = FXCollections.observableArrayList(list);
    }

    private void initSubControllers() {
        MyController myController1 = PageLoader.load(contentPane, directoryName, "addedit");
        var spareAddEditController = (SpareAddEditController) myController1.controller();
        spareAddEditController.setSpares(spares);
        MyController myController2 = PageLoader.load(contentPane, directoryName, "view");
        var spareViewController = (SpareViewController) myController2.controller();
        spareViewController.setupTable(spares);
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
