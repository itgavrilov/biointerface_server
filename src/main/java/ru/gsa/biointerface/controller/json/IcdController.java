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
import ru.gsa.biointerface.domain.dto.IcdDTO;
import ru.gsa.biointerface.domain.entity.Icd;
import ru.gsa.biointerface.service.IcdService;

import java.net.URI;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 15/11/2021
 */
@Slf4j
@RequiredArgsConstructor
@Tag(name = "ICDs", description = "ICD disease codes")
@RestController
@RequestMapping(
        value = "/icds",
        produces = MediaType.APPLICATION_JSON_VALUE,
        consumes = MediaType.APPLICATION_JSON_VALUE)
public class IcdController {
    private static final String version = "0.0.1-SNAPSHOT";

    private final IcdService service;
    private final ObjectMapper mapper;

    @Operation(summary = "Get all ICD disease codes")
    @GetMapping
    public Set<IcdDTO> getAll() {
        log.info("REST GET /icds");
        Set<Icd> entities = service.findAll();
        Set<IcdDTO> dtos = new TreeSet<>();
        entities.forEach(entity -> dtos.add(
                service.convertEntityToDto(entity)
        ));

        return dtos;
    }

    @Operation(summary = "Get ICD disease code by ID")
    @GetMapping("/{id}")
    public IcdDTO get(@PathVariable int id) {
        log.info("REST GET /icds/{}", id);

        return service.convertEntityToDto(service.findById(id));
    }

    @Operation(summary = "Delete ICD disease code by ID")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@RequestBody IcdDTO dto) {
        service.delete(service.convertDtoToEntity(dto));
        log.info("REST POST /icds/delete/(id={})", dto.getId());
    }

    @Operation(summary = "Save new ICD disease code")
    @PutMapping
    public ResponseEntity<String> save(@RequestBody IcdDTO dto) throws JsonProcessingException {
        Icd entity = service.save(service.convertDtoToEntity(dto));
        log.info("REST POST /icds/save/(id={})", entity.getId());
        URI newResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/icds/{id}")
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
