package ru.gsa.biointerface.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.gsa.biointerface.domain.dto.ChannelNameDTO;
import ru.gsa.biointerface.domain.entity.ChannelName;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 26/05/2022
 */
@Mapper(componentModel = "spring")
public interface ChannelNameMapper {

    ChannelNameDTO toDTO(ChannelName channelName);

    @Mapping(target = "channels", expression = "java(new java.util.ArrayList<>())")
    ChannelName toEntity(ChannelNameDTO dto);
}

