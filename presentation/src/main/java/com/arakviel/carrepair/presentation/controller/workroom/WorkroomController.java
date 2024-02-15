package com.arakviel.carrepair.presentation.controller.workroom;

import com.arakviel.carrepair.domain.factory.RepositoryFactory;
import com.arakviel.carrepair.domain.repository.WorkroomRepository;
import com.arakviel.carrepair.presentation.mapper.impl.ModelMapperFactory;
import com.arakviel.carrepair.presentation.model.impl.WorkroomModel;
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

public class WorkroomController implements Initializable {

    @FXML
    private final ToggleGroup toggleGroup = new ToggleGroup();

    @FXML
    private MFXRectangleToggleNode menuItemView;

    @FXML
    private MFXRectangleToggleNode menuItemAddEdit;

    @FXML
    private AnchorPane contentPane;

    private ObservableList<WorkroomModel> workrooms;
    private WorkroomRepository workroomRepository;
    private final String directoryName = "workrooms";

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        toggleGroup.getToggles().addAll(menuItemView, menuItemAddEdit);
        menuItemView.setSelected(true);

        loadListsData();
        initSubControllers();
    }

    private void loadListsData() {
        workroomRepository = RepositoryFactory.getInstance().getWorkroomRepository();
        var workroomList = workroomRepository.getAll();
        var workroomModelMapper = ModelMapperFactory.getInstance().getWorkroomModelMapper();
        List<WorkroomModel> list = workroomList.stream()
                .map(workroomModelMapper::toModel)
                .sorted(Comparator.comparing(WorkroomModel::getName))
                .toList();
        workrooms = FXCollections.observableArrayList(list);
    }

    private void initSubControllers() {
        MyController myController1 = PageLoader.load(contentPane, directoryName, "addedit");
        var workroomAddEditController = (WorkroomAddEditController) myController1.controller();
        workroomAddEditController.setWorkrooms(workrooms);
        MyController myController2 = PageLoader.load(contentPane, directoryName, "view");
        var workroomViewController = (WorkroomViewController) myController2.controller();
        workroomViewController.setupTable(workrooms);
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
