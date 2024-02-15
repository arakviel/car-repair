package com.arakviel.carrepair.persistence.entity.impl;

import com.arakviel.carrepair.persistence.entity.BaseEntity;
import com.arakviel.carrepair.persistence.entity.Entity;
import java.util.StringJoiner;
import java.util.UUID;

public class UserEntity extends BaseEntity<UUID> implements Entity {

    private String email;
    private String login;
    private String password;
    private EmployeeEntity employeeEntity;

    private UserEntity(UUID id, String email, String login, String password) {
        super(id);
        this.email = email;
        this.login = login;
        this.password = password;
    }

    public static UserEntityBuilderId builder() {
        return id -> email -> login -> password -> () -> new UserEntity(id, email, login, password);
    }

    public interface UserEntityBuilderId {
        UserEntityBuilderEmail id(UUID id);
    }

    public interface UserEntityBuilderEmail {

        UserEntityBuilderLogin email(String email);
    }

    public interface UserEntityBuilderLogin {

        UserEntityBuilderPassword login(String login);
    }

    public interface UserEntityBuilderPassword {

        UserEntityBuilder password(String password);
    }

    public interface UserEntityBuilder {

        UserEntity build();
    }

    public String getEmail() {
        return email;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public void setEmployeeEntity(EmployeeEntity employeeEntity) {
        this.employeeEntity = employeeEntity;
    }

    /*    public EmployeeEntity getEmployeeEntity() {
        if (Objects.isNull(employeeEntity)) {
            employeeEntity = DaoFactory.getInstance()
                    .getEmployeeDao()
                    .findOneById(id)
                    .orElseThrow(
                            () -> new EntityNotFoundException("Дивовижно, але цієї помилки не може існувати" + " О.о"));
        }
        return employeeEntity;
    }*/

    public EmployeeEntity getEmployeeEntity() {
        return employeeEntity;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", UserEntity.class.getSimpleName() + "[", "]")
                .add("email='" + email + "'")
                .add("login='" + login + "'")
                .add("password='" + password + "'")
                .add("employeeEntity=" + employeeEntity)
                .add("id=" + id)
                .toString();
    }
}
