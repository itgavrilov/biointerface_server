package ru.gsa.biointerface.controller.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.gsa.biointerface.domain.dto.ChannelDTO;
import ru.gsa.biointerface.domain.dto.ChannelNameDTO;
import ru.gsa.biointerface.domain.dto.ErrorResponse;
import ru.gsa.biointerface.domain.dto.ExaminationDTO;
import ru.gsa.biointerface.domain.dto.PatientDTO;
import ru.gsa.biointerface.domain.entity.Channel;
import ru.gsa.biointerface.domain.entity.ChannelID;
import ru.gsa.biointerface.service.ChannelNameService;
import ru.gsa.biointerface.service.ChannelService;
import ru.gsa.biointerface.service.ExaminationService;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
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
                .map(service::convertEntityToDto)
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
        Set<ChannelDTO> dtos = new TreeSet<>();
        entities.forEach(entity -> dtos.add(
                service.convertEntityToDto(entity)
        ));

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
        ChannelDTO response = service.convertEntityToDto(service.findById(examinationId, number));

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
    public ResponseEntity<ChannelDTO> save(@RequestBody ChannelDTO dto) throws JsonProcessingException {
        Channel entity = service.save(service.convertDtoToEntity(dto));
        log.info("REST PUT /channels/{}/{}",
                entity.getId().getExamination_id(),
                entity.getId().getNumber());
        URI newResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("channels/{examinationId}/{number}")
                .buildAndExpand(entity.getId().getExamination_id(), entity.getId().getNumber()).toUri();
        ChannelDTO response = service.convertEntityToDto(entity);

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
