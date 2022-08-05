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
import ru.gsa.biointerface.domain.entity.ChannelName;
import ru.gsa.biointerface.dto.ChannelNameDTO;
import ru.gsa.biointerface.exception.NotFoundException;
import ru.gsa.biointerface.repository.ChannelNameRepository;
import ru.gsa.biointerface.service.ChannelNameService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChannelNameServiceTest {

    private final EasyRandom generator = new EasyRandom();

    @Mock
    private ChannelNameRepository repository;

    @InjectMocks
    private ChannelNameService service;

    @Test
    void findAll() {
        List<ChannelName> entities = generator.objects(ChannelName.class, 5).toList();
        when(repository.findAll()).thenReturn(entities);

        List<ChannelName> entityTests = service.findAll();
        assertNotNull(entityTests);
        assertIterableEquals(entities, entityTests);
        for (int i = 0; i < entityTests.size(); i++) {
            assertNotNull(entities.get(i));
            assertEquals(entities.get(i).getId(), entityTests.get(i).getId());
            assertEquals(entities.get(i).getName(), entityTests.get(i).getName());
            assertEquals(entities.get(i).getComment(), entityTests.get(i).getComment());
            assertIterableEquals(entities.get(i).getChannels(), entityTests.get(i).getChannels());
        }

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
        List<ChannelName> entities = generator.objects(ChannelName.class, 15).toList();
        Pageable pageable = PageRequest.of(0, 5);

        while (pageable.getPageNumber() * pageable.getPageSize() <= entities.size()) {
            int start = pageable.getPageNumber() * pageable.getPageSize();
            int end = Math.min(start + pageable.getPageSize(), entities.size());
            List<ChannelName> pageList = entities.subList(start, end);
            Page<ChannelName> entityPage = new PageImpl<>(pageList, pageable, pageList.size());
            when(repository.findAll(pageable)).thenReturn(entityPage);

            Page<ChannelName> entityPageTests = service.findAll(pageable);
            assertNotNull(entityPageTests);
            assertIterableEquals(entityPage, entityPageTests);
            for (int i = 0; i < entityPage.getContent().size(); i++) {
                assertNotNull(entityPageTests.getContent().get(i));
                assertEquals(entityPage.getContent().get(i).getId(), entityPageTests.getContent().get(i).getId());
                assertEquals(entityPage.getContent().get(i).getName(), entityPageTests.getContent().get(i).getName());
                assertEquals(entityPage.getContent().get(i).getComment(), entityPageTests.getContent().get(i).getComment());
                assertIterableEquals(entityPage.getContent().get(i).getChannels(), entityPageTests.getContent().get(i).getChannels());
            }
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
        ChannelName entity = generator.nextObject(ChannelName.class);
        when(repository.getOrThrow(entity.getId())).thenReturn(entity);

        ChannelName entityTest = service.getById(entity.getId());
        assertNotNull(entityTest);
        assertEquals(entity.getId(), entityTest.getId());
        assertEquals(entity.getName(), entityTest.getName());
        assertEquals(entity.getComment(), entityTest.getComment());
        assertIterableEquals(entity.getChannels(), entityTest.getChannels());
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
        ChannelNameDTO dto = generator.nextObject(ChannelNameDTO.class);
        ChannelName entity = new ChannelName(
                dto.getId(),
                dto.getName(),
                dto.getComment(),
                new ArrayList<>());
        when(repository.findById(entity.getId())).thenReturn(Optional.of(entity));
        when(repository.save(entity)).thenReturn(entity);

        ChannelName entityTest = service.saveOrUpdate(dto);
        assertNotNull(entityTest);
        assertEquals(entity.getId(), entityTest.getId());
        assertEquals(entity.getName(), entityTest.getName());
        assertEquals(entity.getComment(), entityTest.getComment());
        assertIterableEquals(entity.getChannels(), entityTest.getChannels());

        verify(repository).findById(entity.getId());
        verify(repository).save(entity);
    }

    @Test
    void delete() {
        ChannelName entity = generator.nextObject(ChannelName.class);
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