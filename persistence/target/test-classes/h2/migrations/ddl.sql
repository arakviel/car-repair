DROP TABLE IF EXISTS car_photos;
DROP TABLE IF EXISTS employee_order;
DROP TABLE IF EXISTS payrolls;
DROP TABLE IF EXISTS phones;
DROP TABLE IF EXISTS roles;
DROP TABLE IF EXISTS service_order;
DROP TABLE IF EXISTS services;
DROP TABLE IF EXISTS spare_order;
DROP TABLE IF EXISTS orders;
DROP TABLE IF EXISTS cars;
DROP TABLE IF EXISTS clients;
DROP TABLE IF EXISTS discounts;
DROP TABLE IF EXISTS models;
DROP TABLE IF EXISTS brands;
DROP TABLE IF EXISTS spares;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS staff;
DROP TABLE IF EXISTS positions;
DROP TABLE IF EXISTS currencies;
DROP TABLE IF EXISTS workrooms;
DROP TABLE IF EXISTS addresses;

--
-- Таблиця "addresses" сутності "Адреси"
-- 1NF
--
CREATE TABLE addresses
    (
        PRIMARY KEY (id),
        id VARCHAR(36) NOT NULL,
        country VARCHAR(190) NOT NULL,
        region VARCHAR(128) NOT NULL,
        city VARCHAR(64) NOT NULL,
        street VARCHAR(256) NOT NULL,
        home VARCHAR(32) NOT NULL
    );

--
-- Таблиця "workrooms" сутності "Майстерня"
-- 2NF
--
CREATE TABLE workrooms
    (
        PRIMARY KEY (id),
        id VARCHAR(36) NOT NULL,
        address_id VARCHAR(36) NOT NULL,
        CONSTRAINT workrooms_address_id_addresses_id_fkey
            FOREIGN KEY (address_id) REFERENCES addresses (id) ON UPDATE CASCADE ON DELETE CASCADE,
        name VARCHAR(64) NOT NULL,
        CONSTRAINT workrooms_name_unique
            UNIQUE (name),
        photo BLOB NOT NULL,
        description VARCHAR(256) NULL
    );

CREATE TABLE currencies
    (
        PRIMARY KEY (id),
        id INT NOT NULL AUTO_INCREMENT,
        name VARCHAR(32) NOT NULL,
        CONSTRAINT currencies_name_unique
            UNIQUE (name),
        symbol VARCHAR(3) NOT NULL
    );

CREATE TABLE positions
    (
        PRIMARY KEY (id),
        id INT NOT NULL AUTO_INCREMENT,
        name VARCHAR(64) NOT NULL,
        CONSTRAINT positions_name_unique
            UNIQUE (name),
        description VARCHAR(256),
        currency_id INT NOT NULL,
        CONSTRAINT workrooms_currency_id_currencies_id_fkey
            FOREIGN KEY (currency_id) REFERENCES currencies (id) ON UPDATE CASCADE,
        salary_per_hour_whole_part INT NOT NULL,
        salary_per_hour_decimal_part INT NOT NULL
    );

CREATE TABLE roles
    (
        PRIMARY KEY (position_id),
        position_id INT NOT NULL,
        CONSTRAINT roles_position_id_positions_id_fkey
            FOREIGN KEY (position_id) REFERENCES positions (id) ON UPDATE CASCADE ON DELETE CASCADE,
        can_edit_users BOOL NOT NULL,
        can_edit_spares BOOL NOT NULL,
        can_edit_clients BOOL NOT NULL,
        can_edit_services BOOL NOT NULL,
        can_edit_orders BOOL NOT NULL,
        can_edit_payrolls BOOL NOT NULL
    );

CREATE TABLE staff
    (
        PRIMARY KEY (id),
        id VARCHAR(36) NOT NULL,
        address_id VARCHAR(36) NOT NULL,
        CONSTRAINT staff_address_id_addresses_id_fkey
            FOREIGN KEY (address_id) REFERENCES addresses (id) ON UPDATE CASCADE ON DELETE CASCADE,
        workroom_id VARCHAR(36) NOT NULL,
        CONSTRAINT staff_workroom_id_workrooms_id_fkey
            FOREIGN KEY (workroom_id) REFERENCES workrooms (id) ON UPDATE CASCADE ON DELETE CASCADE,
        position_id INT NOT NULL,
        CONSTRAINT staff_position_id_positions_id_fkey
            FOREIGN KEY (position_id) REFERENCES positions (id) ON UPDATE CASCADE ON DELETE CASCADE,
        first_name VARCHAR(256) NOT NULL,
        last_name VARCHAR(256) NOT NULL,
        middle_name VARCHAR(256),
        photo BLOB NOT NULL,
        passport_doc_copy BLOB NOT NULL,
        bank_number_doc_copy BLOB,
        other_doc_copy BLOB,
        updated_at TIMESTAMP,
        created_at TIMESTAMP
    );

