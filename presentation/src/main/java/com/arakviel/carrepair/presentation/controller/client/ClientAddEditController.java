package com.arakviel.carrepair.presentation.controller.client;

import com.arakviel.carrepair.domain.factory.RepositoryFactory;
import com.arakviel.carrepair.domain.impl.Client;
import com.arakviel.carrepair.domain.repository.ClientRepository;
import com.arakviel.carrepair.presentation.mapper.impl.ClientModelMapper;
import com.arakviel.carrepair.presentation.mapper.impl.ModelMapperFactory;
import com.arakviel.carrepair.presentation.model.impl.ClientModel;
import com.arakviel.carrepair.presentation.util.CrudState;
import com.arakviel.carrepair.presentation.util.UiHelpers;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXTextField;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.IntStream;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;

public class ClientAddEditController implements Initializable {

    @FXML
    private MFXButton editResetButton;

    @FXML
    private MFXTextField phoneTextField;

    @FXML
    private Label phoneErrorLabel;

    @FXML
    private MFXTextField emailTextField;

    @FXML
    private Label emailErrorLabel;

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
    private MFXButton addEditButton;

    private ClientModel clientModel;
    private ClientRepository clientRepository;
    private ClientModelMapper clientModelMapper;
    private ObservableList<ClientModel> clients;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.clientRepository = RepositoryFactory.getInstance().getClientRepository();
        this.clientModelMapper = ModelMapperFactory.getInstance().getClientModelMapper();
        ClientModel emptyClientModel = ClientModel.builder().phone("").email("").build();
        clientModel = emptyClientModel;

        Tooltip tooltip = new Tooltip("Скинути редагування");
        Tooltip.install(editResetButton, tooltip);

        initBindings();
    }

    public void onAddEditButtonClick(ActionEvent actionEvent) {
        try {
            Client client = clientModelMapper.toDomain(clientModel);
            if (addEditButton.getText().equals(CrudState.ADD.getValue())) {
                Client newClient = clientRepository.add(client);
                ClientModel newClientModel = clientModelMapper.toModel(newClient);
                clients.add(newClientModel);
            } else {
                clientRepository.set(client.getId(), client);
                int index = IntStream.range(0, clients.size())
                        .filter(i -> clients.get(i).getId().equals(clientModel.getId()))
                        .findFirst()
                        .orElse(-1);
                clients.set(index, clientModel.clone());
                // Скидаємо все до дефолту.
                addEditButton.setText(CrudState.ADD.getValue());
                editResetButton.setVisible(false);
                editResetButton.setManaged(false);
            }
            resetClientModel();
        } catch (Exception e) {
            Map<String, List<String>> validationMessages = clientRepository.getValidationMessages();
            UiHelpers.validateShow(validationMessages, "phone", phoneErrorLabel);
            UiHelpers.validateShow(validationMessages, "email", emailErrorLabel);
            UiHelpers.validateShow(validationMessages, "firstName", firstNameErrorLabel);
            UiHelpers.validateShow(validationMessages, "lastName", lastNameErrorLabel);
            UiHelpers.validateShow(validationMessages, "middleName", middleNameErrorLabel);
        }
    }

    public void onPhotoButtonClick(ActionEvent actionEvent) {
        UiHelpers.initFileChooserForPhoto(photoImageView);
    }

    @FXML
    private void onEditResetButtonClick(ActionEvent actionEvent) {
        addEditButton.setText(CrudState.ADD.getValue());
        editResetButton.setManaged(false);
        editResetButton.setVisible(false);
        resetClientModel();
    }

    public void setClientModel(ClientModel clientModel) {
        this.clientModel = clientModel.clone();
        addEditButton.setText(CrudState.EDIT.getValue());
        editResetButton.setManaged(true);
        editResetButton.setVisible(true);
        initBindings();
    }

    private void initBindings() {
        phoneTextField.textProperty().bindBidirectional(clientModel.phoneProperty());
        emailTextField.textProperty().bindBidirectional(clientModel.emailProperty());
        firstNameTextField.textProperty().bindBidirectional(clientModel.firstNameProperty());
        lastNameTextField.textProperty().bindBidirectional(clientModel.lastNameProperty());
        middleNameTextField.textProperty().bindBidirectional(clientModel.middleNameProperty());
        photoImageView.imageProperty().bindBidirectional(clientModel.photoProperty());
        photoImageView.managedProperty().bind(clientModel.photoProperty().isNotNull());
    }

    public void setClients(ObservableList<ClientModel> clients) {
        this.clients = clients;
    }

    private void resetClientModel() {
        clientModel.setId(null);
        clientModel.setPhone("");
        clientModel.setEmail("");
        clientModel.setFirstName("");
        clientModel.setLastName("");
        clientModel.setMiddleName("");
        clientModel.setPhoto(null);
        clientModel.setUpdatedAt(null);
        clientModel.setCreatedAt(null);
    }
}
