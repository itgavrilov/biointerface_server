package ru.gsa.biointerface.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.gsa.biointerface.TestUtils;
import ru.gsa.biointerface.domain.entity.Channel;
import ru.gsa.biointerface.domain.entity.ChannelName;
import ru.gsa.biointerface.domain.entity.Device;
import ru.gsa.biointerface.domain.entity.Examination;
import ru.gsa.biointerface.domain.entity.Patient;
import ru.gsa.biointerface.exception.NotFoundException;
import ru.gsa.biointerface.repository.ChannelRepository;

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

@ExtendWith(MockitoExtension.class)
class ChannelServiceUnitTest {

    @Mock
    private ChannelRepository repository;

    @InjectMocks
    private ChannelService service;

    @Test
    void findAll() {
        List<Channel> entities = getNewEntityList(getNewExamination(), null, 5);
        when(repository.findAllByExaminationIdAndChannelNameId(null, null)).thenReturn(entities);

        List<Channel> entityTests = service.findAll(null, null);

        assertNotNull(entityTests);

        entityTests.forEach(entityTest -> {
            Channel entity = entities.stream()
                    .filter(e -> e.getId().equals(entityTest.getId()))
                    .findAny().orElseThrow();
            assertEqualsEntity(entity, entityTest);
        });

        verify(repository).findAllByExaminationIdAndChannelNameId(null, null);
    }

    @Test
    void findAll_empty() {
        when(repository.findAllByExaminationIdAndChannelNameId(null, null)).thenReturn(new ArrayList<>());

        List<Channel> entityTests1 = service.findAll(null, null);
        assertNotNull(entityTests1);
        assertIterableEquals(new ArrayList<>(), entityTests1);
        verify(repository).findAllByExaminationIdAndChannelNameId(null, null);
    }

    @Test
    void findAll_byExamination() {
        Examination examination = getNewExamination();
        List<Channel> entities = getNewEntityList(examination, null, 5);
        when(repository.findAllByExaminationIdAndChannelNameId(examination.getId(), null)).thenReturn(entities);

        List<Channel> entityTests = service.findAll(examination.getId(), null);

        assertNotNull(entityTests);

        entityTests.forEach(entityTest -> {
            Channel entity = entities.stream()
                    .filter(e -> e.getId().equals(entityTest.getId()))
                    .findAny().orElseThrow();
            assertEqualsEntity(entity, entityTest);
        });

        verify(repository).findAllByExaminationIdAndChannelNameId(examination.getId(), null);
    }

    @Test
    void findAll_byChannelName() {
        ChannelName channelName = TestUtils.getNewChannelName();
        List<Channel> entities = getNewEntityList(getNewExamination(), channelName, 5);
        entities.forEach(e -> e.setChannelName(channelName));
        when(repository.findAllByExaminationIdAndChannelNameId(null, channelName.getId())).thenReturn(entities);

        List<Channel> entityTests = service.findAll(null, channelName.getId());

        assertNotNull(entityTests);

        entityTests.forEach(entityTest -> {
            Channel entity = entities.stream()
                    .filter(e -> e.getId().equals(entityTest.getId()))
                    .findAny().orElseThrow();
            assertEqualsEntity(entity, entityTest);
        });

        verify(repository).findAllByExaminationIdAndChannelNameId(null, channelName.getId());
    }

    @Test
    void findAll_byExaminationAndChannelName() {
        Examination examination = getNewExamination();
        ChannelName channelName = TestUtils.getNewChannelName();
        List<Channel> entities = getNewEntityList(examination, channelName, 5);
        when(repository.findAllByExaminationIdAndChannelNameId(examination.getId(), channelName.getId())).thenReturn(entities);

        List<Channel> entityTests = service.findAll(examination.getId(), channelName.getId());

        assertNotNull(entityTests);

        entityTests.forEach(entityTest -> {
            Channel entity = entities.stream()
                    .filter(e -> e.getId().equals(entityTest.getId()))
                    .findAny().orElseThrow();
            assertEqualsEntity(entity, entityTest);
        });

        verify(repository).findAllByExaminationIdAndChannelNameId(examination.getId(), channelName.getId());
    }

    @Test
    void getById() {
        Examination examination = getNewExamination();
        Channel entity = TestUtils.getNewChannel(examination, null, (byte) 1);
        when(repository.getOrThrow(entity.getId().getExaminationId(), entity.getId().getNumber())).thenReturn(entity);

        Channel entityTest = service.getById(entity.getId().getExaminationId(), entity.getId().getNumber());

        assertEqualsEntity(entity, entityTest);

        verify(repository).getOrThrow(entity.getId().getExaminationId(), entity.getId().getNumber());
    }

