package ru.gsa.biointerface.mapper;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import ru.gsa.biointerface.domain.dto.examination.ExaminationDTO;
import ru.gsa.biointerface.domain.dto.examination.ExaminationUpdateDTO;
import ru.gsa.biointerface.domain.entity.Examination;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ExaminationMapperUnitTest {

    private final EasyRandom generator = new EasyRandom();
    private final ExaminationMapper mapper = new ExaminationMapperImpl();

    @Test
    void toDTO() {
        Examination entity = generator.nextObject(Examination.class);

        ExaminationDTO dto = mapper.toDTO(entity);

        assertNotNull(dto);
        assertNotNull(dto.getId());
        assertEquals(entity.getId(), dto.getId());
        assertNotNull(dto.getDeviceId());
        assertEquals(entity.getDeviceId(), dto.getDeviceId());
        assertNotNull(dto.getPatientId());
        assertEquals(entity.getPatientId(), dto.getPatientId());
        assertNotNull(dto.getDatetime());
        assertEquals(entity.getDatetime(), dto.getDatetime());
        assertNotNull(dto.getComment());
        assertEquals(entity.getComment(), dto.getComment());
    }

    @Test
    void toEntityFromExaminationUpdateDTO() {
        ExaminationUpdateDTO dto = generator.nextObject(ExaminationUpdateDTO.class);
        UUID id = generator.nextObject(UUID.class);

        Examination entity = mapper.toEntity(dto, id);

        assertNotNull(entity);
        assertNotNull(entity.getId());
        assertEquals(id, entity.getId());
        assertNotNull(entity.getComment());
        assertEquals(dto.getComment(), entity.getComment());
        assertNotNull(entity.getPatientId());
        assertEquals(dto.getPatientId(), entity.getPatientId());
        assertNotNull(entity.getDeviceId());
        assertEquals(dto.getDeviceId(), entity.getDeviceId());
        assertNotNull(entity.getChannels());
    }

    @Test
    void toEntityFromExaminationDTO() {
        ExaminationDTO dto = generator.nextObject(ExaminationDTO.class);

        Examination entity = mapper.toEntity(dto);

        assertNotNull(entity);
        assertNotNull(entity.getId());
        assertEquals(dto.getId(), entity.getId());
        assertNotNull(entity.getComment());
        assertEquals(dto.getComment(), entity.getComment());
        assertNotNull(entity.getPatientId());
        assertEquals(dto.getPatientId(), entity.getPatientId());
        assertNotNull(entity.getDeviceId());
        assertEquals(dto.getDeviceId(), entity.getDeviceId());
        assertNotNull(entity.getChannels());
    }
}