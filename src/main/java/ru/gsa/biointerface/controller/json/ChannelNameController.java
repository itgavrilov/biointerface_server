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
import ru.gsa.biointerface.domain.dto.channelName.ChannelNameDTO;
import ru.gsa.biointerface.domain.dto.channelName.ChannelNameSaveOrUpdateDTO;
import ru.gsa.biointerface.domain.entity.ChannelName;
import ru.gsa.biointerface.mapper.ChannelNameMapper;
import ru.gsa.biointerface.service.ChannelNameService;

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
@Tag(name = "Channel`s names", description = "names for controller`s channel")
@RestController
@RequestMapping(value = "/api/v1/channel-names")
public class ChannelNameController {

    private final ChannelNameService service;
    private final ChannelNameMapper mapper;

    @Operation(summary = "Get all channel`s names")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successfully",
                    content = @Content(
                            array = @ArraySchema(schema = @Schema(implementation = ChannelNameDTO.class))))})
    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ChannelNameDTO>> getAll() {
        log.debug("REST GET /channelNames");
        List<ChannelNameDTO> responses = service.findAll().stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
        log.debug("End REST GET /channelNames");

        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "Get all channel`s names wish paging")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successfully",
                    content = @Content(
                            array = @ArraySchema(schema = @Schema(implementation = ChannelNameDTO.class))))})
    @GetMapping(path = "/pageable", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<ChannelNameDTO>> getAll(Pageable pageable) {
        log.debug("REST GET /channelNames/pageable");
        Page<ChannelNameDTO> responses = service.findAll(pageable)
                .map(mapper::toDTO);
        log.debug("End REST GET /channelNames/pageable");

        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "Get channel`s name by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successfully",
                    content = @Content(schema = @Schema(implementation = ChannelNameDTO.class))),
            @ApiResponse(responseCode = "404", description = "object not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))})
    @GetMapping(path = "/{id}",
            produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<ChannelNameDTO> get(
            @Parameter(description = "Channel name's ID", required = true)
            @PathVariable(value = "id") UUID id) {
        log.debug("REST GET /channelNames/{}", id);
        ChannelNameDTO response = mapper.toDTO(service.getById(id));
        log.debug("End REST GET /channelNames/{}", id);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Save new channel`s name")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "created",
                    content = @Content(schema = @Schema(implementation = ChannelNameDTO.class))),
            @ApiResponse(responseCode = "400", description = "bad request",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "406", description = "validation error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))})
    @PostMapping(produces = APPLICATION_JSON_VALUE,
            consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<ChannelNameDTO> save(
            @Parameter(description = "Channel name's DTO", required = true)
            @Valid @RequestBody ChannelNameSaveOrUpdateDTO dto) {
        log.debug("REST POST /channelNames wish params: {}", dto);
        ChannelName entity = service.save(mapper.toEntity(dto, null));
        URI newResource = UriComponentsBuilder.fromPath("/api/v1/channel-names/{id}")
                .buildAndExpand(entity.getId()).toUri();
        log.debug("End REST POST /channelNames");

        return ResponseEntity.created(newResource).body(mapper.toDTO(entity));
    }

    @Operation(summary = "Update new channel`s name")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "created",
                    content = @Content(schema = @Schema(implementation = ChannelNameDTO.class))),
            @ApiResponse(responseCode = "400", description = "bad request",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "406", description = "validation error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))})
    @PutMapping(path = "/{id}",
            produces = APPLICATION_JSON_VALUE,
            consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<ChannelNameDTO> update(
            @Parameter(description = "Channel name's ID", required = true)
            @PathVariable(value = "id") UUID id,
            @Parameter(description = "Channel name's DTO", required = true)
            @Valid @RequestBody ChannelNameSaveOrUpdateDTO dto) {
        log.debug("REST PUT /channelNames wish params: id={}, dto={}", id, dto);
        ChannelName request = mapper.toEntity(dto, id);
        ChannelNameDTO response = mapper.toDTO(service.update(request));
        log.debug("End REST PUT /channelNames");

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Delete channel`s name by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "successfully"),
            @ApiResponse(responseCode = "404", description = "object not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))})
    @DeleteMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @Parameter(description = "Channel name's ID", required = true)
            @PathVariable(value = "id") UUID id) {
        log.info("REST DELETE /channelNames/{}", id);
        service.delete(id);
        log.debug("End REST DELETE /channelNames/{}", id);
    }
}
