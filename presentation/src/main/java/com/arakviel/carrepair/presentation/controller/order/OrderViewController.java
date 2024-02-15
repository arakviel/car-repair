package com.arakviel.carrepair.presentation.controller.order;

import com.arakviel.carrepair.domain.factory.RepositoryFactory;
import com.arakviel.carrepair.domain.impl.Client;
import com.arakviel.carrepair.domain.impl.Order;
import com.arakviel.carrepair.domain.repository.OrderRepository;
import com.arakviel.carrepair.presentation.converter.KeyValueStringConverter;
import com.arakviel.carrepair.presentation.mapper.impl.ModelMapperFactory;
import com.arakviel.carrepair.presentation.mapper.impl.OrderModelMapper;
import com.arakviel.carrepair.presentation.model.impl.EmployeeModel;
import com.arakviel.carrepair.presentation.model.impl.OrderModel;
import com.arakviel.carrepair.presentation.model.impl.ServiceModel;
import com.arakviel.carrepair.presentation.model.impl.SpareModel;
import com.arakviel.carrepair.presentation.util.CrudState;
import com.arakviel.carrepair.presentation.util.PageLoader;
import com.arakviel.carrepair.presentation.util.UiHelpers;
import com.arakviel.carrepair.presentation.view.ButtonTableCell;
import com.arakviel.carrepair.presentation.view.KeyValue;
import com.arakviel.carrepair.presentation.view.LocalDateTimeTableCell;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXCheckbox;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXTextField;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.Set;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class OrderViewController implements Initializable {
    @FXML
    public MFXCheckbox showHideDateTimeFields;

    @FXML
    private TableView<OrderModel> table;

    @FXML
    private Label countLabel;

    @FXML
    private MFXTextField searchTextField;

    @FXML
    private MFXButton searchButton;

    @FXML
    private MFXComboBox<KeyValue> searchComboBox;

    private ObservableList<OrderModel> orders;
    private OrderRepository orderRepository;
    private OrderModelMapper orderModelMapper;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        orderRepository = RepositoryFactory.getInstance().getOrderRepository();
        orderModelMapper = ModelMapperFactory.getInstance().getOrderModelMapper();
        initSearchComboBox();
    }

    public void setupTable(ObservableList<OrderModel> orders) {
        countLabel.textProperty().bind(Bindings.size(orders).asString());

        TableColumn<OrderModel, String> clientColumn = new TableColumn<>("Клієнт");
        clientColumn.setCellValueFactory(param ->
                new SimpleStringProperty(param.getValue().getClientModel().getFullName()));

        TableColumn<OrderModel, String> carColumn = new TableColumn<>("Машина");
        carColumn.setCellValueFactory(new PropertyValueFactory<>("carModel"));

        TableColumn<OrderModel, String> discountColumn = new TableColumn<>("Знижка");
        discountColumn.setCellValueFactory(new PropertyValueFactory<>("discountModel"));

        TableColumn<OrderModel, String> employeesColumn = new TableColumn<>("Задіяні працівники");
        employeesColumn.setCellValueFactory(param -> {
            String employees = String.join(
                    ", ",
                    param.getValue().getEmployeeModels().stream()
                            .map(EmployeeModel::toString)
                            .toList());

            return new SimpleStringProperty(employees);
        });

        TableColumn<OrderModel, String> periodTypeColumn = new TableColumn<>("Тип платежу");
        periodTypeColumn.setCellValueFactory(param ->
                new SimpleStringProperty(param.getValue().getPaymentType().getName()));

        TableColumn<OrderModel, String> priceColumn = new TableColumn<>("Ціна");
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));

        TableColumn<OrderModel, LocalDateTime> paymentAtColumn = new TableColumn<>("Дата оплати");
        paymentAtColumn.setCellValueFactory(new PropertyValueFactory<>("paymentAt"));
        paymentAtColumn.setCellFactory(column -> new LocalDateTimeTableCell<>());

        TableColumn<OrderModel, String> servicesColumn = new TableColumn<>("Виконані послуги");
        servicesColumn.setCellValueFactory(param -> {
            String services = String.join(
                    ", ",
                    param.getValue().getServiceModels().stream()
                            .map(ServiceModel::toString)
                            .toList());

            return new SimpleStringProperty(services);
        });

        TableColumn<OrderModel, String> sparesColumn = new TableColumn<>("Задіяні запчастини");
        sparesColumn.setCellValueFactory(param -> {
            String spares = String.join(
                    ", ",
                    param.getValue().getSpareModels().stream()
                            .map(SpareModel::toString)
                            .toList());

            return new SimpleStringProperty(spares);
        });

        TableColumn<OrderModel, LocalDateTime> updatedAtColumn = new TableColumn<>("Оновлено");
        updatedAtColumn.setCellValueFactory(new PropertyValueFactory<>("updatedAt"));
        updatedAtColumn.setCellFactory(column -> new LocalDateTimeTableCell<>());
        updatedAtColumn.visibleProperty().bind(showHideDateTimeFields.selectedProperty());

        TableColumn<OrderModel, LocalDateTime> createdAtColumn = new TableColumn<>("Створено");
        createdAtColumn.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
        createdAtColumn.setCellFactory(column -> new LocalDateTimeTableCell<>());
        createdAtColumn.visibleProperty().bind(showHideDateTimeFields.selectedProperty());

        // Створюємо колонку "Редагувати"
        TableColumn<OrderModel, Void> editColumn = new TableColumn<>("");
        editColumn.setCellFactory(column -> new ButtonTableCell<>(CrudState.EDIT.getValue(), om -> {
            var myController = PageLoader.getController("orders.addedit");
            var controller = (OrderAddEditController) myController.controller();
            controller.setOrderModel(om);
            controller.setOrders(orders);
            PageLoader.setInnerContent(myController);
        }));

        // Створюємо колонку "Видалити"
        TableColumn<OrderModel, Void> deleteColumn = new TableColumn<>("");
        deleteColumn.setCellFactory(column -> new ButtonTableCell<>(CrudState.REMOVE.getValue(), om -> {
            if (UiHelpers.showDeleteConfirmation()) {
                orderRepository.remove(om.getId());
                orders.remove(om);
            }
        }));

        table.getColumns()
                .addAll(
                        clientColumn,
                        carColumn,
                        discountColumn,
                        employeesColumn,
                        periodTypeColumn,
                        priceColumn,
                        paymentAtColumn,
                        servicesColumn,
                        sparesColumn,
                        updatedAtColumn,
                        createdAtColumn,
                        editColumn,
                        deleteColumn);
        table.setItems(orders);
    }

    private void initSearchComboBox() {
        searchComboBox.setConverter(new KeyValueStringConverter());
        searchComboBox.getItems().add(new KeyValue("paymentType", "Тип платежу"));
        searchComboBox.getItems().add(new KeyValue("clientPhone", "Телефон клієнта"));
        searchComboBox.getItems().add(new KeyValue("clientEmail", "Email клієнта"));
        searchComboBox.getItems().add(new KeyValue("clientFirstName", "Ім'я клієнта"));
        searchComboBox.getItems().add(new KeyValue("clientLastName", "Фамілія клієнта"));
        searchComboBox.selectFirst();
    }

    public void onSearchButton(ActionEvent actionEvent) {
        String search = searchTextField.getText();
        String selectedKey = "";
        var selectedKeyValue = searchComboBox.getValue();
        if (Objects.nonNull(selectedKeyValue)) {
            selectedKey = selectedKeyValue.key();
        }

        var clientRepository = RepositoryFactory.getInstance().getClientRepository();
        List<Client> clients = clientRepository.getAllWhere(
                selectedKey.equals("clientPhone") ? search : null,
                selectedKey.equals("clientEmail") ? search : null,
                selectedKey.equals("clientFirstName") ? search : null,
                selectedKey.equals("clientLastName") ? search : null,
                null,
                null,
                null);
        Set<Order> orderSet = new HashSet<>();
        for (var client : clients) {
            List<Order> innerOrderList = orderRepository.getAllWhere(
                    client, null, null, null, selectedKey.equals("paymentType") ? search : null, null, null, null);
            orderSet.addAll(innerOrderList);
        }

        if (orderSet.isEmpty()) {
            orderSet.addAll(orderRepository.getAll());
        }

        var orderModels = orderSet.stream()
                .map(orderModelMapper::toModel)
                .sorted(Comparator.comparing(OrderModel::getCreatedAt).reversed())
                .toList();
        orders = FXCollections.observableArrayList(orderModels);
        table.setItems(orders);
    }
}
