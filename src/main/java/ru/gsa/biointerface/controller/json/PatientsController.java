package ru.gsa.biointerface.controller.json;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.gsa.biointerface.domain.dto.IcdDTO;
import ru.gsa.biointerface.domain.dto.PatientDTO;
import ru.gsa.biointerface.domain.entity.Patient;
import ru.gsa.biointerface.service.IcdService;
import ru.gsa.biointerface.service.PatientService;

import java.util.Set;
import java.util.TreeSet;

/**
 * Created by Gavrilov Stepan (itgavrilov@gmail.com) on 15/11/2021
 */
@Slf4j
@RestController
@RequestMapping(
        value = "/patients",
        produces = MediaType.APPLICATION_JSON_VALUE,
        consumes = MediaType.APPLICATION_JSON_VALUE)
public class PatientsController {
    @Autowired
    PatientService service;
    @Autowired
    IcdService icdService;

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

    @PostMapping("/getbyicd")
    public Set<PatientDTO> getByIcd(@RequestBody IcdDTO icdDTO) {
        log.info("REST GET /patients/getbyicd(icd_id={})", icdDTO.getId());
        Set<Patient> entities =
                service.findAllByIcd(icdService.convertDtoToEntity(icdDTO));
        Set<PatientDTO> dtos = new TreeSet<>();
        entities.forEach(entity -> dtos.add(
                service.convertEntityToDto(entity)
        ));

        return dtos;
    }

    @GetMapping("/{id}")
    public PatientDTO get(@PathVariable int id) {
        log.info("REST GET /patients/{}", id);

        return service.convertEntityToDto(service.findById(id));
    }

    @GetMapping("/get")
    public PatientDTO getP(@RequestParam int id) {
        log.info("REST GET /patients/get?id={}", id);

        return service.convertEntityToDto(service.findById(id));
    }

    @PostMapping(value = "/save")
    public PatientDTO save(@RequestBody PatientDTO dto) {

        Patient entity = service.save(service.convertDtoToEntity(dto));
        log.info("REST POST /patients/save(id={})", entity.getId());

        return service.convertEntityToDto(entity);
    }

    @PostMapping(value = "/delete")
    public void delete(@RequestBody PatientDTO dto) {
        service.delete(service.convertDtoToEntity(dto));
        log.info("REST POST /patients/delete(id={})", dto.getId());
    }
}
