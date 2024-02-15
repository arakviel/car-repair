package com.arakviel.carrepair.presentation.controller.client;

import com.arakviel.carrepair.domain.factory.RepositoryFactory;
import com.arakviel.carrepair.domain.impl.Client;
import com.arakviel.carrepair.domain.repository.ClientRepository;
import com.arakviel.carrepair.presentation.converter.KeyValueStringConverter;
import com.arakviel.carrepair.presentation.mapper.impl.ClientModelMapper;
import com.arakviel.carrepair.presentation.mapper.impl.ModelMapperFactory;
import com.arakviel.carrepair.presentation.model.impl.ClientModel;
import com.arakviel.carrepair.presentation.util.CrudState;
import com.arakviel.carrepair.presentation.util.PageLoader;
import com.arakviel.carrepair.presentation.util.UiHelpers;
import com.arakviel.carrepair.presentation.view.ButtonTableCell;
import com.arakviel.carrepair.presentation.view.KeyValue;
import com.arakviel.carrepair.presentation.view.LocalDateTimeTableCell;
import com.arakviel.carrepair.presentation.view.PhotoTableCell;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXCheckbox;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;

public class ClientViewController implements Initializable {

    @FXML
    public MFXCheckbox showHideDateTimeFields;

    @FXML
    private MFXComboBox<KeyValue> searchComboBox;

    @FXML
    private MFXTextField searchTextField;

    @FXML
    private MFXButton searchButton;

    @FXML
    private Label countLabel;

    @FXML
    private TableView<ClientModel> table = new TableView<>();

    private ObservableList<ClientModel> clients;
    private ClientRepository clientRepository;
    private ClientModelMapper clientModelMapper;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        clientRepository = RepositoryFactory.getInstance().getClientRepository();
        clientModelMapper = ModelMapperFactory.getInstance().getClientModelMapper();

        initSearchComboBox();
    }

    public void setupTable(ObservableList<ClientModel> clients) {
        countLabel.textProperty().bind(Bindings.size(clients).asString());

        // Створюємо колонки таблиці
        TableColumn<ClientModel, String> fullNameColumn = new TableColumn<>("ПІБ");
        fullNameColumn.setCellValueFactory(new PropertyValueFactory<>("fullName"));

        TableColumn<ClientModel, String> phoneNameColumn = new TableColumn<>("Телефон");
        phoneNameColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));

        TableColumn<ClientModel, String> emailColumn = new TableColumn<>("Електронна пошта");
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));

        TableColumn<ClientModel, Image> photoColumn = new TableColumn<>("Фото");
        photoColumn.setCellValueFactory(cellData -> {
            ObjectProperty<Image> photoProperty = cellData.getValue().photoProperty();
            return new SimpleObjectProperty<>(photoProperty.get());
        });
        photoColumn.setCellFactory(column -> new PhotoTableCell<>());

        TableColumn<ClientModel, LocalDateTime> updatedAtColumn = new TableColumn<>("Оновлено");
        updatedAtColumn.setCellValueFactory(new PropertyValueFactory<>("updatedAt"));
        updatedAtColumn.setCellFactory(column -> new LocalDateTimeTableCell<>());
        updatedAtColumn.visibleProperty().bind(showHideDateTimeFields.selectedProperty());

        TableColumn<ClientModel, LocalDateTime> createdAtColumn = new TableColumn<>("Створено");
        createdAtColumn.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
        createdAtColumn.setCellFactory(column -> new LocalDateTimeTableCell<>());
        createdAtColumn.visibleProperty().bind(showHideDateTimeFields.selectedProperty());

        // Створюємо колонку "Редагувати"
        TableColumn<ClientModel, Void> editColumn = new TableColumn<>("");
        editColumn.setCellFactory(column -> new ButtonTableCell<>(CrudState.EDIT.getValue(), cm -> {
            var myController = PageLoader.getController("clients.addedit");
            var controller = (ClientAddEditController) myController.controller();
            controller.setClientModel(cm);
            controller.setClients(clients);

            PageLoader.setInnerContent(myController);
        }));

        // Створюємо колонку "Видалити"
        TableColumn<ClientModel, Void> deleteColumn = new TableColumn<>("");
        deleteColumn.setCellFactory(column -> new ButtonTableCell<>(CrudState.REMOVE.getValue(), cm -> {
            if (UiHelpers.showDeleteConfirmation()) {
                clientRepository.remove(cm.getId());
                clients.remove(cm);
            }
        }));

        table.setItems(clients);
        table.getColumns()
                .addAll(
                        photoColumn,
                        phoneNameColumn,
                        emailColumn,
                        fullNameColumn,
                        updatedAtColumn,
                        createdAtColumn,
                        editColumn,
                        deleteColumn);
    }

    private void initSearchComboBox() {
        searchComboBox.setConverter(new KeyValueStringConverter());
        searchComboBox.getItems().add(new KeyValue("phone", "Телефон"));
        searchComboBox.getItems().add(new KeyValue("email", "Ел. пошта"));
        searchComboBox.getItems().add(new KeyValue("firstName", "Ім'я"));
        searchComboBox.getItems().add(new KeyValue("lastName", "Фамілія"));
        searchComboBox.getItems().add(new KeyValue("middleName", "По батькові"));
        searchComboBox.selectFirst();
    }

    @FXML
    private void onSearchButton(ActionEvent actionEvent) {
        String search = searchTextField.getText();
        // List<Client> newClients = clientRepository.getAllByWorkroomWhere();
        String selectedKey = "";
        var selectedKeyValue = searchComboBox.getValue();
        if (Objects.nonNull(selectedKeyValue)) {
            selectedKey = selectedKeyValue.key();
        }

        List<Client> newClients = clientRepository.getAllWhere(
                selectedKey.equals("phone") ? search : null,
                selectedKey.equals("email") ? search : null,
                selectedKey.equals("firstName") ? search : null,
                selectedKey.equals("lastName") ? search : null,
                selectedKey.equals("middleName") ? search : null,
                null,
                null);

        List<ClientModel> clientModels = newClients.stream()
                .map(clientModelMapper::toModel)
                .sorted(Comparator.comparing(ClientModel::getUpdatedAt).reversed())
                .toList();
        clients = FXCollections.observableArrayList(clientModels);
        table.setItems(clients);
    }
}
