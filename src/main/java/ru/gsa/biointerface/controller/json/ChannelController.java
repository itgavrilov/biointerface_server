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
import ru.gsa.biointerface.domain.dto.сhannel.ChannelDTO;
import ru.gsa.biointerface.domain.dto.сhannel.ChannelUpdateDTO;
import ru.gsa.biointerface.domain.entity.Channel;
import ru.gsa.biointerface.domain.entity.ChannelName;
import ru.gsa.biointerface.domain.entity.Examination;
import ru.gsa.biointerface.mapper.ChannelMapper;
import ru.gsa.biointerface.service.ChannelNameService;
import ru.gsa.biointerface.service.ChannelService;
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
@Tag(name = "Channels", description = "controller`s channels")
@RestController
@RequestMapping(value = "/api/v1/channels")
public class ChannelController {

    private final ChannelService service;
    private final ExaminationService examinationService;
    private final ChannelNameService channelNameService;
    private final ChannelMapper mapper;

    @Operation(summary = "Get channels")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully",
                    content = @Content(array = @ArraySchema(
                            schema = @Schema(implementation = ChannelDTO.class)))),
            @ApiResponse(responseCode = "404", description = "Object not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),})
    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ChannelDTO>> findAll(
            @Parameter(description = "Examination's ID")
            @RequestParam(value = "examinationId", required = false) UUID examinationId,
            @Parameter(description = "ChannelName's ID")
            @RequestParam(value = "channelNameId", required = false) UUID channelNameId) {
        log.debug("REST GET /channels wish params: examinationId={}, channelNameId={}", examinationId, channelNameId);
        List<ChannelDTO> response = service.findAll(examinationId, channelNameId).stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
        log.debug("End REST GET /channels");

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get channels by examination ID and number")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully",
                    content = @Content(schema = @Schema(implementation = ChannelDTO.class))),
            @ApiResponse(responseCode = "404", description = "Object not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    @GetMapping(path = "/{examinationId}/{number}",
            produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<ChannelDTO> get(
            @Parameter(description = "Examination's ID", required = true)
            @PathVariable(value = "examinationId") UUID examinationId,
            @Parameter(description = "Channel number", required = true)
            @PathVariable(value = "number") Byte number) {
        log.debug("REST GET /channels/{}/{}", examinationId, number);
        ChannelDTO response = mapper.toDTO(service.getById(examinationId, number));
        log.debug("End REST GET /channels/{}/{}", examinationId, number);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Update channel")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Created",
                    content = @Content(schema = @Schema(implementation = ChannelDTO.class))),
            @ApiResponse(responseCode = "400", description = "Bad request",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Object not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping(path = "/{examinationId}/{number}",
            produces = APPLICATION_JSON_VALUE,
            consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<ChannelDTO> update(
            @Parameter(description = "Examination's ID", required = true)
            @PathVariable(value = "examinationId") UUID examinationId,
            @Parameter(description = "Channel number", required = true)
            @PathVariable(value = "number") Byte number,
            @Parameter(description = "Controller channel's DTO", required = true)
            @Valid @RequestBody ChannelUpdateDTO dto) {
        log.info("REST PUT /channels wish params: {}", dto);
        Examination examination = examinationService.getById(examinationId);
        ChannelName channelName = channelNameService.getByIdOrNull(dto.getChannelNameId());
        Channel request = mapper.toEntity(dto, number, examination, channelName);
        ChannelDTO response = mapper.toDTO(service.update(request));
        log.debug("End REST PUT /channels");

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Delete channels by examination ID and  number")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successfully"),
            @ApiResponse(responseCode = "404", description = "Object not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping(path = "/{examinationId}/{number}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @Parameter(description = "Examination's ID", required = true)
            @PathVariable(value = "examinationId") UUID examinationId,
            @Parameter(description = "Channel number", required = true)
            @PathVariable(value = "number") Byte number) {
        log.info("REST DELETE /channels/{}/{}", examinationId, number);
        service.delete(examinationId, number);
        log.debug("End REST DELETE /channels/{}/{}", examinationId, number);
    }
}
