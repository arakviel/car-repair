package com.arakviel.carrepair.presentation.controller.service;

import com.arakviel.carrepair.domain.factory.RepositoryFactory;
import com.arakviel.carrepair.domain.impl.Service;
import com.arakviel.carrepair.domain.repository.ServiceRepository;
import com.arakviel.carrepair.presentation.mapper.impl.ModelMapperFactory;
import com.arakviel.carrepair.presentation.mapper.impl.ServiceModelMapper;
import com.arakviel.carrepair.presentation.model.impl.CurrencyModel;
import com.arakviel.carrepair.presentation.model.impl.MoneyModel;
import com.arakviel.carrepair.presentation.model.impl.ServiceModel;
import com.arakviel.carrepair.presentation.util.CrudState;
import com.arakviel.carrepair.presentation.util.UiHelpers;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXFilterComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import java.net.URL;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.IntStream;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.util.converter.IntegerStringConverter;

public class ServiceAddEditController implements Initializable {

    public MFXTextField nameTextField;
    public Label nameErrorLabel;
    public TextArea descriptionTextArea;
    public Label descriptionErrorLabel;
    public MFXFilterComboBox<CurrencyModel> currencyComboBox;
    public Label currencyErrorLabel;
    public MFXButton photoButton;
    public ImageView photoImageView;
    public Label photoErrorLabel;
    public MFXTextField priceWholePartTextField;
    public MFXTextField priceDecimalPartTextField;
    public Label priceErrorLabel;
    public MFXButton addEditButton;
    public MFXButton editResetButton;
    private ServiceModel serviceModel;
    private MoneyModel moneyModel;
    private ServiceRepository serviceRepository;
    private ServiceModelMapper serviceModelMapper;
    private ObservableList<ServiceModel> services;
    private List<CurrencyModel> currencies;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.serviceRepository = RepositoryFactory.getInstance().getServiceRepository();
        this.serviceModelMapper = ModelMapperFactory.getInstance().getServiceModelMapper();
        Tooltip tooltip = new Tooltip("Скинути редагування");
        Tooltip.install(editResetButton, tooltip);
        initCurrencyComboBox();

        moneyModel = new MoneyModel(0, 0);
        serviceModel = ServiceModel.builder()
                .name(null)
                .description("")
                .photo(null)
                .currency(currencies.get(0))
                .price(moneyModel)
                .build();

        initBindings();
    }

    public void onAddEditButtonClick(ActionEvent actionEvent) {
        try {
            Service service = serviceModelMapper.toDomain(serviceModel);
            if (addEditButton.getText().equals(CrudState.ADD.getValue())) {
                Service newService = serviceRepository.add(service);
                ServiceModel newServiceModel = serviceModelMapper.toModel(newService);
                services.add(newServiceModel);
            } else {
                serviceRepository.set(service.getId(), service);
                serviceModel.setPrice(moneyModel.clone());
                int index = IntStream.range(0, services.size())
                        .filter(i -> services.get(i).getId().equals(serviceModel.getId()))
                        .findFirst()
                        .orElse(-1);
                services.set(index, serviceModel.clone());
                // Скидаємо все до дефолту.
                addEditButton.setText(CrudState.ADD.getValue());
                editResetButton.setVisible(false);
                editResetButton.setManaged(false);
            }
            resetServiceModel();
        } catch (Exception e) {
            Map<String, List<String>> validationMessages = serviceRepository.getValidationMessages();
            UiHelpers.validateShow(validationMessages, "name", nameErrorLabel);
            UiHelpers.validateShow(validationMessages, "description", descriptionErrorLabel);
            UiHelpers.validateShow(validationMessages, "currency", currencyErrorLabel);
            UiHelpers.validateShow(validationMessages, "priceWholePart", priceErrorLabel);
            UiHelpers.validateShow(validationMessages, "priceDecimalPart", priceErrorLabel);
        }
    }

    public void setServices(ObservableList<ServiceModel> services) {
        this.services = services;
    }

    public void setServiceModel(ServiceModel serviceModel) {
        this.serviceModel = serviceModel.clone();
        this.moneyModel = this.serviceModel.getPrice();
        addEditButton.setText(CrudState.EDIT.getValue());
        editResetButton.setManaged(true);
        editResetButton.setVisible(true);
        initBindings();
    }

    public void onPhotoButtonClick(ActionEvent actionEvent) {
        UiHelpers.initFileChooserForPhoto(photoImageView);
    }

    public void onEditResetButtonClick(ActionEvent actionEvent) {
        addEditButton.setText(CrudState.ADD.getValue());
        editResetButton.setManaged(false);
        editResetButton.setVisible(false);
        resetServiceModel();
    }

    private void initCurrencyComboBox() {
        var currencyRepository = RepositoryFactory.getInstance().getCurrencyRepository();
        var currencyModelMapper = ModelMapperFactory.getInstance().getCurrencyModelMapper();
        currencies = currencyRepository.getAll().stream()
                .map(currencyModelMapper::toModel)
                .sorted(Comparator.comparing(CurrencyModel::getName))
                .toList();
        currencyComboBox.getItems().addAll(currencies);
        currencyComboBox.selectFirst();
        currencyComboBox.setText(currencies.get(0).toString());
    }

    private void initBindings() {
        nameTextField.textProperty().bindBidirectional(serviceModel.nameProperty());
        descriptionTextArea.textProperty().bindBidirectional(serviceModel.descriptionProperty());
        photoImageView.imageProperty().bindBidirectional(serviceModel.photoProperty());
        photoImageView.managedProperty().bind(serviceModel.photoProperty().isNotNull());
        currencyComboBox.valueProperty().bindBidirectional(serviceModel.currencyModelProperty());

        TextFormatter<Integer> textFormatterForWholePart = new TextFormatter<>(new IntegerStringConverter());
        priceWholePartTextField.setTextFormatter(textFormatterForWholePart);
        textFormatterForWholePart
                .valueProperty()
                .bindBidirectional(moneyModel.wholePartProperty().asObject());
        textFormatterForWholePart.valueProperty().addListener((obs, oldValue, newValue) -> {
            moneyModel.setWholePart(newValue);
        });

        TextFormatter<Integer> textFormatterForDecimalPart = new TextFormatter<>(new IntegerStringConverter());
        priceDecimalPartTextField.setTextFormatter(textFormatterForDecimalPart);
        textFormatterForDecimalPart
                .valueProperty()
                .bindBidirectional(moneyModel.decimalPartProperty().asObject());
        textFormatterForDecimalPart.valueProperty().addListener((obs, oldValue, newValue) -> {
            moneyModel.setDecimalPart(newValue);
        });
    }

    private void resetServiceModel() {
        moneyModel.setWholePart(0);
        moneyModel.setDecimalPart(0);
        serviceModel.setId(null);
        serviceModel.setName("");
        serviceModel.setDescription("");
        serviceModel.setPhoto(null);
        serviceModel.setCurrencyModel(currencies.get(0));
        serviceModel.setPrice(moneyModel);
    }
}
