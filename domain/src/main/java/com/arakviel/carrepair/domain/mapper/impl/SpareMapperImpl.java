package com.arakviel.carrepair.domain.mapper.impl;

import com.arakviel.carrepair.domain.impl.Money;
import com.arakviel.carrepair.domain.impl.Spare;
import com.arakviel.carrepair.domain.mapper.MapperFactory;
import com.arakviel.carrepair.domain.mapper.SpareMapper;
import com.arakviel.carrepair.persistence.entity.impl.SpareEntity;

public class SpareMapperImpl implements SpareMapper {

    @Override
    public Spare toDomain(SpareEntity entity) {
        var spare = Spare.builder()
                .workroom(MapperFactory.getInstance().getWorkroomMapper().toDomain(entity.getWorkroomEntity()))
                .name(entity.getName())
                .price(new Money(
                        entity.getPrice().wholePart(), entity.getPrice().decimalPart()))
                .quantityInStock(entity.getQuantityInStock())
                .build();
        spare.setId(entity.getId());
        spare.setPhoto(entity.getPhoto());
        spare.setDescription(entity.getDescription());
        spare.setExtraFieldQuantity(entity.getExtraFieldQuantity());
        return spare;
    }

    @Override
    public SpareEntity toEntity(Spare domain) {
        var spareEntity = SpareEntity.builder()
                .id(domain.getId())
                .workroomEntity(MapperFactory.getInstance().getWorkroomMapper().toEntity(domain.getWorkroom()))
                .name(domain.getName())
                .price(new com.arakviel.carrepair.persistence.entity.impl.Money(
                        domain.getPrice().wholePart(), domain.getPrice().decimalPart()))
                .quantityInStock(domain.getQuantityInStock())
                .build();
        spareEntity.setPhoto(domain.getPhoto());
        spareEntity.setDescription(domain.getDescription());
        spareEntity.setExtraFieldQuantity(domain.getExtraFieldQuantity());
        return spareEntity;
    }

    private SpareMapperImpl() {}

    private static class SingletonHolder {
        public static final SpareMapperImpl INSTANCE = new SpareMapperImpl();
    }

    public static SpareMapperImpl getInstance() {
        return SingletonHolder.INSTANCE;
    }
}
