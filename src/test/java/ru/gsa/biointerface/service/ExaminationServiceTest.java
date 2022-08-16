package ru.gsa.biointerface.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import ru.gsa.biointerface.TestUtils;
import ru.gsa.biointerface.domain.entity.Device;
import ru.gsa.biointerface.domain.entity.Examination;
import ru.gsa.biointerface.domain.entity.Patient;
import ru.gsa.biointerface.exception.NotFoundException;
import ru.gsa.biointerface.repository.DeviceRepository;
import ru.gsa.biointerface.repository.ExaminationRepository;
import ru.gsa.biointerface.repository.PatientRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
class ExaminationServiceTest {

    @Autowired
    private ExaminationService service;
    @Autowired
    private ExaminationRepository repository;
    @Autowired
    private PatientRepository patientRepository;
    @Autowired
    private DeviceRepository deviceRepository;

    @AfterEach
    void tearDown() {
        repository.deleteAll(repository.findAll());
        patientRepository.deleteAll(patientRepository.findAll());
        deviceRepository.deleteAll(deviceRepository.findAll());
    }

    @Test
    void findAll() {
        Patient patient = getPatientFromDB();
        Device device = getDeviceFromDB();
        List<Examination> entities = getNewEntityListFromDB(patient, device, 5);

        List<Examination> entityTests = service.findAll(null, null);

        assertNotNull(entityTests);

        entityTests.forEach(entityTest -> {
            Examination entity = entities.stream()
                    .filter(e -> e.getId().equals(entityTest.getId()))
                    .findAny().orElseThrow();
            assertEqualsEntity(entity, entityTest);
        });
    }

    @Test
    void findAll_empty() {
        List<Examination> entityTests = service.findAll(null, null);

        assertNotNull(entityTests);

        assertIterableEquals(new ArrayList<>(), entityTests);
    }

    @Test
    void findAll_byPatient() {
        Patient patient = getPatientFromDB();
        Device device = getDeviceFromDB();
        List<Examination> entities = getNewEntityListFromDB(patient, device, 5);

        List<Examination> entityTests = service.findAll(patient.getId(), null);

        assertNotNull(entityTests);

        entityTests.forEach(entityTest -> {
            Examination entity = entities.stream()
                    .filter(e -> e.getId().equals(entityTest.getId()))
                    .findAny().orElseThrow();
            assertEqualsEntity(entity, entityTest);
        });
    }

    @Test
    void findAll_byDevice() {
        Patient patient = getPatientFromDB();
        Device device = getDeviceFromDB();
        List<Examination> entities = getNewEntityListFromDB(patient, device, 5);

        List<Examination> entityTests = service.findAll(null, device.getId());

        assertNotNull(entityTests);

        entityTests.forEach(entityTest -> {
            Examination entity = entities.stream()
                    .filter(e -> e.getId().equals(entityTest.getId()))
                    .findAny().orElseThrow();
            assertEqualsEntity(entity, entityTest);
        });
    }

    @Test
    void findAll_byPatientAndDevice() {
        Patient patient = getPatientFromDB();
        Device device = getDeviceFromDB();
        List<Examination> entities = getNewEntityListFromDB(patient, device, 5);

        List<Examination> entityTests = service.findAll(patient.getId(), device.getId());

        assertNotNull(entityTests);

        entityTests.forEach(entityTest -> {
            Examination entity = entities.stream()
                    .filter(e -> e.getId().equals(entityTest.getId()))
                    .findAny().orElseThrow();
            assertEqualsEntity(entity, entityTest);
        });
    }

    @Test
    void findAllPageable() {
        List<Examination> entities = getNewEntityListFromDB(getPatientFromDB(), getDeviceFromDB(), 15);
        Pageable pageable = PageRequest.of(0, 5);

        while (pageable.getPageNumber() * pageable.getPageSize() <= entities.size()) {
            Page<Examination> entityPageTests = service.findAll(null, null, pageable);

            assertNotNull(entityPageTests);

            entityPageTests.getContent().forEach(entityTest -> {
                assertNotNull(entityTest);
                Examination entity = entities.stream()
                        .filter(e -> e.getId().equals(entityTest.getId()))
                        .findAny().orElseThrow();
                assertEqualsEntity(entity, entityTest);
            });

            pageable = PageRequest.of(pageable.getPageNumber() + 1, pageable.getPageSize());
        }
    }

