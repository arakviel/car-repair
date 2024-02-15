package com.arakviel.carrepair.presentation.mapper.impl;

import com.arakviel.carrepair.domain.impl.Workroom;
import com.arakviel.carrepair.presentation.mapper.ModelMapper;
import com.arakviel.carrepair.presentation.mapper.util.PhotoImageToByteConverter;
import com.arakviel.carrepair.presentation.model.impl.WorkroomModel;
import java.io.ByteArrayInputStream;
import java.util.Objects;
import javafx.scene.image.Image;

public class WorkroomModelMapper implements ModelMapper<Workroom, WorkroomModel> {

    @Override
    public WorkroomModel toModel(Workroom workroom) {
        var addressModelMapper = ModelMapperFactory.getInstance().getAddressModelMapper();
        Image photo = new Image(new ByteArrayInputStream(workroom.getPhoto()));
        WorkroomModel workroomModel = WorkroomModel.builder()
                .address(addressModelMapper.toModel(workroom.getAddress()))
                .name(workroom.getName())
                .photo(photo)
                .build();
        workroomModel.setId(workroom.getId());
        workroomModel.setDescription(workroom.getDescription());
        return workroomModel;
    }

    @Override
    public Workroom toDomain(WorkroomModel workroomModel) {
        var addressModelMapper = ModelMapperFactory.getInstance().getAddressModelMapper();
        byte[] photo = null;
        if (Objects.nonNull(workroomModel.getPhoto())) {
            photo = PhotoImageToByteConverter.convert(workroomModel.getPhoto());
        }
        Workroom workroom = Workroom.builder()
                .address(addressModelMapper.toDomain(workroomModel.getAddress()))
                .name(workroomModel.getName())
                .photo(photo)
                .build();
        workroom.setId(workroomModel.getId());
        workroom.setDescription(workroomModel.getDescription());
        return workroom;
    }
}
