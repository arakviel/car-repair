package com.arakviel.carrepair.domain.mapper.impl;

import com.arakviel.carrepair.domain.impl.Client;
import com.arakviel.carrepair.domain.mapper.ClientMapper;
import com.arakviel.carrepair.persistence.entity.impl.ClientEntity;

public class ClientMapperImpl implements ClientMapper {

    @Override
    public Client toDomain(ClientEntity entity) {
        var client = Client.builder()
                .phone(entity.getPhone())
                .email(entity.getEmail())
                .build();
        client.setId(entity.getId());
        client.setFirstName(entity.getFirstName());
        client.setLastName(entity.getLastName());
        client.setMiddleName(entity.getMiddleName());
        client.setPhoto(entity.getPhoto());
        client.setUpdatedAt(entity.getUpdatedAt());
        client.setCreatedAt(entity.getCreatedAt());
        return client;
    }

    @Override
    public ClientEntity toEntity(Client domain) {
        var clientEntity = ClientEntity.builder()
                .id(domain.getId())
                .phone(domain.getPhone())
                .email(domain.getEmail())
                .build();
        clientEntity.setFirstName(domain.getFirstName());
        clientEntity.setLastName(domain.getLastName());
        clientEntity.setMiddleName(domain.getMiddleName());
        clientEntity.setPhoto(domain.getPhoto());
        clientEntity.setUpdatedAt(domain.getUpdatedAt());
        clientEntity.setCreatedAt(domain.getCreatedAt());
        return clientEntity;
    }

    private ClientMapperImpl() {}

    private static class SingletonHolder {
        public static final ClientMapperImpl INSTANCE = new ClientMapperImpl();
    }

    public static ClientMapperImpl getInstance() {
        return SingletonHolder.INSTANCE;
    }
}
