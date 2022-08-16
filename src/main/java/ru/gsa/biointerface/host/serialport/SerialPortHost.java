package ru.gsa.biointerface.host.serialport;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import ru.gsa.biointerface.host.serialport.packets.ChannelPacket;
import ru.gsa.biointerface.host.serialport.packets.ConfigPacket;
import ru.gsa.biointerface.host.serialport.packets.ControlPacket;
import ru.gsa.biointerface.host.serialport.packets.Packet;
import ru.gsa.biointerface.host.serialport.packets.PacketType;
import ru.gsa.biointerface.host.serialport.serverByPuchkov.AbstractServer;

import java.util.Arrays;

/**
 * Created by Пучков Константин on 12.03.2019.
 * Modified by Gavrilov Stepan on 16.08.2021.
 */
@Slf4j
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class SerialPortHost extends AbstractServer<Packet, Packet, SerialPort> implements SerialPortDataListener {
    @EqualsAndHashCode.Include
    private final SerialPort serialPort;

    public SerialPortHost(SerialPort serialPort, DataCollector dataCollector) {
        super();

        if (serialPort == null)
            throw new NullPointerException("SerialPort is null");
        if (dataCollector == null)
            throw new NullPointerException("DataCollector is null");
        this.serialPort = serialPort;

        handler = new SerialPortHandler(dataCollector);
        log.info("Handler for serialPort(SystemPortName={}) is started", serialPort.getSystemPortName());
    }

    public boolean portIsOpen() {
        return serialPort.isOpen();
    }

    @Override
    protected void doStart() {
        try {
            super.doStart();
            serialPort.setParity(SerialPort.NO_PARITY);
            serialPort.setNumStopBits(SerialPort.ONE_STOP_BIT);
            serialPort.setNumDataBits(8);
            serialPort.setBaudRate(512000);
            serialPort.setFlowControl(SerialPort.FLOW_CONTROL_DISABLED);
            serialPort.openPort();
            serialPort.addDataListener(this);
            log.info("SerialPortHost with serialPort(SystemPortName={}) started", serialPort.getSystemPortName());
        } catch (Exception e) {
            log.error("Error started serialPortHost with serialPort(SystemPortName={})", serialPort.getSystemPortName(), e);
        }
    }

    @Override
    protected void doStop() {
        try {
            super.doStop();
            if (serialPort.isOpen()) {
                serialPort.closePort();
                serialPort.removeDataListener();
                log.info("SerialPortHost with serialPort(SystemPortName={}) come to stop", serialPort.getSystemPortName());
            } else {
                log.warn("SerialPort(SystemPortName={}) is not open", serialPort.getSystemPortName());
            }
        } catch (Exception e) {
            log.error("SerialPortHost with serialPort(SystemPortName={} stop error)", serialPort.getSystemPortName(), e);
        }
    }

    @Override
    protected SerialPort getInterface() {
        return serialPort;
    }

    @Override
    protected void send(Packet packet) throws SerialPortNotOpenException {
        if (packet == null)
            throw new NullPointerException("Packet is null");
        if (!serialPort.isOpen())
            throw new SerialPortNotOpenException();

        serialPort.writeBytes(packet.getBytes(), packet.getBytes().length);
    }

    @Override
    public int getListeningEvents() {
        return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
    }

    @Override
    public void serialEvent(SerialPortEvent event) {
        if (event == null)
            throw new NullPointerException("Event is null");

        if (event.getEventType() != SerialPort.LISTENING_EVENT_DATA_AVAILABLE)
            return;

        if (serialPort.isOpen() && serialPort.bytesAvailable() > 0) {
            byte[] bytesFromSerialPort = new byte[serialPort.bytesAvailable()];
            int indexByteArray = 0;

            serialPort.readBytes(bytesFromSerialPort, serialPort.bytesAvailable());

            while (indexByteArray < bytesFromSerialPort.length - 4) {
                if (bytesFromSerialPort[indexByteArray] == -1 && bytesFromSerialPort[indexByteArray + 1] == -1) {
                    PacketType packetType = PacketType.findById(bytesFromSerialPort[indexByteArray + 2]);
                    int msgSize = bytesFromSerialPort[indexByteArray + 3];
                    int startOfPackage = indexByteArray + 4;
                    indexByteArray = startOfPackage + msgSize;

                    if (indexByteArray <= bytesFromSerialPort.length) {
                        byte[] msg = Arrays.copyOfRange(bytesFromSerialPort, startOfPackage, indexByteArray);
                        Packet packet;

                        switch (packetType) {
                            case CONFIG -> packet = new ConfigPacket(msg);
                            case CONTROL -> packet = new ControlPacket(msg);
                            case DATA -> packet = new ChannelPacket(msg);
                            default -> throw new IllegalStateException("Unexpected value: " + packetType);
                        }

                        try {
                            readBuffer.put(packet);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                } else
                    indexByteArray++;
            }
        }
    }

    @Override
    public String toString() {
        return "SerialPortHost{" +
                "serialPort=" + serialPort.getSystemPortName() +
                '}';
    }
}


