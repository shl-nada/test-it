package com.example.testit;

import com.example.testit.model.Task;
import com.example.testit.model.User;
import com.example.testit.model.Status;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class TaskRepositoryTest {

    @Autowired
    TaskRepository taskRepository;

    @Autowired
    UserRepository userRepository;

    @Test
    void crud_and_custom_query_should_work() {
        User user = userRepository.save(new User("john"));

        Task task = new Task("Task 1", "Desc", user);
        task.setStatus(Status.OUVERT);

        Task saved = taskRepository.save(task);

        assertThat(saved.getId()).isNotNull();

        Task found = taskRepository.findById(saved.getId()).orElseThrow();
        assertThat(found.getTitle()).isEqualTo("Task 1");

        found.setStatus(Status.EN_COURS);
        taskRepository.save(found);

        List<Task> tasks =
                taskRepository.findByUserAndStatus(user.getId(), Status.EN_COURS);

        assertThat(tasks).hasSize(1);

        taskRepository.delete(found);
        assertThat(taskRepository.findById(saved.getId())).isEmpty();
    }
}
