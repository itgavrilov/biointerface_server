package ru.gsa.biointerface.host.serialport.packets;

import java.nio.ByteBuffer;

public class ConfigPacket extends AbstractPacket {
    public ConfigPacket(byte[] msg) {
        super(PacketType.CONFIG, msg);
    }

    public int getSerialNumber() {
        byte[] serialNumber = new byte[]{0, 0, msg[1], msg[0]};

        return ByteBuffer.wrap(serialNumber).getInt();
    }

    public int getAmountChannels() {
        return msg[2];
    }
}
