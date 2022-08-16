package ru.gsa.biointerface.unit.controller.json;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.test.web.servlet.MockMvc;
import ru.gsa.biointerface.controller.json.IcdController;
import ru.gsa.biointerface.domain.entity.Icd;
import ru.gsa.biointerface.mapper.IcdMapperImpl;
import ru.gsa.biointerface.service.IcdService;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.gsa.biointerface.utils.IcdUtil.getIcds;

@Tag("UnitTest")
@WebMvcTest(IcdController.class)
@Import({
        IcdMapperImpl.class
})
@Profile("!dev")
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
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.[0].id").value(entities.get(0).getId()))
                .andExpect(jsonPath("$.[0].name").value(entities.get(0).getName()))
                .andExpect(jsonPath("$.[0].version").value(entities.get(0).getVersion()))
                .andExpect(jsonPath("$.[0].comment").value(entities.get(0).getComment()))
                .andExpect(jsonPath("$.[0].creationDate").value(entities.get(0).getCreationDate()))
                .andExpect(jsonPath("$.[0].modifyDate").value(entities.get(0).getModifyDate()))
                .andExpect(jsonPath("$.[1].id").value(entities.get(1).getId()))
                .andExpect(jsonPath("$.[1].name").value(entities.get(1).getName()))
                .andExpect(jsonPath("$.[1].version").value(entities.get(1).getVersion()))
                .andExpect(jsonPath("$.[1].comment").value(entities.get(1).getComment()))
                .andExpect(jsonPath("$.[1].creationDate").value(entities.get(1).getCreationDate()))
                .andExpect(jsonPath("$.[1].modifyDate").value(entities.get(1).getModifyDate()));
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