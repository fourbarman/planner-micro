package ru.fourbarman.planner.micro.plannertodo.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.fourbarman.planner.micro.plannerentity.entity.Task;
import ru.fourbarman.planner.micro.plannertodo.repository.TaskRepository;

import java.util.Date;
import java.util.List;

@Service
@Transactional
public class TaskService {
    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public List<Task> findAll(Long userId) {
        return taskRepository.findByUserIdOrderByTitleAsc(userId);
    }

    public Task add(Task task) {
        return taskRepository.save(task);
    }

    public void deleteById(long id) {
        taskRepository.deleteById(id);
    }

    public Page<Task> findByParams(String title,
                                   Boolean completed,
                                   Long priorityId,
                                   Long categoryId,
                                   Long userId,
                                   Date dateFrom,
                                   Date dateTo,
                                   PageRequest paging) {
        return taskRepository.findByParams(title, completed, priorityId, categoryId, userId, dateFrom, dateTo, paging);
    }

    public Task findById(long id) {
        return taskRepository.findById(id).get();
    }

    public void update(Task task) {
        taskRepository.save(task);
    }
}
