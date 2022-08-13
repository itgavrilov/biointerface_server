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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.gsa.biointerface.domain.dto.ErrorResponse;
import ru.gsa.biointerface.domain.dto.patient.PatientDTO;
import ru.gsa.biointerface.domain.dto.patient.PatientSaveOrUpdateDTO;
import ru.gsa.biointerface.domain.entity.Icd;
import ru.gsa.biointerface.domain.entity.Patient;
import ru.gsa.biointerface.mapper.PatientMapper;
import ru.gsa.biointerface.service.IcdService;
import ru.gsa.biointerface.service.PatientService;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 15/11/2021
 */
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Patients", description = "patient records")
@RestController
@RequestMapping(value = "/api/v1/patients")
public class PatientsController {

    private final PatientService service;
    private final IcdService icdService;
    private final PatientMapper mapper;

    @Operation(summary = "Get all patient records by ICD disease code")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successfully",
                    content = @Content(
                            array = @ArraySchema(schema = @Schema(implementation = PatientDTO.class))))})
    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<List<PatientDTO>> getAll(
            @Parameter(description = "ICD's ID")
            @RequestParam(value = "icdId", required = false) UUID icdId) {
        log.debug("REST GET /patients wish icdId = {}", icdId);
        List<PatientDTO> responses = service.findAll(icdId).stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
        log.debug("End REST GET /patients wish icdId = {}", icdId);

        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "Get all patient records by ICD disease code wish paging")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successfully",
                    content = @Content(
                            array = @ArraySchema(schema = @Schema(implementation = PatientDTO.class))))})
    @GetMapping(path = "/pageable",
            produces = APPLICATION_JSON_VALUE,
            consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<PatientDTO>> getAll(
            @Parameter(description = "ICD's ID")
            @RequestParam(value = "icdId", required = false) UUID icdId,
            Pageable pageable) {
        log.debug("REST GET /patients/pageable wish icdId = {}", icdId);
        Page<PatientDTO> responses = service.findAll(icdId, pageable)
                .map(mapper::toDTO);
        log.debug("End REST GET /patients/pageable wish icdId = {}", icdId);

        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "Get patient record by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successfully",
                    content = @Content(schema = @Schema(implementation = PatientDTO.class))),
            @ApiResponse(responseCode = "404", description = "object not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))})
    @GetMapping(path = "/{id}",
            produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<PatientDTO> getById(
            @Parameter(description = "Patient's ID", required = true)
            @PathVariable(value = "id") UUID id) {
        log.debug("REST GET /patients/{}", id);
        PatientDTO response = mapper.toDTO(service.getById(id));
        log.debug("End REST GET /patients/{}", id);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Save patient record")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "created",
                    content = @Content(schema = @Schema(implementation = PatientDTO.class))),
            @ApiResponse(responseCode = "400", description = "bad request",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "object not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))})
    @PostMapping(produces = APPLICATION_JSON_VALUE,
            consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<PatientDTO> save(
            @Parameter(description = "Patient's DTO", required = true)
            @Valid @RequestBody PatientSaveOrUpdateDTO dto) {
        log.info("REST POST /patients wish params: {}", dto);
        Icd icd = icdService.getByIdOrNull(dto.getIcdId());
        Patient entity = service.save(mapper.toEntity(dto, null, icd));
        URI newResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/patients/{id}")
                .buildAndExpand(entity.getId()).toUri();
        PatientDTO response = mapper.toDTO(entity);
        log.debug("End REST POST /patients");

        return ResponseEntity.created(newResource).body(response);
    }

    @Operation(summary = "Update patient record")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "created",
                    content = @Content(schema = @Schema(implementation = PatientDTO.class))),
            @ApiResponse(responseCode = "400", description = "bad request",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "object not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))})
    @PutMapping(path = "/{id}", produces = APPLICATION_JSON_VALUE,
            consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<PatientDTO> update(
            @Parameter(description = "Patient's ID", required = true)
            @PathVariable(value = "id") UUID id,
            @Parameter(description = "Patient's DTO", required = true)
            @Valid @RequestBody PatientSaveOrUpdateDTO dto) {
        log.info("REST PUT /patients wish params: {}", dto);
        Icd icd = icdService.getByIdOrNull(dto.getIcdId());
        Patient request = mapper.toEntity(dto, id, icd);
        Patient entity = service.update(request);
        PatientDTO response = mapper.toDTO(entity);
        log.debug("End REST PUT /patients");

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Delete patient record by ID")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @Parameter(description = "Patient's ID", required = true)
            @PathVariable(value = "id") UUID id) {
        log.info("REST DELETE /patients/{}", id);
        service.delete(id);
        log.debug("End REST DELETE /patients/{}", id);
    }
}
