package ru.gsa.biointerface.utils;

import lombok.SneakyThrows;
import org.jeasy.random.EasyRandom;
import ru.gsa.biointerface.domain.entity.Device;
import ru.gsa.biointerface.domain.entity.Examination;
import ru.gsa.biointerface.domain.entity.Patient;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static java.lang.Thread.sleep;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static ru.gsa.biointerface.utils.DeviceUtil.getDevice;
import static ru.gsa.biointerface.utils.PatientUtil.getPatient;

public class ExaminationUtil {

    private static final EasyRandom generator = new EasyRandom();

    public static Examination getExamination() {
        Patient patient = getPatient(null, 10);
        Device device = getDevice(8);

        return getExamination(patient, device);
    }

    @SneakyThrows
    public static Examination getExamination(Patient patient, Device device) {
        Examination entity = generator.nextObject(Examination.class);
        entity.setDatetime(LocalDateTime.now());
        entity.setDeviceId(device.getId());
        entity.setPatientId(patient.getId());
        sleep(10);

        return entity;
    }

    public static List<Examination> getExaminations(int count) {
        return getExaminations(UUID.randomUUID(), UUID.randomUUID(), count);
    }

    public static List<Examination> getExaminations(UUID patientId, UUID deviceId, int count) {
        List<Examination> entities = generator.objects(Examination.class, count).toList();

        entities.forEach(e -> {
            e.setDeviceId(deviceId);
            e.setPatientId(patientId);
            e.setChannels(new ArrayList<>());
        });

        return entities;
    }

    public static void assertEqualsExamination(Examination entity, Examination test) {
        assertEqualsExaminationLight(entity, test);
        assertEquals(entity.getId(), test.getId());
        assertEquals(entity.getDatetime(), test.getDatetime());
        assertEquals(entity.getDeviceId(), test.getDeviceId());
        assertIterableEquals(entity.getChannels(), test.getChannels());
    }

    public static void assertEqualsExaminationLight(Examination entity, Examination test) {
        assertNotNull(entity);
        assertNotNull(test);
        assertEquals(entity.getPatientId(), test.getPatientId());
        assertEquals(entity.getComment(), test.getComment());
    }
}
