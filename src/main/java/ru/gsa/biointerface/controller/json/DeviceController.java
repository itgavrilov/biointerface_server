package ru.gsa.biointerface.controller.json;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.gsa.biointerface.domain.dto.DeviceDTO;
import ru.gsa.biointerface.domain.entity.Device;
import ru.gsa.biointerface.service.DeviceService;

import java.util.Set;
import java.util.TreeSet;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 15/11/2021
 */
@Slf4j
@RestController
@RequestMapping(value = "/devices", produces = MediaType.APPLICATION_JSON_VALUE)
public class DeviceController {
    @Autowired
    DeviceService service;

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
    public DeviceDTO save(@RequestBody DeviceDTO dto) {

        Device entity = service.save(service.convertDtoToEntity(dto));
        log.info("REST POST /devices/save/(id={})", entity.getId());

        return service.convertEntityToDto(entity);
    }

    @PostMapping(value = "/delete", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void delete(@RequestBody DeviceDTO dto) {
        service.delete(service.convertDtoToEntity(dto));
        log.info("REST POST /devices/delete/(id={})", dto.getId());
    }
}
