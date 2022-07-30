package ru.gsa.biointerface.mapper;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import ru.gsa.biointerface.domain.entity.Channel;
import ru.gsa.biointerface.dto.ChannelDTO;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ChannelMapperUnitTest {

    private final EasyRandom generator = new EasyRandom();
    private final ChannelMapper mapper = new ChannelMapperImpl();

    @Test
    void toDTO() {
        Channel entity = generator.nextObject(Channel.class);

        ChannelDTO dto = mapper.toDTO(entity);

        assertNotNull(dto);
        assertNotNull(dto.getNumber());
        assertEquals(entity.getId().getNumber(), dto.getNumber());
        assertNotNull(dto.getExaminationId());
        assertEquals(entity.getId().getExaminationId(), dto.getExaminationId());
        assertNotNull(dto.getChannelNameId());
        assertEquals(entity.getChannelName().getId(), dto.getChannelNameId());
    }
}