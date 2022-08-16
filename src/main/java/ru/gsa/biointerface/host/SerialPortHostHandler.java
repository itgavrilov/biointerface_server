package ru.gsa.biointerface.host;

import com.fazecast.jSerialComm.SerialPort;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ru.gsa.biointerface.domain.entity.Channel;
import ru.gsa.biointerface.domain.entity.ChannelName;
import ru.gsa.biointerface.domain.entity.Device;
import ru.gsa.biointerface.domain.entity.Examination;
import ru.gsa.biointerface.domain.entity.Patient;
import ru.gsa.biointerface.host.cash.Cash;
import ru.gsa.biointerface.host.cash.DataListener;
import ru.gsa.biointerface.host.cash.SampleCash;
import ru.gsa.biointerface.host.exception.HostNotRunningException;
import ru.gsa.biointerface.host.exception.HostNotTransmissionException;
import ru.gsa.biointerface.host.serialport.ControlMessages;
import ru.gsa.biointerface.host.serialport.DataCollector;
import ru.gsa.biointerface.host.serialport.SerialPortHost;
import ru.gsa.biointerface.service.ChannelService;
import ru.gsa.biointerface.service.DeviceService;
import ru.gsa.biointerface.service.ExaminationService;
import ru.gsa.biointerface.service.SampleService;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 10.09.2021.
 */
@Slf4j
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Component()
@Scope("prototype")
public class SerialPortHostHandler implements DataCollector, HostHandler {
    @EqualsAndHashCode.Include
    private final SerialPortHost serialPortHost;
    private final List<Cash> cashList = new ArrayList<>();
    private final List<ChannelName> channelNames = new ArrayList<>();
    @Autowired
    private DeviceService deviceService;
    @Autowired
    private ExaminationService examinationService;
    @Autowired
    private ChannelService channelService;
    @Autowired
    private SampleService sampleService;

    private Device device;
    private Patient patient;
    private Examination examination;
    private String comment;
    private boolean flagTransmission = false;

    public SerialPortHostHandler(SerialPort serialPort) {
        if (serialPort == null)
            throw new NullPointerException("SerialPort is null");

        serialPortHost = new SerialPortHost(serialPort, this);
        log.info("Crate connection to serialPort(SystemPortName={})", serialPort.getSystemPortName());
    }

    @Override
    public void setPatientRecord(Patient patient) {
        if (patient == null)
            throw new NullPointerException("PatientRecord is null");
        if (device == null)
            throw new NullPointerException("Device null");

        this.patient = patient;
    }

    @Override
    public int getAmountChannels() {
        return device.getAmountChannels();
    }

    @Override
    public Device getDevice() {
        return device;
    }

    @Override
    public boolean isAvailableDevice() {
        return device != null;
    }

    @Override
    public void setListenerInChannel(int number, DataListener listener) {
        if (listener == null)
            throw new NullPointerException("Listener null");
        if (number >= cashList.size() || number < 0)
            throw new IllegalArgumentException("Number > amount cashList");
        if (device == null)
            throw new NullPointerException("Device is null");

        cashList.get(number).setListener(listener);
    }

    @Override
    public void setNameInChannel(int number, ChannelName channelName) {
        if (number < 0)
            throw new IllegalArgumentException("Number < 0");
        if (device == null)
            throw new NullPointerException("Device is null");
        if (number >= channelNames.size())
            throw new IllegalArgumentException("Number > amount serviceChannelList");

        channelNames.set(number, channelName);

        if (examination != null) {
            examination.getChannels().get(number).setChannelName(channelName);
        }

        if (channelName != null) {
            log.info("ChannelName(id={}) is set in channel(number={})", channelName.getId(), number);
        } else {
            log.info("ChannelName is reset in channel(number={})", number);
        }
    }

    @Override
    public void setCommentForExamination(String newComment) throws Exception {
        this.comment = newComment;
        log.info("Set new comment for examination");

        if (examination != null) {
            String comment = examination.getComment();
            if (Objects.equals(comment, newComment)) {
                try {
                    examination.setComment(newComment);
                    examinationService.update(examination);
                    log.info("New comment is set in examination(number={})", examination.getId());
                } catch (Exception e) {
                    examination.setComment(comment);
                    log.error("Error update examination(number={})", examination.getId(), e);
                    throw e;
                }
            }
        }
    }

