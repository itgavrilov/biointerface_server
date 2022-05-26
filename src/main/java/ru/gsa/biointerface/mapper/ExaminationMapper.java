package ru.gsa.biointerface.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.gsa.biointerface.domain.Channel;
import ru.gsa.biointerface.domain.Device;
import ru.gsa.biointerface.domain.Examination;
import ru.gsa.biointerface.domain.Patient;
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

    Examination toEntity(ExaminationDTO examinationDTO, Patient patient, Device device, List<Channel> channels);
}
