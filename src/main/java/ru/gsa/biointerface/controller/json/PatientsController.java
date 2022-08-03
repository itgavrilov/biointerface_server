package ru.gsa.biointerface.controller.json;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.gsa.biointerface.domain.entity.Patient;
import ru.gsa.biointerface.dto.ErrorResponse;
import ru.gsa.biointerface.dto.PatientDTO;
import ru.gsa.biointerface.service.PatientService;
import ru.gsa.biointerface.unit.mapper.PatientMapper;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.UUID;
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

    @Operation(summary = "get all patient records by ICD disease code")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successfully",
                    content = @Content(
                            array = @ArraySchema(schema = @Schema(implementation = PatientDTO.class))))})
    @GetMapping
    public ResponseEntity<List<PatientDTO>> getAll(
            @Parameter(description = "ICD's ID")
            @RequestParam(value = "icdId", required = false) UUID icdId) {
        log.debug("REST POST /patients wish icdId = {}", icdId);
        List<PatientDTO> responses = service.findAll(icdId).stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
        log.debug("End REST POST /patients wish icdId = {}", icdId);

        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "get all patient records by ICD disease code wish paging")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successfully",
                    content = @Content(
                            array = @ArraySchema(schema = @Schema(implementation = PatientDTO.class))))})
    @GetMapping("/pageable")
    public ResponseEntity<Page<PatientDTO>> getAll(
            @Parameter(description = "ICD's ID")
            @RequestParam(value = "icdId", required = false) UUID icdId,
            Pageable pageable) {
        log.debug("REST POST /patients/pageable wish icdId = {}", icdId);
        Page<PatientDTO> responses = service.findAll(icdId, pageable)
                .map(mapper::toDTO);
        log.debug("End REST POST /patients/pageable wish icdId = {}", icdId);

        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "get patient record by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successfully",
                    content = @Content(schema = @Schema(implementation = PatientDTO.class))),
            @ApiResponse(responseCode = "404", description = "object not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))})
    @GetMapping("/{id}")
    public ResponseEntity<PatientDTO> getById(
            @Parameter(description = "Patient's ID", required = true)
            @PathVariable(value = "id") UUID id) {
        log.debug("REST GET /patients/{}", id);
        PatientDTO response = mapper.toDTO(service.getById(id));
        log.debug("End REST GET /patients/{}", id);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "delete patient record by ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "Patient's ID", required = true)
            @PathVariable(value = "id") UUID id) {
        log.info("REST DELETE /patients/{}", id);
        service.delete(id);
        log.debug("End REST DELETE /patients/{}", id);

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "save patient record")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "created",
                    content = @Content(schema = @Schema(implementation = PatientDTO.class))),
            @ApiResponse(responseCode = "400", description = "bad request",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "object not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))})
    @PutMapping
    public ResponseEntity<PatientDTO> save(
            @Parameter(description = "Patient's DTO", required = true)
            @Valid @RequestBody PatientDTO dto) {
        log.info("REST PUT /patients wish params: {}", dto);
        Patient entity = service.saveOrUpdate(dto);
        URI newResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/patients/{id}")
                .buildAndExpand(entity.getId()).toUri();
        PatientDTO response = mapper.toDTO(entity);
        log.debug("End REST PUT /patients");

        return ResponseEntity.created(newResource).body(response);
    }

    @GetMapping(value = "/health")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void health() {
        log.debug("REST GET /health");
    }

    @GetMapping(value = "/version")
    @ResponseStatus(HttpStatus.OK)
    public String version() {
        log.debug("REST GET /version");
        return version;
    }
}
