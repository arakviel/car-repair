package com.arakviel.carrepair.presentation.mapper;

public interface ModelMapper<D, M> {
    M toModel(D domain);

    D toDomain(M model);
}
