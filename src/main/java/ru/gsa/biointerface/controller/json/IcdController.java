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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;
import ru.gsa.biointerface.domain.dto.ErrorResponse;
import ru.gsa.biointerface.domain.dto.icd.IcdDTO;
import ru.gsa.biointerface.domain.dto.icd.IcdSaveOrUpdateDTO;
import ru.gsa.biointerface.domain.entity.Icd;
import ru.gsa.biointerface.mapper.IcdMapper;
import ru.gsa.biointerface.service.IcdService;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * JSON CRUD-контроллер для работы с заболеваниями по международной классификации болезней (ICD)
 * <p>
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 15/11/2021
 */
@Slf4j
@RequiredArgsConstructor
@Tag(name = "ICDs", description = "ICD disease codes")
@RestController
@RequestMapping(value = "/api/v1/icds")
public class IcdController {

    private final IcdService service;
    private final IcdMapper mapper;

    @Operation(summary = "Get all ICD disease codes")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successfully",
                    content = @Content(
                            array = @ArraySchema(schema = @Schema(implementation = IcdDTO.class))))})
    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<List<IcdDTO>> getAll() {
        log.debug("REST GET /icds");
        List<IcdDTO> responses = service.findAll().stream()
                .map(mapper::toIcdDTO)
                .collect(Collectors.toList());
        log.debug("End REST GET /icds");

        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "Get all ICD disease codes wish paging")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successfully",
                    content = @Content(
                            array = @ArraySchema(schema = @Schema(implementation = IcdDTO.class))))})
    @GetMapping(value = "/pageable", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<IcdDTO>> getAll(Pageable pageable) {
        log.debug("REST GET /icds/pageable");
        Page<IcdDTO> responses = service.findAll(pageable)
                .map(mapper::toIcdDTO);
        log.debug("End REST GET /icds/pageable");

        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "Get ICD disease code by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successfully",
                    content = @Content(schema = @Schema(implementation = IcdDTO.class))),
            @ApiResponse(responseCode = "404", description = "object not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))})
    @GetMapping(path = "/{id}",
            produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<IcdDTO> getById(
            @Parameter(description = "ICD's ID", required = true)
            @PathVariable(value = "id") UUID id) {
        log.debug("REST GET /icds/{}", id);
        IcdDTO response = mapper.toIcdDTO(service.getById(id));
        log.debug("End REST GET /icds/{}", id);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Save new ICD disease code")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "created",
                    content = @Content(schema = @Schema(implementation = IcdDTO.class))),
            @ApiResponse(responseCode = "400", description = "bad request",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "406", description = "validation error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))})
    @PostMapping(produces = APPLICATION_JSON_VALUE,
            consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<IcdDTO> save(
            @Parameter(description = "ICD's DTO", required = true)
            @Valid @RequestBody IcdSaveOrUpdateDTO dto) {
        log.debug("REST POST /icds wish params: {}", dto);
        Icd request = mapper.toEntity(dto, null);
        Icd entity = service.save(request);
        URI newResource = UriComponentsBuilder.fromPath("/api/v1/icds/{id}")
                .buildAndExpand(entity.getId()).toUri();
        IcdDTO response = mapper.toIcdDTO(entity);
        log.debug("End POST PUT /icds");

        return ResponseEntity.created(newResource).body(response);
    }

    @Operation(summary = "Update ICD disease code")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "created",
                    content = @Content(schema = @Schema(implementation = IcdDTO.class))),
            @ApiResponse(responseCode = "400", description = "bad request",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "object not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "406", description = "validation error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))})
    @PutMapping(path = "/{id}",
            produces = APPLICATION_JSON_VALUE,
            consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<IcdDTO> update(
            @Parameter(description = "ICD's ID", required = true)
            @PathVariable(value = "id") UUID id,
            @Parameter(description = "ICD's DTO", required = true)
            @Valid @RequestBody IcdSaveOrUpdateDTO dto) {
        log.debug("REST PUT /icds wish params: {}", dto);
        Icd request = mapper.toEntity(dto, id);
        Icd entity = service.update(request);
        IcdDTO response = mapper.toIcdDTO(entity);
        log.debug("End PUT PUT /icds");

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Delete ICD disease code by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successfully"),
            @ApiResponse(responseCode = "404", description = "object not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))})
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @Parameter(description = "ICD's ID", required = true)
            @PathVariable(value = "id") UUID id) {
        log.debug("REST DELETE /icds/{}", id);
        service.delete(id);
        log.debug("End REST DELETE /icds/{}", id);
    }
}
