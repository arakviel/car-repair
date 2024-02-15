package com.arakviel.carrepair.presentation.mapper.impl;

import com.arakviel.carrepair.domain.impl.Address;
import com.arakviel.carrepair.presentation.mapper.ModelMapper;
import com.arakviel.carrepair.presentation.model.impl.AddressModel;

public class AddressModelMapper implements ModelMapper<Address, AddressModel> {

    @Override
    public AddressModel toModel(Address address) {
        AddressModel addressModel = AddressModel.builder()
                .country(address.getCountry())
                .region(address.getRegion())
                .city(address.getCity())
                .street(address.getStreet())
                .home(address.getHome())
                .build();
        addressModel.setId(address.getId());
        return addressModel;
    }

    @Override
    public Address toDomain(AddressModel addressModel) {
        Address address = Address.builder()
                .country(addressModel.getCountry())
                .region(addressModel.getRegion())
                .city(addressModel.getCity())
                .street(addressModel.getStreet())
                .home(addressModel.getHome())
                .build();
        address.setId(addressModel.getId());
        return address;
    }

    AddressModelMapper() {}
}
