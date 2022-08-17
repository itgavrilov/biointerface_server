package ru.gsa.biointerface.unit.mapper;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import ru.gsa.biointerface.domain.dto.icd.IcdDTO;
import ru.gsa.biointerface.domain.dto.icd.IcdSaveOrUpdateDTO;
import ru.gsa.biointerface.domain.entity.Icd;
import ru.gsa.biointerface.mapper.IcdMapper;
import ru.gsa.biointerface.mapper.IcdMapperImpl;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Tag("UnitTest")
class IcdMapperTest {

    private final EasyRandom generator = new EasyRandom();
    private final IcdMapper mapper = new IcdMapperImpl();

    @Test
    void toIcdSaveOrUpdateDTO() {
        Icd entity = generator.nextObject(Icd.class);

        IcdSaveOrUpdateDTO dto = mapper.toIcdSaveOrUpdateDTO(entity);

        assertNotNull(dto);
        assertNotNull(dto.getName());
        assertEquals(entity.getName(), dto.getName());
        assertNotNull(dto.getVersion());
        assertEquals(entity.getVersion(), dto.getVersion());
        assertNotNull(dto.getComment());
        assertEquals(entity.getComment(), dto.getComment());
    }

    @Test
    void toIcdDTO() {
        Icd entity = generator.nextObject(Icd.class);

        IcdDTO dto = mapper.toIcdDTO(entity);

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