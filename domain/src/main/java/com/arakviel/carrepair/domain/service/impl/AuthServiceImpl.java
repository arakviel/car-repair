package com.arakviel.carrepair.domain.service.impl;

import com.arakviel.carrepair.domain.exception.AuthException;
import com.arakviel.carrepair.domain.exception.DomainNotFoundException;
import com.arakviel.carrepair.domain.factory.RepositoryFactory;
import com.arakviel.carrepair.domain.impl.Employee;
import com.arakviel.carrepair.domain.impl.User;
import com.arakviel.carrepair.domain.impl.Workroom;
import com.arakviel.carrepair.domain.repository.EmployeeRepository;
import com.arakviel.carrepair.domain.repository.UserRepository;
import com.arakviel.carrepair.domain.service.AuthService;
import java.util.Optional;
import org.mindrot.jbcrypt.BCrypt;

public class AuthServiceImpl implements AuthService {
    private User authenticatedUser;
    private Workroom userWorkroom;
    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;

    private AuthServiceImpl() {
        employeeRepository = RepositoryFactory.getInstance().getEmployeeRepository();
        userRepository = RepositoryFactory.getInstance().getUserRepository();
    }

    @Override
    public User register(Employee employee, User user) {
        String hashedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());
        User newUser = User.builder()
                .id(employee.getId())
                .email(user.getEmail())
                .login(user.getLogin())
                .password(hashedPassword)
                .build();
        // employeeRepository.add(employee);
        userRepository.add(newUser);
        newUser.setEmployee(employee);
        authenticatedUser = newUser;
        userWorkroom = employee.getWorkroom();
        return newUser;
    }

    @Override
    public User login(String login, String password) {
        User user = getUserByLogin(login)
                .orElseThrow(() -> new DomainNotFoundException("Користувача з таким логіном не існує"));

        // Check if password is correct
        if (!BCrypt.checkpw(password, user.getPassword())) {
            throw new AuthException("Нажаль, ви ввели не вірний пароль");
        }

        Employee employee = employeeRepository.get(user.getId());
        user.setEmployee(employee);
        authenticatedUser = user;
        userWorkroom = employee.getWorkroom();
        return user;
    }

    @Override
    public void logout() {
        authenticatedUser = null;
        userWorkroom = null;
    }

    @Override
    public User getCurrentUser() {
        return authenticatedUser;
    }

    @Override
    public Workroom getCurentWorkroom() {
        return userWorkroom;
    }

    private Optional<User> getUserByLogin(String login) {
        return Optional.ofNullable(userRepository.getAllWhere(null, login).get(0));
    }

    private static class SingletonHolder {
        public static AuthServiceImpl INSTANCE = new AuthServiceImpl();
    }

    public static AuthServiceImpl getInstance() {
        return SingletonHolder.INSTANCE;
    }
}
