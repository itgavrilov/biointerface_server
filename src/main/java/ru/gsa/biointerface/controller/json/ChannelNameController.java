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
import ru.gsa.biointerface.domain.ChannelName;
import ru.gsa.biointerface.dto.ChannelNameDTO;
import ru.gsa.biointerface.dto.ErrorResponse;
import ru.gsa.biointerface.mapper.ChannelNameMapper;
import ru.gsa.biointerface.service.ChannelNameService;

import java.net.URI;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 15/11/2021
 */
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Channel`s names", description = "names for controller`s channel")
@RestController
@RequestMapping(
        value = "/channel-names",
        produces = MediaType.APPLICATION_JSON_VALUE,
        consumes = MediaType.APPLICATION_JSON_VALUE)
public class ChannelNameController {

    private static final String version = "0.0.1-SNAPSHOT";

    private final ChannelNameService service;
    private final ChannelNameMapper mapper;

    @Operation(summary = "get all channel`s names")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successfully",
                    content = @Content(
                            array = @ArraySchema(
                                    schema = @Schema(implementation = ChannelNameDTO.class))))
    })
    @GetMapping
    public ResponseEntity<Set<ChannelNameDTO>> getAll() {
        log.info("REST GET /channelNames");
        Set<ChannelNameDTO> responses = service.findAll().stream()
                .map(mapper::toDTO)
                .collect(Collectors.toSet());

        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "get channel`s name by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successfully",
                    content = @Content(schema = @Schema(implementation = ChannelNameDTO.class))),
            @ApiResponse(responseCode = "404", description = "object not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<ChannelNameDTO> get(@PathVariable int id) {
        log.info("REST GET /channelNames/{}", id);
        ChannelNameDTO response = mapper.toDTO(service.getById(id));

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "delete channel`s name by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "successfully"),
            @ApiResponse(responseCode = "404", description = "object not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        service.delete(id);
        log.info("REST DELETE /channelNames/{}", id);

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "save new channel`s name")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "created",
                    content = @Content(schema = @Schema(implementation = ChannelNameDTO.class))),
            @ApiResponse(responseCode = "400", description = "bad request",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "406", description = "validation error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping
    public ResponseEntity<ChannelNameDTO> save(@RequestBody ChannelNameDTO dto) throws JsonProcessingException {
        ChannelName entity = service.save(mapper.toEntity(dto));
        log.info("REST PUT /channelNames/{}", entity.getId());
        URI newResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/channelNames/{id}")
                .buildAndExpand(entity.getId()).toUri();
        ChannelNameDTO response = mapper.toDTO(entity);

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
