package com.ptit.datn.service.mapper;

import java.util.List;

public interface EntityMapper <D, E>{
    D toDTO(E entity);
    E toEntity(D dto);

    List<D> toDTO(List<E> entitiesList);
    List<E> toEntity(List<D> dtosList);
}
