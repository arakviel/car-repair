package com.arakviel.carrepair.presentation.controller.order;

import com.arakviel.carrepair.domain.factory.RepositoryFactory;
import com.arakviel.carrepair.domain.impl.Order;
import com.arakviel.carrepair.domain.repository.OrderRepository;
import com.arakviel.carrepair.presentation.mapper.impl.ModelMapperFactory;
import com.arakviel.carrepair.presentation.mapper.impl.OrderModelMapper;
import com.arakviel.carrepair.presentation.model.impl.CarModel;
import com.arakviel.carrepair.presentation.model.impl.ClientModel;
import com.arakviel.carrepair.presentation.model.impl.DiscountModel;
import com.arakviel.carrepair.presentation.model.impl.EmployeeModel;
import com.arakviel.carrepair.presentation.model.impl.MoneyModel;
import com.arakviel.carrepair.presentation.model.impl.OrderModel;
import com.arakviel.carrepair.presentation.model.impl.OrderModel.PaymentType;
import com.arakviel.carrepair.presentation.model.impl.ServiceModel;
import com.arakviel.carrepair.presentation.model.impl.SpareModel;
import com.arakviel.carrepair.presentation.util.CrudState;
import com.arakviel.carrepair.presentation.util.UiHelpers;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXCheckListView;
import io.github.palexdev.materialfx.controls.MFXDatePicker;
import io.github.palexdev.materialfx.controls.MFXFilterComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.IntStream;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.util.converter.IntegerStringConverter;

public class OrderAddEditController implements Initializable {

    @FXML
    private MFXFilterComboBox<ClientModel> clientComboBox;

    @FXML
    private Label clientErrorLabel;

    @FXML
    private MFXFilterComboBox<CarModel> carComboBox;

    @FXML
    private Label carErrorLabel;

    @FXML
    private MFXFilterComboBox<DiscountModel> discountComboBox;

    @FXML
    private Label discountErrorLabel;

    @FXML
    private MFXFilterComboBox<PaymentType> paymentTypeComboBox;

    @FXML
    private Label paymentTypeErrorLabel;

    @FXML
    private MFXTextField priceWholePartTextField;

    @FXML
    private MFXTextField priceDecimalPartTextField;

    @FXML
    private Label priceErrorLabel;

    @FXML
    private MFXDatePicker paymentAtDatePicker;

    @FXML
    private Label paymentAtErrorLabel;

    @FXML
    private MFXButton addEditButton;

    @FXML
    private MFXButton editResetButton;

    @FXML
    private MFXCheckListView<EmployeeModel> employeesListView;

    @FXML
    private ListView<ServiceModelCheckboxItem> servicesListView;

    @FXML
    private ListView<SpareModelCheckboxItem> sparesListView;

    private ObservableList<ServiceModelCheckboxItem> serviceItems;
    private ObservableList<SpareModelCheckboxItem> spareItems;
    private OrderModel orderModel;
    private MoneyModel moneyModel;
    private ObservableList<OrderModel> orders;
    private OrderRepository orderRepository;
    private OrderModelMapper orderModelMapper;
    private List<ClientModel> clientModels;
    private List<CarModel> carModels;
    private List<DiscountModel> discountModels;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.orderRepository = RepositoryFactory.getInstance().getOrderRepository();
        this.orderModelMapper = ModelMapperFactory.getInstance().getOrderModelMapper();

        Tooltip tooltip = new Tooltip("Скинути редагування");
        Tooltip.install(editResetButton, tooltip);

        initClientComboBox();
        initCarComboBox();
        initDiscountComboBox();
        initPaymentTypeComboBox();
        initEmployeeListView();
        initServiceListView();
        initSpareListView();

        moneyModel = new MoneyModel(0, 0);
        orderModel = OrderModel.builder()
                .clientModel(clientModels.get(0))
                .carModel(carModels.get(0))
                .discountModel(null)
                .price(moneyModel)
                .paymentType(PaymentType.CARD)
                .paymentAt(null)
                .build();

