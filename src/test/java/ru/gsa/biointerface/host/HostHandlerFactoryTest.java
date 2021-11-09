package ru.gsa.biointerface.host;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.gsa.biointerface.config.JpaConfig;
import ru.gsa.biointerface.domain.entity.Device;

import java.util.List;

import static java.lang.Thread.sleep;

class HostHandlerFactoryTest {

    @Test
    void getDevices() {
        try (AnnotationConfigApplicationContext context
                     = new AnnotationConfigApplicationContext(JpaConfig.class)) {

            ConnectionToDeviceHandlerFactory connectionToDeviceHandlerFactory = context.getBean(ConnectionToDeviceHandlerFactory.class);

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
}