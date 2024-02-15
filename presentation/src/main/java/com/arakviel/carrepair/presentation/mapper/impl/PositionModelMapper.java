package com.arakviel.carrepair.presentation.mapper.impl;

import com.arakviel.carrepair.domain.impl.Money;
import com.arakviel.carrepair.domain.impl.Position;
import com.arakviel.carrepair.presentation.mapper.ModelMapper;
import com.arakviel.carrepair.presentation.model.impl.MoneyModel;
import com.arakviel.carrepair.presentation.model.impl.PositionModel;
import java.util.Objects;

public class PositionModelMapper implements ModelMapper<Position, PositionModel> {

    @Override
    public PositionModel toModel(Position position) {
        var currencyModelMapper = ModelMapperFactory.getInstance().getCurrencyModelMapper();
        var roleModelMapper = ModelMapperFactory.getInstance().getRoleModelMapper();
        PositionModel positionModel = PositionModel.builder()
                .name(position.getName())
                .currencyModel(currencyModelMapper.toModel(position.getCurrency()))
                .salaryPerHour(new MoneyModel(
                        position.getSalaryPerHour().wholePart(),
                        position.getSalaryPerHour().decimalPart()))
                .build();
        positionModel.setId(position.getId());
        positionModel.setDescription(position.getDescription());
        if (Objects.nonNull(position.getRole())) {
            positionModel.setRoleModel(roleModelMapper.toModel(position.getRole()));
        }
        return positionModel;
    }

    @Override
    public Position toDomain(PositionModel positionModel) {
        var currencyModelMapper = ModelMapperFactory.getInstance().getCurrencyModelMapper();
        var roleModelMapper = ModelMapperFactory.getInstance().getRoleModelMapper();
        Position position = Position.builder()
                .name(positionModel.getName())
                .currency(currencyModelMapper.toDomain(positionModel.getCurrencyModel()))
                .salaryPerHour(new Money(
                        positionModel.getSalaryPerHour().wholePart(),
                        positionModel.getSalaryPerHour().decimalPart()))
                .build();
        position.setId(positionModel.getId());
        position.setDescription(positionModel.getDescription());
        if (Objects.nonNull(positionModel.getRoleModel())) {
            position.setRole(roleModelMapper.toDomain(positionModel.getRoleModel()));
        }
        return position;
    }

    PositionModelMapper() {}
}
