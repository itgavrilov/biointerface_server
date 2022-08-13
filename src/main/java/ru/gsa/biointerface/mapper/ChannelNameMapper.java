package ru.gsa.biointerface.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import ru.gsa.biointerface.domain.dto.channelName.ChannelNameDTO;
import ru.gsa.biointerface.domain.dto.channelName.ChannelNameSaveOrUpdateDTO;
import ru.gsa.biointerface.domain.entity.ChannelName;

import java.util.UUID;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 26/05/2022
 */
@Mapper(componentModel = "spring")
public interface ChannelNameMapper {

    ChannelNameDTO toDTO(ChannelName channelName);

    @Mappings({
            @Mapping(target = "creationDate", ignore = true),
            @Mapping(target = "modifyDate", ignore = true)
    })
    ChannelName toEntity(ChannelNameSaveOrUpdateDTO dto, UUID id);

    ChannelName toEntity(ChannelNameDTO dto);
}

