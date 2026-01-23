package com.example.testit;

import com.example.testit.adapter.mail.MailService;
import com.example.testit.model.Status;
import com.example.testit.model.Task;
import com.example.testit.model.User;

import com.example.testit.service.TaskService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@SpringBootTest
class TaskServiceIT {

    @Autowired
    TaskService taskService;

    @Autowired
    TaskRepository taskRepository;

    @Autowired
    UserRepository userRepository;

    @MockBean
    MailService mailService;

    @Test
    void start_task_should_update_status_and_send_mail() {
        User user = userRepository.save(new User("john"));

        Task task = new Task("Task", "Desc", user);
        task = taskRepository.save(task);

        Task result = taskService.startTask(task.getId(), user.getId());

        assertThat(result.getStatus()).isEqualTo(Status.EN_COURS);
        verify(mailService).sendMail(null, null, null);
    }
}
