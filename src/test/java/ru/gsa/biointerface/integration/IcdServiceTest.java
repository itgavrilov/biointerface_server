package ru.gsa.biointerface.integration;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import ru.gsa.biointerface.service.IcdService;

@RunWith(SpringRunner.class)
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