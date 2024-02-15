package com.arakviel.carrepair.presentation.controller.position;

import com.arakviel.carrepair.domain.factory.RepositoryFactory;
import com.arakviel.carrepair.domain.impl.Role;
import com.arakviel.carrepair.domain.repository.PositionRepository;
import com.arakviel.carrepair.presentation.mapper.impl.ModelMapperFactory;
import com.arakviel.carrepair.presentation.model.impl.PositionModel;
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

public class PositionController implements Initializable {

    @FXML
    private final ToggleGroup toggleGroup = new ToggleGroup();

    @FXML
    private MFXRectangleToggleNode menuItemView;

    @FXML
    private MFXRectangleToggleNode menuItemAddEdit;

    @FXML
    private AnchorPane contentPane;

    private ObservableList<PositionModel> positions;
    private PositionRepository positionRepository;
    private final String directoryName = "positions";

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        toggleGroup.getToggles().addAll(menuItemView, menuItemAddEdit);
        menuItemView.setSelected(true);

        loadListsData();
        initSubControllers();
    }

    private void loadListsData() {
        positionRepository = RepositoryFactory.getInstance().getPositionRepository();
        var roleRepository = RepositoryFactory.getInstance().getRoleRepository();
        var positionList = positionRepository.getAll();
        for (var position : positionList) {
            Role role = roleRepository.get(position.getId());
            position.setRole(role);
        }
        var positionModelMapper = ModelMapperFactory.getInstance().getPositionModelMapper();
        List<PositionModel> list = positionList.stream()
                .map(positionModelMapper::toModel)
                .sorted(Comparator.comparing(PositionModel::getName))
                .toList();
        positions = FXCollections.observableArrayList(list);
    }

    private void initSubControllers() {
        MyController myController1 = PageLoader.load(contentPane, directoryName, "addedit");
        var positionAddEditController = (PositionAddEditController) myController1.controller();
        positionAddEditController.setPositions(positions);
        MyController myController2 = PageLoader.load(contentPane, directoryName, "view");
        var positionViewController = (PositionViewController) myController2.controller();
        positionViewController.setupTable(positions);
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
