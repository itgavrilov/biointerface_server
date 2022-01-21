package ru.gsa.biointerface.controller.json;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.gsa.biointerface.domain.dto.ChannelDTO;
import ru.gsa.biointerface.domain.entity.Sample;
import ru.gsa.biointerface.service.ChannelService;
import ru.gsa.biointerface.service.SampleService;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 15/11/2021
 */
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Samples", description = "Readings of biopotential measurements")
@RestController
@RequestMapping(
        value = "/samples",
        produces = MediaType.APPLICATION_JSON_VALUE,
        consumes = MediaType.APPLICATION_JSON_VALUE)
public class SampleController {
    private static final String version = "0.0.1-SNAPSHOT";

    private final SampleService service;
    private final ChannelService channelService;

    @PostMapping
    public List<Integer> getByChannel(@RequestBody ChannelDTO channelDTO) {
        log.info("REST GET /samples/getbychannel(examination_id={}, id={})",
                channelDTO.getNumber(),
                channelDTO.getExaminationId());
        List<Sample> entities =
                service.findAllByChannel(
                        channelService.convertDtoToEntity(channelDTO)
                );
        List<Integer> dtos = new LinkedList<>();
        entities.forEach(entity -> dtos.add(entity.getValue()));

        return dtos;
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
