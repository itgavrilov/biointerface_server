package ru.gsa.biointerface.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.gsa.biointerface.TestUtils;
import ru.gsa.biointerface.domain.entity.Device;
import ru.gsa.biointerface.domain.entity.Examination;
import ru.gsa.biointerface.domain.entity.Patient;
import ru.gsa.biointerface.exception.NotFoundException;
import ru.gsa.biointerface.repository.ExaminationRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExaminationServiceUnitTest {

    @Mock
    private ExaminationRepository repository;
    @Mock
    private PatientService patientService;
    @Mock
    private DeviceService deviceService;

    @InjectMocks
    private ExaminationService service;

    @Test
    void findAll() {
        Patient patient = TestUtils.getNewPatient(null, 10);
        Device device = TestUtils.getNewDevice(8);
        List<Examination> entities = getNewEntityList(patient, device, 5);
        when(repository.findAllByPatientIdAndDeviceId(null, null)).thenReturn(entities);

        List<Examination> entityTests = service.findAll(null, null);

        assertNotNull(entityTests);

        entityTests.forEach(entityTest -> {
            Examination entity = entities.stream()
                    .filter(e -> e.getId().equals(entityTest.getId()))
                    .findAny().orElseThrow();
            assertEqualsEntity(entity, entityTest);
        });

        verify(repository).findAllByPatientIdAndDeviceId(null, null);
    }

    @Test
    void findAll_empty() {
        when(repository.findAllByPatientIdAndDeviceId(null, null)).thenReturn(new ArrayList<>());

        List<Examination> entityTests = service.findAll(null, null);
        assertNotNull(entityTests);
        assertIterableEquals(new ArrayList<>(), entityTests);

        verify(repository).findAllByPatientIdAndDeviceId(null, null);
    }

    @Test
    void findAll_byPatient() {
        Patient patient = TestUtils.getNewPatient(null, 10);
        Device device = TestUtils.getNewDevice(8);
        List<Examination> entities = getNewEntityList(patient, device, 5);

        when(repository.findAllByPatientIdAndDeviceId(patient.getId(), null)).thenReturn(entities);

        List<Examination> entityTests = service.findAll(patient.getId(), null);

        assertNotNull(entityTests);

        entityTests.forEach(entityTest -> {
            Examination entity = entities.stream()
                    .filter(e -> e.getId().equals(entityTest.getId()))
                    .findAny().orElseThrow();
            assertEqualsEntity(entity, entityTest);
        });

        verify(repository).findAllByPatientIdAndDeviceId(patient.getId(), null);
    }

    @Test
    void findAll_byDevice() {
        Patient patient = TestUtils.getNewPatient(null, 10);
        Device device = TestUtils.getNewDevice(8);
        List<Examination> entities = getNewEntityList(patient, device, 5);
        when(repository.findAllByPatientIdAndDeviceId(null, device.getId())).thenReturn(entities);

        List<Examination> entityTests = service.findAll(null, device.getId());

        assertNotNull(entityTests);

        entityTests.forEach(entityTest -> {
            Examination entity = entities.stream()
                    .filter(e -> e.getId().equals(entityTest.getId()))
                    .findAny().orElseThrow();
            assertEqualsEntity(entity, entityTest);
        });

        verify(repository).findAllByPatientIdAndDeviceId(null, device.getId());
    }

    @Test
    void findAll_byPatientAndDevice() {
        Patient patient = TestUtils.getNewPatient(null, 10);
        Device device = TestUtils.getNewDevice(8);
        List<Examination> entities = getNewEntityList(patient, device, 5);
        when(repository.findAllByPatientIdAndDeviceId(patient.getId(), device.getId())).thenReturn(entities);

        List<Examination> entityTests = service.findAll(patient.getId(), device.getId());

        assertNotNull(entityTests);

        entityTests.forEach(entityTest -> {
            Examination entity = entities.stream()
                    .filter(e -> e.getId().equals(entityTest.getId()))
                    .findAny().orElseThrow();
            assertEqualsEntity(entity, entityTest);
        });

        verify(repository).findAllByPatientIdAndDeviceId(patient.getId(), device.getId());
    }

    @Test
    void findAllPageable() {
        Patient patient = TestUtils.getNewPatient(null, 10);
        Device device = TestUtils.getNewDevice(8);
        List<Examination> entities = getNewEntityList(patient, device, 15);
        Pageable pageable = PageRequest.of(0, 5);

        while (pageable.getPageNumber() * pageable.getPageSize() <= entities.size()) {
            int start = pageable.getPageNumber() * pageable.getPageSize();
            int end = Math.min(start + pageable.getPageSize(), entities.size());
            List<Examination> pageList = entities.subList(start, end);
            Page<Examination> entityPage = new PageImpl<>(pageList, pageable, pageList.size());
            when(repository.findAllByPatientIdAndDeviceId(null, null, pageable)).thenReturn(entityPage);

            Page<Examination> entityPageTests = service.findAll(null, null, pageable);

            assertNotNull(entityPageTests);

            entityPageTests.getContent().forEach(entityTest -> {
                assertNotNull(entityTest);
                Examination entity = entities.stream()
                        .filter(e -> e.getId().equals(entityTest.getId()))
                        .findAny().orElseThrow();
                assertEqualsEntity(entity, entityTest);
            });

            verify(repository).findAllByPatientIdAndDeviceId(null, null, pageable);
            pageable = PageRequest.of(pageable.getPageNumber() + 1, pageable.getPageSize());
        }
    }

    @Test
    void findAllPageable_empty() {
        Pageable pageable = PageRequest.of(0, 5);
        Page<Examination> entityPage = new PageImpl<>(new ArrayList<>(), pageable, 0);
        when(repository.findAllByPatientIdAndDeviceId(null, null, pageable)).thenReturn(entityPage);

        Page<Examination> entityPageTests = service.findAll(null, null, pageable);
        assertNotNull(entityPageTests);
        assertEquals(entityPage, entityPageTests);
        verify(repository).findAllByPatientIdAndDeviceId(null, null, pageable);
    }

    @Test
    void findAllPageable_byPatient() {
        Patient patient = TestUtils.getNewPatient(null, 10);
        Device device = TestUtils.getNewDevice(8);
        List<Examination> entities = getNewEntityList(patient, device, 15);
        Pageable pageable = PageRequest.of(0, 5);

        while (pageable.getPageNumber() * pageable.getPageSize() <= entities.size()) {
            int start = pageable.getPageNumber() * pageable.getPageSize();
            int end = Math.min(start + pageable.getPageSize(), entities.size());
            List<Examination> pageList = entities.subList(start, end);
            Page<Examination> entityPage = new PageImpl<>(pageList, pageable, pageList.size());
            when(repository.findAllByPatientIdAndDeviceId(patient.getId(), null, pageable)).thenReturn(entityPage);

            Page<Examination> entityPageTests = service.findAll(patient.getId(), null, pageable);

            assertNotNull(entityPageTests);

            entityPageTests.getContent().forEach(entityTest -> {
                assertNotNull(entityTest);
                Examination entity = entities.stream()
                        .filter(e -> e.getId().equals(entityTest.getId()))
                        .findAny().orElseThrow();
                assertEqualsEntity(entity, entityTest);
            });

            verify(repository).findAllByPatientIdAndDeviceId(patient.getId(), null, pageable);
            pageable = PageRequest.of(pageable.getPageNumber() + 1, pageable.getPageSize());
        }
    }

    @Test
    void findAllPageable_byDevice() {
        Patient patient = TestUtils.getNewPatient(null, 10);
        Device device = TestUtils.getNewDevice(8);
        List<Examination> entities = getNewEntityList(patient, device, 15);
        Pageable pageable = PageRequest.of(0, 5);

        while (pageable.getPageNumber() * pageable.getPageSize() <= entities.size()) {
            int start = pageable.getPageNumber() * pageable.getPageSize();
            int end = Math.min(start + pageable.getPageSize(), entities.size());
            List<Examination> pageList = entities.subList(start, end);
            Page<Examination> entityPage = new PageImpl<>(pageList, pageable, pageList.size());
            when(repository.findAllByPatientIdAndDeviceId(null, device.getId(), pageable)).thenReturn(entityPage);

            Page<Examination> entityPageTests = service.findAll(null, device.getId(), pageable);

            assertNotNull(entityPageTests);

            entityPageTests.getContent().forEach(entityTest -> {
                assertNotNull(entityTest);
                Examination entity = entities.stream()
                        .filter(e -> e.getId().equals(entityTest.getId()))
                        .findAny().orElseThrow();
                assertEqualsEntity(entity, entityTest);
            });

            verify(repository).findAllByPatientIdAndDeviceId(null, device.getId(), pageable);
            pageable = PageRequest.of(pageable.getPageNumber() + 1, pageable.getPageSize());
        }
    }

    @Test
    void findAllPageable_byPatientAndDevice() {
        Patient patient = TestUtils.getNewPatient(null, 10);
        Device device = TestUtils.getNewDevice(8);
        List<Examination> entities = getNewEntityList(patient, device, 15);
        Pageable pageable = PageRequest.of(0, 5);

        while (pageable.getPageNumber() * pageable.getPageSize() <= entities.size()) {
            int start = pageable.getPageNumber() * pageable.getPageSize();
            int end = Math.min(start + pageable.getPageSize(), entities.size());
            List<Examination> pageList = entities.subList(start, end);
            Page<Examination> entityPage = new PageImpl<>(pageList, pageable, pageList.size());
            when(repository.findAllByPatientIdAndDeviceId(patient.getId(), device.getId(), pageable)).thenReturn(entityPage);

            Page<Examination> entityPageTests = service.findAll(patient.getId(), device.getId(), pageable);

            assertNotNull(entityPageTests);

            entityPageTests.getContent().forEach(entityTest -> {
                assertNotNull(entityTest);
                Examination entity = entities.stream()
                        .filter(e -> e.getId().equals(entityTest.getId()))
                        .findAny().orElseThrow();
                assertEqualsEntity(entity, entityTest);
            });

            verify(repository).findAllByPatientIdAndDeviceId(patient.getId(), device.getId(), pageable);
            pageable = PageRequest.of(pageable.getPageNumber() + 1, pageable.getPageSize());
        }
    }

    @Test
    void getById() {
        Patient patient = TestUtils.getNewPatient(null, 10);
        Device device = TestUtils.getNewDevice(8);
        Examination entity = TestUtils.getNewExamination(patient, device);
        when(repository.getOrThrow(entity.getId())).thenReturn(entity);

        Examination entityTest = service.getById(entity.getId());

        assertEqualsEntity(entity, entityTest);

        verify(repository).getOrThrow(entity.getId());
    }

    @Test
    void getById_rnd() {
        UUID rndId = UUID.randomUUID();
        String message = String.format(repository.MASK_NOT_FOUND, rndId);
        when(repository.getOrThrow(rndId)).thenThrow(new NotFoundException(message));

        assertThrows(NotFoundException.class, () -> service.getById(rndId), message);
        verify(repository).getOrThrow(rndId);
    }

    @Test
    void save() {
        Patient patient = TestUtils.getNewPatient(null, 10);
        Device device = TestUtils.getNewDevice(8);
        Examination entity = TestUtils.getNewExamination(patient, device);
        Examination entityClone = entity.toBuilder().build();
        when(repository.existsByPatientIdAndDeviceIdAndDatetime(
                entityClone.getPatientId(),
                entityClone.getDeviceId(),
                entityClone.getDatetime())).thenReturn(false);
        when(repository.save(entityClone)).thenReturn(entityClone);

        Examination entityTest = service.save(entityClone);

        assertEqualsEntity(entity, entityTest);

        verify(repository).existsByPatientIdAndDeviceIdAndDatetime(
                entityClone.getPatientId(),
                entityClone.getDeviceId(),
                entityClone.getDatetime());
        verify(repository).save(entityClone);
    }

    @Test
    void update() {
        Patient patient = TestUtils.getNewPatient(null, 10);
        Device device = TestUtils.getNewDevice(8);
        Examination entity = TestUtils.getNewExamination(patient, device);
        Examination entityClone = entity.toBuilder().build();

        Patient patientForTest = TestUtils.getNewPatient(null, 50);
        Device deviceForTest = TestUtils.getNewDevice(4);
        Examination entityForTest = TestUtils.getNewExamination(patientForTest, deviceForTest);
        entityForTest.setId(entity.getId());

        when(repository.getOrThrow(entityForTest.getId())).thenReturn(entityClone);
        when(patientService.getById(entityForTest.getPatientId())).thenReturn(patient);

        Examination entityTest = service.update(entityForTest);
        assertEqualsEntityWithoutIDatetimeDeviceTimestamps(entityForTest, entityTest);

        assertEquals(entity.getId(), entityTest.getId());
        assertEquals(entity.getDatetime(), entityTest.getDatetime());
        assertEquals(entity.getCreationDate(), entityTest.getCreationDate());
        assertEquals(entity.getDeviceId(), entityTest.getDeviceId());
        assertNotEquals(entity.getPatientId(), entityTest.getPatientId());
        assertNotEquals(entity.getComment(), entityTest.getComment());

        verify(repository).getOrThrow(entity.getId());
        verify(patientService).getById(entityForTest.getPatientId());
    }

    @Test
    void delete() {
        Patient patient = TestUtils.getNewPatient(null, 10);
        Device device = TestUtils.getNewDevice(8);
        Examination entity = TestUtils.getNewExamination(patient, device);
        when(repository.getOrThrow(entity.getId())).thenReturn(entity);

        assertDoesNotThrow(() -> service.delete(entity.getId()));
        verify(repository).getOrThrow(entity.getId());
        verify(repository).delete(entity);
    }

    @Test
    void delete_rnd() {
        UUID rndId = UUID.randomUUID();
        String message = String.format(repository.MASK_NOT_FOUND, rndId);
        when(repository.getOrThrow(rndId)).thenThrow(new NotFoundException(message));

        assertThrows(NotFoundException.class, () -> service.delete(rndId), message);
        verify(repository).getOrThrow(rndId);
    }

    @Test
    void loadWithGraphsById() {
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

    private List<Examination> getNewEntityList(Patient patient, Device device, int count) {
        List<Examination> entities = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            Examination entity = TestUtils.getNewExamination(patient, device);
            entities.add(entity);
        }

        return entities;
    }

    private void assertEqualsEntity(Examination entity, Examination test) {
        assertEqualsEntityWithoutIDatetimeDeviceTimestamps(entity, test);
        assertEquals(entity.getId(), test.getId());
        assertEquals(entity.getDatetime(), test.getDatetime());
        assertEquals(entity.getDeviceId(), test.getDeviceId());
        assertIterableEquals(entity.getChannels(), test.getChannels());
    }

    private void assertEqualsEntityWithoutIDatetimeDeviceTimestamps(Examination entity, Examination test) {
        assertNotNull(entity);
        assertNotNull(test);
        assertEquals(entity.getPatientId(), test.getPatientId());
        assertEquals(entity.getComment(), test.getComment());
    }
}