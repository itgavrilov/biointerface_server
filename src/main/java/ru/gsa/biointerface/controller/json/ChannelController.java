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
import ru.gsa.biointerface.domain.dto.ExaminationDTO;
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

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 15/11/2021
 */
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Channels", description = "controller channels")
@RestController
@RequestMapping(
        value = "/channels",
        produces = MediaType.APPLICATION_JSON_VALUE,
        consumes = MediaType.APPLICATION_JSON_VALUE)
public class ChannelController {
    private static final String version = "0.0.1-SNAPSHOT";

    private final ExaminationService examinationService;
    private final ChannelNameService channelNameService;
    private final ChannelService service;
    private final ObjectMapper mapper;

    @Operation(summary = "Get channels by examination")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "Successfully",
//                    content = @Content(array = @ArraySchema(schema =
//                    @Schema(implementation = DrawHistoryLightWeightResponse.class)))),
//            @ApiResponse(responseCode = "404", description = "Object not found",
//                    content = @Content(schema = @Schema(implementation = ResponseError.class))),
//    })
    @PostMapping("/getByExamination")
    public List<ChannelDTO> getByExamination(@RequestBody ExaminationDTO examinationDTO) {
        log.info("REST GET /channels/getByExamination(examinationId={})", examinationDTO.getId());
        List<Channel> entities =
                service.findAllByExamination(
                        examinationService.convertDtoToEntity(examinationDTO)
                );
        List<ChannelDTO> dtos = new ArrayList<>();
        entities.forEach(entity -> dtos.add(
                service.convertEntityToDto(entity)
        ));

        return dtos;
    }

    @PostMapping("/getByChannelName")
    public Set<ChannelDTO> getByDevice(@RequestBody ChannelNameDTO channelNameDTO) {
        log.info("REST GET /channels/getByChannelName(channelNameId={})", channelNameDTO.getId());
        Set<Channel> entities =
                service.findAllByChannelName(
                        channelNameService.convertDtoToEntity(channelNameDTO)
                );
        Set<ChannelDTO> dtos = new TreeSet<>();
        entities.forEach(entity -> dtos.add(
                service.convertEntityToDto(entity)
        ));

        return dtos;
    }

    @GetMapping("/{examinationId}/{number}/")
    public ChannelDTO get(@PathVariable int examinationId, @PathVariable int number) {
        log.info("REST GET /channels/{}/{}", examinationId, number);

        return service.convertEntityToDto(
                service.findById(new ChannelID(examinationId, number))
        );
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@RequestBody ChannelDTO dto) {
        service.delete(service.convertDtoToEntity(dto));
        log.info("REST POST /channels/delete(examinationId={}, number={})",
                dto.getExaminationId(),
                dto.getNumber());
    }

    @PutMapping
    public ResponseEntity<String> save(@RequestBody ChannelDTO dto) throws JsonProcessingException {
        Channel entity = service.save(service.convertDtoToEntity(dto));
        log.info("REST POST /channels/save(examinationId={}, number={})",
                entity.getId().getExamination_id(),
                entity.getId().getNumber());
        URI newResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("channels/{examinationId}/{number}")
                .buildAndExpand(entity.getId().getExamination_id(), entity.getId().getNumber()).toUri();
        String body = mapper.writeValueAsString(
                service.convertEntityToDto(entity)
        );

        return ResponseEntity.created(newResource).body(body);
    }

    @GetMapping(value = "/health")
    @ResponseStatus(HttpStatus.OK)
    public void health() {
    }

    @GetMapping(value = "/version")
    @ResponseStatus(HttpStatus.OK)
    public String version() {
        return version;
    }
}
