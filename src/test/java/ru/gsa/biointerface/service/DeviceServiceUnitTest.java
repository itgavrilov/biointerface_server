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
import ru.gsa.biointerface.exception.NotFoundException;
import ru.gsa.biointerface.repository.DeviceRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
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
        List<Device> entities = getNewEntityList(8, 5);
        when(repository.findAll()).thenReturn(entities);

        List<Device> entityTests = service.findAll();

        assertNotNull(entityTests);

        entityTests.forEach(entityTest -> {
            Device entity = entities.stream()
                    .filter(e -> e.getId().equals(entityTest.getId()))
                    .findAny().orElseThrow();
            assertEqualsEntity(entity, entityTest);
        });

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
        List<Device> entities = getNewEntityList(8, 15);
        Pageable pageable = PageRequest.of(0, 5);

        while (pageable.getPageNumber() * pageable.getPageSize() <= entities.size()) {
            int start = pageable.getPageNumber() * pageable.getPageSize();
            int end = Math.min(start + pageable.getPageSize(), entities.size());
            List<Device> pageList = entities.subList(start, end);
            Page<Device> entityPage = new PageImpl<>(pageList, pageable, pageList.size());
            when(repository.findAll(pageable)).thenReturn(entityPage);

            Page<Device> entityPageTests = service.findAll(pageable);

            assertNotNull(entityPageTests);

            entityPageTests.getContent().forEach(entityTest -> {
                assertNotNull(entityTest);
                Device entity = entities.stream()
                        .filter(e -> e.getId().equals(entityTest.getId()))
                        .findAny().orElseThrow();
                assertEqualsEntity(entity, entityTest);
            });

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
        Device entity = getNewEntity(8);
        when(repository.getOrThrow(entity.getId())).thenReturn(entity);

        Device entityTest = service.getById(entity.getId());

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
    void save_entity() {
        Device entity = getNewEntity(8);
        Device entityClone = entity.toBuilder().build();
        when(repository.existsByNumber(entityClone.getNumber())).thenReturn(false);
        when(repository.save(entityClone)).thenReturn(entity);
        when(repository.getOrThrow(entity.getId())).thenReturn(entity);

        Device entityTest = service.save(entityClone);
        assertEqualsEntity(entity, entityTest);

        verify(repository).existsByNumber(entityClone.getNumber());
        verify(repository).save(entityClone);
        verify(repository).getOrThrow(entity.getId());
    }

    @Test
    void update() {
        Device entity = getNewEntity(8);
        Device entityForTest = entity.toBuilder()
                .comment(generator.nextObject(String.class))
                .build();

        when(repository.getOrThrow(entityForTest.getId())).thenReturn(entity.toBuilder().build());
        when(repository.save(entityForTest)).thenReturn(entityForTest);

        Device entityTest = service.update(entityForTest);
        assertEqualsEntityWithoutIdAndTimestamps(entityForTest, entityTest);

        assertEquals(entity.getId(), entityTest.getId());
        assertEquals(entity.getNumber(), entityTest.getNumber());
        assertEquals(entity.getAmountChannels(), entityTest.getAmountChannels());
        assertNotEquals(entity.getComment(), entityTest.getComment());

        verify(repository, times(2)).getOrThrow(entity.getId());
        verify(repository).save(entityForTest);
    }

    @Test
    void update_rnd() {
        Device entity = generator.nextObject(Device.class);
        UUID rnd = entity.getId();
        String message = String.format(repository.MASK_NOT_FOUND, rnd);
        when(repository.getOrThrow(rnd)).thenThrow(new NotFoundException(message));

        assertThrows(NotFoundException.class, () -> service.update(entity), message);
        verify(repository).getOrThrow(rnd);
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

    private Device getNewEntity(int amountChannels){
        Device entity = generator.nextObject(Device.class);
        entity.setAmountChannels(amountChannels);

        return entity;
    }

    private List<Device> getNewEntityList(int amountChannels, int count){
        List<Device> entities = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            entities.add(getNewEntity(amountChannels));
        }

        return entities;
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