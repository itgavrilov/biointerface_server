package ru.gsa.biointerface.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.gsa.biointerface.domain.Channel;
import ru.gsa.biointerface.domain.ChannelName;
import ru.gsa.biointerface.domain.Examination;
import ru.gsa.biointerface.domain.Sample;
import ru.gsa.biointerface.dto.ChannelDTO;

import java.util.List;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 26/05/2022
 */
@Mapper(componentModel = "spring")
public interface ChannelMapper {

    @Mapping(target = "examinationId", source = "channel.examination.id")
    @Mapping(target = "channelNameId", source = "channel.channelName.id")
    ChannelDTO toDTO(Channel channel);

    Channel toEntity(ChannelDTO channelDTO, Examination examination, ChannelName channelName, List<Sample> samples);
}

