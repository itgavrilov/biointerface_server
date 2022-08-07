package ru.gsa.biointerface.unit.service;

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
import ru.gsa.biointerface.domain.dto.DeviceDTO;
import ru.gsa.biointerface.exception.NotFoundException;
import ru.gsa.biointerface.repository.DeviceRepository;
import ru.gsa.biointerface.service.DeviceService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeviceServiceTest {

    private final EasyRandom generator = new EasyRandom();

    @Mock
    private DeviceRepository repository;

    @InjectMocks
    private DeviceService service;

    @Test
    void findAll() {
        List<Device> entities = generator.objects(Device.class, 5).toList();
        when(repository.findAll()).thenReturn(entities);

        List<Device> entityTests = service.findAll();
        assertNotNull(entityTests);
        assertIterableEquals(entities, entityTests);
        for (int i = 0; i < entityTests.size(); i++) {
            assertNotNull(entities.get(i));
            assertEquals(entities.get(i).getId(), entityTests.get(i).getId());
            assertEquals(entities.get(i).getNumber(), entityTests.get(i).getNumber());
            assertEquals(entities.get(i).getComment(), entityTests.get(i).getComment());
            assertEquals(entities.get(i).getAmountChannels(), entityTests.get(i).getAmountChannels());
            assertIterableEquals(entities.get(i).getExaminations(), entityTests.get(i).getExaminations());
        }

        verify(repository).findAll();
    }

    @Test
    void findAll_empty() {
        when(repository.findAll()).thenReturn(new ArrayList<>());

        List<Device> entityTests = service.findAll();
        assertNotNull(entityTests);
        assertIterableEquals(new ArrayList<>(), entityTests);
        verify(repository).findAll();
    }

    @Test
    void findAllPageable() {
        List<Device> entities = generator.objects(Device.class, 15).toList();
        Pageable pageable = PageRequest.of(0, 5);

        while (pageable.getPageNumber() * pageable.getPageSize() <= entities.size()) {
            int start = pageable.getPageNumber() * pageable.getPageSize();
            int end = Math.min(start + pageable.getPageSize(), entities.size());
            List<Device> pageList = entities.subList(start, end);
            Page<Device> entityPage = new PageImpl<>(pageList, pageable, pageList.size());
            when(repository.findAll(pageable)).thenReturn(entityPage);

            Page<Device> entityPageTests = service.findAll(pageable);
            assertNotNull(entityPageTests);
            assertIterableEquals(entityPage, entityPageTests);
            for (int i = 0; i < entityPage.getContent().size(); i++) {
                assertNotNull(entityPage.getContent().get(i));
                assertEquals(entityPage.getContent().get(i).getId(), entityPageTests.getContent().get(i).getId());
                assertEquals(entityPage.getContent().get(i).getNumber(), entityPageTests.getContent().get(i).getNumber());
                assertEquals(entityPage.getContent().get(i).getComment(), entityPageTests.getContent().get(i).getComment());
                assertEquals(entityPage.getContent().get(i).getAmountChannels(), entityPageTests.getContent().get(i).getAmountChannels());
                assertIterableEquals(entityPage.getContent().get(i).getExaminations(), entityPageTests.getContent().get(i).getExaminations());
            }

            verify(repository).findAll(pageable);
            pageable = PageRequest.of(pageable.getPageNumber() + 1, pageable.getPageSize());
        }
    }

    @Test
    void findAllPageable_empty() {
        Pageable pageable = PageRequest.of(0, 5);
        Page<Device> entityPage = new PageImpl<>(new ArrayList<>(), pageable, 0);
        when(repository.findAll(pageable)).thenReturn(entityPage);

        Page<Device> entityPageTests = service.findAll(pageable);
        assertNotNull(entityPageTests);
        assertEquals(entityPage, entityPageTests);

        verify(repository).findAll(pageable);
    }

    @Test
    void getById() {
        Device entity = generator.nextObject(Device.class);
        when(repository.getOrThrow(entity.getId())).thenReturn(entity);

        Device entityTest = service.getById(entity.getId());
        assertNotNull(entityTest);
        assertEquals(entity, entityTest);
        assertEquals(entity.getId(), entityTest.getId());
        assertEquals(entity.getNumber(), entityTest.getNumber());
        assertEquals(entity.getComment(), entityTest.getComment());
        assertEquals(entity.getAmountChannels(), entityTest.getAmountChannels());
        assertIterableEquals(entity.getExaminations(), entityTest.getExaminations());

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
    void update() {
        Device entity = generator.nextObject(Device.class);
        DeviceDTO dto = DeviceDTO.builder()
                .id(entity.getId())
                .amountChannels(generator.nextInt())
                .comment(generator.nextObject(String.class))
                .build();
        when(repository.getOrThrow(entity.getId())).thenReturn(entity);

        Device entityTest = service.update(dto);
        assertNotNull(entityTest);
        assertEquals(entity, entityTest);
        assertEquals(entity.getId(), entityTest.getId());
        assertEquals(entity.getNumber(), entityTest.getNumber());
        assertEquals(entity.getComment(), entityTest.getComment());
        assertEquals(entity.getAmountChannels(), entityTest.getAmountChannels());
        assertIterableEquals(entity.getExaminations(), entityTest.getExaminations());
        verify(repository).getOrThrow(entity.getId());
    }

    @Test
    void update_rnd() {
        DeviceDTO dto = generator.nextObject(DeviceDTO.class);
        UUID rnd = dto.getId();
        String message = String.format(repository.MASK_NOT_FOUND, rnd);
        when(repository.getOrThrow(rnd)).thenThrow(new NotFoundException(message));

        assertThrows(NotFoundException.class, () -> service.delete(rnd), message);
        verify(repository).getOrThrow(rnd);
    }

    @Test
    void save_entity() {
        Device entity = generator.nextObject(Device.class);
        when(repository.save(entity)).thenReturn(entity);

        Device entityTest = service.save(entity);
        assertNotNull(entityTest);
        assertEquals(entity, entityTest);
        assertEquals(entity.getId(), entityTest.getId());
        assertEquals(entity.getNumber(), entityTest.getNumber());
        assertEquals(entity.getComment(), entityTest.getComment());
        assertEquals(entity.getAmountChannels(), entityTest.getAmountChannels());
        assertIterableEquals(entity.getExaminations(), entityTest.getExaminations());
        verify(repository).save(entity);
    }

    @Test
    void delete() {
        Device entity = generator.nextObject(Device.class);
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
}