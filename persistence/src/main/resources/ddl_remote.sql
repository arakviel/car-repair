START TRANSACTION;

CREATE DATABASE IF NOT EXISTS railway CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

DROP TABLE IF EXISTS railway.car_photos;
DROP TABLE IF EXISTS railway.employee_order;
DROP TABLE IF EXISTS railway.payrolls;
DROP TABLE IF EXISTS railway.phones;
DROP TABLE IF EXISTS railway.roles;
DROP TABLE IF EXISTS railway.service_order;
DROP TABLE IF EXISTS railway.services;
DROP TABLE IF EXISTS railway.spare_order;
DROP TABLE IF EXISTS railway.orders;
DROP TABLE IF EXISTS railway.cars;
DROP TABLE IF EXISTS railway.clients;
DROP TABLE IF EXISTS railway.discounts;
DROP TABLE IF EXISTS railway.models;
DROP TABLE IF EXISTS railway.brands;
DROP TABLE IF EXISTS railway.spares;
DROP TABLE IF EXISTS railway.users;
DROP TABLE IF EXISTS railway.staff;
DROP TABLE IF EXISTS railway.positions;
DROP TABLE IF EXISTS railway.currencies;
DROP TABLE IF EXISTS railway.workrooms;
DROP TABLE IF EXISTS railway.addresses;

--
-- Таблиця "addresses" сутності "Адреси"
-- 1NF
--
CREATE TABLE railway.addresses
    (
        PRIMARY KEY(id),
        id          VARCHAR(36)  NOT NULL,
        country     VARCHAR(190) NOT NULL,
        region      VARCHAR(128) NOT NULL,
        city        VARCHAR(64)  NOT NULL,
        street      VARCHAR(256) NOT NULL,
        home        VARCHAR(32)  NOT NULL
    )
ENGINE = INNODB;

--
-- Таблиця "workrooms" сутності "Майстерня"
-- 2NF
--
CREATE TABLE railway.workrooms
    (
        PRIMARY KEY(id),
        id          VARCHAR(36)  NOT NULL,
        address_id  VARCHAR(36)  NOT NULL,
                    CONSTRAINT workrooms_address_id_addresses_id_fkey
                    FOREIGN KEY(address_id) REFERENCES railway.addresses(id)
                    ON UPDATE CASCADE
                    ON DELETE CASCADE,
        name        VARCHAR(64)  NOT NULL,
                    CONSTRAINT workrooms_name_unique
                    UNIQUE(name),
        photo       BLOB         NOT NULL,
        description VARCHAR(256) NULL
    )
ENGINE = INNODB;

CREATE TABLE railway.currencies(
    PRIMARY KEY(id),
    id      INT(11)     UNSIGNED            NOT NULL AUTO_INCREMENT,
    name    VARCHAR(32)                     NOT NULL,
            CONSTRAINT currencies_name_unique
            UNIQUE(name),
    symbol  VARCHAR(3) NOT NULL
) ENGINE=INNODB;

CREATE TABLE railway.positions(
    PRIMARY KEY (id),
    id                              INT(11)         UNSIGNED    NOT NULL AUTO_INCREMENT,
    name                            VARCHAR(64)                 NOT NULL,
                                    CONSTRAINT positions_name_unique
                                    UNIQUE(name),
    description                     VARCHAR(256),
    currency_id                     INT(11)         UNSIGNED    NOT NULL,
                                    CONSTRAINT workrooms_currency_id_currencies_id_fkey
                                    FOREIGN KEY(currency_id) REFERENCES railway.currencies(id)
                                    ON UPDATE CASCADE,
    salary_per_hour_whole_part      INT(11)         UNSIGNED    NOT NULL,
    salary_per_hour_decimal_part    INT(11)         UNSIGNED    NOT NULL
);

