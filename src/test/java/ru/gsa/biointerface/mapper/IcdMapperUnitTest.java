package ru.gsa.biointerface.mapper;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import ru.gsa.biointerface.domain.dto.icd.IcdDTO;
import ru.gsa.biointerface.domain.dto.icd.IcdSaveOrUpdateDTO;
import ru.gsa.biointerface.domain.entity.Icd;

import java.util.UUID;

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

    @Test
    void toEntityFormIcdSaveOrUpdateDTO() {
        IcdSaveOrUpdateDTO dto = generator.nextObject(IcdSaveOrUpdateDTO.class);
        UUID id = generator.nextObject(UUID.class);

        Icd entity = mapper.toEntity(dto, id);

        assertNotNull(entity);
        assertNotNull(entity.getId());
        assertEquals(id, entity.getId());
        assertNotNull(entity.getName());
        assertEquals(dto.getName(), entity.getName());
        assertNotNull(entity.getVersion());
        assertEquals(dto.getVersion(), entity.getVersion());
        assertNotNull(entity.getComment());
        assertEquals(dto.getComment(), entity.getComment());
    }

    @Test
    void toEntityFormIcdDTO() {
        IcdDTO dto = generator.nextObject(IcdDTO.class);

        Icd entity = mapper.toEntity(dto);

        assertNotNull(entity);
        assertNotNull(entity.getId());
        assertEquals(dto.getId(), entity.getId());
        assertNotNull(entity.getName());
        assertEquals(dto.getName(), entity.getName());
        assertNotNull(entity.getVersion());
        assertEquals(dto.getVersion(), entity.getVersion());
        assertNotNull(entity.getComment());
        assertEquals(dto.getComment(), entity.getComment());
    }
}