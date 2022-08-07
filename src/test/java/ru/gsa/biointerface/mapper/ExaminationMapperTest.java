package ru.gsa.biointerface.mapper;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import ru.gsa.biointerface.domain.dto.ExaminationDTO;
import ru.gsa.biointerface.domain.entity.Channel;
import ru.gsa.biointerface.domain.entity.Device;
import ru.gsa.biointerface.domain.entity.Examination;
import ru.gsa.biointerface.domain.entity.Patient;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ExaminationMapperTest {

    private final EasyRandom generator = new EasyRandom();
    private final ExaminationMapper mapper = new ExaminationMapperImpl();

    @Test
    void toDTO() {
        Examination entity = generator.nextObject(Examination.class);

        ExaminationDTO dto = mapper.toDTO(entity);

        assertNotNull(dto);
        assertNotNull(dto.getId());
        assertEquals(entity.getId(), dto.getId());
        assertNotNull(dto.getDeviceId());
        assertEquals(entity.getDevice().getId(), dto.getDeviceId());
        assertNotNull(dto.getPatientId());
        assertEquals(entity.getPatient().getId(), dto.getPatientId());
        assertNotNull(dto.getDatetime());
        assertEquals(entity.getDatetime(), dto.getDatetime());
        assertNotNull(dto.getComment());
        assertEquals(entity.getComment(), dto.getComment());
    }

    @Test
    void toEntity() {
        ExaminationDTO dto = generator.nextObject(ExaminationDTO.class);
        Patient patient = generator.nextObject(Patient.class);
        Device device = generator.nextObject(Device.class);
        List<Channel> channels = generator.objects(Channel.class, 2).collect(Collectors.toList());
        device.setAmountChannels(channels.size());
        dto.setDeviceId(device.getId());
        dto.setPatientId(patient.getId());

        Examination entity = mapper.toEntity(dto, patient, device, channels);

        assertNotNull(entity);
        assertNotNull(entity.getId());
        assertEquals(dto.getId(), entity.getId());
        assertNotNull(entity.getComment());
        assertEquals(dto.getComment(), entity.getComment());
        assertNotNull(entity.getDevice());
        assertEquals(device, entity.getDevice());
        assertNotNull(entity.getPatient());
        assertEquals(patient, entity.getPatient());
        assertNotNull(entity.getChannels());
        assertIterableEquals(channels, entity.getChannels());
    }
}