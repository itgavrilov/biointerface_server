package ru.gsa.biointerface.unit.controller.json;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import ru.gsa.biointerface.controller.json.IcdController;
import ru.gsa.biointerface.domain.entity.Icd;
import ru.gsa.biointerface.service.IcdService;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.gsa.biointerface.utils.IcdUtil.getIcds;

@Tag("UnitTest")
@WebMvcTest(IcdController.class)
@ActiveProfiles("test")
class IcdControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private IcdService service;

    @Test
    void getAll() throws Exception {
        List<Icd> entities = getIcds(2);
        when(service.findAll()).thenReturn(entities);

        mvc.perform(get("/api/v1/icds"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.length()").value(2));
//                .andExpect(jsonPath("$.[1].id").value(entities.get(0).getId().toString()))
    }

//    @Test
//    void getAllPageable() {
//    }
//
//    @Test
//    void getById() {
//    }
//
//    @Test
//    void save() {
//    }
//
//    @Test
//    void update() {
//    }
//
//    @Test
//    void delete() {
//    }
}