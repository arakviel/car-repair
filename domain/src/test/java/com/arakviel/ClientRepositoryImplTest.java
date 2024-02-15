package com.arakviel;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.arakviel.carrepair.domain.exception.DomainNotFoundException;
import com.arakviel.carrepair.domain.impl.Client;
import com.arakviel.carrepair.domain.mapper.impl.ClientMapperImpl;
import com.arakviel.carrepair.domain.repository.ClientRepository;
import com.arakviel.carrepair.domain.repository.impl.ClientRepositoryImpl;
import com.arakviel.carrepair.persistence.dao.ClientDao;
import com.arakviel.carrepair.persistence.entity.impl.ClientEntity;
import com.arakviel.carrepair.persistence.filter.impl.ClientFilterDto;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.IntStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;

class ClientRepositoryImplTest {

    private ClientDao clientDao = mock(ClientDao.class);
    private ClientRepository spyClientRepository = spy(ClientRepositoryImpl.class);
    private ClientEntity invalidClientEntity =
            ClientEntity.builder().id(UUID.randomUUID()).phone("").email("").build();

    @BeforeEach
    void init() {
        setMock(clientDao);
    }

    private void setMock(ClientDao clientDao) {
        try {
            Field clientDaoField = ClientRepositoryImpl.class.getDeclaredField("clientDao");
            clientDaoField.setAccessible(true);
            clientDaoField.set(this.spyClientRepository, clientDao);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void getAll_ReturnsListOfClient() {
        // Given
        doReturn(IntStream.range(0, 3).mapToObj(i -> invalidClientEntity).toList())
                .when(this.clientDao)
                .findAll();

        // When
        List<Client> clients = this.spyClientRepository.getAll();

        // Then
        verify(this.clientDao).findAll();
        assertNotNull(clients);
        assertEquals(3L, clients.size());
    }

    @Test
    void getAllWhere_WithFilter_ReturnsFilteredListOfClient() {
        // Given
        String phone = "0661122333";
        String firstName = "Микола";
        doReturn(IntStream.range(0, 3)
                        .mapToObj(i -> {
                            ClientEntity clientEntity = ClientEntity.builder()
                                    .id(UUID.randomUUID())
                                    .phone(phone)
                                    .email("")
                                    .build();
                            clientEntity.setFirstName(firstName);
                            return clientEntity;
                        })
                        .toList())
                .when(this.clientDao)
                .findAll(ArgumentMatchers.any(ClientFilterDto.class));

        // When
        List<Client> clients = this.spyClientRepository.getAllWhere(phone, null, firstName, null, null, null, null);

        // Then
        verify(this.clientDao)
                .findAll(ArgumentMatchers.argThat(cf -> Objects.equals(cf.phone(), phone)
                        && Objects.isNull(cf.email())
                        && Objects.equals(cf.firstName(), firstName)
                        && Objects.isNull(cf.lastName())
                        && Objects.isNull(cf.middleName())
                        && Objects.isNull(cf.updatedAt())
                        && Objects.isNull(cf.createdAt())));
        assertNotNull(clients);
        assertEquals(3L, clients.size());
    }

    @Test
    void get_ClientEntityExists_ReturnClient() {
        // Given
        var id = UUID.randomUUID();
        invalidClientEntity.setId(id);
        doReturn(Optional.of(invalidClientEntity)).when(this.clientDao).findOneById(ArgumentMatchers.any(UUID.class));

        // When
        Client client = this.spyClientRepository.get(id);

        // Then
        verify(this.clientDao).findOneById(ArgumentMatchers.eq(id));
        assertNotNull(client);
    }

    @Test
    void get_ClientEntityDoesNotExist_ReturnClient() {
        // Given
        var id = UUID.randomUUID();
        var expectedExceptionMessage = "";
        doReturn(Optional.empty()).when(this.clientDao).findOneById(ArgumentMatchers.any(UUID.class));
        // When
        DomainNotFoundException domainNotFoundException =
                assertThrows(DomainNotFoundException.class, () -> this.spyClientRepository.get(id));

        // Then
        verify(this.clientDao).findOneById(ArgumentMatchers.eq(id));
        assertEquals("Не вдалось знайти клієнта по ідентифікатору", domainNotFoundException.getMessage());
    }

    @Test
    void add_ReturnsClientWithId() {
        // Given
        Client client =
                Client.builder().phone("0601122333").email("ivan@gmail.com").build();
        client.setFirstName("Іван");
        client.setLastName("Шевченко");
        LocalDateTime now = LocalDateTime.now();
        client.setUpdatedAt(now);
        client.setCreatedAt(now);
        ClientEntity clientEntity = getValidClientEntity(now);
        doReturn(clientEntity).when(this.clientDao).save(ArgumentMatchers.any(ClientEntity.class));

        // When
        assertDoesNotThrow(() -> this.spyClientRepository.add(client));

        // Then
        verify(this.clientDao).save(ArgumentMatchers.eq(clientEntity));
    }

    @Test
    void set_ClientExists_ReturnVoid() {
        // Given
        var id = UUID.randomUUID();
        var clientEntity = getValidClientEntity();
        clientEntity.setId(id);
        Client client = ClientMapperImpl.getInstance().toDomain(clientEntity);
        doReturn(client).when(this.spyClientRepository).get(ArgumentMatchers.any(UUID.class));

        // When
        assertDoesNotThrow(() -> this.spyClientRepository.set(id, client));

        // Then
        verify(this.spyClientRepository).get(ArgumentMatchers.eq(id));
        verify(this.clientDao).save(ArgumentMatchers.eq(clientEntity));
    }

    @Test
    void set_ClientDoesNotExist_ThrowsException() {
        // Given
        var id = UUID.randomUUID();
        doThrow(DomainNotFoundException.class).when(this.spyClientRepository).get(ArgumentMatchers.any(UUID.class));

        // When
        assertThrows(DomainNotFoundException.class, () -> this.spyClientRepository.set(id, getValidClient()));

        // Then
        verify(this.spyClientRepository).get(ArgumentMatchers.eq(id));
        verify(this.clientDao, never()).save(ArgumentMatchers.any());
    }

    @Test
    void delete_ClientExists_ReturnVoid() {
        // Given
        var id = UUID.randomUUID();
        Client validClient = getValidClient();
        validClient.setId(id);
        doReturn(validClient).when(this.spyClientRepository).get(ArgumentMatchers.any(UUID.class));
        doNothing().when(this.clientDao).remove(ArgumentMatchers.any(UUID.class));

        // When
        this.spyClientRepository.remove(id);

        // Then
        verify(this.spyClientRepository).get(ArgumentMatchers.eq(id));
        verify(this.clientDao).remove(ArgumentMatchers.eq(id));
    }

    @Test
    void delete_ClientDoesNotExist_ReturnVoid() {
        // Given
        var id = UUID.randomUUID();
        doThrow(new DomainNotFoundException()).when(this.spyClientRepository).get(ArgumentMatchers.any(UUID.class));

        // When
        assertThrows(DomainNotFoundException.class, () -> this.spyClientRepository.remove(id));

        // Then
        verify(this.spyClientRepository).get(ArgumentMatchers.eq(id));
        verify(this.clientDao, never()).remove(ArgumentMatchers.eq(id));
    }

    private static ClientEntity getValidClientEntity(LocalDateTime now) {
        ClientEntity clientEntity = ClientEntity.builder()
                .id(null)
                .phone("0601122333")
                .email("ivan@gmail.com")
                .build();
        clientEntity.setFirstName("Іван");
        clientEntity.setLastName("Шевченко");
        clientEntity.setUpdatedAt(now);
        clientEntity.setCreatedAt(now);
        return clientEntity;
    }

    private static ClientEntity getValidClientEntity() {
        LocalDateTime now = LocalDateTime.now();
        ClientEntity clientEntity = ClientEntity.builder()
                .id(null)
                .phone("0601122333")
                .email("ivan@gmail.com")
                .build();
        clientEntity.setFirstName("Іван");
        clientEntity.setLastName("Шевченко");
        clientEntity.setUpdatedAt(now);
        clientEntity.setCreatedAt(now);
        return clientEntity;
    }

    private static Client getValidClient() {
        return ClientMapperImpl.getInstance().toDomain(getValidClientEntity());
    }
}
