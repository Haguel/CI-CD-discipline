package com.astera.cicd;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ItemControllerTests {
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders.standaloneSetup(new ItemController(new ItemService())).build();
    }

    @Test
    void crudReturnsJsonAndCorrectStatusCodes() throws Exception {
        mvc.perform(get("/api/items")).andExpect(status().isOk()).andExpect(content().json("[]"));
        mvc.perform(post("/api/items").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Book\",\"description\":\"Lab notes\"}"))
                .andExpect(status().isCreated()).andExpect(header().string("Location", "/api/items/1"))
                .andExpect(jsonPath("$.id").value(1)).andExpect(jsonPath("$.name").value("Book"));
        mvc.perform(get("/api/items/1")).andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.description").value("Lab notes"));
        mvc.perform(get("/api/items")).andExpect(jsonPath("$[0].id").value(1));
        mvc.perform(put("/api/items/1").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Updated\",\"description\":\"New notes\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.name").value("Updated"));
        mvc.perform(get("/api/items/1")).andExpect(jsonPath("$.description").value("New notes"));
        mvc.perform(delete("/api/items/1")).andExpect(status().isNoContent()).andExpect(content().string(""));
        mvc.perform(get("/api/items")).andExpect(content().json("[]"));
    }

    @Test
    void missingItemsReturn404() throws Exception {
        mvc.perform(get("/api/items/99")).andExpect(status().isNotFound());
        mvc.perform(put("/api/items/99").contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Missing\"}")).andExpect(status().isNotFound());
        mvc.perform(delete("/api/items/99")).andExpect(status().isNotFound());
    }

    @Test
    void invalidRequestsReturn400WithoutChangingStoredItem() throws Exception {
        mvc.perform(post("/api/items").contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\" \"}")).andExpect(status().isBadRequest());
        mvc.perform(post("/api/items").contentType(MediaType.APPLICATION_JSON)
                .content("{}")).andExpect(status().isBadRequest());
        mvc.perform(post("/api/items").contentType(MediaType.APPLICATION_JSON)
                .content("{broken")).andExpect(status().isBadRequest());
        mvc.perform(get("/api/items")).andExpect(content().json("[]"));
        mvc.perform(post("/api/items").contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Original\"}")).andExpect(status().isCreated());
        mvc.perform(put("/api/items/1").contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"\"}")).andExpect(status().isBadRequest());
        mvc.perform(get("/api/items/1")).andExpect(jsonPath("$.name").value("Original"));
    }
}
