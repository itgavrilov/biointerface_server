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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.gsa.biointerface.domain.dto.ErrorResponse;
import ru.gsa.biointerface.domain.dto.channelName.ChannelNameDTO;
import ru.gsa.biointerface.domain.dto.examination.ExaminationDTO;
import ru.gsa.biointerface.domain.dto.examination.ExaminationUpdateDTO;
import ru.gsa.biointerface.domain.entity.Examination;
import ru.gsa.biointerface.mapper.ExaminationMapper;
import ru.gsa.biointerface.service.ExaminationService;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 15/11/2021
 */
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Examinations", description = "results of biopotential measurements")
@RestController
@RequestMapping(value = "/api/v1/examinations")
public class ExaminationsController {

    private final ExaminationService service;
    private final ExaminationMapper mapper;

    @Operation(summary = "Get all results of biopotential measurements")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successfully",
                    content = @Content(
                            array = @ArraySchema(schema = @Schema(implementation = ExaminationDTO.class))))})
    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ExaminationDTO>> getAll(
            @Parameter(description = "ID patient record")
            @RequestParam(value = "patientId", required = false) UUID patientId,
            @Parameter(description = "ID device")
            @RequestParam(value = "deviceId", required = false) UUID deviceId) {
        log.debug("REST GET /examinations wish params: patientId={}, deviceId={}", patientId, deviceId);
        List<ExaminationDTO> responses = service.findAll(patientId, deviceId).stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
        log.debug("End REST GET /examinations");

        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "Get all results of biopotential measurements wish paging")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successfully",
                    content = @Content(
                            array = @ArraySchema(schema = @Schema(implementation = ExaminationDTO.class))))})
    @GetMapping(path = "/pageable",
            produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<ExaminationDTO>> getAll(
            @Parameter(description = "ID patient record")
            @RequestParam(value = "patientId", required = false) UUID patientId,
            @Parameter(description = "ID device")
            @RequestParam(value = "deviceId", required = false) UUID deviceId,
            Pageable pageable) {
        log.debug("REST GET /examinations/pageable wish params: patientId={}, deviceId={}", patientId, deviceId);
        Page<ExaminationDTO> responses = service.findAll(patientId, deviceId, pageable)
                .map(mapper::toDTO);
        log.debug("End REST GET /examinations/pageable");

        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "Get result of biopotential measurements by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successfully",
                    content = @Content(schema = @Schema(implementation = ExaminationDTO.class))),
            @ApiResponse(responseCode = "404", description = "object not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))})
    @GetMapping(path = "/{id}",
            produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<ExaminationDTO> get(
            @Parameter(description = "Examination's ID", required = true)
            @PathVariable(value = "id") UUID id) {
        log.debug("REST GET /examinations/{}", id);
        ExaminationDTO response = mapper.toDTO(service.getById(id));
        log.debug("End REST GET /examinations/{}", id);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Update result of biopotential")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "successfully",
                    content = @Content(schema = @Schema(implementation = ChannelNameDTO.class))),
            @ApiResponse(responseCode = "400", description = "bad request",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "406", description = "validation error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "object not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))})
    @PutMapping(path = "/{id}",
            produces = APPLICATION_JSON_VALUE,
            consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<ExaminationDTO> update(
            @Parameter(description = "Examination's ID", required = true)
            @PathVariable(value = "id") UUID id,
            @Parameter(description = "Examination's DTO", required = true)
            @Valid @RequestBody ExaminationUpdateDTO dto) {
        log.info("REST PUT /examinations wish params: {}", dto);
        Examination request = mapper.toEntity(dto, id);
        ExaminationDTO response = mapper.toDTO(service.update(request));
        log.debug("End REST PUT /examinations");

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Delete result of biopotential by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "successfully"),
            @ApiResponse(responseCode = "404", description = "object not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))})
    @DeleteMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @Parameter(description = "Examination's ID", required = true)
            @PathVariable(value = "id") UUID id) {
        log.info("REST DELETE /examinations/{}", id);
        service.delete(id);
        log.debug("End REST DELETE /examinations/{}", id);
    }
}
