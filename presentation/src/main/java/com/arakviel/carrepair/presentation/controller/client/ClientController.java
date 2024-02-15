package com.arakviel.carrepair.presentation.controller.client;

import com.arakviel.carrepair.domain.factory.RepositoryFactory;
import com.arakviel.carrepair.domain.repository.ClientRepository;
import com.arakviel.carrepair.presentation.mapper.impl.ModelMapperFactory;
import com.arakviel.carrepair.presentation.model.impl.ClientModel;
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

public class ClientController implements Initializable {

    @FXML
    public MFXRectangleToggleNode menuItemView;

    @FXML
    public MFXRectangleToggleNode menuItemAddEdit;

    @FXML
    private final ToggleGroup toggleGroup = new ToggleGroup();

    @FXML
    private AnchorPane contentPane;

    private ObservableList<ClientModel> clients;
    private ClientRepository clientRepository;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        toggleGroup.getToggles().addAll(menuItemView, menuItemAddEdit);
        menuItemView.setSelected(true);

        loadListsData();
        initSubControllers();
    }

    private void loadListsData() {
        clientRepository = RepositoryFactory.getInstance().getClientRepository();
        var clientList = clientRepository.getAll();
        var clientModelMapper = ModelMapperFactory.getInstance().getClientModelMapper();
        List<ClientModel> list = clientList.stream()
                .map(clientModelMapper::toModel)
                .sorted(Comparator.comparing(ClientModel::getUpdatedAt).reversed())
                .toList();
        clients = FXCollections.observableArrayList(list);
    }

    private void initSubControllers() {
        MyController myController1 = PageLoader.load(contentPane, "clients", "addedit");
        var clientAddEditController = (ClientAddEditController) myController1.controller();
        clientAddEditController.setClients(clients);
        MyController myController2 = PageLoader.load(contentPane, "clients", "view");
        var clientViewController = (ClientViewController) myController2.controller();
        clientViewController.setupTable(clients);
    }

    public void onMenuItemViewSelected(ActionEvent actionEvent) {
        PageLoader.load(contentPane, "clients", "view");
    }

    public void onMenuItemAddEditSelected(ActionEvent actionEvent) {
        PageLoader.load(contentPane, "clients", "addedit");
    }
}
