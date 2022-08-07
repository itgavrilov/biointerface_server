package ru.gsa.biointerface.mapper;

import org.mapstruct.Mapper;
import ru.gsa.biointerface.domain.entity.ChannelName;
import ru.gsa.biointerface.domain.dto.ChannelNameDTO;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 26/05/2022
 */
@Mapper(componentModel = "spring")
public interface ChannelNameMapper {

    ChannelNameDTO toDTO(ChannelName channelName);
}
