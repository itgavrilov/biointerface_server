package ru.gsa.biointerface.controller.json;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.gsa.biointerface.domain.dto.IcdDTO;
import ru.gsa.biointerface.domain.entity.Icd;
import ru.gsa.biointerface.service.IcdService;

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
    public IcdDTO save(@RequestBody IcdDTO dto) {

        Icd entity = service.save(service.convertDtoToEntity(dto));
        log.info("REST POST /icds/save/(id={})", entity.getId());

        return service.convertEntityToDto(entity);
    }

    @PostMapping(value = "/delete", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void delete(@RequestBody IcdDTO dto) {
        service.delete(service.convertDtoToEntity(dto));
        log.info("REST POST /icds/delete/(id={})", dto.getId());
    }
}
