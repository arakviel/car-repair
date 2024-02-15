package com.arakviel.carrepair.presentation.controller.order;

import com.arakviel.carrepair.domain.factory.RepositoryFactory;
import com.arakviel.carrepair.domain.repository.OrderRepository;
import com.arakviel.carrepair.presentation.mapper.impl.ModelMapperFactory;
import com.arakviel.carrepair.presentation.model.impl.OrderModel;
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

public class OrderController implements Initializable {

    @FXML
    private final ToggleGroup toggleGroup = new ToggleGroup();

    @FXML
    private MFXRectangleToggleNode menuItemView;

    @FXML
    private MFXRectangleToggleNode menuItemAddEdit;

    @FXML
    private AnchorPane contentPane;

    private ObservableList<OrderModel> orders;
    private OrderRepository orderRepository;
    private final String directoryName = "orders";

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        toggleGroup.getToggles().addAll(menuItemView, menuItemAddEdit);
        menuItemView.setSelected(true);

        loadListsData();
        initSubControllers();
    }

    private void loadListsData() {
        orderRepository = RepositoryFactory.getInstance().getOrderRepository();
        var orderList = orderRepository.getAll();
        var orderModelMapper = ModelMapperFactory.getInstance().getOrderModelMapper();
        List<OrderModel> list = orderList.stream()
                .map(orderModelMapper::toModel)
                .sorted(Comparator.comparing(OrderModel::getCreatedAt))
                .toList();
        orders = FXCollections.observableArrayList(list);
    }

    private void initSubControllers() {
        MyController myController1 = PageLoader.load(contentPane, directoryName, "addedit");
        var orderAddEditController = (OrderAddEditController) myController1.controller();
        orderAddEditController.setOrders(orders);
        MyController myController2 = PageLoader.load(contentPane, directoryName, "view");
        var orderViewController = (OrderViewController) myController2.controller();
        orderViewController.setupTable(orders);
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
