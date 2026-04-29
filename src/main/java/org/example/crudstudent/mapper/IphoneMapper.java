package org.example.crudstudent.mapper;

import org.example.crudstudent.dto.IphoneRequest;
import org.example.crudstudent.dto.IphoneResponse;
import org.example.crudstudent.entity.Iphone;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface IphoneMapper {
    IphoneResponse toDto(Iphone iphone);

    Iphone toEntity(IphoneRequest request);

    void update(IphoneRequest request, @MappingTarget Iphone iphone);
}