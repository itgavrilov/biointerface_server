package ru.gsa.biointerface.mapper;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.gsa.biointerface.domain.entity.Device;
import ru.gsa.biointerface.dto.DeviceDTO;

class DeviceMapperUnitTest {

    private final EasyRandom generator = new EasyRandom();
    private final DeviceMapper mapper = new DeviceMapperImpl();

    @Test
    void toDTO() {
        Device entity = generator.nextObject(Device.class);

        DeviceDTO dto = mapper.toDTO(entity);

        Assertions.assertEquals(entity.getId(), dto.getId());
        Assertions.assertEquals(entity.getAmountChannels(), dto.getAmountChannels());
        Assertions.assertEquals(entity.getComment(), dto.getComment());
    }
}