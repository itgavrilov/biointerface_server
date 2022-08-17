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
import ru.gsa.biointerface.domain.entity.ChannelName;
import ru.gsa.biointerface.exception.NotFoundException;
import ru.gsa.biointerface.repository.ChannelNameRepository;
import ru.gsa.biointerface.service.ChannelNameService;

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
import static ru.gsa.biointerface.utils.ChannelNameUtil.assertEqualsChannelName;
import static ru.gsa.biointerface.utils.ChannelNameUtil.assertEqualsChannelNameLight;
import static ru.gsa.biointerface.utils.ChannelNameUtil.getChannelName;
import static ru.gsa.biointerface.utils.ChannelNameUtil.getChannelNames;

@Tag("UnitTest")
@ExtendWith(MockitoExtension.class)
class ChannelNameServiceTest {

    @Mock
    private ChannelNameRepository repository;

    @InjectMocks
    private ChannelNameService service;

    @Test
    void findAll() {
        List<ChannelName> entities = getChannelNames(5);
        when(repository.findAll()).thenReturn(entities);

        List<ChannelName> entityTests = service.findAll();

        assertNotNull(entityTests);

        entityTests.forEach(entityTest -> {
            ChannelName entity = entities.stream()
                    .filter(e -> e.getId().equals(entityTest.getId()))
                    .findAny().orElseThrow();
            assertEqualsChannelName(entity, entityTest);
        });

        verify(repository).findAll();
    }

    @Test
    void findAll_empty() {
        when(repository.findAll()).thenReturn(new ArrayList<>());

        List<ChannelName> entityTests = service.findAll();
        assertNotNull(entityTests);
        assertIterableEquals(new ArrayList<>(), entityTests);
        verify(repository).findAll();
    }

    @Test
    void findAllPageable() {
        List<ChannelName> entities = getChannelNames(15);
        Pageable pageable = PageRequest.of(0, 5);

        while (pageable.getPageNumber() * pageable.getPageSize() <= entities.size()) {
            int start = pageable.getPageNumber() * pageable.getPageSize();
            int end = Math.min(start + pageable.getPageSize(), entities.size());
            List<ChannelName> pageList = entities.subList(start, end);
            Page<ChannelName> entityPage = new PageImpl<>(pageList, pageable, entities.size());
            when(repository.findAll(pageable)).thenReturn(entityPage);

            Page<ChannelName> entityPageTests = service.findAll(pageable);
            assertNotNull(entityPageTests);

            entityPageTests.getContent().forEach(entityTest -> {
                assertNotNull(entityTest);
                ChannelName entity = entities.stream()
                        .filter(e -> e.getId().equals(entityTest.getId()))
                        .findAny().orElseThrow();
                assertEqualsChannelName(entity, entityTest);
            });

            verify(repository).findAll(pageable);
            pageable = PageRequest.of(pageable.getPageNumber() + 1, pageable.getPageSize());
        }
    }

    @Test
    void findAllPageable_empty() {
        Pageable pageable = PageRequest.of(0, 5);
        Page<ChannelName> entityPage = new PageImpl<>(new ArrayList<>(), pageable, 0);
        when(repository.findAll(pageable)).thenReturn(entityPage);

        Page<ChannelName> entityPageTests = service.findAll(pageable);
        assertNotNull(entityPageTests);
        assertIterableEquals(entityPage, entityPageTests);
        verify(repository).findAll(pageable);
    }

    @Test
    void getById() {
        ChannelName entity = getChannelName();
        when(repository.getOrThrow(entity.getId())).thenReturn(entity);

        ChannelName entityTest = service.getById(entity.getId());

        assertEqualsChannelName(entity, entityTest);

        verify(repository).getOrThrow(entity.getId());
    }

    @Test
    void getById_rnd() {
        UUID rnd = UUID.randomUUID();
        String message = String.format(repository.MASK_NOT_FOUND, rnd);
        when(repository.getOrThrow(rnd)).thenThrow(new NotFoundException(message));

        assertThrows(NotFoundException.class, () -> service.getById(rnd), message);
        verify(repository).getOrThrow(rnd);
    }

    @Test
    void save() {
        ChannelName entity = getChannelName();
        ChannelName entityClone = entity.toBuilder().build();
        when(repository.existsByName(entityClone.getName())).thenReturn(false);
        when(repository.save(entityClone)).thenReturn(entity);

        ChannelName entityTest = service.save(entityClone);
        assertEqualsChannelName(entity, entityTest);

        verify(repository).existsByName(entityClone.getName());
        verify(repository).save(entityClone);
    }

    @Test
    void update() {
        ChannelName entity = getChannelName();

        ChannelName entityForTest = getChannelName();
        entityForTest.setId(entity.getId());

        when(repository.getOrThrow(entity.getId())).thenReturn(entity.toBuilder().build());

        ChannelName entityTest = service.update(entityForTest);
        assertEqualsChannelNameLight(entityForTest, entityTest);
        assertEquals(entityForTest.getId(), entityTest.getId());

        assertEquals(entity.getId(), entityTest.getId());
        assertNotEquals(entity.getName(), entityTest.getName());
        assertNotEquals(entity.getComment(), entityTest.getComment());

        verify(repository).getOrThrow(entity.getId());
    }

    @Test
    void update_rnd() {
        ChannelName entity = getChannelName();
        String message = String.format(repository.MASK_NOT_FOUND, entity.getId());
        when(repository.getOrThrow(entity.getId())).thenThrow(new NotFoundException(message));

        assertThrows(NotFoundException.class, () -> service.update(entity), message);
        verify(repository).getOrThrow(entity.getId());
    }

    @Test
    void delete() {
        ChannelName entity = getChannelName();
        when(repository.getOrThrow(entity.getId())).thenReturn(entity);

        assertDoesNotThrow(() -> service.delete(entity.getId()));
        verify(repository).getOrThrow(entity.getId());
        verify(repository).delete(entity);
    }

    @Test
    void delete_rnd() {
        UUID rnd = UUID.randomUUID();
        String message = String.format(repository.MASK_NOT_FOUND, rnd);
        when(repository.getOrThrow(rnd)).thenThrow(new NotFoundException(message));

        assertThrows(NotFoundException.class, () -> service.delete(rnd), message);
        verify(repository).getOrThrow(rnd);
    }
}