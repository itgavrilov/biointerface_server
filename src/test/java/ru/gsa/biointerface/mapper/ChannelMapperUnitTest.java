package ru.gsa.biointerface.mapper;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.gsa.biointerface.domain.entity.Channel;
import ru.gsa.biointerface.dto.ChannelDTO;

class ChannelMapperUnitTest {

    private final EasyRandom generator = new EasyRandom();
    private final ChannelMapper mapper = new ChannelMapperImpl();

    @Test
    void toDTO() {
        Channel entity = generator.nextObject(Channel.class);

        ChannelDTO dto = mapper.toDTO(entity);

        Assertions.assertEquals(entity.getId().getNumber(), dto.getNumber());
        Assertions.assertEquals(entity.getId().getExaminationId(), dto.getExaminationId());
        Assertions.assertEquals(entity.getChannelName().getId(), dto.getChannelNameId());
    }
}