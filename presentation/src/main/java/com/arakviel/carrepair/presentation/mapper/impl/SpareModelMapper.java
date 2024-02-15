package com.arakviel.carrepair.presentation.mapper.impl;

import com.arakviel.carrepair.domain.impl.Money;
import com.arakviel.carrepair.domain.impl.Spare;
import com.arakviel.carrepair.presentation.mapper.ModelMapper;
import com.arakviel.carrepair.presentation.mapper.util.PhotoImageToByteConverter;
import com.arakviel.carrepair.presentation.model.impl.MoneyModel;
import com.arakviel.carrepair.presentation.model.impl.SpareModel;
import java.io.ByteArrayInputStream;
import java.util.Objects;
import javafx.scene.image.Image;

public class SpareModelMapper implements ModelMapper<Spare, SpareModel> {

    @Override
    public SpareModel toModel(Spare spare) {
        var workroomModelMapper = ModelMapperFactory.getInstance().getWorkroomModelMapper();
        SpareModel spareModel = SpareModel.builder()
                .workroom(workroomModelMapper.toModel(spare.getWorkroom()))
                .name(spare.getName())
                .price(new MoneyModel(
                        spare.getPrice().wholePart(), spare.getPrice().decimalPart()))
                .quantityInStock(spare.getQuantityInStock())
                .build();
        spareModel.setId(spare.getId());
        spareModel.setDescription(spare.getDescription());
        if (Objects.nonNull(spare.getPhoto())) {
            spareModel.setPhoto(new Image(new ByteArrayInputStream(spare.getPhoto())));
        }
        spareModel.setExtraFieldQuantity(spare.getExtraFieldQuantity());
        return spareModel;
    }

    @Override
    public Spare toDomain(SpareModel spareModel) {
        var workroomModelMapper = ModelMapperFactory.getInstance().getWorkroomModelMapper();
        Spare spare = Spare.builder()
                .workroom(workroomModelMapper.toDomain(spareModel.getWorkroomModel()))
                .name(spareModel.getName())
                .price(new Money(
                        spareModel.getPrice().wholePart(), spareModel.getPrice().decimalPart()))
                .quantityInStock(spareModel.getQuantityInStock())
                .build();
        spare.setId(spareModel.getId());
        spare.setDescription(spareModel.getDescription());
        if (Objects.nonNull(spareModel.getPhoto())) {
            spare.setPhoto(PhotoImageToByteConverter.convert(spareModel.getPhoto()));
        }
        spare.setExtraFieldQuantity(spare.getExtraFieldQuantity());
        return spare;
    }

    SpareModelMapper() {}
}
