package com.arakviel.carrepair.domain.impl;

import com.arakviel.carrepair.domain.BaseDomain;
import com.arakviel.carrepair.domain.Domain;
import java.util.StringJoiner;
import java.util.UUID;

public class User extends BaseDomain<UUID> implements Domain {

    private String email;
    private String login;
    private String password;
    private Employee employee;

    private User(UUID id, String email, String login, String password) {
        super.id = id;
        this.email = email;
        this.login = login;
        this.password = password;
    }

    public static UserBuilderId builder() {
        return id -> email -> login -> password -> () -> new User(id, email, login, password);
    }

    public interface UserBuilderId {
        UserBuilderEmail id(UUID id);
    }

    public interface UserBuilderEmail {
        UserBuilderLogin email(String email);
    }

    public interface UserBuilderLogin {
        UserBuilderPassword login(String login);
    }

    public interface UserBuilderPassword {
        UserBuilder password(String password);
    }

    public interface UserBuilder {
        User build();
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

    public void setPassword(String password) {
        this.password = password;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", User.class.getSimpleName() + "[", "]")
                .add("email='" + email + "'")
                .add("login='" + login + "'")
                .add("password='" + password + "'")
                .add("employee=" + employee)
                .add("id=" + id)
                .toString();
    }
}