    @Test
    void save() {
        Examination examination = getNewExamination();
        Channel entity = TestUtils.getNewChannel(examination, null, (byte) 1);
        when(repository.save(entity)).thenReturn(entity);

        Channel entityTest = service.save(entity);

        assertEqualsEntity(entity, entityTest);

        verify(repository).save(entity);
    }

    @Test
    void update() {
        ChannelName channelName = TestUtils.getNewChannelName();
        Examination examination = getNewExamination();
        Channel entity = TestUtils.getNewChannel(examination, channelName, (byte) 1);
        Channel entityClone = entity.toBuilder().build();
        ChannelName channelNameForTest = TestUtils.getNewChannelName();
        Channel entityForTest = TestUtils.getNewChannel(examination, channelNameForTest, (byte) 1);

        when(repository.getOrThrow(entityForTest.getId())).thenReturn(entityClone);

        Channel entityTest = service.update(entityForTest);

        assertEqualsEntityWithoutIdTimestamps(entityForTest, entityTest);
        assertEquals(entityForTest.getId(), entityTest.getId());
        assertEquals(entity.getId(), entityTest.getId());
        assertEquals(entity.getExamination(), entityTest.getExamination());

        assertNotEquals(entity.getChannelName(), entityTest.getChannelName());
        assertNotEquals(entity.getComment(), entityTest.getComment());
        assertNotEquals(entityForTest.getCreationDate(), entityTest.getCreationDate());
        assertNotEquals(entityForTest.getModifyDate(), entityTest.getModifyDate());

        verify(repository).getOrThrow(entityForTest.getId());
    }

    @Test
    void update_rnd() {
        Examination examination = getNewExamination();
        String message = String.format(repository.MASK_NOT_FOUND, examination.getId(), 1);
        when(repository.getOrThrow(examination.getId(), (byte) 1)).thenThrow(new NotFoundException(message));

        assertThrows(NotFoundException.class, () -> service.delete(examination.getId(), (byte) 1), message);
        verify(repository).getOrThrow(examination.getId(), (byte) 1);
    }

    @Test
    void delete() {
        Examination examination = getNewExamination();
        Channel entity = TestUtils.getNewChannel(examination, null, (byte) 1);
        UUID examinationId = examination.getId();
        Byte number = entity.getId().getNumber();
        when(repository.getOrThrow(examinationId, number)).thenReturn(entity);

        assertDoesNotThrow(() -> service.delete(examinationId, number));
        verify(repository).getOrThrow(examinationId, number);
        verify(repository).delete(entity);
    }

    @Test
    void delete_rnd() {
        Examination examination = getNewExamination();
        Channel entity = TestUtils.getNewChannel(examination, null, (byte) 1);
        UUID examinationId = examination.getId();
        Byte number = 1;
        String message = String.format(repository.MASK_NOT_FOUND, examinationId, number);
        when(repository.getOrThrow(examinationId, number)).thenThrow(new NotFoundException(message));

        assertThrows(NotFoundException.class, () -> service.delete(examinationId, number), message);

        verify(repository).getOrThrow(examinationId, number);
    }

    private Examination getNewExamination() {
        Patient patient = TestUtils.getNewPatient(null, 10);
        Device device = TestUtils.getNewDevice(8);
        Examination examination = TestUtils.getNewExamination(patient, device);

        return examination;
    }

    private List<Channel> getNewEntityList(Examination examination, ChannelName channelName, int count) {
        List<Channel> entities = new ArrayList<>();

        for (byte i = 0; i < count; i++) {
            Channel entity = TestUtils.getNewChannel(examination, channelName, i);
            entities.add(entity);
        }

        examination.setChannels(entities);

        return entities;
    }

    private void assertEqualsEntity(Channel entity, Channel test) {
        assertEqualsEntityWithoutIdTimestamps(entity, test);
        assertEquals(entity.getId(), test.getId());
        assertEquals(entity.getExamination(), test.getExamination());
        assertIterableEquals(entity.getSamples(), test.getSamples());
    }

    private void assertEqualsEntityWithoutIdTimestamps(Channel entity, Channel test) {
        assertNotNull(entity);
        assertNotNull(test);
        assertEquals(entity.getChannelName(), test.getChannelName());
        assertEquals(entity.getComment(), test.getComment());
    }
}