package ru.gsa.biointerface.service;

import org.jeasy.random.EasyRandom;
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

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExaminationServiceUnitTest {

    private final EasyRandom generator = new EasyRandom();

    @Mock
    private ExaminationRepository repository;

    @InjectMocks
    private ExaminationService service;

    @Test
    void findAll() {
        List<Examination> entities = generator.objects(Examination.class, 5).toList();
        when(repository.findAllByPatientIdAndDeviceId(null, null)).thenReturn(entities);

        List<Examination> entityTests = service.findAll(null, null);
        assertNotNull(entityTests);
        assertIterableEquals(entities, entityTests);
        verify(repository).findAllByPatientIdAndDeviceId(null, null);
    }

    @Test
    void findAll_empty() {
        when(repository.findAllByPatientIdAndDeviceId(null, null)).thenReturn(new ArrayList<>());

        List<Examination> entityTests1 = service.findAll(null, null);
        assertNotNull(entityTests1);
        assertIterableEquals(new ArrayList<>(), entityTests1);
        verify(repository).findAllByPatientIdAndDeviceId(null, null);
    }

    @Test
    void findAll_byPatient() {
        List<Examination> entities = generator.objects(Examination.class, 5).toList();
        Patient patient = generator.nextObject(Patient.class);
        entities.forEach(e -> e.setPatient(patient));
        when(repository.findAllByPatientIdAndDeviceId(patient.getId(), null)).thenReturn(entities);

        List<Examination> entityTests = service.findAll(patient.getId(), null);
        assertNotNull(entityTests);
        assertIterableEquals(entities, entityTests);
        verify(repository).findAllByPatientIdAndDeviceId(patient.getId(), null);
    }

    @Test
    void findAll_byDevice() {
        List<Examination> entities = generator.objects(Examination.class, 5).toList();
        Device device = generator.nextObject(Device.class);
        entities.forEach(e -> e.setDevice(device));
        when(repository.findAllByPatientIdAndDeviceId(null, device.getId())).thenReturn(entities);

        List<Examination> entityTests = service.findAll(null, device.getId());
        assertNotNull(entityTests);
        assertIterableEquals(entities, entityTests);
        verify(repository).findAllByPatientIdAndDeviceId(null, device.getId());
    }

    @Test
    void findAll_byPatientAndDevice() {
        List<Examination> entities = generator.objects(Examination.class, 5).toList();
        Patient patient = generator.nextObject(Patient.class);
        Device device = generator.nextObject(Device.class);
        entities.forEach(e -> {
            e.setPatient(patient);
            e.setDevice(device);
        });
        when(repository.findAllByPatientIdAndDeviceId(patient.getId(), device.getId())).thenReturn(entities);

        List<Examination> entityTests = service.findAll(patient.getId(), device.getId());
        assertNotNull(entityTests);
        assertIterableEquals(entities, entityTests);
        verify(repository).findAllByPatientIdAndDeviceId(patient.getId(), device.getId());
    }

    @Test
    void findAllPageable() {
        List<Examination> entities = generator.objects(Examination.class, 15).toList();
        Pageable pageable = PageRequest.of(0, 5);

        while (pageable.getPageNumber() * pageable.getPageSize() <= entities.size()) {
            int start = pageable.getPageNumber() * pageable.getPageSize();
            int end = Math.min(start + pageable.getPageSize(), entities.size());
            List<Examination> pageList = entities.subList(start, end);
            Page<Examination> entityPage = new PageImpl<>(pageList, pageable, pageList.size());
            when(repository.findAllByPatientIdAndDeviceId(null, null, pageable)).thenReturn(entityPage);

            Page<Examination> entityPageTests = service.findAll(null, null, pageable);
            assertNotNull(entityPageTests);
            assertIterableEquals(entityPage, entityPageTests);
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
        List<Examination> entities = generator.objects(Examination.class, 15).toList();
        Patient patient = generator.nextObject(Patient.class);
        entities.forEach(e -> e.setPatient(patient));
        Pageable pageable = PageRequest.of(0, 5);

        while (pageable.getPageNumber() * pageable.getPageSize() <= entities.size()) {
            int start = pageable.getPageNumber() * pageable.getPageSize();
            int end = Math.min(start + pageable.getPageSize(), entities.size());
            List<Examination> pageList = entities.subList(start, end);
            Page<Examination> entityPage = new PageImpl<>(pageList, pageable, pageList.size());
            when(repository.findAllByPatientIdAndDeviceId(patient.getId(), null, pageable)).thenReturn(entityPage);

            Page<Examination> entityPageTests = service.findAll(patient.getId(), null, pageable);
            assertNotNull(entityPageTests);
            assertIterableEquals(entityPage, entityPageTests);
            verify(repository).findAllByPatientIdAndDeviceId(patient.getId(), null, pageable);
            pageable = PageRequest.of(pageable.getPageNumber() + 1, pageable.getPageSize());
        }
    }

    @Test
    void findAllPageable_byDevice() {
        List<Examination> entities = generator.objects(Examination.class, 15).toList();
        Device device = generator.nextObject(Device.class);
        entities.forEach(e -> e.setDevice(device));
        Pageable pageable = PageRequest.of(0, 5);

        while (pageable.getPageNumber() * pageable.getPageSize() <= entities.size()) {
            int start = pageable.getPageNumber() * pageable.getPageSize();
            int end = Math.min(start + pageable.getPageSize(), entities.size());
            List<Examination> pageList = entities.subList(start, end);
            Page<Examination> entityPage = new PageImpl<>(pageList, pageable, pageList.size());
            when(repository.findAllByPatientIdAndDeviceId(null, device.getId(), pageable)).thenReturn(entityPage);

            Page<Examination> entityPageTests = service.findAll(null, device.getId(), pageable);
            assertNotNull(entityPageTests);
            assertIterableEquals(entityPage, entityPageTests);
            verify(repository).findAllByPatientIdAndDeviceId(null, device.getId(), pageable);
            pageable = PageRequest.of(pageable.getPageNumber() + 1, pageable.getPageSize());
        }
    }

    @Test
    void findAllPageable_byPatientAndDevice() {
        List<Examination> entities = generator.objects(Examination.class, 15).toList();
        Patient patient = generator.nextObject(Patient.class);
        Device device = generator.nextObject(Device.class);
        entities.forEach(e -> {
            e.setPatient(patient);
            e.setDevice(device);
        });
        Pageable pageable = PageRequest.of(0, 5);

        while (pageable.getPageNumber() * pageable.getPageSize() <= entities.size()) {
            int start = pageable.getPageNumber() * pageable.getPageSize();
            int end = Math.min(start + pageable.getPageSize(), entities.size());
            List<Examination> pageList = entities.subList(start, end);
            Page<Examination> entityPage = new PageImpl<>(pageList, pageable, pageList.size());
            when(repository.findAllByPatientIdAndDeviceId(patient.getId(), device.getId(), pageable)).thenReturn(entityPage);

            Page<Examination> entityPageTests = service.findAll(patient.getId(), device.getId(), pageable);
            assertNotNull(entityPageTests);
            assertIterableEquals(entityPage, entityPageTests);
            verify(repository).findAllByPatientIdAndDeviceId(patient.getId(), device.getId(), pageable);
            pageable = PageRequest.of(pageable.getPageNumber() + 1, pageable.getPageSize());
        }
    }

    @Test
    void getById() {
        Examination entity = generator.nextObject(Examination.class);
        when(repository.getOrThrow(entity.getId())).thenReturn(entity);

        Examination entityTest = service.getById(entity.getId());
        assertNotNull(entityTest);
        assertEquals(entity, entityTest);
        verify(repository).getOrThrow(entity.getId());
    }

    @Test
    void getById_rnd() {
        int rnd = generator.nextInt();
        String message = String.format(repository.MASK_NOT_FOUND, rnd);
        when(repository.getOrThrow(rnd)).thenThrow(new NotFoundException(message));

        assertThrows(NotFoundException.class, () -> service.getById(rnd), message);
        verify(repository).getOrThrow(rnd);
    }

    @Test
    void save() {
        Examination entity = generator.nextObject(Examination.class);
        when(repository.save(entity)).thenReturn(entity);

        Examination entityTest = service.save(entity);
        assertNotNull(entityTest);
        assertEquals(entity, entityTest);
        verify(repository).save(entity);
    }

    @Test
    void delete() {
        Examination entity = generator.nextObject(Examination.class);
        when(repository.getOrThrow(entity.getId())).thenReturn(entity);

        assertDoesNotThrow(() -> service.delete(entity.getId()));
        verify(repository).getOrThrow(entity.getId());
        verify(repository).delete(entity);
    }

    @Test
    void delete_rnd() {
        int rnd = generator.nextInt();
        String message = String.format(repository.MASK_NOT_FOUND, rnd);
        when(repository.getOrThrow(rnd)).thenThrow(new NotFoundException(message));

        assertThrows(NotFoundException.class, () -> service.delete(rnd), message);
        verify(repository).getOrThrow(rnd);
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