CREATE TABLE railway.roles(
    PRIMARY KEY(position_id),
    position_id         INT(11)     UNSIGNED    NOT NULL,
                        CONSTRAINT roles_position_id_positions_id_fkey
                        FOREIGN KEY(position_id) REFERENCES railway.positions(id)
                        ON UPDATE CASCADE
                        ON DELETE CASCADE,
    can_edit_users      BOOL                    NOT NULL,
    can_edit_spares     BOOL                    NOT NULL,
    can_edit_clients    BOOL                    NOT NULL,
    can_edit_services   BOOL                    NOT NULL,
    can_edit_orders     BOOL                    NOT NULL,
    can_edit_payrolls   BOOL                    NOT NULL
);

CREATE TABLE railway.staff(
    PRIMARY KEY(id),
    id                      VARCHAR(36)             NOT NULL,
    address_id              VARCHAR(36)             NOT NULL,
                            CONSTRAINT staff_address_id_addresses_id_fkey
                            FOREIGN KEY(address_id) REFERENCES railway.addresses(id)
                            ON UPDATE CASCADE
                            ON DELETE CASCADE,
    workroom_id             VARCHAR(36)             NOT NULL,
                            CONSTRAINT staff_workroom_id_workrooms_id_fkey
                            FOREIGN KEY(workroom_id) REFERENCES railway.workrooms(id)
                            ON UPDATE CASCADE
                            ON DELETE CASCADE,
    position_id             INT(11)     UNSIGNED    NOT NULL,
                            CONSTRAINT staff_position_id_positions_id_fkey
                            FOREIGN KEY(position_id) REFERENCES railway.positions(id)
                            ON UPDATE CASCADE
                            ON DELETE CASCADE,
    first_name              VARCHAR(256)            NOT NULL,
    last_name               VARCHAR(256)            NOT NULL,
    middle_name             VARCHAR(256),
    photo                   BLOB                    NOT NULL,
    passport_doc_copy       BLOB                    NOT NULL,
    bank_number_doc_copy    BLOB,
    other_doc_copy          BLOB,
    updated_at              TIMESTAMP,
    created_at              TIMESTAMP
);

CREATE TABLE railway.phones(
    PRIMARY KEY(id),
    id          VARCHAR(36)                     NOT NULL,
    employee_id VARCHAR(36)                     NOT NULL,
                CONSTRAINT phones_employee_id_staff_id_fkey
                FOREIGN KEY(employee_id) REFERENCES railway.staff(id)
                ON UPDATE CASCADE
                ON DELETE CASCADE,
    type        ENUM('work', 'personal')        NOT NULL,
    value       VARCHAR(15)                     NOT NULL,
                CONSTRAINT phones_value_unique
                UNIQUE(value),
                CONSTRAINT phones_value_min_length_check
                CHECK(LENGTH(value) > 6)
);

CREATE TABLE railway.users(
    PRIMARY KEY(employee_id),
    employee_id VARCHAR(36)     NOT NULL,
                CONSTRAINT users_employee_id_staff_id_fkey
                FOREIGN KEY(employee_id) REFERENCES railway.staff(id)
                ON UPDATE CASCADE
                ON DELETE CASCADE,
    email       VARCHAR(256)    NOT NULL,
                CONSTRAINT users_email_unique
                UNIQUE(email),
    login       VARCHAR(32)     NOT NULL,
                CONSTRAINT users_login_unique
                UNIQUE(login),
                CONSTRAINT users_login_min_length_check
                CHECK(LENGTH(login) > 4),
    password    VARCHAR(64)     NOT NULL,
                CONSTRAINT users_password_min_length_check
                CHECK(LENGTH(password) > 8)
);

CREATE TABLE railway.payrolls(
    PRIMARY KEY(id),
    id                  VARCHAR(36)                 NOT NULL,
    employee_id         VARCHAR(36)                 NOT NULL,
                        CONSTRAINT payrolls_employee_id_staff_id_fkey
                        FOREIGN KEY(employee_id) REFERENCES railway.staff(id)
                        ON UPDATE CASCADE
                        ON DELETE CASCADE,
    period_type         ENUM('quarterly', 'monthly', 'weekly', 'daily') NOT NULL,
    hour_count          INT(11)         UNSIGNED    NOT NULL,
    salary_whole_part   INT(11)         UNSIGNED    NOT NULL,
    salary_decimal_part INT(11)         UNSIGNED    NOT NULL,
    payment_at          TIMESTAMP                   NOT NULL,
    updated_at          TIMESTAMP,
    created_at          TIMESTAMP
);

