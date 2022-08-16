package ru.gsa.biointerface.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import ru.gsa.biointerface.domain.dto.icd.IcdDTO;
import ru.gsa.biointerface.domain.dto.icd.IcdSaveOrUpdateDTO;
import ru.gsa.biointerface.domain.entity.Icd;

import java.util.UUID;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 26/05/2022
 */
@Mapper(componentModel = "spring")
public interface IcdMapper {

    IcdDTO toDTO(Icd icd);

    @Mappings({
            @Mapping(target = "creationDate", ignore = true),
            @Mapping(target = "modifyDate", ignore = true)
    })
    Icd toEntity(IcdSaveOrUpdateDTO dto, UUID id);

    Icd toEntity(IcdDTO dto);
}
