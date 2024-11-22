package ru.fourbarman.planner.micro.plannertodo.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.fourbarman.planner.micro.plannerentity.entity.Stat;
import ru.fourbarman.planner.micro.plannertodo.repository.StatRepository;


@Service
@Transactional
public class StatService {

    private final StatRepository statRepository;

    public StatService(StatRepository statRepository) {
        this.statRepository = statRepository;
    }

    public Stat findStat(Long userId) {
        return statRepository.findByUserId(userId);
    }
}