        initBindings();
    }

    public void onAddEditButtonClick(ActionEvent actionEvent) {
        try {
            Order order = orderModelMapper.toDomain(orderModel);
            if (addEditButton.getText().equals(CrudState.ADD.getValue())) {
                Order newOrder = orderRepository.add(order);
                OrderModel newOrderModel = orderModelMapper.toModel(newOrder);
                orders.add(newOrderModel);

                // прикріпляємо до замовлення задіяних працівників
                var employeeModels = employeesListView.getSelectionModel().getSelectedValues();
                for (var employeeModel : employeeModels) {
                    orderRepository.attachToEmployee(newOrder.getId(), employeeModel.getId());
                }

                // прикріпляємо до замовлення задіяні послуги з додатковим описом
                var servicesListViewItems = servicesListView.getItems();
                for (var item : servicesListViewItems) {
                    if (item.getCheckBox().isSelected()) {
                        ServiceModel serviceModel = item.getServiceModel();
                        orderRepository.attachToService(
                                newOrder.getId(), serviceModel.getId(), serviceModel.getExtraFieldDescription());
                    }
                }

                // прикріпляємо до замовлення задіяні запчастини та їх кількість
                var sparesListViewItems = sparesListView.getItems();
                for (var item : sparesListViewItems) {
                    if (item.getCheckBox().isSelected()) {
                        var spareModel = item.getSpareModel();
                        orderRepository.attachToSpare(
                                newOrder.getId(), spareModel.getId(), spareModel.getQuantityInStock());
                    }
                }

            } else {
                orderRepository.set(order.getId(), order);
                orderModel.setPrice(moneyModel.clone());
                int index = IntStream.range(0, orders.size())
                        .filter(i -> orders.get(i).getId().equals(orderModel.getId()))
                        .findFirst()
                        .orElse(-1);
                orders.set(index, orderModel.clone());
                // Скидаємо все до дефолту.
                addEditButton.setText(CrudState.ADD.getValue());
                editResetButton.setVisible(false);
                editResetButton.setManaged(false);
            }
            resetOrderModel();
        } catch (Exception e) {
            Map<String, List<String>> validationMessages = orderRepository.getValidationMessages();
            UiHelpers.validateShow(validationMessages, "clients", clientErrorLabel);
            UiHelpers.validateShow(validationMessages, "car", carErrorLabel);
            UiHelpers.validateShow(validationMessages, "discount", discountErrorLabel);
            UiHelpers.validateShow(validationMessages, "priceWholePart", priceErrorLabel);
            UiHelpers.validateShow(validationMessages, "priceDecimalPart", priceErrorLabel);
            UiHelpers.validateShow(validationMessages, "paymentType", paymentTypeErrorLabel);
            UiHelpers.validateShow(validationMessages, "paymentAt", paymentAtErrorLabel);
        }
    }

    private void initClientComboBox() {
        var clientRepository = RepositoryFactory.getInstance().getClientRepository();
        // clientRepository.getAllByWorkroom();
        var clientModelMapper = ModelMapperFactory.getInstance().getClientModelMapper();
        clientModels = clientRepository.getAll().stream()
                .map(clientModelMapper::toModel)
                .sorted(Comparator.comparing(ClientModel::getCreatedAt).reversed())
                .toList();
        clientComboBox.getItems().addAll(clientModels);
        clientComboBox.selectFirst();
        clientComboBox.setText(clientModels.get(0).toString());
    }

    private void initCarComboBox() {
        var carRepository = RepositoryFactory.getInstance().getCarRepository();
        // carRepository.getAllByWorkroom();
        var carModelMapper = ModelMapperFactory.getInstance().getCarModelMapper();
        carModels = carRepository.getAll().stream()
                .map(carModelMapper::toModel)
                .sorted(Comparator.comparing(CarModel::getCreatedAt).reversed())
                .toList();
        carComboBox.getItems().addAll(carModels);
        carComboBox.selectFirst();
        carComboBox.setText(carModels.get(0).toString());
    }

    private void initDiscountComboBox() {
        var discountRepository = RepositoryFactory.getInstance().getDiscountRepository();
        // discountRepository.getAllByWorkroom();
        var discountModelMapper = ModelMapperFactory.getInstance().getDiscountModelMapper();
        discountModels = discountRepository.getAll().stream()
                .map(discountModelMapper::toModel)
                .sorted(Comparator.comparing(DiscountModel::getName))
                .toList();
        discountComboBox.getItems().addAll(discountModels);
        discountComboBox.selectFirst();
        discountComboBox.setText(discountModels.get(0).toString());
    }

    private void initPaymentTypeComboBox() {
        paymentTypeComboBox.getItems().addAll(PaymentType.values());
        paymentTypeComboBox.selectFirst();
        paymentTypeComboBox.setText(PaymentType.CARD.toString());
    }

    private void initEmployeeListView() {
        var modelMapperFactory = ModelMapperFactory.getInstance();
        var employeeRepository = RepositoryFactory.getInstance().getEmployeeRepository();
        var employeeModelMapper = modelMapperFactory.getEmployeeModelMapper();
        List<EmployeeModel> employeeModels = employeeRepository.getAll().stream()
                .map(employeeModelMapper::toModel)
                .sorted(Comparator.comparing(EmployeeModel::getCreatedAt).reversed())
                .toList();
        employeesListView.getItems().addAll(employeeModels);
    }

    private void initServiceListView() {
        var serviceRepository = RepositoryFactory.getInstance().getServiceRepository();
        var serviceModelMapper = ModelMapperFactory.getInstance().getServiceModelMapper();
        var serviceModels = serviceRepository.getAll().stream()
                .map(serviceModelMapper::toModel)
                .sorted(Comparator.comparing(ServiceModel::getName))
                .toList();
        var serviceModelCheckboxItems =
                serviceModels.stream().map(ServiceModelCheckboxItem::new).toList();

        serviceItems = FXCollections.observableArrayList(serviceModelCheckboxItems);

        servicesListView.getItems().addAll(serviceItems);
        servicesListView.setCellFactory(param -> new ServiceModelCheckboxListCell());
    }

    private void initSpareListView() {
        var spareRepository = RepositoryFactory.getInstance().getSpareRepository();
        var spareModelMapper = ModelMapperFactory.getInstance().getSpareModelMapper();
        var spareModels = spareRepository.getAll().stream()
                .map(spareModelMapper::toModel)
                .sorted(Comparator.comparing(SpareModel::getName))
                .toList();
        var spareModelCheckboxItems =
                spareModels.stream().map(SpareModelCheckboxItem::new).toList();
        spareItems = FXCollections.observableArrayList(spareModelCheckboxItems);
        sparesListView.getItems().addAll(spareItems);
        sparesListView.setCellFactory(param -> new SpareModelCheckboxListCell());
    }

    public void setOrders(ObservableList<OrderModel> orders) {
        this.orders = orders;
    }

    public void setOrderModel(OrderModel orderModel) {
        this.orderModel = orderModel.clone();
        this.moneyModel = this.orderModel.getPrice();
        addEditButton.setText(CrudState.EDIT.getValue());
        editResetButton.setManaged(true);
        editResetButton.setVisible(true);
        initBindings();
    }

    public void onEditResetButtonClick(ActionEvent actionEvent) {
        addEditButton.setText(CrudState.ADD.getValue());
        editResetButton.setManaged(false);
        editResetButton.setVisible(false);
        resetOrderModel();
    }

    private void initBindings() {
        clientComboBox.valueProperty().bindBidirectional(orderModel.clientModelProperty());
        carComboBox.valueProperty().bindBidirectional(orderModel.carModelProperty());
        discountComboBox.valueProperty().bindBidirectional(orderModel.discountModelProperty());

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

        paymentTypeComboBox.valueProperty().bindBidirectional(orderModel.paymentTypeProperty());
        paymentAtDatePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            LocalDate date = newValue;
            LocalDateTime dateTime = date.atTime(LocalTime.now());
            orderModel.setPaymentAt(dateTime);
        });

        orderModel.paymentAtProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                LocalDateTime dateTime = newValue;
                LocalDate date = dateTime.toLocalDate();
                paymentAtDatePicker.setValue(date);
            }
        });
    }

    private void resetOrderModel() {
        moneyModel.setWholePart(0);
        moneyModel.setDecimalPart(0);
        orderModel.setId(null);
        orderModel.setClientModel(clientModels.get(0));
        orderModel.setCarModel(carModels.get(0));
        orderModel.setDiscountModel(discountModels.get(0));
        orderModel.setPrice(moneyModel);
        orderModel.setPaymentType(PaymentType.CARD);
        orderModel.setPaymentAt(null);
    }

    private static class ServiceModelCheckboxItem {
        private final ServiceModel serviceModel;
        private final CheckBox checkBox;
        private final TextField textField;

        public ServiceModelCheckboxItem(ServiceModel serviceModel) {
            this.serviceModel = serviceModel;
            this.checkBox = new CheckBox();
            this.textField = new TextField();
            this.textField.setDisable(true);
            this.textField.setPromptText("Додатковий опис послуги замовлення");
            this.checkBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
                textField.setDisable(!newValue);
                if (Boolean.FALSE.equals(newValue)) {
                    textField.clear();
                }
            });

            textField.textProperty().bindBidirectional(this.serviceModel.extraFieldDescriptionProperty());
        }

        public ServiceModel getServiceModel() {
            return serviceModel;
        }

        public CheckBox getCheckBox() {
            return checkBox;
        }

        public TextField getTextField() {
            return textField;
        }
    }

    private static class ServiceModelCheckboxListCell extends ListCell<ServiceModelCheckboxItem> {

        @Override
        protected void updateItem(ServiceModelCheckboxItem item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setText(null);
                setGraphic(null);
            } else {
                setText(item.getServiceModel().getName());
                setGraphic(createCellContent(item));
            }
        }

        private HBox createCellContent(ServiceModelCheckboxItem item) {
            HBox content = new HBox(10);
            content.getChildren().addAll(item.getCheckBox(), item.getTextField());
            return content;
        }
    }

    private static class SpareModelCheckboxItem {
        private final SpareModel spareModel;
        private final CheckBox checkBox;
        private final TextField textField;

        public SpareModelCheckboxItem(SpareModel spareModel) {
            this.spareModel = spareModel;
            this.checkBox = new CheckBox();
            this.textField = new TextField();
            this.textField.setDisable(true);
            this.textField.setPromptText("Кількість одиниць");
            this.checkBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
                textField.setDisable(!newValue);
                if (Boolean.FALSE.equals(newValue)) {
                    textField.clear();
                }
            });

            var extraFieldQuantityProperty = spareModel.extraFieldQuantityProperty();

            textField.textProperty().addListener((observable, oldValue, newValue) -> {
                try {
                    int intValue = Integer.parseInt(newValue);
                    extraFieldQuantityProperty.set(intValue);
                } catch (NumberFormatException e) {
                    // Обробка помилки, якщо введено некоректне значення
                    extraFieldQuantityProperty.set(0); // Наприклад, задати значення 0
                }
            });

            extraFieldQuantityProperty.addListener((observable, oldValue, newValue) -> {
                textField.setText(newValue.toString());
            });
        }

        public SpareModel getSpareModel() {
            return spareModel;
        }

        public CheckBox getCheckBox() {
            return checkBox;
        }

        public TextField getTextField() {
            return textField;
        }
    }

    private static class SpareModelCheckboxListCell extends ListCell<SpareModelCheckboxItem> {

        @Override
        protected void updateItem(SpareModelCheckboxItem item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setText(null);
                setGraphic(null);
            } else {
                setText(item.getSpareModel().getName());
                setGraphic(createCellContent(item));
            }
        }

        private HBox createCellContent(SpareModelCheckboxItem item) {
            HBox content = new HBox(10);
            content.getChildren().addAll(item.getCheckBox(), item.getTextField());
            return content;
        }
    }
}