    @Test
    void findAllPageable_empty() {
        Pageable pageable = PageRequest.of(0, 5);
        Page<Examination> entityPage = new PageImpl<>(new ArrayList<>(), pageable, 0);

        Page<Examination> entityPageTests = service.findAll(null, null, pageable);
        assertNotNull(entityPageTests);
        assertEquals(entityPage, entityPageTests);
    }

    @Test
    void findAllPageable_byPatient() {
        Patient patient = getPatientFromDB();
        List<Examination> entities = getNewEntityListFromDB(patient, getDeviceFromDB(), 15);
        Pageable pageable = PageRequest.of(0, 5);

        while (pageable.getPageNumber() * pageable.getPageSize() <= entities.size()) {
            Page<Examination> entityPageTests = service.findAll(patient.getId(), null, pageable);

            assertNotNull(entityPageTests);

            entityPageTests.getContent().forEach(entityTest -> {
                assertNotNull(entityTest);
                Examination entity = entities.stream()
                        .filter(e -> e.getId().equals(entityTest.getId()))
                        .findAny().orElseThrow();
                assertEqualsEntity(entity, entityTest);
            });

            pageable = PageRequest.of(pageable.getPageNumber() + 1, pageable.getPageSize());
        }
    }

    @Test
    void findAllPageable_byDevice() {
        Device device = getDeviceFromDB();
        List<Examination> entities = getNewEntityListFromDB(getPatientFromDB(), device, 15);
        Pageable pageable = PageRequest.of(0, 5);

        while (pageable.getPageNumber() * pageable.getPageSize() <= entities.size()) {
            Page<Examination> entityPageTests = service.findAll(null, device.getId(), pageable);

            assertNotNull(entityPageTests);

            entityPageTests.getContent().forEach(entityTest -> {
                assertNotNull(entityTest);
                Examination entity = entities.stream()
                        .filter(e -> e.getId().equals(entityTest.getId()))
                        .findAny().orElseThrow();
                assertEqualsEntity(entity, entityTest);
            });

            pageable = PageRequest.of(pageable.getPageNumber() + 1, pageable.getPageSize());
        }
    }

    @Test
    void findAllPageable_byPatientAndDevice() {
        Patient patient = getPatientFromDB();
        Device device = getDeviceFromDB();
        List<Examination> entities = getNewEntityListFromDB(patient, device, 15);
        Pageable pageable = PageRequest.of(0, 5);

        while (pageable.getPageNumber() * pageable.getPageSize() <= entities.size()) {
            Page<Examination> entityPageTests = service.findAll(patient.getId(), device.getId(), pageable);

            assertNotNull(entityPageTests);

            entityPageTests.getContent().forEach(entityTest -> {
                assertNotNull(entityTest);
                Examination entity = entities.stream()
                        .filter(e -> e.getId().equals(entityTest.getId()))
                        .findAny().orElseThrow();
                assertEqualsEntity(entity, entityTest);
            });

            pageable = PageRequest.of(pageable.getPageNumber() + 1, pageable.getPageSize());
        }
    }

    @Test
    void getById() {
        Examination entity = getNewEntityFromDB(getPatientFromDB(), getDeviceFromDB());
        Examination entityClone = entity.toBuilder().build();

        Examination entityTest = service.getById(entityClone.getId());

        assertEqualsEntity(entityClone, entityTest);
    }

    @Test
    void getById_rnd() {
        UUID rndId = UUID.randomUUID();
        String message = String.format(repository.MASK_NOT_FOUND, rndId);

        assertThrows(NotFoundException.class, () -> service.getById(rndId), message);
    }

