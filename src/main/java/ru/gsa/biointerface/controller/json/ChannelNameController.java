package ru.gsa.biointerface.controller.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
@RestController
@RequestMapping(
        value = "/channelNames",
        produces = MediaType.APPLICATION_JSON_VALUE,
        consumes = MediaType.APPLICATION_JSON_VALUE)
public class ChannelNameController {
    private static final String version = "0.0.1-SNAPSHOT";
    @Autowired
    ChannelNameService service;
    @Autowired
    ObjectMapper mapper;

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

    @GetMapping("/{id}")
    public ChannelNameDTO get(@PathVariable int id) {
        log.info("REST GET /channelNames/{}", id);

        return service.convertEntityToDto(service.findById(id));
    }

    @PutMapping
    public ResponseEntity<String> save(@RequestBody ChannelNameDTO dto) throws JsonProcessingException {
        ChannelName entity = service.save(service.convertDtoToEntity(dto));
        log.info("REST POST /channelNames/save/(id={})", entity.getId());
        URI newResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/channelNames/{id}")
                .buildAndExpand(entity.getId()).toUri();
        String body = mapper.writeValueAsString(
                service.convertEntityToDto(entity)
        );

        return ResponseEntity.created(newResource).body(body);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@RequestBody ChannelNameDTO dto) {
        service.delete(service.convertDtoToEntity(dto));
        log.info("REST POST /channelNames/delete/(id={})", dto.getId());
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
