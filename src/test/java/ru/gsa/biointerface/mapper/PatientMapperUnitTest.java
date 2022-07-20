package ru.gsa.biointerface.mapper;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.gsa.biointerface.domain.entity.Patient;
import ru.gsa.biointerface.dto.PatientDTO;

class PatientMapperUnitTest {

    private final EasyRandom generator = new EasyRandom();
    private final PatientMapper mapper = new PatientMapperImpl();

    @Test
    void toDTO() {
        Patient entity = generator.nextObject(Patient.class);

        PatientDTO dto = mapper.toDTO(entity);

        Assertions.assertEquals(entity.getId(), dto.getId());
        Assertions.assertEquals(entity.getFirstName(), dto.getFirstName());
        Assertions.assertEquals(entity.getSecondName(), dto.getSecondName());
        Assertions.assertEquals(entity.getPatronymic(), dto.getPatronymic());
        Assertions.assertEquals(entity.getBirthday(), dto.getBirthday());
        Assertions.assertEquals(entity.getIcd().getId(), dto.getIcdId());
        Assertions.assertEquals(entity.getComment(), dto.getComment());
    }
}