package com.arakviel.carrepair.presentation.mapper.impl;

import com.arakviel.carrepair.domain.impl.Money;
import com.arakviel.carrepair.domain.impl.Service;
import com.arakviel.carrepair.presentation.mapper.ModelMapper;
import com.arakviel.carrepair.presentation.mapper.util.PhotoImageToByteConverter;
import com.arakviel.carrepair.presentation.model.impl.MoneyModel;
import com.arakviel.carrepair.presentation.model.impl.ServiceModel;
import java.io.ByteArrayInputStream;
import java.util.Objects;
import javafx.scene.image.Image;

public class ServiceModelMapper implements ModelMapper<Service, ServiceModel> {

    @Override
    public ServiceModel toModel(Service service) {
        var currencyModelMapper = ModelMapperFactory.getInstance().getCurrencyModelMapper();
        var serviceModel = ServiceModel.builder()
                .name(service.getName())
                .description(service.getDescription())
                .photo(new Image(new ByteArrayInputStream(service.getPhoto())))
                .currency(currencyModelMapper.toModel(service.getCurrency()))
                .price(new MoneyModel(
                        service.getPrice().wholePart(), service.getPrice().decimalPart()))
                .build();
        serviceModel.setId(service.getId());
        serviceModel.setExtraFieldDescription(service.getExtraFieldDescription());
        return serviceModel;
    }

    @Override
    public Service toDomain(ServiceModel serviceModel) {
        var currencyModelMapper = ModelMapperFactory.getInstance().getCurrencyModelMapper();

        byte[] photo = null;
        if (Objects.nonNull(serviceModel.getPhoto())) {
            photo = PhotoImageToByteConverter.convert(serviceModel.getPhoto());
        }

        Service service = Service.builder()
                .name(serviceModel.getName())
                .description(serviceModel.getDescription())
                .photo(photo)
                .currency(currencyModelMapper.toDomain(serviceModel.getCurrencyModel()))
                .price(new Money(
                        serviceModel.getPrice().wholePart(),
                        serviceModel.getPrice().decimalPart()))
                .build();
        service.setId(serviceModel.getId());
        service.setExtraFieldDescription(serviceModel.getExtraFieldDescription());
        return service;
    }

    ServiceModelMapper() {}
}
