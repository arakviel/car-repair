package com.arakviel.carrepair.persistence.dao;

import com.arakviel.carrepair.persistence.entity.impl.CarPhotoEntity;
import com.arakviel.carrepair.persistence.entity.impl.WorkroomEntity;
import com.arakviel.carrepair.persistence.filter.impl.CarPhotoFilterDto;
import java.util.List;
import java.util.UUID;

public interface CarPhotoDao extends GenericDao<UUID, CarPhotoEntity, CarPhotoFilterDto> {

    List<CarPhotoEntity> findAllByWorkroomEntity(WorkroomEntity workroomEntity);

    List<CarPhotoEntity> findAllByWorkroomEntity(CarPhotoFilterDto filter, WorkroomEntity workroomEntity);
}
