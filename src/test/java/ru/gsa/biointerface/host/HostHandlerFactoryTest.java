package ru.gsa.biointerface.host;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import ru.gsa.biointerface.domain.entity.Device;

import java.util.List;

import static java.lang.Thread.sleep;

@RunWith(SpringRunner.class)
@SpringBootTest
class HostHandlerFactoryTest {
    @Autowired
    private ConnectionToDeviceHandlerFactory connectionToDeviceHandlerFactory;

    @Test
    void getDevices() {
        try {
            connectionToDeviceHandlerFactory.scanningSerialPort();
            sleep(1000);
            List<Device> serialPorts = connectionToDeviceHandlerFactory.getDevices();
            System.out.println(serialPorts);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}