package ru.gsa.biointerface.mapper;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import ru.gsa.biointerface.domain.dto.DeviceDTO;
import ru.gsa.biointerface.domain.entity.Device;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class DeviceMapperUnitTest {

    private final EasyRandom generator = new EasyRandom();
    private final DeviceMapper mapper = new DeviceMapperImpl();

    @Test
    void toDTO() {
        Device entity = generator.nextObject(Device.class);

        DeviceDTO dto = mapper.toDTO(entity);

        assertNotNull(dto);
        assertNotNull(dto.getId());
        assertEquals(entity.getId(), dto.getId());
        assertNotNull(dto.getAmountChannels());
        assertEquals(entity.getAmountChannels(), dto.getAmountChannels());
        assertNotNull(dto.getComment());
        assertEquals(entity.getComment(), dto.getComment());
    }

    @Test
    void toEntity() {
        DeviceDTO dto = generator.nextObject(DeviceDTO.class);

        Device entity = mapper.toEntity(dto);

        assertNotNull(entity);
        assertNotNull(entity.getId());
        assertEquals(dto.getId(), entity.getId());
        assertNotNull(entity.getAmountChannels());
        assertEquals(dto.getAmountChannels(), entity.getAmountChannels());
        assertNotNull(entity.getComment());
        assertEquals(dto.getComment(), entity.getComment());
    }
}