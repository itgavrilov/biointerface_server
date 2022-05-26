package ru.gsa.biointerface.controller.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.gsa.biointerface.domain.Device;
import ru.gsa.biointerface.domain.Examination;
import ru.gsa.biointerface.domain.Patient;
import ru.gsa.biointerface.dto.ChannelNameDTO;
import ru.gsa.biointerface.dto.ErrorResponse;
import ru.gsa.biointerface.dto.ExaminationDTO;
import ru.gsa.biointerface.mapper.ExaminationMapper;
import ru.gsa.biointerface.service.ChannelService;
import ru.gsa.biointerface.service.DeviceService;
import ru.gsa.biointerface.service.ExaminationService;
import ru.gsa.biointerface.service.PatientService;

import java.net.URI;
import java.util.ArrayList;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 15/11/2021
 */
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Examinations", description = "results of biopotential measurements")
@RestController
@RequestMapping(
        value = "/examinations",
        produces = MediaType.APPLICATION_JSON_VALUE,
        consumes = MediaType.APPLICATION_JSON_VALUE)
public class ExaminationsController {
    private static final String version = "0.0.1-SNAPSHOT";

    private final ExaminationService service;
    private final PatientService patientService;
    private final DeviceService deviceService;
    private final ChannelService channelService;
    private final ExaminationMapper mapper;

    @Operation(summary = "get all results of biopotential measurements")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successfully",
                    content = @Content(
                            array = @ArraySchema(
                                    schema = @Schema(implementation = ExaminationDTO.class))))
    })
    @GetMapping
    public ResponseEntity<Set<ExaminationDTO>> getAll() {
        log.info("REST GET /examinations");
        Set<ExaminationDTO> responses = service.findAll().stream()
                .map(mapper::toDTO)
                .collect(Collectors.toSet());

        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "get all results of biopotential measurements by patient record")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successfully",
                    content = @Content(
                            array = @ArraySchema(
                                    schema = @Schema(implementation = ExaminationDTO.class))))
    })
    @GetMapping("/by-patient/{patientId}")
    public ResponseEntity<Set<ExaminationDTO>> getByPatient(@PathVariable int patientId) {
        log.info("REST Get /examinations/by-patient/{}", patientId);
        Set<ExaminationDTO> responses = service.findByPatient(patientId).stream()
                .map(mapper::toDTO)
                .collect(Collectors.toSet());

        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "get all results of biopotential measurements by device")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successfully",
                    content = @Content(
                            array = @ArraySchema(
                                    schema = @Schema(implementation = ExaminationDTO.class))))
    })
    @GetMapping("/by-device/{deviceId}")
    public ResponseEntity<Set<ExaminationDTO>> getByDevice(@PathVariable int deviceId) {
        log.info("REST GET /examinations/by-device/{}", deviceId);
        Set<ExaminationDTO> responses = service.findByDevice(deviceId).stream()
                .map(mapper::toDTO)
                .collect(Collectors.toSet());

        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "get result of biopotential measurements by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successfully",
                    content = @Content(schema = @Schema(implementation = ExaminationDTO.class))),
            @ApiResponse(responseCode = "404", description = "object not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<ExaminationDTO> get(@PathVariable int id) {
        log.info("REST GET /examinations/{}", id);
        ExaminationDTO response = mapper.toDTO(service.getById(id));

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "delete result of biopotential by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "successfully"),
            @ApiResponse(responseCode = "404", description = "object not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        service.delete(id);
        log.info("REST DELETE /examinations/{}", id);

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "save result of biopotential")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "created",
                    content = @Content(schema = @Schema(implementation = ChannelNameDTO.class))),
            @ApiResponse(responseCode = "400", description = "bad request",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "406", description = "validation error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping
    public ResponseEntity<ExaminationDTO> save(@RequestBody ExaminationDTO dto) throws JsonProcessingException {
        Patient patient = patientService.getById(dto.getPatientId());
        Device device = deviceService.getById(dto.getDeviceId());
        Examination entity = service.save(mapper.toEntity(dto, patient, device, new ArrayList<>()));
        log.info("REST PUT /examinations/{}", entity.getId());
        URI newResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/examinations/{id}")
                .buildAndExpand(entity.getId()).toUri();
        ExaminationDTO response = mapper.toDTO(entity);

        return ResponseEntity.created(newResource).body(response);
    }

    @GetMapping(value = "/health")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void health() {
    }

    @GetMapping(value = "/version")
    @ResponseStatus(HttpStatus.OK)
    public String version() {
        return version;
    }
}
