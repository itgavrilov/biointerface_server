package ru.gsa.biointerface.unit.service;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.gsa.biointerface.domain.entity.Device;
import ru.gsa.biointerface.domain.entity.Examination;
import ru.gsa.biointerface.domain.entity.Patient;
import ru.gsa.biointerface.exception.NotFoundException;
import ru.gsa.biointerface.repository.ExaminationRepository;
import ru.gsa.biointerface.service.DeviceService;
import ru.gsa.biointerface.service.ExaminationService;
import ru.gsa.biointerface.service.PatientService;

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
import static ru.gsa.biointerface.utils.DeviceUtil.getDevice;
import static ru.gsa.biointerface.utils.ExaminationUtil.assertEqualsExamination;
import static ru.gsa.biointerface.utils.ExaminationUtil.assertEqualsExaminationLight;
import static ru.gsa.biointerface.utils.ExaminationUtil.getExamination;
import static ru.gsa.biointerface.utils.ExaminationUtil.getExaminations;
import static ru.gsa.biointerface.utils.PatientUtil.getPatient;

@Tag("UnitTest")
@ExtendWith(MockitoExtension.class)
class ExaminationServiceTest {

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
        List<Examination> entities = getExaminations(5);
        when(repository.findAllByPatientIdAndDeviceId(null, null)).thenReturn(entities);

        List<Examination> entityTests = service.findAll(null, null);

        assertNotNull(entityTests);

