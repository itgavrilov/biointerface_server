package ru.gsa.biointerface.unit.mapper;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import ru.gsa.biointerface.domain.dto.device.DeviceDTO;
import ru.gsa.biointerface.domain.dto.device.DeviceUpdateDTO;
import ru.gsa.biointerface.domain.entity.Device;
import ru.gsa.biointerface.mapper.DeviceMapper;
import ru.gsa.biointerface.mapper.DeviceMapperImpl;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Tag("UnitTest")
class DeviceMapperTest {

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
    void toEntityFormDeviceUpdateDTO() {
        DeviceUpdateDTO dto = generator.nextObject(DeviceUpdateDTO.class);
        UUID id = generator.nextObject(UUID.class);

        Device entity = mapper.toEntity(dto, id);

        assertNotNull(entity);
        assertNotNull(entity.getId());
        assertEquals(id, entity.getId());
        assertNotNull(entity.getComment());
        assertEquals(dto.getComment(), entity.getComment());
    }

    @Test
    void toEntityFormDeviceDTO() {
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