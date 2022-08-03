package ru.gsa.biointerface.unit.mapper;

import org.mapstruct.Mapper;
import ru.gsa.biointerface.domain.entity.Icd;
import ru.gsa.biointerface.dto.IcdDTO;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 26/05/2022
 */
@Mapper(componentModel = "spring")
public interface IcdMapper {

    IcdDTO toDTO(Icd icd);
}
