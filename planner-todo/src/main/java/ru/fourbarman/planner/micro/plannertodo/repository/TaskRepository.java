package ru.fourbarman.planner.micro.plannertodo.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.fourbarman.planner.micro.plannerentity.entity.Task;

import java.util.Date;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    @Query("""
SELECT t FROM Task t WHERE\s
(:title is null or :title='' or lower(t.title) like lower(concat('%', :title, '%'))) and\s
(:completed is null or t.completed=:completed) and\s
(:priorityId is null or t.priority.id=:priorityId) and\s
(:categoryId is null or t.category.id=:categoryId) and\s
(:categoryId is null or t.category.id=:categoryId) and\s
(
(cast(:dateFrom as timestamp) is null or t.taskDate>=:dateFrom) and\s
(cast(:dateTo as timestamp) is null or t.taskDate<=:dateTo)
) and\s
(t.userId=:userId)
""")
    Page<Task> findByParams(@Param("title") String title,
                            @Param("completed") Boolean completed,
                            @Param("priorityId") Long priorityId,
                            @Param("categoryId") Long categoryId,
                            @Param("userId") Long userId,
                            @Param("dateFrom") Date dateFrom,
                            @Param("dateTo") Date dateTo,
                            Pageable pageable);

    List<Task> findByUserIdOrderByTitleAsc(Long userId);
}