    @Override
    public void connect() {
        if (!isConnected()) {
            try {
                serialPortHost.start();
                serialPortHost.sendPackage(ControlMessages.GET_CONFIG);
                log.info("Connecting to device");
            } catch (Exception e) {
                log.error("Host is not running", e);
            }
        } else {
            log.warn("Device is already connected");
        }
    }

    @Override
    public void disconnect() throws Exception {
        if (isConnected()) {
            if (isTransmission()) {
                if (isRecording()) {
                    recordingStop();
                }
                transmissionStop();
            }
            serialPortHost.stop();
            log.info("Disconnecting from device");
        } else {
            log.warn("Device is already disconnected");
        }
    }

    @Override
    public boolean isConnected() {
        boolean result = false;

        if (serialPortHost != null && serialPortHost.isRunning())
            result = serialPortHost.portIsOpen();

        return result;
    }

    @Override
    public void transmissionStart() throws Exception {
        if (serialPortHost == null)
            throw new NullPointerException("Host is null");
        if (!serialPortHost.isRunning())
            throw new HostNotRunningException();
        if (device == null)
            throw new NullPointerException("Device null");
        if (patient == null)
            throw new NullPointerException("PatientRecordService is not init. First call setPatientRecord()");

        serialPortHost.sendPackage(ControlMessages.START_TRANSMISSION);
        flagTransmission = true;
        log.info("Start transmission");
    }

    @Override
    public void transmissionStop() throws Exception {
        if (serialPortHost == null)
            throw new NullPointerException("Host is null");
        if (!serialPortHost.isRunning())
            throw new HostNotRunningException();

        if (isRecording()) {
            recordingStop();
        }

        serialPortHost.sendPackage(ControlMessages.STOP_TRANSMISSION);
        flagTransmission = false;
        log.info("Stop transmission");
    }

    @Override
    public boolean isTransmission() {
        return flagTransmission;
    }

    @Override
    public void recordingStart() throws Exception {
        if (device == null)
            throw new NullPointerException("Device null");
        if (patient == null)
            throw new NullPointerException("PatientRecordService is not init. First call setPatientRecord()");
        if (!flagTransmission)
            throw new HostNotTransmissionException();

        if (!isRecording()) {
            device = deviceService.save(device);
            examination = new Examination(patient.getId(), device.getId(), comment);
            examination = examinationService.save(examination);

            for (byte i = 0; i < device.getAmountChannels(); i++) {
                Channel channel = new Channel(i, examination, channelNames.get(i));
                channel = channelService.save(channel);
                examination.getChannels().add(channel);
            }

            examinationService.recordingStart(examination);
            log.info("Start recording");
        } else {
            log.warn("Recording is already in progress");
        }
    }

    @Override
    public void recordingStop() throws Exception {
        if (!isRecording())
            throw new HostNotRunningException();

        examinationService.recordingStop();
        examination = null;
        log.info("Stop recording");
    }

    @Override
    public boolean isRecording() {
        boolean result = false;

        if (examination != null) {
            result = examinationService.isRecording();
        }

        return result;
    }

    @Override
    public void controllerReboot() throws Exception {
        if (serialPortHost == null)
            throw new NullPointerException("Host is null");
        if (!serialPortHost.isRunning())
            throw new HostNotRunningException();

        serialPortHost.sendPackage(ControlMessages.REBOOT);
        disconnect();
        log.info("Reboot controller");
    }

    @Override
    public void setDevice(String serialNumber, byte amountChannels) {
        if (serialNumber.isBlank())
            throw new IllegalArgumentException("SerialNumber can't be blank");
        if (amountChannels <= 0)
            throw new IllegalArgumentException("amountChannels <= 0");

        if (device == null || device.getNumber().equals(serialNumber)) {
            device = new Device(serialNumber, amountChannels, "Found automatically");
            examination = null;
            patient = null;

            for (int i = 0; i < device.getAmountChannels(); i++) {
                cashList.add(new SampleCash());
                channelNames.add(null);
            }
        }
    }

    @Override
    public void setSampleInChannel(int number, int value) {
        if (number >= cashList.size() || number < 0)
            throw new IllegalArgumentException("Number > amount cashList");
        if (device == null)
            throw new NullPointerException("Device is null");

        cashList.get(number).add(value);

        if (isRecording()) {
            try {
                sampleService.setSampleInChannel(
                        examination.getChannels().get(number),
                        value
                );
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void setFlagTransmission() {
        flagTransmission = true;
    }

    @Override
    public String toString() {
        return "ConnectionHandler{" +
                "serialPortHost=" + serialPortHost.toString() +
                '}';
    }
}
