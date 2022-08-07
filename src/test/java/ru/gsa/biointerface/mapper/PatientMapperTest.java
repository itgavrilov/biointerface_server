package ru.gsa.biointerface.mapper;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import ru.gsa.biointerface.domain.entity.Patient;
import ru.gsa.biointerface.domain.dto.PatientDTO;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class PatientMapperTest {

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
}