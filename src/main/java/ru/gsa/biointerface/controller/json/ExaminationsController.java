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
import ru.gsa.biointerface.domain.dto.ExaminationDTO;
import ru.gsa.biointerface.domain.dto.PatientDTO;
import ru.gsa.biointerface.domain.entity.Examination;
import ru.gsa.biointerface.service.DeviceService;
import ru.gsa.biointerface.service.ExaminationService;
import ru.gsa.biointerface.service.PatientService;

import java.net.URI;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 15/11/2021
 */
@Slf4j
@RestController
@RequestMapping(
        value = "/examinations",
        produces = MediaType.APPLICATION_JSON_VALUE,
        consumes = MediaType.APPLICATION_JSON_VALUE)
public class ExaminationsController {
    private static final String version = "0.0.1-SNAPSHOT";
    @Autowired
    ExaminationService service;
    @Autowired
    PatientService patientService;
    @Autowired
    DeviceService deviceService;
    @Autowired
    ObjectMapper mapper;

    @GetMapping
    public Set<ExaminationDTO> getAll() {
        log.info("REST GET /examinations");
        Set<Examination> entities = service.findAll();
        Set<ExaminationDTO> dtos = new TreeSet<>();
        entities.forEach(entity -> dtos.add(
                service.convertEntityToDto(entity)
        ));

        return dtos;
    }

    @PostMapping("/getByPatient")
    public Set<ExaminationDTO> getByPatient(@RequestBody PatientDTO patientDTO) {
        log.info("REST GET /examinations/getByPatient(patientId={})", patientDTO.getId());
        Set<Examination> entities =
                service.findByPatient(patientService.convertDtoToEntity(patientDTO));
        Set<ExaminationDTO> dtos = new TreeSet<>();
        entities.forEach(entity -> dtos.add(
                service.convertEntityToDto(entity)
        ));

        return dtos;
    }

    @PostMapping("/getByDevice")
    public Set<ExaminationDTO> getByDevice(@RequestBody DeviceDTO deviceDTO) {
        log.info("REST GET /examinations/getByDevice(deviceId={})", deviceDTO.getId());
        Set<Examination> entities =
                service.findByDevice(deviceService.convertDtoToEntity(deviceDTO));
        Set<ExaminationDTO> dtos = new TreeSet<>();
        entities.forEach(entity -> dtos.add(
                service.convertEntityToDto(entity)
        ));

        return dtos;
    }

    @GetMapping("/{id}")
    public ExaminationDTO get(@PathVariable int id) {
        log.info("REST GET /examinations/{}", id);

        return service.convertEntityToDto(service.findById(id));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@RequestBody ExaminationDTO dto) {
        service.delete(service.convertDtoToEntity(dto));
        log.info("REST POST /examinations/delete/(id={})", dto.getId());
    }

    @PutMapping
    public ResponseEntity<String> save(@RequestBody ExaminationDTO dto) throws JsonProcessingException {
        Examination entity = service.save(service.convertDtoToEntity(dto));
        log.info("REST POST /examinations/save/(id={})", entity.getId());
        URI newResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/devices/{id}")
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
