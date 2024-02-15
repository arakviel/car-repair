module com.arakviel.carrepair.domain {
    requires com.arakviel.carrapair.persistence;
    requires java.desktop;
    requires jbcrypt;

    exports com.arakviel.carrepair.domain.impl;
    exports com.arakviel.carrepair.domain.proxy;
    exports com.arakviel.carrepair.domain.repository;
    exports com.arakviel.carrepair.domain.factory;
    exports com.arakviel.carrepair.domain.service;
    exports com.arakviel.carrepair.domain.service.impl;
}
