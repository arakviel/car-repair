package com.arakviel.carrepair.domain.validator.address;

import com.arakviel.carrepair.domain.impl.Address;
import com.arakviel.carrepair.domain.validator.AddressValidator;
import com.arakviel.carrepair.domain.validator.ValidatorChain;

public class AddressValidatorChain extends ValidatorChain {

    AddressValidator firstValidator;

    @Override
    protected void validateChain() {
        firstValidator = new CountryAddressValidator(validationMessages);
        firstValidator
                .setNext(new RegionAddressValidator(validationMessages))
                .setNext(new CityAddressValidator(validationMessages))
                .setNext(new StreetAddressValidator(validationMessages))
                .setNext(new HomeAddressValidator(validationMessages));
    }

    public boolean validate(Address address) {
        validationMessages.clear();
        firstValidator.validate(address);
        return validationMessages.size() == 0;
    }

    private static class SingletonHolder {
        public static final AddressValidatorChain INSTANCE = new AddressValidatorChain();
    }

    public static AddressValidatorChain getInstance() {
        return SingletonHolder.INSTANCE;
    }
}
