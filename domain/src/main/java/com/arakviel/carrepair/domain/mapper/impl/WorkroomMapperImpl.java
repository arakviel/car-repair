package com.arakviel.carrepair.domain.mapper.impl;

import com.arakviel.carrepair.domain.impl.Workroom;
import com.arakviel.carrepair.domain.mapper.MapperFactory;
import com.arakviel.carrepair.domain.mapper.WorkroomMapper;
import com.arakviel.carrepair.persistence.entity.impl.WorkroomEntity;

public class WorkroomMapperImpl implements WorkroomMapper {

    @Override
    public Workroom toDomain(WorkroomEntity entity) {
        var workroom = Workroom.builder()
                .address(MapperFactory.getInstance().getAddressMapper().toDomain(entity.getAddressEntity()))
                .name(entity.getName())
                .photo(entity.getPhoto())
                .build();
        workroom.setId(entity.getId());
        workroom.setDescription(entity.getDescription());
        return workroom;
    }

    @Override
    public WorkroomEntity toEntity(Workroom domain) {
        var workroom = WorkroomEntity.builder()
                .id(domain.getId())
                .addressEntity(MapperFactory.getInstance().getAddressMapper().toEntity(domain.getAddress()))
                .name(domain.getName())
                .photo(domain.getPhoto())
                .build();
        workroom.setDescription(domain.getDescription());
        return workroom;
    }

    private WorkroomMapperImpl() {}

    private static class SingletonHolder {
        public static final WorkroomMapperImpl INSTANCE = new WorkroomMapperImpl();
    }

    public static WorkroomMapperImpl getInstance() {
        return SingletonHolder.INSTANCE;
    }
}
