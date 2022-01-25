package ru.gsa.biointerface.controller.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.gsa.biointerface.domain.dto.ChannelNameDTO;
import ru.gsa.biointerface.domain.entity.ChannelName;
import ru.gsa.biointerface.service.ChannelNameService;

import java.net.URI;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 15/11/2021
 */
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Channel`s names", description = "Names for channel")
@RestController
@RequestMapping(
        value = "/channel-names",
        produces = MediaType.APPLICATION_JSON_VALUE,
        consumes = MediaType.APPLICATION_JSON_VALUE)
public class ChannelNameController {
    private static final String version = "0.0.1-SNAPSHOT";

    private final ChannelNameService service;
    private final ObjectMapper mapper;

    @Operation(summary = "Get all channel`s names")
    @GetMapping
    public Set<ChannelNameDTO> getAll() {
        log.info("REST GET /channelNames");
        Set<ChannelName> entities = service.findAll();
        Set<ChannelNameDTO> dtos = new TreeSet<>();
        entities.forEach(entity -> dtos.add(
                service.convertEntityToDto(entity)
        ));

        return dtos;
    }

    @Operation(summary = "Get channel`s name by ID")
    @GetMapping("/{id}")
    public ChannelNameDTO get(@PathVariable int id) {
        log.info("REST GET /channelNames/{}", id);

        return service.convertEntityToDto(service.findById(id));
    }

    @Operation(summary = "Delete channel`s name by ID")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@RequestBody ChannelNameDTO dto) {
        service.delete(service.convertDtoToEntity(dto));
        log.info("REST POST /channelNames/delete/(id={})", dto.getId());
    }

    @Operation(summary = "Save new channel`s name")
    @PutMapping
    public ResponseEntity<String> save(@RequestBody ChannelNameDTO dto) throws JsonProcessingException {
        ChannelName entity = service.save(service.convertDtoToEntity(dto));
        log.info("REST POST /channelNames/save/(id={})", entity.getId());
        URI newResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/{id}")
                .buildAndExpand(entity.getId()).toUri();
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
