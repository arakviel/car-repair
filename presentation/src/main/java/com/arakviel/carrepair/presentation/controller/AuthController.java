package com.arakviel.carrepair.presentation.controller;

import com.arakviel.carrepair.domain.impl.User;
import com.arakviel.carrepair.domain.service.impl.AuthServiceImpl;
import com.arakviel.carrepair.presentation.mapper.impl.ModelMapperFactory;
import com.arakviel.carrepair.presentation.model.impl.EmployeeModel;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXPasswordField;
import io.github.palexdev.materialfx.controls.MFXTextField;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;

public class AuthController implements Initializable {

    @FXML
    private MFXTextField loginTextField;

    @FXML
    private MFXPasswordField passwordPasswordField;

    @FXML
    private MFXButton signInButton;

    private User currentUser;
    private MainController mainController;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        currentUser = AuthServiceImpl.getInstance().getCurrentUser();
    }

    public void onSignInButtonClick(ActionEvent actionEvent) {
        if (!loginTextField.getText().isBlank()
                && !passwordPasswordField.getText().isBlank()) {
            currentUser =
                    AuthServiceImpl.getInstance().login(loginTextField.getText(), passwordPasswordField.getText());
            mainController.emailText.textProperty().bind(new SimpleStringProperty(currentUser.getEmail()));
            mainController
                    .fullNameText
                    .textProperty()
                    .bind(new SimpleStringProperty(currentUser.getEmployee().getFirstName() + " "
                            + currentUser.getEmployee().getLastName() + " "
                            + currentUser.getEmployee().getMiddleName()));
            EmployeeModel employeeModel =
                    ModelMapperFactory.getInstance().getEmployeeModelMapper().toModel(currentUser.getEmployee());
            mainController.userPhotoImageView.imageProperty().bind(employeeModel.photoProperty());
            if (Objects.nonNull(currentUser)) {
                loginTextField.setText("");
                passwordPasswordField.setText("");
                this.mainController.sideMenuInit();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Помилка");
            alert.setHeaderText(null);
            alert.setContentText("Будь ласка, заповніть поля для логіну та паролю.");
            alert.showAndWait();
        }
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }
}
