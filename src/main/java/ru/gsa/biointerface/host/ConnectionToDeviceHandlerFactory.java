package ru.gsa.biointerface.host;

import com.fazecast.jSerialComm.SerialPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import ru.gsa.biointerface.domain.entity.Device;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 28.10.2021.
 */
@Slf4j
@Component
public class ConnectionToDeviceHandlerFactory {
    private final List<HostHandler> hostHandlers = new ArrayList<>();
    private final ApplicationContext context;

    @Autowired
    public ConnectionToDeviceHandlerFactory(ApplicationContext context) {
        this.context = context;
    }

    public void scanningSerialPort() {
        hostHandlers.clear();
        List<SerialPort> serialPorts = getSerialPortsWithDevises();
        for (SerialPort serialPort : serialPorts) {
            HostHandler hostHandler = context.getBean(HostHandler.class, serialPort);
            hostHandler.connect();
            hostHandlers.add(hostHandler);
        }
        log.info("Scanning devices");
    }

    private List<SerialPort> getSerialPortsWithDevises() {
        return Arrays.stream(SerialPort.getCommPorts())
                .filter(o -> "BiointerfaceController".equals(o.getPortDescription()))
                .collect(Collectors.toList());
    }

    public List<Device> getDevices() {
        return hostHandlers.stream()
                .peek(o -> {
                    if (o.isConnected()) {
                        try {
                            o.disconnect();
                        } catch (Exception e) {
                            log.error("Device disconnect error", e);
                        }
                    }
                })
                .filter(HostHandler::isAvailableDevice)
                .map(HostHandler::getDevice)
                .sorted()
                .collect(Collectors.toList());
    }

    public HostHandler getConnection(Device device) {
        HostHandler hostHandler = hostHandlers.stream()
                .filter(o -> device.equals(o.getDevice()))
                .findFirst()
                .orElseThrow(NoSuchElementException::new);
        log.info("Get available devices");

        return hostHandler;
    }
}
