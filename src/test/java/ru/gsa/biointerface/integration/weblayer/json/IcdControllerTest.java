package ru.gsa.biointerface.integration.weblayer.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import ru.gsa.biointerface.controller.json.IcdController;
import ru.gsa.biointerface.domain.dto.icd.IcdSaveOrUpdateDTO;
import ru.gsa.biointerface.domain.entity.Icd;
import ru.gsa.biointerface.mapper.IcdMapper;
import ru.gsa.biointerface.service.IcdService;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.gsa.biointerface.utils.IcdUtil.getIcd;
import static ru.gsa.biointerface.utils.IcdUtil.getIcds;

@Tag("UnitTest")
@WebMvcTest(IcdController.class)
@ActiveProfiles("test")
class IcdControllerTest {

    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private IcdMapper mapper;

    @MockBean
    private IcdService service;

    @Test
    @SneakyThrows
    void getAll() {
        List<Icd> entities = getIcds(15);
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        when(service.findAll()).thenReturn(entities);

        ResultActions resultActions = mvc.perform(get("/api/v1/icds")
                        .contentType(MediaType.ALL))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE));
        verify(service).findAll();

        JSONArray arr = new JSONArray(resultActions.andReturn().getResponse().getContentAsString());

        assertEquals(entities.size(), arr.length());

        for (int i = 0; i < arr.length(); i++) {
            JSONObject json = arr.getJSONObject(i);
            UUID id = UUID.fromString(json.getString("id"));
            Icd entity = entities.stream()
                    .filter(e -> id.equals(e.getId()))
                    .findFirst().orElseThrow();

            assertEquals(entity.getName(), json.getString("name"));
            assertEquals(entity.getVersion().toString(), json.getString("version"));
            assertEquals(entity.getCreationDate().format(formatter), json.getString("creationDate"));
            assertEquals(entity.getModifyDate().format(formatter), json.getString("modifyDate"));
        }
    }

    @Test
    @SneakyThrows
    void getAllPageable() {
        List<Icd> entities = getIcds(15);
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        int totalPages = 3;
        int page = 0;
        int size = entities.size() / totalPages;

        while (page * size <= entities.size()) {
            Pageable pageable = PageRequest.of(page, size);
            int start = pageable.getPageNumber() * pageable.getPageSize();
            int end = Math.min(start + pageable.getPageSize(), entities.size());
            List<Icd> pageList = entities.subList(start, end);
            Page<Icd> entityPage = new PageImpl<>(pageList, pageable, entities.size());
            when(service.findAll(pageable)).thenReturn(entityPage);

            ResultActions resultActions = mvc.perform(
                            get("/api/v1/icds/pageable")
                                    .contentType(MediaType.ALL)
                                    .param("page", Integer.toString(page))
                                    .param("size", Integer.toString(size)))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                    .andExpect(jsonPath("$.pageable.pageNumber").value(String.valueOf(pageable.getPageNumber())))
                    .andExpect(jsonPath("$.pageable.pageSize").value(String.valueOf(pageable.getPageSize())))
                    .andExpect(jsonPath("$.totalElements").value(String.valueOf(entities.size())))
                    .andExpect(jsonPath("$.totalPages").value(String.valueOf(totalPages)));
            verify(service).findAll(pageable);

            JSONObject jsonPage = new JSONObject(resultActions.andReturn().getResponse().getContentAsString());

            JSONArray arr = jsonPage.getJSONArray("content");

            for (int i = 0; i < arr.length(); i++) {
                JSONObject json = arr.getJSONObject(i);
                UUID id = UUID.fromString(json.getString("id"));
                Icd entity = entities.stream()
                        .filter(e -> id.equals(e.getId()))
                        .findFirst().orElseThrow();

                assertEquals(entity.getName(), json.getString("name"));
                assertEquals(entity.getVersion().toString(), json.getString("version"));
                assertEquals(entity.getCreationDate().format(formatter), json.getString("creationDate"));
                assertEquals(entity.getModifyDate().format(formatter), json.getString("modifyDate"));
            }

            page += 1;
        }
    }

    @Test
    @SneakyThrows
    void getById() {
        Icd entity = getIcd(10);
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        when(service.getById(entity.getId())).thenReturn(entity);

        ResultActions resultActions = mvc.perform(get("/api/v1/icds/{id}", entity.getId())
                        .contentType(MediaType.ALL))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE));
        verify(service).getById(entity.getId());

        JSONObject json = new JSONObject(resultActions.andReturn().getResponse().getContentAsString());

        assertEquals(entity.getId().toString(), json.getString("id"));
        assertEquals(entity.getName(), json.getString("name"));
        assertEquals(entity.getVersion().toString(), json.getString("version"));
        assertEquals(entity.getCreationDate().format(formatter), json.getString("creationDate"));
        assertEquals(entity.getModifyDate().format(formatter), json.getString("modifyDate"));
    }

    @Test
    @SneakyThrows
    void save() {
        Icd entity = getIcd(10);
        Icd entityForTest = entity.toBuilder()
                .id(null)
                .creationDate(null)
                .modifyDate(null)
                .build();
        IcdSaveOrUpdateDTO dto = mapper.toIcdSaveOrUpdateDTO(entityForTest);
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        when(service.save(entityForTest)).thenReturn(entity);

        ResultActions resultActions = mvc.perform(post("/api/v1/icds")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(header().stringValues("Location", "/api/v1/icds/" + entity.getId()))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE));
        verify(service).save(entityForTest);

        JSONObject json = new JSONObject(resultActions.andReturn().getResponse().getContentAsString());

        assertEquals(entity.getId().toString(), json.getString("id"));
        assertEquals(entity.getName(), json.getString("name"));
        assertEquals(entity.getVersion().toString(), json.getString("version"));
        assertEquals(entity.getCreationDate().format(formatter), json.getString("creationDate"));
        assertEquals(entity.getModifyDate().format(formatter), json.getString("modifyDate"));
    }

    @Test
    @SneakyThrows
    void updateEntity() {
        Icd entity = getIcd(10);
        IcdSaveOrUpdateDTO dto = mapper.toIcdSaveOrUpdateDTO(entity);
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        when(service.update(entity)).thenReturn(entity);

        ResultActions resultActions = mvc.perform(put("/api/v1/icds/{id}", entity.getId())
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE));
        verify(service).update(entity);

        JSONObject json = new JSONObject(resultActions.andReturn().getResponse().getContentAsString());

        assertEquals(entity.getId().toString(), json.getString("id"));
        assertEquals(entity.getName(), json.getString("name"));
        assertEquals(entity.getVersion().toString(), json.getString("version"));
        assertEquals(entity.getCreationDate().format(formatter), json.getString("creationDate"));
        assertEquals(entity.getModifyDate().format(formatter), json.getString("modifyDate"));
    }

    @Test
    @SneakyThrows
    void deleteEntity() {
        UUID id = UUID.randomUUID();

        mvc.perform(delete("/api/v1/icds/{id}", id).contentType(MediaType.ALL))
                .andExpect(status().isNoContent());
        verify(service).delete(id);
    }
}