CREATE TABLE phones
    (
        PRIMARY KEY (id),
        id VARCHAR(36) NOT NULL,
        employee_id VARCHAR(36) NOT NULL,
        CONSTRAINT phones_employee_id_staff_id_fkey
            FOREIGN KEY (employee_id) REFERENCES staff (id) ON UPDATE CASCADE ON DELETE CASCADE,
        type ENUM ('work', 'personal') NOT NULL,
        `value` VARCHAR(15) NOT NULL,
        CONSTRAINT phones_value_unique
            UNIQUE (`value`),
        CONSTRAINT phones_value_min_length_check
            CHECK (LENGTH(`value`) > 6)
    );

CREATE TABLE users
    (
        PRIMARY KEY (employee_id),
        employee_id VARCHAR(36) NOT NULL,
        CONSTRAINT users_employee_id_staff_id_fkey
            FOREIGN KEY (employee_id) REFERENCES staff (id) ON UPDATE CASCADE ON DELETE CASCADE,
        email VARCHAR(256) NOT NULL,
        CONSTRAINT users_email_unique
            UNIQUE (email),
        login VARCHAR(32) NOT NULL,
        CONSTRAINT users_login_unique
            UNIQUE (login),
        CONSTRAINT users_login_min_length_check
            CHECK (LENGTH(login) > 4),
        password VARCHAR(64) NOT NULL,
        CONSTRAINT users_password_min_length_check
            CHECK (LENGTH(password) > 8)
    );

CREATE TABLE payrolls
    (
        PRIMARY KEY (id),
        id VARCHAR(36) NOT NULL,
        employee_id VARCHAR(36) NOT NULL,
        CONSTRAINT payrolls_employee_id_staff_id_fkey
            FOREIGN KEY (employee_id) REFERENCES staff (id) ON UPDATE CASCADE ON DELETE CASCADE,
        period_type ENUM ('quarterly', 'monthly', 'weekly', 'daily') NOT NULL,
        hour_count INT NOT NULL,
        salary_whole_part INT NOT NULL,
        salary_decimal_part INT NOT NULL,
        payment_at TIMESTAMP NOT NULL,
        updated_at TIMESTAMP,
        created_at TIMESTAMP
    );

CREATE TABLE spares
    (
        PRIMARY KEY (id),
        id VARCHAR(36) NOT NULL,
        workroom_id VARCHAR(36) NOT NULL,
        CONSTRAINT spares_workroom_id_staff_id_fkey
            FOREIGN KEY (workroom_id) REFERENCES workrooms (id) ON UPDATE CASCADE,
        name VARCHAR(128) NOT NULL,
        CONSTRAINT spares_name_unique
            UNIQUE (name),
        description VARCHAR(512),
        photo BLOB,
        price_whole_part INT NOT NULL,
        price_decimal_part INT NOT NULL,
        quantity_in_stock INT NOT NULL
    );

CREATE TABLE clients
    (
        PRIMARY KEY (id),
        id VARCHAR(36) NOT NULL,
        phone VARCHAR(15) NOT NULL,
        CONSTRAINT clients_phone_unique
            UNIQUE (phone),
        email VARCHAR(256),
        CONSTRAINT clients_email_unique
            UNIQUE (email),
        first_name VARCHAR(256),
        last_name VARCHAR(256),
        middle_name VARCHAR(256),
        photo BLOB,
        updated_at TIMESTAMP,
        created_at TIMESTAMP
    );

CREATE TABLE brands
    (
        PRIMARY KEY (id),
        id INT NOT NULL AUTO_INCREMENT,
        name VARCHAR(128) NOT NULL,
        CONSTRAINT brands_name_unique
            UNIQUE (name)
    );

CREATE TABLE models
    (
        PRIMARY KEY (id),
        id INT NOT NULL AUTO_INCREMENT,
        brand_id INT NOT NULL,
        CONSTRAINT models_brand_id_brands_id_fkey
            FOREIGN KEY (brand_id) REFERENCES brands (id) ON UPDATE CASCADE,
        name VARCHAR(256) NOT NULL,
        CONSTRAINT models_name_unique
            UNIQUE (name)
    );

