package ru.gsa.biointerface.integration;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import ru.gsa.biointerface.service.PatientService;

@RunWith(SpringRunner.class)
@SpringBootTest
@ComponentScan(basePackageClasses = {
        PatientService.class
})
@ActiveProfiles("test")
class PatientServiceTest {

    @Autowired
    private PatientService service;

    @BeforeAll
    static void setUp() {
    }

    @Test
    void findAll() {
        System.out.println(service.findAll(null));
    }

    @Test
    void testFindAll() {
    }

    @Test
    void getById() {
    }

    @Test
    void saveOrUpdate() {
    }

    @Test
    void delete() {
    }
}