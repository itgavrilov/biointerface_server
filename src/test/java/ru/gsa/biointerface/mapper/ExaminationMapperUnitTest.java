package ru.gsa.biointerface.mapper;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.gsa.biointerface.domain.entity.Channel;
import ru.gsa.biointerface.domain.entity.Device;
import ru.gsa.biointerface.domain.entity.Examination;
import ru.gsa.biointerface.domain.entity.Patient;
import ru.gsa.biointerface.dto.ExaminationDTO;

import java.util.List;
import java.util.stream.Collectors;

class ExaminationMapperUnitTest {

    private final EasyRandom generator = new EasyRandom();
    private final ExaminationMapper mapper = new ExaminationMapperImpl();

    @Test
    void toDTO() {
        Examination entity = generator.nextObject(Examination.class);

        ExaminationDTO dto = mapper.toDTO(entity);

        Assertions.assertEquals(entity.getId(), dto.getId());
        Assertions.assertEquals(entity.getDevice().getId(), dto.getDeviceId());
        Assertions.assertEquals(entity.getPatient().getId(), dto.getPatientId());
        Assertions.assertEquals(entity.getStarttime(), dto.getStarttime());
        Assertions.assertEquals(entity.getComment(), dto.getComment());
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

        Assertions.assertEquals(dto.getId(), entity.getId());
        Assertions.assertEquals(dto.getComment(), entity.getComment());
        Assertions.assertEquals(device, entity.getDevice());
        Assertions.assertEquals(patient, entity.getPatient());
        Assertions.assertIterableEquals(channels, entity.getChannels());
    }
}