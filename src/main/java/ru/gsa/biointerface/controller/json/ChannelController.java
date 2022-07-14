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
import ru.gsa.biointerface.domain.ErrorResponse;
import ru.gsa.biointerface.domain.dto.ChannelDTO;
import ru.gsa.biointerface.domain.entity.Channel;
import ru.gsa.biointerface.mapper.ChannelMapper;
import ru.gsa.biointerface.service.ChannelService;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 15/11/2021
 */
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Channels", description = "controller`s channels")
@RestController
@RequestMapping(
        value = "/channels",
        produces = MediaType.APPLICATION_JSON_VALUE,
        consumes = MediaType.APPLICATION_JSON_VALUE)
public class ChannelController {
    private static final String version = "0.0.1-SNAPSHOT";

    private final ChannelService service;
    private final ChannelMapper mapper;


    @Operation(summary = "get channels by examination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully",
                    content = @Content(array = @ArraySchema(
                            schema = @Schema(implementation = ChannelDTO.class)))),
            @ApiResponse(responseCode = "404", description = "Object not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    @GetMapping("/by-examination/{examinationId}")
    public ResponseEntity<List<ChannelDTO>> getByExamination(@PathVariable int examinationId) {
        log.info("REST GET /channels/by-examination/{}", examinationId);
        List<ChannelDTO> response = service.findAllByExamination(examinationId).stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "get channels by сhannel`s name")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully",
                    content = @Content(array = @ArraySchema(
                            schema = @Schema(implementation = ChannelDTO.class)))),
            @ApiResponse(responseCode = "404", description = "Object not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    @GetMapping("/by-channel-name/{channelNameId}")
    public ResponseEntity<Set<ChannelDTO>> getByDevice(@PathVariable int channelNameId) {
        log.info("REST GET /channels/by-channel-name/{}", channelNameId);
        Set<Channel> entities = service.findAllByChannelName(channelNameId);
        Set<ChannelDTO> dtos = entities.stream()
                .sorted()
                .map(mapper::toDTO)
                .collect(Collectors.toSet());

        return ResponseEntity.ok(dtos);
    }

    @Operation(summary = "get channels by examination ID and number")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully",
                    content = @Content(schema = @Schema(implementation = ChannelDTO.class))),
            @ApiResponse(responseCode = "404", description = "Object not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    @GetMapping("/{examinationId}/{number}")
    public ResponseEntity<ChannelDTO> get(@PathVariable int examinationId, @PathVariable int number) {
        log.info("REST GET /channels/{}/{}", examinationId, number);
        ChannelDTO response = mapper.toDTO(service.findById(examinationId, number));

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "delete channels by examination ID and  number")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "successfully"),
            @ApiResponse(responseCode = "404", description = "object not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{examinationId}/{number}")
    public ResponseEntity<Void> delete(@PathVariable int examinationId, @PathVariable int number) {
        log.info("REST DELETE /channels/{}/{}",
                examinationId, number);
        service.delete(examinationId, number);

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "save new channel")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "created",
                    content = @Content(schema = @Schema(implementation = ChannelDTO.class))),
            @ApiResponse(responseCode = "400", description = "bad request",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "object not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping
    public ResponseEntity<ChannelDTO> save(@Valid @RequestBody ChannelDTO dto) throws JsonProcessingException {
        Channel entity = service.save(dto);
        log.info("REST PUT /channels/{}/{}",
                entity.getId().getExaminationId(),
                entity.getId().getNumber());
        URI newResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("channels/{examinationId}/{number}")
                .buildAndExpand(entity.getId().getExaminationId(), entity.getId().getNumber()).toUri();
        ChannelDTO response = mapper.toDTO(entity);

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