CREATE TABLE cars
    (
        PRIMARY KEY (id),
        id VARCHAR(36) NOT NULL,
        model_id INT NOT NULL,
        CONSTRAINT cars_model_id_models_id_fkey
            FOREIGN KEY (model_id) REFERENCES models (id) ON UPDATE CASCADE ON DELETE CASCADE,
        number VARCHAR(12) NOT NULL,
        `year` SMALLINT NOT NULL,
        engine_type ENUM ('benzine', 'diesel', 'electric', 'hybrid', 'gas', 'other') NOT NULL,
        mileage INT NOT NULL,
        color CHAR(6),
        updated_at TIMESTAMP,
        created_at TIMESTAMP
    );

CREATE TABLE car_photos
    (
        PRIMARY KEY (id),
        id VARCHAR(36) NOT NULL,
        car_id VARCHAR(36) NOT NULL,
        CONSTRAINT car_photos_car_id_cars_id_fkey
            FOREIGN KEY (car_id) REFERENCES cars (id) ON UPDATE CASCADE ON DELETE CASCADE,
        photo BLOB NOT NULL,
        description VARCHAR(256)
    );

CREATE TABLE services
    (
        PRIMARY KEY (id),
        id INT NOT NULL AUTO_INCREMENT,
        name VARCHAR(128) NOT NULL,
        description VARCHAR(512) NOT NULL,
        photo BLOB NOT NULL,
        currency_id INT NOT NULL,
        CONSTRAINT services_currency_id_addresses_id_fkey
            FOREIGN KEY (currency_id) REFERENCES currencies (id) ON UPDATE CASCADE,
        price_whole_part INT NOT NULL,
        price_decimal_part INT NOT NULL
    );

CREATE TABLE discounts
    (
        PRIMARY KEY (id),
        id INT NOT NULL AUTO_INCREMENT,
        name VARCHAR(64) NOT NULL,
        CONSTRAINT discounts_name_unique
            UNIQUE (name),
        description VARCHAR(256),
        `value` SMALLINT NOT NULL
    );

CREATE TABLE orders
    (
        PRIMARY KEY (id),
        id VARCHAR(36) NOT NULL,
        client_id VARCHAR(36) NOT NULL,
        CONSTRAINT orders_client_id_clients_id_fkey
            FOREIGN KEY (client_id) REFERENCES clients (id) ON UPDATE CASCADE,
        car_id VARCHAR(36) NOT NULL,
        CONSTRAINT orders_car_id_cars_id_fkey
            FOREIGN KEY (car_id) REFERENCES cars (id) ON UPDATE CASCADE,
        discount_id INT NULL,
        CONSTRAINT orders_discount_id_discounts_id_fkey
            FOREIGN KEY (discount_id) REFERENCES discounts (id) ON UPDATE CASCADE,
        price_whole_part INT NOT NULL,
        price_decimal_part INT NOT NULL,
        payment_type ENUM ('card', 'cash') NOT NULL,
        payment_at TIMESTAMP NOT NULL,
        updated_at TIMESTAMP,
        created_at TIMESTAMP
    );

CREATE TABLE employee_order
    (
        PRIMARY KEY (order_id, employee_id),
        order_id VARCHAR(36) NOT NULL,
        CONSTRAINT employee_order_order_id_orders_id_fkey
            FOREIGN KEY (order_id) REFERENCES orders (id) ON UPDATE CASCADE,
        employee_id VARCHAR(36) NOT NULL,
        CONSTRAINT employee_order_employee_id_staff_id_fkey
            FOREIGN KEY (employee_id) REFERENCES staff (id) ON UPDATE CASCADE
    );

CREATE TABLE spare_order
    (
        PRIMARY KEY (order_id, spare_id),
        order_id VARCHAR(36) NOT NULL,
        CONSTRAINT spare_order_order_id_orders_id_fkey
            FOREIGN KEY (order_id) REFERENCES orders (id) ON UPDATE CASCADE,
        spare_id VARCHAR(36) NOT NULL,
        CONSTRAINT spare_order_spare_id_spares_id_fkey
            FOREIGN KEY (spare_id) REFERENCES spares (id) ON UPDATE CASCADE,
        quantity INT NOT NULL
    );

CREATE TABLE service_order
    (
        PRIMARY KEY (order_id, service_id),
        order_id VARCHAR(36) NOT NULL,
        CONSTRAINT service_order_order_id_orders_id_fkey
            FOREIGN KEY (order_id) REFERENCES orders (id) ON UPDATE CASCADE,
        service_id INT NOT NULL,
        CONSTRAINT service_order_service_id_services_id_fkey
            FOREIGN KEY (service_id) REFERENCES services (id) ON UPDATE CASCADE,
        description VARCHAR(256)
    );
