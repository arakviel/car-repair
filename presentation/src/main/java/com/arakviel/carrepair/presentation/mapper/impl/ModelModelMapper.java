package com.arakviel.carrepair.presentation.mapper.impl;

import com.arakviel.carrepair.domain.impl.Model;
import com.arakviel.carrepair.presentation.mapper.ModelMapper;
import com.arakviel.carrepair.presentation.model.impl.ModelModel;

public class ModelModelMapper implements ModelMapper<Model, ModelModel> {

    @Override
    public ModelModel toModel(Model model) {
        var brandModelMapper = ModelMapperFactory.getInstance().getBrandModelMapper();
        ModelModel modelModel = ModelModel.builder()
                .brandModel(brandModelMapper.toModel(model.getBrand()))
                .name(model.getName())
                .build();
        modelModel.setId(model.getId());
        return modelModel;
    }

    @Override
    public Model toDomain(ModelModel modelModel) {
        var brandModelMapper = ModelMapperFactory.getInstance().getBrandModelMapper();
        Model model = Model.builder()
                .brand(brandModelMapper.toDomain(modelModel.getBrandModel()))
                .name(modelModel.getName())
                .build();
        model.setId(modelModel.getId());
        return model;
    }
}
