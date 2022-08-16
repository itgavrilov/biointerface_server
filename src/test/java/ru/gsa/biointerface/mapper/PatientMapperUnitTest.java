package ru.gsa.biointerface.mapper;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import ru.gsa.biointerface.domain.dto.patient.PatientDTO;
import ru.gsa.biointerface.domain.dto.patient.PatientSaveOrUpdateDTO;
import ru.gsa.biointerface.domain.entity.Icd;
import ru.gsa.biointerface.domain.entity.Patient;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class PatientMapperUnitTest {

    private final EasyRandom generator = new EasyRandom();
    private final PatientMapper mapper = new PatientMapperImpl();

    @Test
    void toDTO() {
        Patient entity = generator.nextObject(Patient.class);
        PatientDTO dto = mapper.toDTO(entity);

        assertNotNull(dto);
        assertNotNull(dto.getId());
        assertEquals(entity.getId(), dto.getId());
        assertNotNull(dto.getFirstName());
        assertEquals(entity.getFirstName(), dto.getFirstName());
        assertNotNull(dto.getSecondName());
        assertEquals(entity.getSecondName(), dto.getSecondName());
        assertNotNull(dto.getPatronymic());
        assertEquals(entity.getPatronymic(), dto.getPatronymic());
        assertNotNull(dto.getBirthday());
        assertEquals(entity.getBirthday(), dto.getBirthday());
        assertNotNull(dto.getIcdId());
        assertEquals(entity.getIcd().getId(), dto.getIcdId());
        assertNotNull(dto.getComment());
        assertEquals(entity.getComment(), dto.getComment());
    }

    @Test
    void toEntityFormIcdSaveOrUpdateDTO() {
        PatientSaveOrUpdateDTO dto = generator.nextObject(PatientSaveOrUpdateDTO.class);
        Icd icd = generator.nextObject(Icd.class);
        icd.setId(dto.getIcdId());
        UUID id = generator.nextObject(UUID.class);

        Patient entity = mapper.toEntity(dto, id, icd);

        assertNotNull(entity);
        assertNotNull(entity.getId());
        assertEquals(id, entity.getId());
        assertNotNull(entity.getFirstName());
        assertEquals(entity.getFirstName(), entity.getFirstName());
        assertNotNull(entity.getSecondName());
        assertEquals(dto.getSecondName(), entity.getSecondName());
        assertNotNull(entity.getPatronymic());
        assertEquals(dto.getPatronymic(), entity.getPatronymic());
        assertNotNull(entity.getBirthday());
        assertEquals(dto.getBirthday(), entity.getBirthday());
        assertNotNull(entity.getIcd());
        assertEquals(icd, entity.getIcd());
        assertNotNull(entity.getComment());
        assertEquals(dto.getComment(), entity.getComment());
    }

    @Test
    void toEntityFormIcdDTO() {
        PatientDTO dto = generator.nextObject(PatientDTO.class);
        Icd icd = generator.nextObject(Icd.class);
        icd.setId(dto.getIcdId());
        Patient entity = mapper.toEntity(dto, icd);

        assertNotNull(entity);
        assertNotNull(entity.getId());
        assertEquals(dto.getId(), entity.getId());
        assertNotNull(entity.getFirstName());
        assertEquals(entity.getFirstName(), entity.getFirstName());
        assertNotNull(entity.getSecondName());
        assertEquals(dto.getSecondName(), entity.getSecondName());
        assertNotNull(entity.getPatronymic());
        assertEquals(dto.getPatronymic(), entity.getPatronymic());
        assertNotNull(entity.getBirthday());
        assertEquals(dto.getBirthday(), entity.getBirthday());
        assertNotNull(entity.getIcd());
        assertEquals(icd, entity.getIcd());
        assertNotNull(entity.getComment());
        assertEquals(dto.getComment(), entity.getComment());
    }
}