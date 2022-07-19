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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.gsa.biointerface.domain.ErrorResponse;
import ru.gsa.biointerface.domain.dto.IcdDTO;
import ru.gsa.biointerface.domain.entity.Icd;
import ru.gsa.biointerface.mapper.IcdMapper;
import ru.gsa.biointerface.service.IcdService;

import javax.validation.Valid;
import java.net.URI;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * JSON CRUD-контроллер для работы с заболеваниями по международной классификации болезней (ICD)
 * <p>
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 15/11/2021
 */
@Slf4j
@RequiredArgsConstructor
@Tag(name = "ICDs", description = "ICD disease codes")
@RestController
@RequestMapping(value = "/icds", produces = MediaType.APPLICATION_JSON_VALUE)
public class IcdController {

    private static final String version = "0.0.1-SNAPSHOT";

    private final IcdService service;
    private final IcdMapper mapper;

    @Operation(summary = "get all ICD disease codes")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successfully",
                    content = @Content(
                            array = @ArraySchema(schema = @Schema(implementation = IcdDTO.class))))})
    @GetMapping
    public ResponseEntity<Set<IcdDTO>> getAll() {
        log.debug("REST GET /icds");
        Set<IcdDTO> responses = service.findAll().stream()
                .map(mapper::toDTO)
                .collect(Collectors.toSet());
        log.debug("End REST GET /icds");

        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "get all ICD disease codes wish paging")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successfully",
                    content = @Content(
                            array = @ArraySchema(schema = @Schema(implementation = IcdDTO.class))))})
    @GetMapping(value = "/pageable")
    public ResponseEntity<Page<IcdDTO>> getAll(Pageable pageable) {
        log.debug("REST GET /icds/pageable");
        Page<IcdDTO> responses = service.findAll(pageable)
                .map(mapper::toDTO);
        log.debug("End REST GET /icds/pageable");

        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "get ICD disease code by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successfully",
                    content = @Content(schema = @Schema(implementation = IcdDTO.class))),
            @ApiResponse(responseCode = "404", description = "object not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))})
    @GetMapping("/{id}")
    public ResponseEntity<IcdDTO> get(@PathVariable int id) {
        log.debug("REST GET /icds/{}", id);
        IcdDTO response = mapper.toDTO(service.getById(id));
        log.debug("End REST GET /icds/{}", id);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "delete ICD disease code by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successfully"),
            @ApiResponse(responseCode = "404", description = "object not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))})
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        log.info("REST DELETE /icds/{}", id);
        service.delete(id);
        log.debug("End REST DELETE /icds/{}", id);

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "save new ICD disease code")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "created",
                    content = @Content(schema = @Schema(implementation = IcdDTO.class))),
            @ApiResponse(responseCode = "400", description = "bad request",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "406", description = "validation error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))})
    @PutMapping
    public ResponseEntity<IcdDTO> save(@Valid @RequestBody IcdDTO dto) throws JsonProcessingException {
        log.info("REST PUT /icds wish params: {}", dto);
        Icd entity = service.save(dto);
        URI newResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/icds/{id}")
                .buildAndExpand(entity.getId()).toUri();
        IcdDTO response = mapper.toDTO(entity);
        log.debug("End REST PUT /icds");

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
