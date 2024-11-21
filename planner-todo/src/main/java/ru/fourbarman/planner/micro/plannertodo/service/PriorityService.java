package ru.fourbarman.planner.micro.plannertodo.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.fourbarman.planner.micro.plannertodo.entity.Priority;
import ru.fourbarman.planner.micro.plannertodo.repository.PriorityRepository;

import java.util.List;

@Service
@Transactional
public class PriorityService {
    private final PriorityRepository priorityRepository;

    public PriorityService(PriorityRepository priorityRepository) {
        this.priorityRepository = priorityRepository;
    }

    public Priority add(Priority priority) {
        return priorityRepository.save(priority);
    }

    public Priority update(Priority priority) {
        return priorityRepository.save(priority);
    }

    public void deleteById(Long id) {
        priorityRepository.deleteById(id);
    }

    public Priority findById(Long id) {
        return priorityRepository.findById(id).get();
    }

    public List<Priority> find(String title, Long userId) {
        return priorityRepository.findByTitle(title, userId);
    }

    public List<Priority> findAll(Long userId) {
        return priorityRepository.findByUserIdOrderByIdAsc(userId);
    }
}
