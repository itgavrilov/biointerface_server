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
import ru.gsa.biointerface.domain.dto.DeviceDTO;
import ru.gsa.biointerface.domain.entity.Device;
import ru.gsa.biointerface.service.DeviceService;

import java.net.URI;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 15/11/2021
 */
@Slf4j
@RestController
@RequestMapping(value = "/devices", produces = MediaType.APPLICATION_JSON_VALUE)
public class DeviceController {
    private static final String version = "0.0.1-SNAPSHOT";
    @Autowired
    DeviceService service;
    @Autowired
    ObjectMapper mapper;

    @GetMapping
    public Set<DeviceDTO> getAll() {
        log.info("REST GET /devices");
        Set<Device> entities = service.findAll();
        Set<DeviceDTO> dtos = new TreeSet<>();
        entities.forEach(entity -> dtos.add(
                service.convertEntityToDto(entity)
        ));

        return dtos;
    }

    @GetMapping("/{id}")
    public DeviceDTO get(@PathVariable int id) {
        log.info("REST GET /devices/{}", id);

        return service.convertEntityToDto(service.findById(id));
    }

    @GetMapping("/get")
    public DeviceDTO getP(@RequestParam int id) {
        log.info("REST GET /devices/get?id={}", id);

        return service.convertEntityToDto(service.findById(id));
    }

    @PostMapping(value = "/save", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> save(@RequestBody DeviceDTO dto) throws JsonProcessingException {
        Device entity = service.save(service.convertDtoToEntity(dto));
        log.info("REST POST /devices/save/(id={})", entity.getId());
        URI newResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/devices/{id}")
                .buildAndExpand(entity.getId()).toUri();
        String body = mapper.writeValueAsString(
                service.convertEntityToDto(entity)
        );

        return ResponseEntity.created(newResource).body(body);
    }

    @PostMapping(value = "/delete", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@RequestBody DeviceDTO dto) {
        service.delete(service.convertDtoToEntity(dto));
        log.info("REST POST /devices/delete/(id={})", dto.getId());
    }

    @PostMapping(value = "/health")
    @ResponseStatus(HttpStatus.OK)
    public void health() {
    }

    @PostMapping(value = "/version")
    @ResponseStatus(HttpStatus.OK)
    public String version() {
        return version;
    }
}
