package ru.gsa.biointerface.mapper;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Test;
import ru.gsa.biointerface.domain.dto.сhannel.ChannelDTO;
import ru.gsa.biointerface.domain.dto.сhannel.ChannelUpdateDTO;
import ru.gsa.biointerface.domain.entity.Channel;
import ru.gsa.biointerface.domain.entity.ChannelName;
import ru.gsa.biointerface.domain.entity.Examination;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class ChannelMapperUnitTest {

    private final EasyRandom generator = new EasyRandom();
    private final ChannelMapper mapper = new ChannelMapperImpl();

    @Test
    void toDTO() {
        Channel entity = generator.nextObject(Channel.class);

        ChannelDTO dto = mapper.toDTO(entity);

        assertNotNull(dto);
        assertNotNull(dto.getNumber());
        assertEquals(entity.getId().getNumber(), dto.getNumber());
        assertNotNull(dto.getExaminationId());
        assertEquals(entity.getId().getExaminationId(), dto.getExaminationId());
        assertNotNull(dto.getChannelNameId());
        assertEquals(entity.getChannelName().getId(), dto.getChannelNameId());
        assertNotNull(dto.getCreationDate());
        assertEquals(entity.getCreationDate(), dto.getCreationDate());
        assertNotNull(dto.getModifyDate());
        assertEquals(entity.getModifyDate(), dto.getModifyDate());
    }

    @Test
    void toEntityFromChannelUpdateDTO() {
        ChannelUpdateDTO dto = generator.nextObject(ChannelUpdateDTO.class);
        Byte number = generator.nextObject(Byte.class);
        Examination examination = generator.nextObject(Examination.class);
        ChannelName channelName = generator.nextObject(ChannelName.class);

        Channel entity = mapper.toEntity(dto, number, examination, channelName);

        assertNotNull(entity);
        assertNotNull(entity.getId());
        assertNotNull(entity.getId().getNumber());
        assertEquals(number, entity.getId().getNumber());
        assertNotNull(entity.getExamination());
        assertEquals(examination, entity.getExamination());
        assertNotNull(entity.getChannelName());
        assertEquals(channelName, entity.getChannelName());
        assertNull(entity.getCreationDate());
        assertNull(entity.getModifyDate());
    }

    @Test
    void toEntityFromChannelDTO() {
        ChannelDTO dto = generator.nextObject(ChannelDTO.class);
        Examination examination = generator.nextObject(Examination.class);
        examination.setId(dto.getExaminationId());
        ChannelName channelName = generator.nextObject(ChannelName.class);
        channelName.setId(dto.getChannelNameId());

        Channel entity = mapper.toEntity(dto, examination, channelName);

        assertNotNull(entity);
        assertNotNull(entity.getId());
        assertNotNull(entity.getId().getNumber());
        assertEquals(dto.getNumber(), entity.getId().getNumber());
        assertNotNull(entity.getExamination());
        assertEquals(examination, entity.getExamination());
        assertNotNull(entity.getChannelName());
        assertEquals(channelName, entity.getChannelName());
        assertNotNull(entity.getCreationDate());
        assertEquals(dto.getCreationDate(), entity.getCreationDate());
        assertNotNull(entity.getModifyDate());
        assertEquals(dto.getModifyDate(), entity.getModifyDate());
    }
}