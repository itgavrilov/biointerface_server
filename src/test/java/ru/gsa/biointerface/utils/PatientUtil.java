package ru.gsa.biointerface.utils;

import lombok.SneakyThrows;
import org.jeasy.random.EasyRandom;
import ru.gsa.biointerface.domain.entity.Icd;
import ru.gsa.biointerface.domain.entity.Patient;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Thread.sleep;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class PatientUtil {

    private static final EasyRandom generator = new EasyRandom();

    @SneakyThrows
    public static Patient getPatient(Icd icd, int minusDays) {
        Patient entity = generator.nextObject(Patient.class);
        entity.setBirthday(LocalDate.now().minusDays(minusDays));
        entity.setIcd(icd);
        sleep(10);

        return entity;
    }

    public static List<Patient> getPatients(Icd icd, int count) {
        List<Patient> entities = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            entities.add(getPatient(icd, 10));
        }

        return entities;
    }

    public static void assertEqualsPatient(Patient entity, Patient test) {
        assertEqualsPatientLight(entity, test);
        assertEquals(entity.getId(), test.getId());
    }

    public static void assertEqualsPatientLight(Patient entity, Patient test) {
        assertNotNull(entity);
        assertNotNull(test);
        assertEquals(entity.getFirstName(), test.getFirstName());
        assertEquals(entity.getSecondName(), test.getSecondName());
        assertEquals(entity.getPatronymic(), test.getPatronymic());
        assertEquals(entity.getBirthday(), test.getBirthday());
        assertEquals(entity.getIcd(), test.getIcd());
        assertEquals(entity.getComment(), test.getComment());
    }
}