CREATE TABLE railway.spares(
    PRIMARY KEY(id),
    id                  VARCHAR(36)                 NOT NULL,
    workroom_id         VARCHAR(36)                 NOT NULL,
                        CONSTRAINT spares_workroom_id_staff_id_fkey
                        FOREIGN KEY(workroom_id) REFERENCES railway.workrooms(id)
                        ON UPDATE CASCADE,
    name                VARCHAR(128)                NOT NULL,
                        CONSTRAINT spares_name_unique
                        UNIQUE(name),
    description         VARCHAR(512),
    photo               BLOB,
    price_whole_part    INT(11)         UNSIGNED    NOT NULL,
    price_decimal_part  INT(11)         UNSIGNED    NOT NULL,
    quantity_in_stock   INT(11)         UNSIGNED    NOT NULL
);

CREATE TABLE railway.clients(
    PRIMARY KEY(id),
    id          VARCHAR(36) NOT NULL,
    phone       VARCHAR(15) NOT NULL,
                CONSTRAINT clients_phone_unique
                UNIQUE(phone),
    email       VARCHAR(256),
                CONSTRAINT clients_email_unique
                UNIQUE(email),
    first_name  VARCHAR(256),
    last_name   VARCHAR(256),
    middle_name VARCHAR(256),
    photo       BLOB,
    updated_at  TIMESTAMP,
    created_at  TIMESTAMP
);

CREATE TABLE railway.brands(
    PRIMARY KEY(id),
    id      INT(11) UNSIGNED NOT NULL AUTO_INCREMENT,
    name    VARCHAR(128)     NOT NULL,
            CONSTRAINT brands_name_unique
            UNIQUE(name)
);

CREATE TABLE railway.models(
    PRIMARY KEY(id),
    id          INT(11) UNSIGNED NOT NULL AUTO_INCREMENT,
    brand_id    INT(11) UNSIGNED NOT NULL,
                CONSTRAINT models_brand_id_brands_id_fkey
                FOREIGN KEY(brand_id) REFERENCES railway.brands(id)
                ON UPDATE CASCADE,
    name        VARCHAR(256)     NOT NULL,
                CONSTRAINT models_name_unique
                UNIQUE(name)
);

CREATE TABLE railway.cars(
    PRIMARY KEY(id),
    id          VARCHAR(36)             NOT NULL,
    model_id    INT(11)     UNSIGNED    NOT NULL,
                CONSTRAINT cars_model_id_models_id_fkey
                FOREIGN KEY(model_id) REFERENCES railway.models(id)
                ON UPDATE CASCADE
                ON DELETE CASCADE,
    number      VARCHAR(12)             NOT NULL,
    year        SMALLINT    UNSIGNED    NOT NULL,
    engine_type ENUM('benzine', 'diesel', 'electric', 'hybrid', 'gas', 'other') NOT NULL,
    mileage     INT(11)     UNSIGNED    NOT NULL,
    color       CHAR(6),
    updated_at  TIMESTAMP,
    created_at  TIMESTAMP
);

CREATE TABLE railway.car_photos(
    PRIMARY KEY(id),
    id          VARCHAR(36) NOT NULL,
    car_id      VARCHAR(36) NOT NULL,
                CONSTRAINT car_photos_car_id_cars_id_fkey
                FOREIGN KEY(car_id) REFERENCES railway.cars(id)
                ON UPDATE CASCADE
                ON DELETE CASCADE,
    photo       BLOB        NOT NULL,
    description VARCHAR(256)
);

