package ru.gsa.biointerface.integration;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.gsa.biointerface.service.IcdService;

@SpringBootTest
@ActiveProfiles("test")
class IcdServiceTest {

    @Autowired
    private IcdService service;

    @BeforeAll
    static void setUp() {
    }

    @Test
    void findAll() {
        System.out.println(service.findAll());
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