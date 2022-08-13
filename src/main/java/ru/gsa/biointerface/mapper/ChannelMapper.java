package ru.gsa.biointerface.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import ru.gsa.biointerface.domain.dto.сhannel.ChannelDTO;
import ru.gsa.biointerface.domain.dto.сhannel.ChannelUpdateDTO;
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


    @Mappings({
            @Mapping(target = "id",
                    expression = "java(new ru.gsa.biointerface.domain.entity.ChannelID(examination.getId(), number))"),
            @Mapping(target = "comment", source = "dto.comment"),
            @Mapping(target = "samples", expression = "java(new java.util.ArrayList<>())"),
            @Mapping(target = "creationDate", ignore = true),
            @Mapping(target = "modifyDate", ignore = true)
    })
    Channel toEntity(ChannelUpdateDTO dto, Byte number, Examination examination, ChannelName channelName);

    @Mappings({
            @Mapping(target = "id",
                    expression = "java(new ru.gsa.biointerface.domain.entity.ChannelID(dto.getExaminationId(), dto.getNumber()))"),
            @Mapping(target = "comment", source = "dto.comment"),
            @Mapping(target = "creationDate", source = "dto.creationDate"),
            @Mapping(target = "modifyDate", source = "dto.modifyDate"),
            @Mapping(target = "samples", expression = "java(new java.util.ArrayList<>())")
    })
    Channel toEntity(ChannelDTO dto, Examination examination, ChannelName channelName);
}

