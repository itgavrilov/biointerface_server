package ru.gsa.biointerface.utils;

import lombok.SneakyThrows;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.Assertions;
import ru.gsa.biointerface.domain.entity.Device;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Thread.sleep;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class DeviceUtil {

    private static final EasyRandom generator = new EasyRandom();

    @SneakyThrows
    public static Device getDevice(int amountChannels) {
        Device entity = generator.nextObject(Device.class);
        entity.setAmountChannels((byte) amountChannels);
        sleep(10);

        return entity;
    }

    public static List<Device> getDevices(int amountChannels, int count) {
        List<Device> entities = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            entities.add(DeviceUtil.getDevice(amountChannels));
        }

        return entities;
    }

    public static void assertEqualsDevice(Device entity, Device test) {
        assertEqualsDeviceLight(entity, test);
        Assertions.assertEquals(entity.getId(), test.getId());
    }

    public static void assertEqualsDeviceLight(Device entity, Device test) {
        assertNotNull(entity);
        assertNotNull(test);
        Assertions.assertEquals(entity.getNumber(), test.getNumber());
        Assertions.assertEquals(entity.getComment(), test.getComment());
        Assertions.assertEquals(entity.getAmountChannels(), test.getAmountChannels());
    }
}
