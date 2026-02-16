package com.example.testit.service;

import com.example.testit.adapter.mail.MailService;
import com.example.testit.model.Status;
import com.example.testit.model.Task;
import com.example.testit.model.User;
import com.example.testit.repository.TaskRepository;
import com.example.testit.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final MailService mailService;

    public TaskService(TaskRepository taskRepository, UserRepository userRepository, MailService mailService) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.mailService = mailService;
    }

    public List<Task> findAll() {
        return taskRepository.findAll();
    }

    public Optional<Task> findById(Long id) {
        return taskRepository.findById(id);
    }

    public List<Task> findByUserId(Long userId) {
        return taskRepository.findByAssignedUserId(userId);
    }

    public Task createTask(String title, String description, Long requesterId, Long assignedId) {
        User requester = userRepository.findById(requesterId)
                .orElseThrow(() -> new IllegalArgumentException("Requester not found"));
        User assigned = userRepository.findById(assignedId)
                .orElseThrow(() -> new IllegalArgumentException("Assigned user not found"));
        Task task = new Task(title, description, assigned);
        task.setRequester(requester);
        return taskRepository.save(task);
    }

    public Task updateTask(Task task) {
        if (task.getId() == null || !taskRepository.existsById(task.getId())) {
            throw new IllegalArgumentException("Task not found");
        }
        return taskRepository.save(task);
    }

    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }

    @Transactional
    public Task startTask(Long taskId, Long userId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));
        if (!task.getAssignedUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Task not assigned to this user");
        }
        if (task.getStatus() != Status.OUVERT) {
            throw new IllegalStateException("Task can only be started if status is OUVERT");
        }

        List<Task> ongoingTasks = taskRepository.findByUserAndStatus(userId, Status.EN_COURS);
        if (!ongoingTasks.isEmpty()) {
            throw new IllegalStateException("User cannot have more than one task EN_COURS");
        }
        task.setStatus(Status.EN_COURS);
        Task saved = taskRepository.save(task);
        mailService.sendMail(task.getAssignedUser(), "Tâche commencée", "La tâche '" + task.getTitle() + "' a été commencée.");
        return saved;
    }

    @Transactional
    public Task finishTask(Long taskId, Long userId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));
        if (!task.getAssignedUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Task not assigned to this user");
        }
        if (task.getStatus() != Status.EN_COURS) {
            throw new IllegalStateException("Task can only be finished if status is EN_COURS");
        }
        task.setStatus(Status.FINI);
        Task saved = taskRepository.save(task);
        mailService.sendMail(task.getAssignedUser(), "Tâche terminée", "La tâche '" + task.getTitle() + "' a été terminée.");
        if (task.getAssignedUser().getManager() != null) {
            mailService.sendMail(task.getAssignedUser().getManager(), "Tâche de votre subordonné terminée",
                    "La tâche '" + task.getTitle() + "' assignée à " + task.getAssignedUser().getUsername() + " a été terminée.");
        }
        return saved;
    }
}
