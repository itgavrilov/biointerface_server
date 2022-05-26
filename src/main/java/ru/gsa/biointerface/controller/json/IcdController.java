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
import ru.gsa.biointerface.domain.Icd;
import ru.gsa.biointerface.dto.ErrorResponse;
import ru.gsa.biointerface.dto.IcdDTO;
import ru.gsa.biointerface.mapper.IcdMapper;
import ru.gsa.biointerface.service.IcdService;

import java.net.URI;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 15/11/2021
 */
@Slf4j
@RequiredArgsConstructor
@Tag(name = "ICDs", description = "ICD disease codes")
@RestController
@RequestMapping(
        value = "/icds",
        produces = MediaType.APPLICATION_JSON_VALUE,
        consumes = MediaType.APPLICATION_JSON_VALUE)
public class IcdController {

    private static final String version = "0.0.1-SNAPSHOT";

    private final IcdService service;
    private final IcdMapper mapper;


    @Operation(summary = "get all ICD disease codes")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successfully",
                    content = @Content(
                            array = @ArraySchema(
                                    schema = @Schema(implementation = IcdDTO.class))))
    })
    @GetMapping
    public ResponseEntity<Set<IcdDTO>> getAll() {
        log.info("REST GET /icds");
        Set<IcdDTO> responses = service.findAll().stream()
                .map(mapper::toDTO)
                .collect(Collectors.toSet());

        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "get ICD disease code by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successfully",
                    content = @Content(schema = @Schema(implementation = IcdDTO.class))),
            @ApiResponse(responseCode = "404", description = "object not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<IcdDTO> get(@PathVariable int id) {
        log.info("REST GET /icds/{}", id);
        IcdDTO response = mapper.toDTO(service.getById(id));

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "delete ICD disease code by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successfully"),
            @ApiResponse(responseCode = "404", description = "object not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        service.delete(id);
        log.info("REST DELETE /icds/{}", id);

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "save new ICD disease code")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "created",
                    content = @Content(schema = @Schema(implementation = IcdDTO.class))),
            @ApiResponse(responseCode = "400", description = "bad request",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "406", description = "validation error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping
    public ResponseEntity<IcdDTO> save(@RequestBody IcdDTO dto) throws JsonProcessingException {
        Icd entity = service.save(mapper.toEntity(dto));
        log.info("REST PUT /icds/{}", entity.getId());
        URI newResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/icds/{id}")
                .buildAndExpand(entity.getId()).toUri();
        IcdDTO response = mapper.toDTO(entity);

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
