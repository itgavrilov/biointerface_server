package ru.gsa.biointerface.domain.dto.examination;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import ru.gsa.biointerface.domain.dto.device.DeviceDTO;
import ru.gsa.biointerface.domain.dto.patient.PatientDTO;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.UUID;

/**
 * DTO (Data Transfer Object) исследования
 * <p>
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 17/11/2021
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@Schema(name = "ExaminationUpdateDTO", description = "Result of biopotential measurements for update")
public class ExaminationUpdateDTO implements Serializable {

    static final long SerialVersionUID = 1L;

    /**
     * Идентификатор карточки пациента {@link PatientDTO#getId()}
     */
    @Schema(description = "Patient ID", required = true)
    @NotNull(message = "PatientId can't be null")
    protected UUID patientId;

    /**
     * Идентификатор контроллера биоинтерфейса {@link DeviceDTO#getId()}
     */
    @Schema(description = "Device ID", required = true)
    @NotNull(message = "DeviceId can't be null")
    protected UUID deviceId;

    /**
     * Комментарий
     */
    @Schema(description = "Comment")
    @Size(max = 400, message = "Comment can't be more than 400 chars")
    protected String comment;

    @Override
    public String toString() {

        return "Examination{" +
                ", patientRecord_id=" + patientId +
                ", device_id=" + deviceId +
                '}';
    }
}
