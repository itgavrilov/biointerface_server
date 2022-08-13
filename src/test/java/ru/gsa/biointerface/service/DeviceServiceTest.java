package ru.gsa.biointerface.service;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import ru.gsa.biointerface.domain.entity.Device;
import ru.gsa.biointerface.exception.NotFoundException;
import ru.gsa.biointerface.repository.DeviceRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static java.lang.Thread.sleep;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
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

    @AfterEach
    void tearDown() {
        repository.deleteAll(repository.findAll());
    }

    @Test
    void findAll() {
        List<Device> entities = getNewEntityListFromDB(8, 5);

        List<Device> entityTests = service.findAll();

        assertNotNull(entityTests);

        entityTests.forEach(entityTest -> {
            Device entity = entities.stream()
                    .filter(e -> e.getId().equals(entityTest.getId()))
                    .findAny().orElseThrow();
            assertEqualsEntity(entity, entityTest);
        });
    }

    @Test
    void findAll_empty() {
        List<Device> entityTests = service.findAll();
        assertNotNull(entityTests);
        assertIterableEquals(new ArrayList<>(), entityTests);
    }

    @Test
    void findAllPageable() {
        List<Device> entities = getNewEntityListFromDB(8, 15);
        Pageable pageable = PageRequest.of(0, 5);

        while (pageable.getPageNumber() * pageable.getPageSize() <= entities.size()) {
            Page<Device> entityPageTests = service.findAll(pageable);

            assertNotNull(entityPageTests);

            entityPageTests.getContent().forEach(entityTest -> {
                assertNotNull(entityTest);
                Device entity = entities.stream()
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
        Page<Device> entityPage = new PageImpl<>(new ArrayList<>(), pageable, 0);

        Page<Device> entityPageTests = service.findAll(pageable);
        assertNotNull(entityPageTests);
        assertEquals(entityPage, entityPageTests);
    }

    @Test
    void getById() {
        Device entity = getNewEntityFromDB(8);

        Device entityTest = service.getById(entity.getId());

        assertEqualsEntity(entity, entityTest);
    }

    @Test
    void getById_rnd() {
        UUID rndId = UUID.randomUUID();
        String message = String.format(repository.MASK_NOT_FOUND, rndId);

        assertThrows(NotFoundException.class, () -> service.getById(rndId), message);
    }

    @Test
    void save() {
        Device entity = getNewEntityWithoutIdAndTimestamps(8);

        Device entityTest = service.save(entity.toBuilder().build());
        assertEqualsEntityWithoutIdAndTimestamps(entity, entityTest);

        Device entityFromBD = repository.getOrThrow(entityTest.getId());
        assertEqualsEntity(entityFromBD, entityTest);

        assertNotEquals(entity.getId(), entityTest.getId());
        assertNotEquals(entity.getCreationDate(), entityTest.getCreationDate());
        assertNotEquals(entity.getModifyDate(), entityTest.getModifyDate());
    }

    @Test
    void update() {
        Device entity = getNewEntityFromDB(8);

        Device entityForTest = entity.toBuilder()
                .comment(generator.nextObject(String.class))
                .build();

        Device entityTest = service.update(entityForTest);
        assertEqualsEntityWithoutIdAndTimestamps(entityForTest, entityTest);
        assertEquals(entityForTest.getId(), entityTest.getId());
        assertEquals(entityForTest.getCreationDate(), entityTest.getCreationDate());

        Device entityFromBD = repository.getOrThrow(entityTest.getId());
        assertEqualsEntityWithoutIdAndTimestamps(entityForTest, entityFromBD);
        assertEquals(entityForTest.getId(), entityFromBD.getId());
        assertEquals(entityForTest.getCreationDate(), entityFromBD.getCreationDate());

        assertEqualsEntity(entityFromBD, entityTest);

        assertEquals(entity.getId(), entityTest.getId());
        assertEquals(entity.getCreationDate(), entityTest.getCreationDate());
        assertNotEquals(entity.getComment(), entityTest.getComment());
        assertNotEquals(entity.getModifyDate(), entityTest.getModifyDate());
    }

    @Test
    void update_rnd() {
        Device entity = generator.nextObject(Device.class);
        UUID rnd = entity.getId();
        String message = String.format(repository.MASK_NOT_FOUND, rnd);

        assertThrows(NotFoundException.class, () -> service.update(entity), message);
    }

    @Test
    void delete() {
        Device entity = getNewEntityFromDB(8);

        assertDoesNotThrow(() -> service.delete(entity.getId()));
    }

    @Test
    void delete_rnd() {
        UUID rndId = UUID.randomUUID();
        String message = String.format(repository.MASK_NOT_FOUND, rndId);

        assertThrows(NotFoundException.class, () -> service.delete(rndId), message);
    }

    private Device getNewEntityWithoutIdAndTimestamps(int amountChannels){
        Device entity = generator.nextObject(Device.class);
        entity.setId(null);
        entity.setCreationDate(null);
        entity.setModifyDate(null);
        entity.setAmountChannels(amountChannels);

        return entity;
    }

    private Device getNewEntityFromDB(int amountChannels){
        Device entity = repository.saveAndFlush(getNewEntityWithoutIdAndTimestamps(amountChannels));
        try {
            sleep(10);
        } catch (Exception ignored) {}

        return repository.getOrThrow(entity.getId());
    }

    private List<Device> getNewEntityListFromDB(int amountChannels, int count){
        List<Device> entities = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            entities.add(getNewEntityWithoutIdAndTimestamps(amountChannels));
        }
        repository.saveAllAndFlush(entities);

        return repository.findAll();
    }

    private void assertEqualsEntity(Device entity, Device test){
        assertEqualsEntityWithoutIdAndTimestamps(entity, test);
        assertEquals(entity.getId(), test.getId());
        assertEquals(entity.getCreationDate(), test.getCreationDate());
        assertEquals(entity.getModifyDate(), test.getModifyDate());
    }

    private void assertEqualsEntityWithoutIdAndTimestamps(Device entity, Device test){
        assertNotNull(entity);
        assertNotNull(test);
        assertEquals(entity.getNumber(), test.getNumber());
        assertEquals(entity.getComment(), test.getComment());
        assertEquals(entity.getAmountChannels(), test.getAmountChannels());
    }
}