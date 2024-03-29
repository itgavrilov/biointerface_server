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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.gsa.biointerface.domain.dto.ErrorResponse;
import ru.gsa.biointerface.domain.dto.сhannel.ChannelDTO;
import ru.gsa.biointerface.domain.entity.Sample;
import ru.gsa.biointerface.service.SampleService;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 15/11/2021
 */
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Samples", description = "readings of biopotential measurement")
@RestController
@RequestMapping(value = "/api/v1/samples")
public class SampleController {

    private final SampleService service;

    @Operation(summary = "Get all readings of biopotential measurements by channel")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully",
                    content = @Content(array = @ArraySchema(
                            schema = @Schema(implementation = ChannelDTO.class)))),
            @ApiResponse(responseCode = "404", description = "Object not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),})
    @GetMapping(path = "/{examinationId}/{channelNumber}",
            produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Integer>> getByChannel(
            @Parameter(description = "Examination's ID", required = true)
            @PathVariable(value = "examinationId") UUID examinationId,
            @Parameter(description = "Channel number", required = true)
            @PathVariable(value = "channelNumber") Byte channelNumber) {
        log.debug("REST GET /samples/{}/{}", channelNumber, examinationId);
        List<Integer> responses = service.findAllByExaminationIdAndChannelNumber(examinationId, channelNumber).stream()
                .map(Sample::getValue)
                .collect(Collectors.toList());
        log.debug("End REST GET /samples/{}/{}", channelNumber, examinationId);

        return ResponseEntity.ok(responses);
    }
}
