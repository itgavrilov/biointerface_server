package ru.gsa.biointerface.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import ru.gsa.biointerface.domain.dto.patient.PatientDTO;
import ru.gsa.biointerface.domain.dto.patient.PatientSaveOrUpdateDTO;
import ru.gsa.biointerface.domain.entity.Icd;
import ru.gsa.biointerface.domain.entity.Patient;

import java.util.UUID;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 26/05/2022
 */
@Mapper(componentModel = "spring")
public interface PatientMapper {

    @Mapping(target = "icdId", source = "patient.icd.id")
    PatientDTO toDTO(Patient patient);

    @Mappings({
            @Mapping(target = "id", source = "id"),
            @Mapping(target = "birthday", expression = "java(dto.getBirthday())"),
            @Mapping(target = "comment", source = "dto.comment"),
            @Mapping(target = "icd", source = "icd"),
            @Mapping(target = "creationDate", ignore = true),
            @Mapping(target = "modifyDate", ignore = true)
    })
    Patient toEntity(PatientSaveOrUpdateDTO dto, UUID id, Icd icd);

    @Mappings({
            @Mapping(target = "id", source = "dto.id"),
            @Mapping(target = "birthday", expression = "java(dto.getBirthday())"),
            @Mapping(target = "comment", source = "dto.comment"),
            @Mapping(target = "icd", source = "icd"),
            @Mapping(target = "creationDate", source = "dto.creationDate"),
            @Mapping(target = "modifyDate", source = "dto.modifyDate")
    })
    Patient toEntity(PatientDTO dto, Icd icd);
}
