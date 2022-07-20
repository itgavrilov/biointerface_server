package ru.gsa.biointerface.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.gsa.biointerface.domain.entity.Channel;
import ru.gsa.biointerface.domain.entity.Device;
import ru.gsa.biointerface.domain.entity.Examination;
import ru.gsa.biointerface.domain.entity.Patient;
import ru.gsa.biointerface.dto.ExaminationDTO;

import java.util.List;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 26/05/2022
 */
@Mapper(componentModel = "spring")
public interface ExaminationMapper {

    @Mapping(target = "patientId", source = "examination.patient.id")
    @Mapping(target = "deviceId", source = "examination.device.id")
    ExaminationDTO toDTO(Examination examination);

    @Mapping(target = "id", source = "examinationDTO.id")
    @Mapping(target = "comment", source = "examinationDTO.comment")
    Examination toEntity(ExaminationDTO examinationDTO, Patient patient, Device device, List<Channel> channels);
}
