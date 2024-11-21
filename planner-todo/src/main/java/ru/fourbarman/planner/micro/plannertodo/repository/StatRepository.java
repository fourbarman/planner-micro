package ru.fourbarman.planner.micro.plannertodo.repository;

import org.springframework.data.repository.CrudRepository;
import ru.fourbarman.planner.micro.plannertodo.entity.Stat;

public interface StatRepository extends CrudRepository<Stat, Long> {

    Stat findByUserId(Long userId);
}
