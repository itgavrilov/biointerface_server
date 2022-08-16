package ru.gsa.biointerface.integration.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import ru.gsa.biointerface.domain.entity.ChannelName;
import ru.gsa.biointerface.exception.NotFoundException;
import ru.gsa.biointerface.repository.ChannelNameRepository;
import ru.gsa.biointerface.service.ChannelNameService;
import ru.gsa.biointerface.utils.ChannelNameUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Tag("IntegrationTest")
@SpringBootTest
@ActiveProfiles("test")
class ChannelNameServiceTest {

    private final ChannelNameService service;
    private final ChannelNameRepository repository;

    @Autowired
    public ChannelNameServiceTest(ChannelNameService service, ChannelNameRepository repository) {
        this.service = service;
        this.repository = repository;
    }

    @AfterEach
    void tearDown() {
        repository.deleteAll(repository.findAll());
    }

    @Test
    void findAll() {
        List<ChannelName> entities = getNewEntityListFromDB(5);

        List<ChannelName> entityTests = service.findAll();

        assertNotNull(entityTests);

        entityTests.forEach(entityTest -> {
            ChannelName entity = entities.stream()
                    .filter(e -> e.getId().equals(entityTest.getId()))
                    .findAny().orElseThrow();
            assertEqualsEntity(entity, entityTest);
        });
    }

    @Test
    void findAll_empty() {
        List<ChannelName> entityTests = service.findAll();
        assertNotNull(entityTests);
        assertIterableEquals(new ArrayList<>(), entityTests);
    }

    @Test
    void findAllPageable() {
        List<ChannelName> entities = getNewEntityListFromDB(15);
        Pageable pageable = PageRequest.of(0, 5);

        while (pageable.getPageNumber() * pageable.getPageSize() <= entities.size()) {
            Page<ChannelName> entityPageTests = service.findAll(pageable);
            assertNotNull(entityPageTests);

            entityPageTests.getContent().forEach(entityTest -> {
                assertNotNull(entityTest);
                ChannelName entity = entities.stream()
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
        Page<ChannelName> entityPage = new PageImpl<>(new ArrayList<>(), pageable, 0);

        Page<ChannelName> entityPageTests = service.findAll(pageable);
        assertNotNull(entityPageTests);
        assertIterableEquals(entityPage, entityPageTests);
    }

    @Test
    void getById() {
        ChannelName entity = getNewEntityFromDB();

        ChannelName entityTest = service.getById(entity.getId());

        assertEqualsEntity(entity, entityTest);
    }

    @Test
    void getById_rnd() {
        UUID rnd = UUID.randomUUID();
        String message = String.format(repository.MASK_NOT_FOUND, rnd);

        assertThrows(NotFoundException.class, () -> service.getById(rnd), message);
    }

    @Test
    void save() {
        ChannelName entity = getNewEntityWithoutIdAndTimestamps();

        ChannelName entityTest = service.save(entity.toBuilder().build());
        assertEqualsEntityWithoutIdAndTimestamps(entity, entityTest);

        ChannelName entityFromBD = repository.getOrThrow(entityTest.getId());
        assertEqualsEntity(entityFromBD, entityTest);

        assertNotEquals(entity.getId(), entityTest.getId());
        assertNotEquals(entity.getCreationDate(), entityTest.getCreationDate());
        assertNotEquals(entity.getModifyDate(), entityTest.getModifyDate());
    }

    @Test
    void update() {
        ChannelName entity = getNewEntityFromDB();

        ChannelName entityForTest = ChannelNameUtil.getChannelName();
        entityForTest.setId(entity.getId());
        entityForTest.setCreationDate(entity.getCreationDate());

        ChannelName entityTest = service.update(entityForTest);
        assertEqualsEntityWithoutIdAndTimestamps(entityForTest, entityTest);
        assertEquals(entityForTest.getId(), entityTest.getId());
        assertThat(entityForTest.getCreationDate()).isEqualToIgnoringNanos(entityTest.getCreationDate());

        ChannelName entityFromBD = repository.getOrThrow(entityTest.getId());
        assertEqualsEntityWithoutIdAndTimestamps(entityForTest, entityFromBD);
        assertEquals(entityForTest.getId(), entityFromBD.getId());
        assertThat(entityForTest.getCreationDate()).isEqualToIgnoringNanos(entityFromBD.getCreationDate());

        assertEqualsEntity(entityFromBD, entityTest);

        assertEquals(entity.getId(), entityTest.getId());
        assertThat(entity.getCreationDate()).isEqualToIgnoringNanos(entityTest.getCreationDate());
        assertNotEquals(entity.getName(), entityTest.getName());
        assertNotEquals(entity.getComment(), entityTest.getComment());
        assertNotEquals(entity.getModifyDate(), entityTest.getModifyDate());
    }

    @Test
    void update_rnd() {
        ChannelName entity = ChannelNameUtil.getChannelName();

        String message = String.format(repository.MASK_NOT_FOUND, entity.getId());
        assertThrows(NotFoundException.class, () -> service.update(entity), message);
    }

    @Test
    void delete() {
        ChannelName entity = getNewEntityFromDB();

        ChannelName finalEntity = entity;
        assertDoesNotThrow(() -> service.delete(finalEntity.getId()));
    }

    @Test
    void delete_rnd() {
        ChannelName entity = getNewEntityFromDB();

        assertDoesNotThrow(() -> service.delete(entity.getId()));
    }

    private ChannelName getNewEntityWithoutIdAndTimestamps() {
        ChannelName entity = ChannelNameUtil.getChannelName();
        entity.setId(null);
        entity.setCreationDate(null);
        entity.setModifyDate(null);

        return entity;
    }

    private ChannelName getNewEntityFromDB() {
        ChannelName entity = ChannelNameUtil.getChannelName();

        return repository.save(entity);
    }

    private List<ChannelName> getNewEntityListFromDB(int count) {
        List<ChannelName> entities = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            entities.add(getNewEntityWithoutIdAndTimestamps());
        }

        return repository.saveAll(entities);
    }

    private void assertEqualsEntity(ChannelName entity, ChannelName test) {
        assertEqualsEntityWithoutIdAndTimestamps(entity, test);
        assertEquals(entity.getId(), test.getId());
        assertThat(entity.getCreationDate()).isEqualToIgnoringNanos(test.getCreationDate());
        assertThat(entity.getModifyDate()).isEqualToIgnoringNanos(test.getModifyDate());
    }

    private void assertEqualsEntityWithoutIdAndTimestamps(ChannelName entity, ChannelName test) {
        assertNotNull(entity);
        assertNotNull(test);
        assertEquals(entity.getName(), test.getName());
        assertEquals(entity.getComment(), test.getComment());
    }
}