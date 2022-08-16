package ru.gsa.biointerface.mapper;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import ru.gsa.biointerface.domain.dto.channelName.ChannelNameDTO;
import ru.gsa.biointerface.domain.dto.channelName.ChannelNameSaveOrUpdateDTO;
import ru.gsa.biointerface.domain.entity.ChannelName;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ChannelNameMapperUnitTest {

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

    @Test
    void toEntityFromChannelNameSaveOrUpdateDTO() {
        ChannelNameSaveOrUpdateDTO dto = generator.nextObject(ChannelNameSaveOrUpdateDTO.class);
        UUID id = generator.nextObject(UUID.class);

        ChannelName entity = mapper.toEntity(dto, id);

        assertNotNull(entity);
        assertNotNull(entity.getId());
        assertEquals(id, entity.getId());
        assertNotNull(entity.getName());
        assertEquals(dto.getName(), entity.getName());
        assertNotNull(entity.getComment());
        assertEquals(dto.getComment(), entity.getComment());
    }


    @Test
    void toEntityFromChannelNameDTO() {
        ChannelNameDTO dto = generator.nextObject(ChannelNameDTO.class);

        ChannelName entity = mapper.toEntity(dto);

        assertNotNull(entity);
        assertNotNull(entity.getId());
        assertEquals(dto.getId(), entity.getId());
        assertNotNull(entity.getName());
        assertEquals(dto.getName(), entity.getName());
        assertNotNull(entity.getComment());
        assertEquals(dto.getComment(), entity.getComment());
    }
}