CREATE TABLE railway.services(
    PRIMARY KEY(id),
    id                  INT(11)      UNSIGNED    NOT NULL    AUTO_INCREMENT,
    name                VARCHAR(128)             NOT NULL,
    description         VARCHAR(512)             NOT NULL,
    photo               BLOB                     NOT NULL,
    currency_id         INT(11)      UNSIGNED    NOT NULL,
                        CONSTRAINT services_currency_id_addresses_id_fkey
                        FOREIGN KEY(currency_id) REFERENCES railway.currencies(id)
                        ON UPDATE CASCADE,
    price_whole_part    INT(11)      UNSIGNED    NOT NULL,
    price_decimal_part  INT(11)      UNSIGNED    NOT NULL
);

CREATE TABLE railway.discounts(
    PRIMARY KEY(id),
    id          INT(11)     UNSIGNED NOT NULL AUTO_INCREMENT,
    name        VARCHAR(64)          NOT NULL,
                CONSTRAINT discounts_name_unique
                UNIQUE(name),
    description VARCHAR(256),
    value       SMALLINT             NOT NULL
);

CREATE TABLE railway.orders(
    PRIMARY KEY(id),
    id                  VARCHAR(36)                     NOT NULL,
    client_id           VARCHAR(36)                     NOT NULL,
                        CONSTRAINT orders_client_id_clients_id_fkey
                        FOREIGN KEY(client_id) REFERENCES railway.clients(id)
                        ON UPDATE CASCADE,
    car_id              VARCHAR(36)                     NOT NULL,
                        CONSTRAINT orders_car_id_cars_id_fkey
                        FOREIGN KEY(car_id) REFERENCES railway.cars(id)
                        ON UPDATE CASCADE,
    discount_id         INT(11)     UNSIGNED            NULL,
                        CONSTRAINT orders_discount_id_discounts_id_fkey
                        FOREIGN KEY(discount_id) REFERENCES railway.discounts(id)
                        ON UPDATE CASCADE,
    price_whole_part    INT(11)     UNSIGNED            NOT NULL,
    price_decimal_part  INT(11)     UNSIGNED            NOT NULL,
    payment_type        ENUM('card', 'cash')      NOT NULL,
    payment_at          TIMESTAMP                       NOT NULL,
    updated_at          TIMESTAMP,
    created_at          TIMESTAMP
);

CREATE TABLE railway.employee_order(
    PRIMARY KEY(order_id, employee_id),
    order_id    VARCHAR(36) NOT NULL,
                CONSTRAINT employee_order_order_id_orders_id_fkey
                FOREIGN KEY(order_id) REFERENCES railway.orders(id)
                ON UPDATE CASCADE,
    employee_id VARCHAR(36) NOT NULL,
                CONSTRAINT employee_order_employee_id_staff_id_fkey
                FOREIGN KEY(employee_id) REFERENCES railway.staff(id)
                ON UPDATE CASCADE
);

CREATE TABLE railway.spare_order(
    PRIMARY KEY(order_id, spare_id),
    order_id    VARCHAR(36)             NOT NULL,
                CONSTRAINT spare_order_order_id_orders_id_fkey
                FOREIGN KEY(order_id) REFERENCES railway.orders(id)
                ON UPDATE CASCADE,
    spare_id    VARCHAR(36)             NOT NULL,
                CONSTRAINT spare_order_spare_id_spares_id_fkey
                FOREIGN KEY(spare_id) REFERENCES railway.spares(id)
                ON UPDATE CASCADE,
    quantity    INT(11)     UNSIGNED    NOT NULL
);

CREATE TABLE railway.service_order(
    PRIMARY KEY(order_id, service_id),
    order_id    VARCHAR(36)             NOT NULL,
                CONSTRAINT service_order_order_id_orders_id_fkey
                FOREIGN KEY(order_id) REFERENCES railway.orders(id)
                ON UPDATE CASCADE,
    service_id  INT(11)     UNSIGNED    NOT NULL,
                CONSTRAINT service_order_service_id_services_id_fkey
                FOREIGN KEY(service_id) REFERENCES railway.services(id)
                ON UPDATE CASCADE,
    description VARCHAR(256)
);

COMMIT;