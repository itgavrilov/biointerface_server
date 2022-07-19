package ru.gsa.biointerface.controller.json;

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
import org.springframework.web.bind.annotation.*;
import ru.gsa.biointerface.domain.ErrorResponse;
import ru.gsa.biointerface.domain.dto.ChannelDTO;
import ru.gsa.biointerface.domain.entity.Sample;
import ru.gsa.biointerface.service.SampleService;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 15/11/2021
 */
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Samples", description = "readings of biopotential measurement")
@RestController
@RequestMapping(value = "/samples", produces = MediaType.APPLICATION_JSON_VALUE)
public class SampleController {

    private static final String version = "0.0.1-SNAPSHOT";

    private final SampleService service;

    @Operation(summary = "get all readings of biopotential measurements by channel")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully",
                    content = @Content(array = @ArraySchema(
                            schema = @Schema(implementation = ChannelDTO.class)))),
            @ApiResponse(responseCode = "404", description = "Object not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),})
    @GetMapping("/{examinationId}/{channelNumber}")
    public ResponseEntity<List<Integer>> getByChannel(@PathVariable int examinationId, @PathVariable int channelNumber) {
        log.debug("REST GET /samples/{}/{}", channelNumber, examinationId);
        List<Integer> responses = service.findAllByExaminationIdAndChannelNumber(examinationId, channelNumber).stream()
                .map(Sample::getValue)
                .collect(Collectors.toList());
        log.debug("End REST GET /samples/{}/{}", channelNumber, examinationId);

        return ResponseEntity.ok(responses);
    }

    @GetMapping(value = "/health")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void health() {
        log.debug("REST GET /health");
    }

    @GetMapping(value = "/version")
    @ResponseStatus(HttpStatus.OK)
    public String version() {
        log.debug("REST GET /version");
        return version;
    }
}