        entityTests.forEach(entityTest -> {
            Examination entity = entities.stream()
                    .filter(e -> e.getId().equals(entityTest.getId()))
                    .findAny().orElseThrow();
            assertEqualsExamination(entity, entityTest);
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
        List<Examination> entities = getExaminations(5);
        UUID patientId = entities.get(0).getPatientId();

        when(repository.findAllByPatientIdAndDeviceId(patientId, null)).thenReturn(entities);

        List<Examination> entityTests = service.findAll(patientId, null);

        assertNotNull(entityTests);

        entityTests.forEach(entityTest -> {
            Examination entity = entities.stream()
                    .filter(e -> e.getId().equals(entityTest.getId()))
                    .findAny().orElseThrow();
            assertEqualsExamination(entity, entityTest);
        });

        verify(repository).findAllByPatientIdAndDeviceId(patientId, null);
    }

    @Test
    void findAll_byDevice() {
        List<Examination> entities = getExaminations(5);
        UUID deviceId = entities.get(0).getDeviceId();
        when(repository.findAllByPatientIdAndDeviceId(null, deviceId)).thenReturn(entities);

        List<Examination> entityTests = service.findAll(null, deviceId);

        assertNotNull(entityTests);

        entityTests.forEach(entityTest -> {
            Examination entity = entities.stream()
                    .filter(e -> e.getId().equals(entityTest.getId()))
                    .findAny().orElseThrow();
            assertEqualsExamination(entity, entityTest);
        });

        verify(repository).findAllByPatientIdAndDeviceId(null, deviceId);
    }

    @Test
    void findAll_byPatientAndDevice() {
        List<Examination> entities = getExaminations(5);
        UUID patientId = entities.get(0).getPatientId();
        UUID deviceId = entities.get(0).getDeviceId();
        when(repository.findAllByPatientIdAndDeviceId(patientId, deviceId)).thenReturn(entities);

        List<Examination> entityTests = service.findAll(patientId, deviceId);

        assertNotNull(entityTests);

        entityTests.forEach(entityTest -> {
            Examination entity = entities.stream()
                    .filter(e -> e.getId().equals(entityTest.getId()))
                    .findAny().orElseThrow();
            assertEqualsExamination(entity, entityTest);
        });

        verify(repository).findAllByPatientIdAndDeviceId(patientId, deviceId);
    }

    @Test
    void findAllPageable() {
        List<Examination> entities = getExaminations(15);
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
                assertEqualsExamination(entity, entityTest);
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
        List<Examination> entities = getExaminations(15);
        UUID patientId = entities.get(0).getPatientId();
        Pageable pageable = PageRequest.of(0, 5);

        while (pageable.getPageNumber() * pageable.getPageSize() <= entities.size()) {
            int start = pageable.getPageNumber() * pageable.getPageSize();
            int end = Math.min(start + pageable.getPageSize(), entities.size());
            List<Examination> pageList = entities.subList(start, end);
            Page<Examination> entityPage = new PageImpl<>(pageList, pageable, pageList.size());
            when(repository.findAllByPatientIdAndDeviceId(patientId, null, pageable)).thenReturn(entityPage);

            Page<Examination> entityPageTests = service.findAll(patientId, null, pageable);

            assertNotNull(entityPageTests);

            entityPageTests.getContent().forEach(entityTest -> {
                assertNotNull(entityTest);
                Examination entity = entities.stream()
                        .filter(e -> e.getId().equals(entityTest.getId()))
                        .findAny().orElseThrow();
                assertEqualsExamination(entity, entityTest);
            });

            verify(repository).findAllByPatientIdAndDeviceId(patientId, null, pageable);
            pageable = PageRequest.of(pageable.getPageNumber() + 1, pageable.getPageSize());
        }
    }

    @Test
    void findAllPageable_byDevice() {
        List<Examination> entities = getExaminations(15);
        UUID deviceId = entities.get(0).getDeviceId();
        Pageable pageable = PageRequest.of(0, 5);

        while (pageable.getPageNumber() * pageable.getPageSize() <= entities.size()) {
            int start = pageable.getPageNumber() * pageable.getPageSize();
            int end = Math.min(start + pageable.getPageSize(), entities.size());
            List<Examination> pageList = entities.subList(start, end);
            Page<Examination> entityPage = new PageImpl<>(pageList, pageable, pageList.size());
            when(repository.findAllByPatientIdAndDeviceId(null, deviceId, pageable)).thenReturn(entityPage);

            Page<Examination> entityPageTests = service.findAll(null, deviceId, pageable);

            assertNotNull(entityPageTests);

            entityPageTests.getContent().forEach(entityTest -> {
                assertNotNull(entityTest);
                Examination entity = entities.stream()
                        .filter(e -> e.getId().equals(entityTest.getId()))
                        .findAny().orElseThrow();
                assertEqualsExamination(entity, entityTest);
            });

            verify(repository).findAllByPatientIdAndDeviceId(null, deviceId, pageable);
            pageable = PageRequest.of(pageable.getPageNumber() + 1, pageable.getPageSize());
        }
    }

    @Test
    void findAllPageable_byPatientAndDevice() {
        List<Examination> entities = getExaminations(15);
        UUID patientId = entities.get(0).getPatientId();
        UUID deviceId = entities.get(0).getDeviceId();
        Pageable pageable = PageRequest.of(0, 5);

        while (pageable.getPageNumber() * pageable.getPageSize() <= entities.size()) {
            int start = pageable.getPageNumber() * pageable.getPageSize();
            int end = Math.min(start + pageable.getPageSize(), entities.size());
            List<Examination> pageList = entities.subList(start, end);
            Page<Examination> entityPage = new PageImpl<>(pageList, pageable, pageList.size());
            when(repository.findAllByPatientIdAndDeviceId(patientId, deviceId, pageable)).thenReturn(entityPage);

            Page<Examination> entityPageTests = service.findAll(patientId, deviceId, pageable);

            assertNotNull(entityPageTests);

            entityPageTests.getContent().forEach(entityTest -> {
                assertNotNull(entityTest);
                Examination entity = entities.stream()
                        .filter(e -> e.getId().equals(entityTest.getId()))
                        .findAny().orElseThrow();
                assertEqualsExamination(entity, entityTest);
            });

            verify(repository).findAllByPatientIdAndDeviceId(patientId, deviceId, pageable);
            pageable = PageRequest.of(pageable.getPageNumber() + 1, pageable.getPageSize());
        }
    }

    @Test
    void getById() {
        Examination entity = getExamination();
        when(repository.getOrThrow(entity.getId())).thenReturn(entity);

        Examination entityTest = service.getById(entity.getId());

        assertEqualsExamination(entity, entityTest);

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
        Patient patient = getPatient(null, 10);
        Device device = getDevice(8);
        Examination entity = getExamination(patient, device);
        Examination entityClone = entity.toBuilder().build();
        when(repository.existsByPatientIdAndDeviceIdAndDatetime(
                entityClone.getPatientId(),
                entityClone.getDeviceId(),
                entityClone.getDatetime())).thenReturn(false);
        when(repository.save(entityClone)).thenReturn(entityClone);
        when(patientService.getById(entityClone.getPatientId())).thenReturn(patient);
        when(deviceService.getById(entityClone.getDeviceId())).thenReturn(device);

        Examination entityTest = service.save(entityClone);

        assertEqualsExamination(entity, entityTest);

        verify(repository).existsByPatientIdAndDeviceIdAndDatetime(
                entityClone.getPatientId(),
                entityClone.getDeviceId(),
                entityClone.getDatetime());
        verify(repository).save(entityClone);
        verify(patientService).getById(entityClone.getPatientId());
        verify(deviceService).getById(entityClone.getDeviceId());
    }

    @Test
    void update() {
        Patient patient = getPatient(null, 10);
        Device device = getDevice(8);
        Examination entity = getExamination(patient, device);
        Examination entityClone = entity.toBuilder().build();

        Patient patientForTest = getPatient(null, 50);
        Device deviceForTest = getDevice(4);
        Examination entityForTest = getExamination(patientForTest, deviceForTest);
        entityForTest.setId(entity.getId());

        when(repository.getOrThrow(entityForTest.getId())).thenReturn(entityClone);
        when(patientService.getById(entityForTest.getPatientId())).thenReturn(patient);

        Examination entityTest = service.update(entityForTest);
        assertEqualsExaminationLight(entityForTest, entityTest);

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
        Patient patient = getPatient(null, 10);
        Device device = getDevice(8);
        Examination entity = getExamination(patient, device);
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
}