package com.arakviel.carrepair.presentation.mapper.impl;

import com.arakviel.carrepair.domain.impl.Client;
import com.arakviel.carrepair.presentation.mapper.ModelMapper;
import com.arakviel.carrepair.presentation.mapper.util.PhotoImageToByteConverter;
import com.arakviel.carrepair.presentation.model.impl.ClientModel;
import java.io.ByteArrayInputStream;
import java.util.Objects;
import javafx.scene.image.Image;

public class ClientModelMapper implements ModelMapper<Client, ClientModel> {

    @Override
    public ClientModel toModel(Client client) {
        var clientModel = ClientModel.builder()
                .phone(client.getPhone())
                .email(client.getEmail())
                .build();
        clientModel.setId(client.getId());
        clientModel.setFirstName(client.getFirstName());
        clientModel.setLastName(client.getLastName());
        clientModel.setMiddleName(client.getMiddleName());
        if (Objects.nonNull(client.getPhoto())) {
            Image photo = new Image(new ByteArrayInputStream(client.getPhoto()));
            clientModel.setPhoto(photo);
        }
        clientModel.setUpdatedAt(client.getUpdatedAt());
        clientModel.setCreatedAt(client.getCreatedAt());
        clientModel.getFullName();

        return clientModel;
    }

    @Override
    public Client toDomain(ClientModel clientModel) {
        var client = Client.builder()
                .phone(clientModel.getPhone())
                .email(clientModel.getEmail())
                .build();
        client.setId(clientModel.getId());
        client.setFirstName(clientModel.getFirstName());
        client.setLastName(clientModel.getLastName());
        client.setMiddleName(clientModel.getMiddleName());
        if (Objects.nonNull(clientModel.getPhoto())) {
            client.setPhoto(PhotoImageToByteConverter.convert(clientModel.getPhoto()));
        }
        client.setUpdatedAt(clientModel.getUpdatedAt());
        client.setCreatedAt(clientModel.getCreatedAt());
        return client;
    }

    ClientModelMapper() {}
}
