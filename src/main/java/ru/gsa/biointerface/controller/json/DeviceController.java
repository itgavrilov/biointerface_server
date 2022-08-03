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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.gsa.biointerface.domain.entity.Device;
import ru.gsa.biointerface.dto.DeviceDTO;
import ru.gsa.biointerface.dto.ErrorResponse;
import ru.gsa.biointerface.dto.IcdDTO;
import ru.gsa.biointerface.service.DeviceService;
import ru.gsa.biointerface.unit.mapper.DeviceMapper;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * JSON CRUD-контроллер для работы с контроллерами биоинтерфейсов
 * <p>
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 15/11/2021
 */
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Devices", description = "biointerface controllers")
@RestController
@RequestMapping(value = "/devices", produces = MediaType.APPLICATION_JSON_VALUE)
public class DeviceController {

    private static final String version = "0.0.1-SNAPSHOT";

    private final DeviceService service;
    private final DeviceMapper mapper;

    @Operation(summary = "get all biointerface controllers")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successfully",
                    content = @Content(
                            array = @ArraySchema(schema = @Schema(implementation = DeviceDTO.class))))})
    @GetMapping
    public ResponseEntity<List<DeviceDTO>> getAll() {
        log.debug("REST GET /devices");
        List<DeviceDTO> responses = service.findAll().stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
        log.debug("End REST GET /devices");

        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "get all biointerface controllers wish paging")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successfully",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = DeviceDTO.class))))})
    @GetMapping(value = "/pageable")
    public ResponseEntity<Page<DeviceDTO>> getAll(Pageable pageable) {
        log.debug("REST GET /devices/pageable");
        Page<DeviceDTO> responses = service.findAll(pageable)
                .map(mapper::toDTO);
        log.debug("End REST GET /devices/pageable");

        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "get biointerface controller by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successfully",
                    content = @Content(schema = @Schema(implementation = DeviceDTO.class))),
            @ApiResponse(responseCode = "404", description = "object not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))})
    @GetMapping("/{id}")
    public ResponseEntity<DeviceDTO> get(
            @Parameter(description = "Device's ID", required = true)
            @PathVariable(value = "id") UUID id) {
        log.debug("REST GET /devices/{}", id);
        DeviceDTO response = mapper.toDTO(service.getById(id));
        log.debug("End REST GET /devices/{}", id);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "delete biointerface controller by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "successfully"),
            @ApiResponse(responseCode = "404", description = "object not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))})
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "Device's ID", required = true)
            @PathVariable(value = "id") UUID id) {
        log.info("REST DELETE /devices/{}", id);
        service.delete(id);
        log.debug("End REST DELETE /devices/{}", id);

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "save new biointerface controller")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "created",
                    content = @Content(schema = @Schema(implementation = IcdDTO.class))),
            @ApiResponse(responseCode = "400", description = "bad request",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "406", description = "validation error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))})
    @PutMapping
    public ResponseEntity<DeviceDTO> save(
            @Parameter(description = "Device's DTO", required = true)
            @Valid @RequestBody DeviceDTO dto) {
        log.info("REST PUT /devices wish params: {}", dto);
        Device entity = service.update(dto);
        URI newResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/devices/{id}")
                .buildAndExpand(entity.getId()).toUri();
        DeviceDTO response = mapper.toDTO(entity);
        log.debug("End REST PUT /devices");

        return ResponseEntity.created(newResource).body(response);
    }

    @GetMapping("/health")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void health() {
        log.debug("REST GET /health");
    }

    @GetMapping("/version")
    @ResponseStatus(HttpStatus.OK)
    public String version() {
        log.debug("REST GET /version");
        return version;
    }
}
