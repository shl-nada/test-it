package service;

import com.example.testit.adapter.mail.MailService;
import com.example.testit.model.Status;
import com.example.testit.model.Task;
import com.example.testit.model.User;
import com.example.testit.repository.TaskRepository;
import com.example.testit.repository.UserRepository;
import com.example.testit.service.TaskService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {

    @Mock
    TaskRepository taskRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    MailService mailService;

    @InjectMocks
    TaskService taskService;

    @Test
    void should_start_task_successfully() {
        User user = new User();
        user.setId(1L);

        Task task = new Task("Titre", "Desc", user);
        task.setId(10L);
        task.setStatus(Status.OUVERT);

        when(taskRepository.findById(10L)).thenReturn(Optional.of(task));
        when(taskRepository.findByUserAndStatus(1L, Status.EN_COURS))
                .thenReturn(List.of());
        when(taskRepository.save(task)).thenReturn(task);

        Task result = taskService.startTask(10L, 1L);

        assertEquals(Status.EN_COURS, result.getStatus());

        verify(mailService).sendMail(any(), any(), any());
    }
}
