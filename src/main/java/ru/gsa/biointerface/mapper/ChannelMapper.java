package ru.gsa.biointerface.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import ru.gsa.biointerface.domain.dto.ChannelDTO;
import ru.gsa.biointerface.domain.entity.Channel;
import ru.gsa.biointerface.domain.entity.ChannelName;
import ru.gsa.biointerface.domain.entity.Examination;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 26/05/2022
 */
@Mapper(componentModel = "spring")
public interface ChannelMapper {

    @Mappings({
            @Mapping(target = "number", source = "channel.id.number"),
            @Mapping(target = "examinationId", source = "channel.id.examinationId"),
            @Mapping(target = "channelNameId", source = "channel.channelName.id")
    })
    ChannelDTO toDTO(Channel channel);

    @Mapping(target = "id",
            expression = "java(new ru.gsa.biointerface.domain.entity.ChannelID(examination.getId(), dto.getNumber()))")
    @Mapping(target = "samples", expression = "java(new java.util.ArrayList<>())")
    Channel toEntity(ChannelDTO dto, Examination examination, ChannelName channelName);
}

