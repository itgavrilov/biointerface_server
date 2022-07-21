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
import ru.gsa.biointerface.domain.entity.Icd;
import ru.gsa.biointerface.dto.IcdDTO;
import ru.gsa.biointerface.exception.NotFoundException;
import ru.gsa.biointerface.repository.IcdRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IcdServiceUnitTest {

    private final EasyRandom generator = new EasyRandom();

    @Mock
    private IcdRepository repository;

    @InjectMocks
    private IcdService service;

    @Test
    void findAll() {
        List<Icd> entities = generator.objects(Icd.class, 5).toList();
        when(repository.findAll()).thenReturn(entities);

        List<Icd> entityTests2 = service.findAll();
        assertNotNull(entityTests2);
        assertIterableEquals(entities, entityTests2);
        verify(repository).findAll();
    }

    @Test
    void findAll_empty() {
        when(repository.findAll()).thenReturn(new ArrayList<>());

        List<Icd> entityTests1 = service.findAll();
        assertNotNull(entityTests1);
        assertIterableEquals(new ArrayList<>(), entityTests1);
        verify(repository).findAll();
    }

    @Test
    void findAllPageable() {
        List<Icd> entities = generator.objects(Icd.class, 15).toList();
        Pageable pageable = PageRequest.of(0, 5);

        while (pageable.getPageNumber() * pageable.getPageSize() <= entities.size()) {
            int start = pageable.getPageNumber() * pageable.getPageSize();
            int end = Math.min(start + pageable.getPageSize(), entities.size());
            List<Icd> pageList = entities.subList(start, end);
            Page<Icd> entityPage2 = new PageImpl<>(pageList, pageable, pageList.size());
            when(repository.findAll(pageable)).thenReturn(entityPage2);

            Page<Icd> entityPageTests2 = service.findAll(pageable);
            assertNotNull(entityPageTests2);
            assertIterableEquals(entityPage2, entityPageTests2);
            verify(repository).findAll(pageable);
            pageable = PageRequest.of(pageable.getPageNumber() + 1, pageable.getPageSize());
        }
    }

    @Test
    void findAllPageable_empty() {
        Pageable pageable = PageRequest.of(0, 5);
        Page<Icd> entityPage1 = new PageImpl<>(new ArrayList<>(), pageable, 0);
        when(repository.findAll(pageable)).thenReturn(entityPage1);

        Page<Icd> entityPageTests1 = service.findAll(pageable);
        assertNotNull(entityPageTests1);
        assertEquals(entityPage1, entityPageTests1);
        verify(repository).findAll(pageable);
    }

    @Test
    void getById() {
        Icd entity = generator.nextObject(Icd.class);
        when(repository.getOrThrow(entity.getId())).thenReturn(entity);

        Icd entityTest = service.getById(entity.getId());
        assertNotNull(entityTest);
        assertEquals(entity, entityTest);
        verify(repository).getOrThrow(entity.getId());
    }

    @Test
    void getById_rnd() {
        int rnd = generator.nextInt();
        String message = String.format("Icd(id=%s) is not found", rnd);
        when(repository.getOrThrow(rnd)).thenThrow(new NotFoundException(message));

        assertThrows(NotFoundException.class, () -> service.getById(rnd), message);
        verify(repository).getOrThrow(rnd);
    }

    @Test
    void save() {
        IcdDTO dto = generator.nextObject(IcdDTO.class);
        Icd entity = new Icd(
                dto.getId(),
                dto.getName(),
                dto.getVersion(),
                dto.getComment(),
                new ArrayList<>());
        when(repository.findById(entity.getId())).thenReturn(Optional.of(entity));
        when(repository.save(entity)).thenReturn(entity);

        Icd entityTest = service.save(dto);
        assertNotNull(entityTest);
        assertEquals(entity, entityTest);
        verify(repository).findById(entity.getId());
        verify(repository).save(entity);
    }

    @Test
    void delete() {
    }
}