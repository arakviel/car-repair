package com.arakviel.carrepair.domain.mapper;

public interface DomainMapper<E, D> {
    D toDomain(E entity);

    E toEntity(D domain);
}
