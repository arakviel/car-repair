package com.arakviel.carrepair.domain.service;

import com.arakviel.carrepair.domain.impl.Employee;
import com.arakviel.carrepair.domain.impl.User;
import com.arakviel.carrepair.domain.impl.Workroom;

public interface AuthService {

    User register(Employee employee, User user);

    User login(String login, String password);

    void logout();

    User getCurrentUser();

    Workroom getCurentWorkroom();
}
