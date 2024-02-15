package com.arakviel.carrepair.presentation.controller;

import com.arakviel.carrepair.domain.factory.RepositoryFactory;
import com.arakviel.carrepair.domain.impl.Role;
import com.arakviel.carrepair.domain.impl.User;
import com.arakviel.carrepair.domain.service.impl.AuthServiceImpl;
import com.arakviel.carrepair.presentation.util.PageLoader;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXIconWrapper;
import io.github.palexdev.materialfx.controls.MFXRectangleToggleNode;
import io.github.palexdev.materialfx.utils.ToggleButtonsUtil;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class MainController implements Initializable {

    private final List<Page> pages = new ArrayList<>();

    @FXML
    private final ToggleGroup toggleGroup = new ToggleGroup();

    @FXML
    public MFXButton authButton;

    @FXML
    public Label emailText;

    @FXML
    public Label fullNameText;

    public ImageView userPhotoImageView;

    @FXML
    private Text titleText;

    @FXML
    private Label descriptionLabel;

    @FXML
    private Pane contentPane;

    @FXML
    private VBox menuBarVBox;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ToggleButtonsUtil.addAlwaysOneSelectedSupport(toggleGroup);
        auth();

        // Page homePage = new Page("home", "Головна", "Опис 1", createToggle("fas-house", "Головна"));
        // pages.add(homePage);
        // loadPage(homePage.fxmlName(), homePage.title(), homePage.description());

        // sideMenuInit();
    }

    public void sideMenuInit() {
        User currentUser = AuthServiceImpl.getInstance().getCurrentUser();
        Role role = RepositoryFactory.getInstance()
                .getRoleRepository()
                .get(currentUser.getEmployee().getPosition().getId());

        if (role.canEditOrders()) {
            pages.add(new Page(
                    "orders",
                    "Замовлення",
                    "Обробка всіх замовлень компанії. Відділ: %s"
                            .formatted(AuthServiceImpl.getInstance()
                                    .getCurentWorkroom()
                                    .getName()),
                    createToggle("fas-handshake", "Замовлення")));
        }
        if (role.canEditClients()) {
            pages.add(new Page("clients", "Клієнти", "Облік клієнтів компанії.", createToggle("fas-users", "Клієнти")));
            pages.add(new Page(
                    "cars",
                    "Автомобілі",
                    "Облік автомобілів клієнтів компанії.",
                    createToggle("fas-car-side", "Автомобілі")));
        }
        if (role.canEditSpares()) {
            pages.add(new Page(
                    "spares",
                    "Запчастини",
                    "Облік запчастин по відділам.",
                    createToggle("fas-screwdriver-wrench", "Запчастини")));
        }
        if (role.canEditUsers()) {
            pages.add(new Page(
                    "staff",
                    "Працівники",
                    "Облік працівників компанії по відділам.",
                    createToggle("fas-id-card", "Працівники")));
            pages.add(
                    new Page("positions", "Професії", "Облік посад компанії.", createToggle("fas-list-ul", "Посади")));
        }
        if (role.canEditServices()) {
            pages.add(new Page(
                    "services",
                    "Послуги",
                    "Конфігурація наявних послух компанії.",
                    createToggle("fas-user-group", "Послуги")));
        }
        if (role.canEditPayrolls()) {
            pages.add(new Page(
                    "payrolls",
                    "Бухгалтерія",
                    "Облік зарплат співробітників.",
                    createToggle("fas-money-bills", "Зарплати")));
        }

        if (currentUser.getEmployee().getPosition().getName().equals("Директор відділу")) {
            pages.add(new Page(
                    "workrooms", "Філії", "Облік відділів компанії.", createToggle("fas-map-location-dot", "Філії")));
        }

        List<ToggleButton> menuItems = pages.stream().map(Page::menuItem).toList();
        menuBarVBox.getChildren().addAll(menuItems);

        for (var page : pages) {
            ToggleButton menuItem = page.menuItem();
            menuItem.setOnAction(event -> {
                if (menuItem.isSelected()) {
                    PageLoader.load(contentPane, page.fxmlName(), page.fxmlName());
                    titleText.setText(page.title());
                    descriptionLabel.setText(page.description());
                }
            });
        }
    }

    private ToggleButton createToggle(String icon, String text) {
        MFXIconWrapper wrapper = new MFXIconWrapper(icon, 12, 20);
        MFXRectangleToggleNode toggleNode = new MFXRectangleToggleNode(text, wrapper);
        toggleNode.setAlignment(Pos.CENTER_LEFT);
        toggleNode.setMaxWidth(Double.MAX_VALUE);
        toggleNode.setToggleGroup(toggleGroup);
        return toggleNode;
    }

    public void onAuthButtonClick(ActionEvent actionEvent) {
        auth();
    }

    private void auth() {
        PageLoader.load(contentPane, "auth", "auth");
        titleText.setText("Авторизація в систему");
        descriptionLabel.setText("");

        var myController = PageLoader.getController("auth.auth");
        var controller = (AuthController) myController.controller();
        controller.setMainController(this);
    }

    private record Page(String fxmlName, String title, String description, ToggleButton menuItem) {}
}
