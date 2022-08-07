package ru.gsa.biointerface.mapper;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import ru.gsa.biointerface.domain.entity.ChannelName;
import ru.gsa.biointerface.domain.dto.ChannelNameDTO;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ChannelNameMapperTest {

    private final EasyRandom generator = new EasyRandom();
    private final ChannelNameMapper mapper = new ChannelNameMapperImpl();

    @Test
    void toDTO() {
        ChannelName entity = generator.nextObject(ChannelName.class);

        ChannelNameDTO dto = mapper.toDTO(entity);

        assertNotNull(dto);
        assertNotNull(dto.getId());
        assertEquals(entity.getId(), dto.getId());
        assertNotNull(dto.getName());
        assertEquals(entity.getName(), dto.getName());
        assertNotNull(dto.getComment());
        assertEquals(entity.getComment(), dto.getComment());
    }
}