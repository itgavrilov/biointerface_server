package ru.gsa.biointerface.service;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.gsa.biointerface.domain.entity.Device;
import ru.gsa.biointerface.exception.NotFoundException;
import ru.gsa.biointerface.repository.DeviceRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
class DeviceServiceTest {

    @Autowired
    private DeviceService service;
    @Autowired
    private DeviceRepository repository;

    private final EasyRandom generator = new EasyRandom();

    @Test
    @Transactional
    void findAll() {
        List<Device> entities = generator.objects(Device.class, 5).toList();
        entities.forEach(entity -> {
            entity.setId(null);
            entity.setAmountChannels(8);
        });
        entities = repository.saveAll(entities);

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
    }

    @Test
    void findAll_empty() {
        List<Device> entityTests = service.findAll();
        assertNotNull(entityTests);
        assertIterableEquals(new ArrayList<>(), entityTests);
    }

    @Test
    @Transactional
    void findAllPageable() {
        List<Device> entities = generator.objects(Device.class, 15).toList();
        entities.forEach(entity -> {
            entity.setId(null);
            entity.setAmountChannels(8);
        });
        entities = repository.saveAll(entities);
        Pageable pageable = PageRequest.of(0, 5);

        while (pageable.getPageNumber() * pageable.getPageSize() <= entities.size()) {
            Page<Device> entityPageTests = service.findAll(pageable);
            assertNotNull(entityPageTests);

            List<Device> finalEntities = entities;
            entityPageTests.getContent().forEach(entityTest -> {
                assertNotNull(entityTest);
                Device entity = finalEntities.stream()
                        .filter(e -> e.getId().equals(entityTest.getId()))
                        .findAny().orElseThrow();
                assertEquals(entity.getId(), entityTest.getId());
                assertEquals(entity.getNumber(), entityTest.getNumber());
                assertEquals(entity.getComment(), entityTest.getComment());
                assertEquals(entity.getAmountChannels(), entityTest.getAmountChannels());
                assertIterableEquals(entity.getExaminations(), entityTest.getExaminations());
            });

            pageable = PageRequest.of(pageable.getPageNumber() + 1, pageable.getPageSize());
        }
    }

    @Test
    void findAllPageable_empty() {
        Pageable pageable = PageRequest.of(0, 5);
        Page<Device> entityPage = new PageImpl<>(new ArrayList<>(), pageable, 0);

        Page<Device> entityPageTests = service.findAll(pageable);
        assertNotNull(entityPageTests);
        assertEquals(entityPage, entityPageTests);
    }

    @Test
    @Transactional
    void getById() {
        Device entity = generator.nextObject(Device.class);
        entity.setId(null);
        entity.setAmountChannels(8);
        entity = repository.save(entity);

        Device entityTest = service.getById(entity.getId());
        assertNotNull(entityTest);
        assertEquals(entity, entityTest);
        assertEquals(entity.getId(), entityTest.getId());
        assertEquals(entity.getNumber(), entityTest.getNumber());
        assertEquals(entity.getComment(), entityTest.getComment());
        assertEquals(entity.getAmountChannels(), entityTest.getAmountChannels());
        assertIterableEquals(entity.getExaminations(), entityTest.getExaminations());
    }

    @Test
    void getById_rnd() {
        UUID rndId = UUID.randomUUID();
        String message = String.format(repository.MASK_NOT_FOUND, rndId);

        assertThrows(NotFoundException.class, () -> service.getById(rndId), message);
    }

    @Test
    @Transactional
    void save_entity() {
        Device entity = generator.nextObject(Device.class);
        entity.setId(null);
        entity.setAmountChannels(8);

        Device entityTest = service.save(entity);
        assertNotNull(entityTest);
        assertNotNull(entityTest.getId());
        assertNotNull(entityTest.getId());
        assertEquals(entity.getNumber(), entityTest.getNumber());
        assertEquals(entity.getComment(), entityTest.getComment());
        assertEquals(entity.getAmountChannels(), entityTest.getAmountChannels());
        assertIterableEquals(entity.getExaminations(), entityTest.getExaminations());
    }

    @Test
    @Transactional
    void update() {
        Device entity = generator.nextObject(Device.class);
        entity.setId(null);
        entity.setAmountChannels(8);
        entity = repository.save(entity);

        Device entityForTest = new Device(
                entity.getId(),
                entity.getNumber(),
                4,
                generator.nextObject(String.class),
                new ArrayList<>());

        Device entityTest = service.update(entityForTest);
        assertNotNull(entityTest);
        assertEquals(entity, entityTest);
        assertEquals(entity.getId(), entityTest.getId());
        assertEquals(entity.getNumber(), entityTest.getNumber());
        assertEquals(entityForTest.getComment(), entityTest.getComment());
        assertEquals(entityForTest.getAmountChannels(), entityTest.getAmountChannels());
        assertIterableEquals(entity.getExaminations(), entityTest.getExaminations());
    }

    @Test
    void update_rnd() {
        Device entity = generator.nextObject(Device.class);
        UUID rnd = entity.getId();
        String message = String.format(repository.MASK_NOT_FOUND, rnd);

        assertThrows(NotFoundException.class, () -> service.update(entity), message);
    }

    @Test
    @Transactional
    void delete() {
        Device entity = generator.nextObject(Device.class);
        entity.setId(null);
        entity.setAmountChannels(8);
        entity = repository.save(entity);

        Device finalEntity = entity;
        assertDoesNotThrow(() -> service.delete(finalEntity.getId()));
    }

    @Test
    void delete_rnd() {
        UUID rndId = UUID.randomUUID();
        String message = String.format(repository.MASK_NOT_FOUND, rndId);

        assertThrows(NotFoundException.class, () -> service.delete(rndId), message);
    }
}