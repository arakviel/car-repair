module com.arakviel.carrapair.persistence {
    requires java.sql;
    requires java.desktop;
    requires org.slf4j;

    exports com.arakviel.carrepair.persistence.entity.impl;
    exports com.arakviel.carrepair.persistence.entity.proxy;
    exports com.arakviel.carrepair.persistence.filter.impl;
    exports com.arakviel.carrepair.persistence.dao;
    exports com.arakviel.carrepair.persistence.filter;
    exports com.arakviel.carrepair.persistence;
}
