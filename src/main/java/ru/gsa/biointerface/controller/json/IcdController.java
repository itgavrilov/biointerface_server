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
@RestController
@RequestMapping(value = "/icds", produces = MediaType.APPLICATION_JSON_VALUE)
public class IcdController {
    @Autowired
    IcdService service;
    @Autowired
    ObjectMapper mapper;

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

    @GetMapping("/{id}")
    public IcdDTO get(@PathVariable int id) {
        log.info("REST GET /icds/{}", id);

        return service.convertEntityToDto(service.findById(id));
    }

    @GetMapping("/get")
    public IcdDTO getP(@RequestParam int id) {
        log.info("REST GET /icds/get?id={}", id);

        return service.convertEntityToDto(service.findById(id));
    }

    @PostMapping(value = "/save", consumes = MediaType.APPLICATION_JSON_VALUE)
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

    @PostMapping(value = "/delete", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@RequestBody IcdDTO dto) {
        service.delete(service.convertDtoToEntity(dto));
        log.info("REST POST /icds/delete/(id={})", dto.getId());
    }
}
