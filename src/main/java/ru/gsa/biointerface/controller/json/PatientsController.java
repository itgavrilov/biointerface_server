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
import ru.gsa.biointerface.domain.dto.PatientDTO;
import ru.gsa.biointerface.domain.entity.Patient;
import ru.gsa.biointerface.service.IcdService;
import ru.gsa.biointerface.service.PatientService;

import java.net.URI;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 15/11/2021
 */
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Patients", description = "Patient records")
@RestController
@RequestMapping(
        value = "/patients",
        produces = MediaType.APPLICATION_JSON_VALUE,
        consumes = MediaType.APPLICATION_JSON_VALUE)
public class PatientsController {
    private static final String version = "0.0.1-SNAPSHOT";

    private final PatientService service;
    private final IcdService icdService;
    private final ObjectMapper mapper;

    @Operation(summary = "Get all patient records")
    @GetMapping
    public Set<PatientDTO> getAll() {
        log.info("REST GET /patients");
        Set<Patient> entities = service.findAll();
        Set<PatientDTO> dtos = new TreeSet<>();
        for (Patient entity : entities) {
            dtos.add(
                    service.convertEntityToDto(entity)
            );
        }

        return dtos;
    }

    @Operation(summary = "Get all patient records by ICD disease code")
    @PostMapping("/by-icd")
    public Set<PatientDTO> getByIcd(@RequestBody IcdDTO icdDTO) {
        log.info("REST GET /patients/getByIcd(icdId={})", icdDTO.getId());
        Set<Patient> entities =
                service.findAllByIcd(icdService.convertDtoToEntity(icdDTO));
        Set<PatientDTO> dtos = new TreeSet<>();
        entities.forEach(entity -> dtos.add(
                service.convertEntityToDto(entity)
        ));

        return dtos;
    }

    @Operation(summary = "Get patient record by ID")
    @GetMapping("/{id}")
    public PatientDTO get(@PathVariable int id) {
        log.info("REST GET /patients/{}", id);

        return service.convertEntityToDto(service.findById(id));
    }

    @Operation(summary = "Delete patient record by ID")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@RequestBody PatientDTO dto) {
        service.delete(service.convertDtoToEntity(dto));
        log.info("REST POST /patients/delete(id={})", dto.getId());
    }

    @Operation(summary = "Save patient record")
    @PutMapping
    public ResponseEntity<String> save(@RequestBody PatientDTO dto) throws JsonProcessingException {
        Patient entity = service.save(service.convertDtoToEntity(dto));
        log.info("REST POST /patients/save(id={})", entity.getId());
        URI newResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/patients/{id}")
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
