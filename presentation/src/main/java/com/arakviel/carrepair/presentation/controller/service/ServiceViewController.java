package com.arakviel.carrepair.presentation.controller.service;

import com.arakviel.carrepair.domain.factory.RepositoryFactory;
import com.arakviel.carrepair.domain.impl.Currency;
import com.arakviel.carrepair.domain.impl.Service;
import com.arakviel.carrepair.domain.repository.ServiceRepository;
import com.arakviel.carrepair.presentation.converter.KeyValueStringConverter;
import com.arakviel.carrepair.presentation.mapper.impl.ModelMapperFactory;
import com.arakviel.carrepair.presentation.mapper.impl.ServiceModelMapper;
import com.arakviel.carrepair.presentation.model.impl.ServiceModel;
import com.arakviel.carrepair.presentation.util.CrudState;
import com.arakviel.carrepair.presentation.util.PageLoader;
import com.arakviel.carrepair.presentation.util.UiHelpers;
import com.arakviel.carrepair.presentation.view.ButtonTableCell;
import com.arakviel.carrepair.presentation.view.KeyValue;
import com.arakviel.carrepair.presentation.view.PhotoTableCell;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import java.net.URL;
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
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;

public class ServiceViewController implements Initializable {

    public TableView<ServiceModel> table = new TableView<>();
    public Label countLabel;
    public MFXTextField searchTextField;
    public MFXButton searchButton;
    public MFXComboBox<KeyValue> searchComboBox;
    private ObservableList<ServiceModel> services;
    private ServiceRepository serviceRepository;
    private ServiceModelMapper serviceModelMapper;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        serviceRepository = RepositoryFactory.getInstance().getServiceRepository();
        serviceModelMapper = ModelMapperFactory.getInstance().getServiceModelMapper();
        initSearchComboBox();
    }

    public void setupTable(ObservableList<ServiceModel> services) {
        countLabel.textProperty().bind(Bindings.size(services).asString());

        TableColumn<ServiceModel, Image> photoColumn = new TableColumn<>("Фото");
        photoColumn.setCellValueFactory(cellData -> {
            ObjectProperty<Image> photoProperty = cellData.getValue().photoProperty();
            return new SimpleObjectProperty<>(photoProperty.get());
        });
        photoColumn.setCellFactory(column -> new PhotoTableCell<>());

        TableColumn<ServiceModel, String> nameColumn = new TableColumn<>("Назва");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<ServiceModel, String> descriptionColumn = new TableColumn<>("Опис");
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        descriptionColumn.setPrefWidth(200);

        TableColumn<ServiceModel, String> currencyColumn = new TableColumn<>("Валюта");
        currencyColumn.setCellValueFactory(new PropertyValueFactory<>("currencyModel"));

        TableColumn<ServiceModel, String> priceColumn = new TableColumn<>("Ціна");
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));

        // Створюємо колонку "Редагувати"
        TableColumn<ServiceModel, Void> editColumn = new TableColumn<>("");
        editColumn.setCellFactory(column -> new ButtonTableCell<>(CrudState.EDIT.getValue(), sm -> {
            var myController = PageLoader.getController("services.addedit");
            var controller = (ServiceAddEditController) myController.controller();
            controller.setServiceModel(sm);
            controller.setServices(services);
            PageLoader.setInnerContent(myController);
        }));

        // Створюємо колонку "Видалити"
        TableColumn<ServiceModel, Void> deleteColumn = new TableColumn<>("");
        deleteColumn.setCellFactory(column -> new ButtonTableCell<>(CrudState.REMOVE.getValue(), sm -> {
            if (UiHelpers.showDeleteConfirmation()) {
                serviceRepository.remove(sm.getId());
                services.remove(sm);
            }
        }));

        table.getColumns()
                .addAll(
                        photoColumn,
                        nameColumn,
                        descriptionColumn,
                        currencyColumn,
                        priceColumn,
                        editColumn,
                        deleteColumn);
        table.setItems(services);
    }

    public void onSearchButton(ActionEvent actionEvent) {
        String search = searchTextField.getText();
        String selectedKey = "";
        var selectedKeyValue = searchComboBox.getValue();
        if (Objects.nonNull(selectedKeyValue)) {
            selectedKey = selectedKeyValue.key();
        }

        Currency currency = null;
        if (selectedKey.equals("currency") && !search.isBlank()) {
            List<Currency> currencies =
                    RepositoryFactory.getInstance().getCurrencyRepository().getAllWhere(search, null);
            currency = !currencies.isEmpty() ? currencies.get(0) : null;
        }

        List<Service> serviceList = serviceRepository.getAllWhere(
                selectedKey.equals("name") ? search : null, Objects.nonNull(currency) ? currency : null, null);
        List<ServiceModel> serviceModels = serviceList.stream()
                .map(serviceModelMapper::toModel)
                .sorted(Comparator.comparing(ServiceModel::getName))
                .toList();
        services = FXCollections.observableArrayList(serviceModels);
        table.setItems(services);
    }

    private void initSearchComboBox() {
        searchComboBox.setConverter(new KeyValueStringConverter());
        searchComboBox.getItems().add(new KeyValue("name", "Назва"));
        searchComboBox.getItems().add(new KeyValue("currency", "Валюта"));
        searchComboBox.selectFirst();
    }
}
