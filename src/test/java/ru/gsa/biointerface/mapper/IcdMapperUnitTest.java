package ru.gsa.biointerface.mapper;

import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.gsa.biointerface.domain.entity.Icd;
import ru.gsa.biointerface.dto.IcdDTO;

class IcdMapperUnitTest {

    private final EasyRandom generator = new EasyRandom();
    private final IcdMapper mapper = new IcdMapperImpl();

    @Test
    void toDTO() {
        Icd entity = generator.nextObject(Icd.class);

        IcdDTO dto = mapper.toDTO(entity);

        Assertions.assertEquals(entity.getId(), dto.getId());
        Assertions.assertEquals(entity.getName(), dto.getName());
        Assertions.assertEquals(entity.getVersion(), dto.getVersion());
        Assertions.assertEquals(entity.getComment(), dto.getComment());
    }
}