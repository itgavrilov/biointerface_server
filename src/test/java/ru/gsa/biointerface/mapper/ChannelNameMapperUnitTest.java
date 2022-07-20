package ru.gsa.biointerface.mapper;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.gsa.biointerface.domain.entity.ChannelName;
import ru.gsa.biointerface.dto.ChannelNameDTO;

class ChannelNameMapperUnitTest {

    private final EasyRandom generator = new EasyRandom();
    private final ChannelNameMapper mapper = new ChannelNameMapperImpl();

    @Test
    void toDTO() {
        ChannelName entity = generator.nextObject(ChannelName.class);

        ChannelNameDTO dto = mapper.toDTO(entity);

        Assertions.assertEquals(entity.getId(), dto.getId());
        Assertions.assertEquals(entity.getName(), dto.getName());
        Assertions.assertEquals(entity.getComment(), dto.getComment());
    }
}