package com.arakviel.carrepair.persistence.filter.impl;

import com.arakviel.carrepair.persistence.entity.impl.CarEntity;
import com.arakviel.carrepair.persistence.filter.FilterDto;

public record CarPhotoFilterDto(CarEntity carEntity) implements FilterDto {}
