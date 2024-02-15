package com.arakviel.carrepair.presentation.model.impl;

import com.arakviel.carrepair.presentation.model.BaseModel;
import com.arakviel.carrepair.presentation.model.Model;
import java.util.StringJoiner;
import java.util.UUID;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class UserModel extends BaseModel<UUID> implements Model, Cloneable {
    private StringProperty email = new SimpleStringProperty();
    private StringProperty login = new SimpleStringProperty();
    private StringProperty password = new SimpleStringProperty();
    private ObjectProperty<EmployeeModel> employeeModel = new SimpleObjectProperty<>();

    private UserModel(UUID id, String email, String login, String password) {
        this.id = new SimpleObjectProperty<>(id);
        this.email.set(email);
        this.login.set(login);
        this.password.set(password);
    }

    public static UserModelBuilderId builder() {
        return id -> email -> login -> password -> () -> new UserModel(id, email, login, password);
    }

    public interface UserModelBuilderId {
        UserModelBuilderEmail id(UUID id);
    }

    public interface UserModelBuilderEmail {
        UserModelBuilderLogin email(String email);
    }

    public interface UserModelBuilderLogin {
        UserModelBuilderPassword login(String login);
    }

    public interface UserModelBuilderPassword {
        UserModelBuilder password(String password);
    }

    public interface UserModelBuilder {
        UserModel build();
    }

    public String getEmail() {
        return email.get();
    }

    public StringProperty emailProperty() {
        return email;
    }

    public void setEmail(String email) {
        this.email.set(email);
    }

    public String getLogin() {
        return login.get();
    }

    public StringProperty loginProperty() {
        return login;
    }

    public void setLogin(String login) {
        this.login.set(login);
    }

    public String getPassword() {
        return password.get();
    }

    public StringProperty passwordProperty() {
        return password;
    }

    public void setPassword(String password) {
        this.password.set(password);
    }

    public EmployeeModel getEmployeeModel() {
        return employeeModel.get();
    }

    public ObjectProperty<EmployeeModel> employeeModelProperty() {
        return employeeModel;
    }

    public void setEmployeeModel(EmployeeModel employeeModel) {
        this.employeeModel.set(employeeModel);
    }

    @Override
    public UserModel clone() {
        try {
            UserModel cloned = (UserModel) super.clone();
            cloned.id = new SimpleObjectProperty<>(this.id.get());
            cloned.email = new SimpleStringProperty(this.email.get());
            cloned.login = new SimpleStringProperty(this.login.get());
            cloned.password = new SimpleStringProperty(this.password.get());

            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new InternalError(e);
        }
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", UserModel.class.getSimpleName() + "[", "]")
                .add("email=" + email)
                .add("login=" + login)
                .add("password=" + password)
                .add("employeeModel=" + employeeModel)
                .add("id=" + id)
                .toString();
    }
}
