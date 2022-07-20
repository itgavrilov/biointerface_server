package ru.gsa.biointerface.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.gsa.biointerface.domain.entity.Patient;
import ru.gsa.biointerface.dto.PatientDTO;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 26/05/2022
 */
@Mapper(componentModel = "spring")
public interface PatientMapper {

    @Mapping(target = "icdId", source = "patient.icd.id")
    PatientDTO toDTO(Patient patient);
}