    @Test
    void update() {
        Examination entity = getNewEntityFromDB(getPatientFromDB(), getDeviceFromDB());
        Examination entityClone = entity.toBuilder().build();

        Examination entityForTest = TestUtils.getNewExamination(getPatientFromDB(), getDeviceFromDB());
        entityForTest.setId(entityClone.getId());

        Examination entityTest = service.update(entityForTest);
        assertEqualsEntityWithoutIDatetimeDeviceTimestamps(entityForTest, entityTest);

        assertEquals(entityClone.getId(), entityTest.getId());
        assertThat(entityClone.getDatetime()).isEqualToIgnoringNanos(entityTest.getDatetime());
        assertThat(entityClone.getCreationDate()).isEqualToIgnoringNanos(entityTest.getCreationDate());
        assertEquals(entityClone.getDeviceId(), entityTest.getDeviceId());
        assertNotEquals(entityClone.getPatientId(), entityTest.getPatientId());
        assertNotEquals(entityClone.getComment(), entityTest.getComment());
    }

    @Test
    void delete() {
        Examination entity = getNewEntityFromDB(getPatientFromDB(), getDeviceFromDB());

        assertDoesNotThrow(() -> service.delete(entity.getId()));
    }

    @Test
    void delete_rnd() {
        UUID rndId = UUID.randomUUID();
        String message = String.format(repository.MASK_NOT_FOUND, rndId);

        assertThrows(NotFoundException.class, () -> service.delete(rndId), message);
    }

    @Test
    void loadWithGraphsById() {
        Device device = getDeviceFromDB();
        Examination entity = getNewEntityFromDB(getPatientFromDB(), device);
        entity.setChannels(TestUtils.getListChannels(device.getAmountChannels()));
        entity = repository.saveAndFlush(entity);
        Examination entityClone = entity.toBuilder().build();

        Examination entityTest = service.loadWithGraphsById(entityClone.getId());

        assertNotNull(entityTest.getChannels());
        assertIterableEquals(entityClone.getChannels(), entityTest.getChannels());
    }

    @Test
    void recordingStart() {
    }

    @Test
    void recordingStop() {
    }

    @Test
    void isRecording() {
    }

    private Patient getPatientFromDB() {
        Patient patient = TestUtils.getNewPatient(null, 10);
        patient.setId(null);
        patient.setCreationDate(null);
        patient.setModifyDate(null);

        return patientRepository.save(patient);
    }

    private Device getDeviceFromDB() {
        Device device = TestUtils.getNewDevice(8);
        device.setId(null);
        device.setCreationDate(null);
        device.setModifyDate(null);

        return deviceRepository.save(device);
    }

    private Examination getNewEntityWithoutIdAndTimestamps(Patient patient, Device device) {
        Examination entity = TestUtils.getNewExamination(patient, device);
        entity.setId(null);
        entity.setCreationDate(null);
        entity.setModifyDate(null);
        entity.setChannels(null);

        return entity;
    }

    private Examination getNewEntityFromDB(Patient patient, Device device) {
        Examination entity = getNewEntityWithoutIdAndTimestamps(patient, device);

        return repository.save(entity);
    }

    private List<Examination> getNewEntityListFromDB(Patient patient, Device device, int count) {
        List<Examination> entities = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            entities.add(getNewEntityWithoutIdAndTimestamps(patient, device));
        }

        return repository.saveAll(entities);
    }

    private void assertEqualsEntity(Examination entity, Examination test) {
        assertEqualsEntityWithoutIDatetimeDeviceTimestamps(entity, test);
        assertEquals(entity.getId(), test.getId());
        assertThat(entity.getDatetime()).isEqualToIgnoringNanos(test.getDatetime());
        assertEquals(entity.getDeviceId(), test.getDeviceId());
        assertThat(entity.getCreationDate()).isEqualToIgnoringNanos(test.getCreationDate());
        assertThat(entity.getModifyDate()).isEqualToIgnoringNanos(test.getModifyDate());
    }

    private void assertEqualsEntityWithoutIDatetimeDeviceTimestamps(Examination entity, Examination test) {
        assertNotNull(entity);
        assertNotNull(test);
        assertEquals(entity.getPatientId(), test.getPatientId());
        assertEquals(entity.getComment(), test.getComment());
    }
}