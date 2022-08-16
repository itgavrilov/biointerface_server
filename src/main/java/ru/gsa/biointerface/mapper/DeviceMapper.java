package ru.gsa.biointerface.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import ru.gsa.biointerface.domain.dto.device.DeviceDTO;
import ru.gsa.biointerface.domain.dto.device.DeviceUpdateDTO;
import ru.gsa.biointerface.domain.entity.Device;

import java.util.UUID;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 26/05/2022
 */
@Mapper(componentModel = "spring")
public interface DeviceMapper {

    DeviceDTO toDTO(Device device);

    @Mappings({
            @Mapping(target = "number", ignore = true),
            @Mapping(target = "amountChannels", ignore = true),
            @Mapping(target = "creationDate", ignore = true),
            @Mapping(target = "modifyDate", ignore = true)
    })
    Device toEntity(DeviceUpdateDTO dto, UUID id);

    Device toEntity(DeviceDTO dto);
}
