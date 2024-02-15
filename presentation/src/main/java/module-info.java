module com.arakviel.carrepair.presentation {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.arakviel.carrepair.domain;
    requires MaterialFX;

    exports com.arakviel.carrepair.presentation;
    exports com.arakviel.carrepair.presentation.controller;
    exports com.arakviel.carrepair.presentation.model.impl;
    exports com.arakviel.carrepair.presentation.mapper.impl;

    opens com.arakviel.carrepair.presentation.controller to
            javafx.fxml;

    exports com.arakviel.carrepair.presentation.controller.client;
    exports com.arakviel.carrepair.presentation.controller.workroom;
    exports com.arakviel.carrepair.presentation.controller.position;
    exports com.arakviel.carrepair.presentation.controller.service;
    exports com.arakviel.carrepair.presentation.controller.spare;
    exports com.arakviel.carrepair.presentation.controller.car;
    exports com.arakviel.carrepair.presentation.controller.employee;
    exports com.arakviel.carrepair.presentation.controller.payroll;
    exports com.arakviel.carrepair.presentation.controller.order;

    opens com.arakviel.carrepair.presentation.controller.client to
            javafx.fxml;
    opens com.arakviel.carrepair.presentation.controller.workroom to
            javafx.fxml;
    opens com.arakviel.carrepair.presentation.controller.position to
            javafx.fxml;
    opens com.arakviel.carrepair.presentation.controller.service to
            javafx.fxml;
    opens com.arakviel.carrepair.presentation.controller.spare to
            javafx.fxml;
    opens com.arakviel.carrepair.presentation.controller.car to
            javafx.fxml;
    opens com.arakviel.carrepair.presentation.controller.employee to
            javafx.fxml;
    opens com.arakviel.carrepair.presentation.controller.payroll to
            javafx.fxml;
    opens com.arakviel.carrepair.presentation.controller.order to
            javafx.fxml;
}
