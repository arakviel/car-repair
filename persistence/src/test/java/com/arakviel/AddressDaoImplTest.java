package com.arakviel;

import static org.junit.jupiter.api.Assertions.*;

import com.arakviel.carrepair.persistence.DaoFactory;
import com.arakviel.carrepair.persistence.dao.AddressDao;
import com.arakviel.carrepair.persistence.entity.impl.AddressEntity;
import com.arakviel.carrepair.persistence.exception.persistance.PersistenceException;
import com.arakviel.carrepair.persistence.util.ConnectionManager;
import com.arakviel.init.H2PersistenceInitialization;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

class AddressDaoImplTest {
    static MockedStatic<ConnectionManager> connectionManager = Mockito.mockStatic(ConnectionManager.class);
    AddressDao addressDao;

    static {
        loadDriver();
        getConnection();
    }

    private static void loadDriver() {
        try {
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static Connection getConnection() {
        try {
            // INIT=RUNSCRIPT FROM 'classpath:init.sql' - можна навіть таке використовувати
            return DriverManager.getConnection("jdbc:h2:file:./src/test/resources/db/test;DB_CLOSE_ON_EXIT=FALSE");
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @BeforeAll
    static void init() {
        H2PersistenceInitialization.run(getConnection());
    }

    @BeforeEach
    void setUp() {
        connectionManager.when(ConnectionManager::get).thenReturn(getConnection());
        addressDao = DaoFactory.getInstance().getAddressDao();
    }

    @Test
    void findOneById_AddressEntityExists_ReturnsAddressEntity() {
        // Given
        UUID addressId = UUID.fromString("926b1ede-0427-4daa-a603-22fcd489d559");
        AddressEntity expectedAddressEntity = AddressEntity.builder()
                .id(addressId)
                .country("Україна")
                .region("Вінницька область")
                .city("Вінниця")
                .street("вул. Соборна")
                .home("25")
                .build();

        // When
        AddressEntity actualAddressEntity = addressDao.findOneById(addressId).orElse(null);

        // Then
        assertNotNull(actualAddressEntity, "The found AddressEntity object is not null");
        assertEquals(expectedAddressEntity, actualAddressEntity, "The searched object is equal to the found one");
    }

    @Test
    void findOneById_AddressEntityDoesNotExist_ReturnsAddressEntity() {
        // Given
        UUID id = UUID.randomUUID();

        // When
        Optional<AddressEntity> optionalAddressEntity = addressDao.findOneById(id);

        // Then
        assertTrue(optionalAddressEntity.isEmpty(), "Empty optional if the address with this id does not exist");
    }

    @Test
    @Tag("slow")
    void findAll_ReturnsListOfAddressEntity() {
        // Given
        H2PersistenceInitialization.run(getConnection());
        int addressEntitiesSize = 26;

        // When
        List<AddressEntity> addressEntities = addressDao.findAll();

        // Then
        assertNotNull(addressEntities);
        assertEquals(addressEntitiesSize, addressEntities.size());
    }

    @Test
    void save_InsertNewAddressEntity_ReturnsAddressEntityWithGeneratedId() {
        // Given
        AddressEntity addressEntity = AddressEntity.builder()
                .id(null)
                .country("Україна")
                .region("Закарпаття")
                .city("Мукачево")
                .street("вул. Головна")
                .home("1")
                .build();

        // When
        AddressEntity savedAddressEntity = addressDao.save(addressEntity);
        UUID id = savedAddressEntity.getId();
        Optional<AddressEntity> optionalFoundedAddressEntity = addressDao.findOneById(id, getConnection());

        // Then
        assertNotNull(savedAddressEntity.getId());
        assertTrue(optionalFoundedAddressEntity.isPresent());
        assertEquals(savedAddressEntity, optionalFoundedAddressEntity.orElse(null));
    }

    @Test
    void save_UpdateExistAddressEntity_ReturnsAddressEntity() {
        // Given
        UUID addressId = UUID.fromString("926b1ede-0427-4daa-a603-22fcd489d559");
        AddressEntity addressEntityToUpdate = AddressEntity.builder()
                .id(addressId)
                .country("Україна")
                .region("Закарпатська область")
                .city("Мукачево")
                .street("вул. Головна")
                .home("1")
                .build();

        // When
        addressDao.save(addressEntityToUpdate);
        AddressEntity updatedAddressEntity =
                addressDao.findOneById(addressId, getConnection()).orElse(null);

        // Then
        assertEquals(addressEntityToUpdate, updatedAddressEntity);
    }

    @Test
    void save_UpdateNotExistAddressEntity_DoNothing() {
        // Given
        UUID addressId = UUID.randomUUID();
        AddressEntity notExistAddressEntityToUpdate = AddressEntity.builder()
                .id(addressId)
                .country("Україна")
                .region("Закарпатська область")
                .city("Мукачево")
                .street("вул. Головна")
                .home("1")
                .build();

        // When
        addressDao.save(notExistAddressEntityToUpdate);
        Optional<AddressEntity> optionalAddressEntity = addressDao.findOneById(addressId, getConnection());

        // Then
        assertTrue(optionalAddressEntity.isEmpty(), "The updated address object is not found in the database");
    }

    @Test
    void remove_AddressEntityExistsWithConstraint_ThrowPersistenceException() {
        // Given
        UUID addressId = UUID.fromString("926b1ede-0427-4daa-a603-22fcd489d559");

        // When
        PersistenceException persistenceException =
                assertThrows(PersistenceException.class, () -> addressDao.remove(addressId));

        // Then
        assertTrue(persistenceException.getMessage().contains("constraint"), "Exception after constraint error");
    }

    @Test
    void remove_AddressEntityExists_ReturnsVoid() {
        // Given
        UUID addressId = UUID.fromString("9750dda0-3bf2-4846-836f-af3a31e0648b");

        // When
        addressDao.remove(addressId);
        Optional<AddressEntity> optionalAddressEntity = addressDao.findOneById(addressId, getConnection());

        // Then
        assertTrue(optionalAddressEntity.isEmpty(), "After removing by id, nothing was found");
    }

    @Test
    @Disabled("Немає сенсу тести?")
    void remove_AddressEntityDoesNotExist_ReturnsVoid() {
        // Given
        UUID addressId = UUID.randomUUID();

        // When
        addressDao.remove(addressId);

        // Then
    }

    @AfterAll
    static void tearDown() {
        // це не працює
        ConnectionManager.closePool();
    }
}
