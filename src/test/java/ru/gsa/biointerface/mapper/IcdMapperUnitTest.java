package ru.gsa.biointerface.mapper;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import ru.gsa.biointerface.domain.entity.Icd;
import ru.gsa.biointerface.dto.IcdDTO;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class IcdMapperUnitTest {

    private final EasyRandom generator = new EasyRandom();
    private final IcdMapper mapper = new IcdMapperImpl();

    @Test
    void toDTO() {
        Icd entity = generator.nextObject(Icd.class);

        IcdDTO dto = mapper.toDTO(entity);

        assertNotNull(dto);
        assertNotNull(dto.getId());
        assertEquals(entity.getId(), dto.getId());
        assertNotNull(dto.getName());
        assertEquals(entity.getName(), dto.getName());
        assertNotNull(dto.getVersion());
        assertEquals(entity.getVersion(), dto.getVersion());
        assertNotNull(dto.getComment());
        assertEquals(entity.getComment(), dto.getComment());
    }
}