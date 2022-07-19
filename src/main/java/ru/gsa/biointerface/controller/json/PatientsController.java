package ru.gsa.biointerface.controller.json;

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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.gsa.biointerface.domain.ErrorResponse;
import ru.gsa.biointerface.domain.dto.PatientDTO;
import ru.gsa.biointerface.domain.entity.Patient;
import ru.gsa.biointerface.mapper.PatientMapper;
import ru.gsa.biointerface.service.PatientService;

import javax.validation.Valid;
import java.net.URI;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 15/11/2021
 */
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Patients", description = "patient records")
@RestController
@RequestMapping(value = "/patients", produces = MediaType.APPLICATION_JSON_VALUE)
public class PatientsController {

    private static final String version = "0.0.1-SNAPSHOT";

    private final PatientService service;
    private final PatientMapper mapper;

    @Operation(summary = "get all patient records")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successfully",
                    content = @Content(
                            array = @ArraySchema(
                                    schema = @Schema(implementation = PatientDTO.class))))
    })
    @GetMapping
    public ResponseEntity<Set<PatientDTO>> getAll() {
        log.info("REST GET /patients");
        Set<PatientDTO> responses = service.findAll().stream()
                .map(mapper::toDTO)
                .collect(Collectors.toSet());

        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "get all patient records by ICD disease code")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successfully",
                    content = @Content(
                            array = @ArraySchema(
                                    schema = @Schema(implementation = PatientDTO.class))))
    })
    @GetMapping("/by-icd/{icdId}")
    public ResponseEntity<Set<PatientDTO>> getByIcd(@PathVariable int icdId) {
        log.info("REST POST /patients/by-icd/{}", icdId);
        Set<PatientDTO> responses = service.findAllByIcd(icdId).stream()
                .map(mapper::toDTO)
                .collect(Collectors.toSet());

        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "get patient record by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successfully",
                    content = @Content(schema = @Schema(implementation = PatientDTO.class))),
            @ApiResponse(responseCode = "404", description = "object not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<PatientDTO> getById(@PathVariable int id) {
        log.info("REST GET /patients/{}", id);
        PatientDTO response = mapper.toDTO(service.getById(id));

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "delete patient record by ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        service.delete(id);
        log.info("REST DELETE /patients/{}", id);

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "save patient record")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "created",
                    content = @Content(schema = @Schema(implementation = PatientDTO.class))),
            @ApiResponse(responseCode = "400", description = "bad request",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "object not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PatientDTO> save(@Valid @RequestBody PatientDTO dto) {
        log.info("REST PUT /patients");
        Patient entity = service.save(dto);
        URI newResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/patients/{id}")
                .buildAndExpand(entity.getId()).toUri();
        PatientDTO response = mapper.toDTO(entity);

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
