package ru.gsa.biointerface.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import ru.gsa.biointerface.domain.dto.examination.ExaminationDTO;
import ru.gsa.biointerface.domain.dto.examination.ExaminationUpdateDTO;
import ru.gsa.biointerface.domain.entity.Examination;

import java.util.UUID;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 26/05/2022
 */
@Mapper(componentModel = "spring")
public interface ExaminationMapper {

    @Mappings({
            @Mapping(target = "patientId", source = "examination.patientId"),
            @Mapping(target = "deviceId", source = "examination.deviceId")
    })
    ExaminationDTO toDTO(Examination examination);

    @Mappings({
            @Mapping(target = "datetime", ignore = true),
            @Mapping(target = "creationDate", ignore = true),
            @Mapping(target = "modifyDate", ignore = true),
            @Mapping(target = "channels", expression = "java(new java.util.ArrayList<>())")
    })
    Examination toEntity(ExaminationUpdateDTO dto, UUID id);

    @Mappings({
            @Mapping(target = "creationDate", source = "dto.creationDate"),
            @Mapping(target = "modifyDate", source = "dto.modifyDate"),
            @Mapping(target = "channels", expression = "java(new java.util.ArrayList<>())")
    })
    Examination toEntity(ExaminationDTO dto);
}
