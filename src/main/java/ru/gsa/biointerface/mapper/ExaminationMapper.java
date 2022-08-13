package ru.gsa.biointerface.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import ru.gsa.biointerface.domain.dto.examination.ExaminationDTO;
import ru.gsa.biointerface.domain.entity.Device;
import ru.gsa.biointerface.domain.entity.Examination;
import ru.gsa.biointerface.domain.entity.Patient;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 26/05/2022
 */
@Mapper(componentModel = "spring")
public interface ExaminationMapper {

    @Mappings({
            @Mapping(target = "patientId", source = "examination.patient.id"),
            @Mapping(target = "deviceId", source = "examination.device.id")
    })
    ExaminationDTO toDTO(Examination examination);

    @Mappings({
            @Mapping(target = "id", source = "dto.id"),
            @Mapping(target = "comment", source = "dto.comment"),
            @Mapping(target = "creationDate", source = "dto.creationDate"),
            @Mapping(target = "modifyDate", source = "dto.modifyDate"),
            @Mapping(target = "channels", expression = "java(new java.util.ArrayList<>())")
    })
    Examination toEntity(ExaminationDTO dto, Patient patient, Device device);
}
