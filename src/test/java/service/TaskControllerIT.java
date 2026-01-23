package com.example.testit;

import com.example.testit.controller.TaskController;
import com.example.testit.service.TaskService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
class TaskControllerIT {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    TaskService taskService;

    @Test
    void create_task_ok() throws Exception {
        when(taskService.create(any())).thenReturn(null);

        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "title": "Task test",
                              "assignedUserId": 1
                            }
                        """))
                .andExpect(status().isCreated());
    }

    @Test
    void create_task_bad_request() throws Exception {
        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "title": ""
                            }
                        """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void get_task_not_found() throws Exception {
        when(taskService.getById(99L)).thenThrow(new RuntimeException());

        mockMvc.perform(get("/tasks/99"))
                .andExpect(status().isInternalServerError());
    }
}
