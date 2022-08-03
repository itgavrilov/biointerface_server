package ru.gsa.biointerface.unit.mapper;

import org.mapstruct.Mapper;
import ru.gsa.biointerface.domain.entity.Device;
import ru.gsa.biointerface.dto.DeviceDTO;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 26/05/2022
 */
@Mapper(componentModel = "spring")
public interface DeviceMapper {

    DeviceDTO toDTO(Device device);
}
