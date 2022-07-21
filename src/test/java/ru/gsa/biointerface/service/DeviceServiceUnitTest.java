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
import ru.gsa.biointerface.dto.DeviceDTO;
import ru.gsa.biointerface.exception.NotFoundException;
import ru.gsa.biointerface.repository.DeviceRepository;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeviceServiceUnitTest {

    private final EasyRandom generator = new EasyRandom();

    @Mock
    private DeviceRepository repository;

    @InjectMocks
    private DeviceService service;

    @Test
    void findAll() {
        List<Device> entities = generator.objects(Device.class, 5).toList();

        when(repository.findAll()).thenReturn(new ArrayList<>());
        List<Device> entityTests1 = service.findAll();
        assertNotNull(entityTests1);
        assertEquals(new ArrayList<>(), entityTests1);

        when(repository.findAll()).thenReturn(entities);
        List<Device> entityTests2 = service.findAll();
        assertNotNull(entityTests2);
        assertIterableEquals(entities, entityTests2);
    }

    @Test
    void findAllPageable() {
        List<Device> entities = generator.objects(Device.class, 15).toList();
        Pageable pageable = PageRequest.of(0, 5);

        Page<Device> entityPage1 = new PageImpl<>(new ArrayList<>(), pageable, 0);
        when(repository.findAll(pageable)).thenReturn(entityPage1);
        Page<Device> entityPageTests1 = service.findAll(pageable);
        assertNotNull(entityPageTests1);
        assertEquals(entityPage1, entityPageTests1);


        while (pageable.getPageNumber() * pageable.getPageSize() <= entities.size()) {
            int start = pageable.getPageNumber() * pageable.getPageSize();
            int end = Math.min(start + pageable.getPageSize(), entities.size());
            List<Device> pageList = entities.subList(start, end);
            Page<Device> entityPage2 = new PageImpl<>(pageList, pageable, pageList.size());
            when(repository.findAll(pageable)).thenReturn(entityPage2);
            Page<Device> entityPageTests2 = service.findAll(pageable);
            assertNotNull(entityPageTests2);
            assertIterableEquals(entityPage2, entityPageTests2);
            pageable = PageRequest.of(pageable.getPageNumber() + 1, pageable.getPageSize());
        }
    }

    @Test
    void getById() {
        Device entity = generator.nextObject(Device.class);

        int rndInt = generator.nextInt();
        String message = String.format("Device(id=%s) is not found", rndInt);
        when(repository.getOrThrow(rndInt)).thenThrow(new NotFoundException(message));
        assertThrows(NotFoundException.class, () -> service.getById(rndInt), message);

        when(repository.getOrThrow(entity.getId())).thenReturn(entity);
        Device entityTest = service.getById(entity.getId());
        assertNotNull(entityTest);
        assertEquals(entity, entityTest);
    }

    @Test
    void saveDTO() {
        DeviceDTO dto = generator.nextObject(DeviceDTO.class);
        Device entity = new Device(
                dto.getId(),
                dto.getAmountChannels(),
                dto.getComment(),
                new ArrayList<>());

        when(repository.save(entity)).thenReturn(entity);
        Device entityTest = service.save(dto);
        assertNotNull(entityTest);
        assertEquals(entity, entityTest);
    }

    @Test
    void saveEntity() {
        Device entity = generator.nextObject(Device.class);

        when(repository.save(entity)).thenReturn(entity);
        Device entityTest = service.save(entity);
        assertNotNull(entityTest);
        assertEquals(entity, entityTest);
    }

    @Test
    void delete() {
        Device entity = generator.nextObject(Device.class);

        int rndInt = generator.nextInt();
        String message = String.format("Device(id=%s) is not found", rndInt);
        when(repository.getOrThrow(rndInt)).thenThrow(new NotFoundException(message));
        assertThrows(NotFoundException.class, () -> service.delete(rndInt), message);

        when(repository.getOrThrow(entity.getId())).thenReturn(entity);
        assertDoesNotThrow(() -> service.delete(entity.getId()));
    }
}