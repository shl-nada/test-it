package com.example.testit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReturn401_whenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/tasks"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    void shouldReturn200_whenAuthenticated() throws Exception {
        mockMvc.perform(get("/tasks"))
                .andExpect(status().isOk());
    }



    @Test
    @WithMockUser(roles = "ADMIN")
    void delete_shouldReturn200_whenAdmin() throws Exception {
        mockMvc.perform(delete("/tasks/1"))
                .andExpect(status().isNoContent());
    }
    @Test
    @WithMockUser(roles = "MANAGER")
    void delete_shouldReturn403_whenManager() throws Exception {
        mockMvc.perform(delete("/tasks/1"))
                .andExpect(status().isForbidden());
    }


}
