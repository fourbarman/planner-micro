package ru.fourbarman.planner.micro.plannertodo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.fourbarman.planner.micro.plannerentity.entity.Priority;

import java.util.List;

@Repository
public interface PriorityRepository extends JpaRepository<Priority, Long> {

    @Query("""
SELECT p FROM Priority p WHERE\s
(:title is null or :title=''\s
or lower(p.title) like lower(concat('%', :title, '%')))\s
AND p.userId=:id\s
ORDER BY p.title ASC
""")
    List<Priority> findByTitle(@Param("title") String title, @Param("id") String id);

    List<Priority> findByUserIdOrderByIdAsc(String id);
}
