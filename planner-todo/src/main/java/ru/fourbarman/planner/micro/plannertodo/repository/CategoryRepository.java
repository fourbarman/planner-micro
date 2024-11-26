package ru.fourbarman.planner.micro.plannertodo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.fourbarman.planner.micro.plannerentity.entity.Category;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    // поиск категорий пользователя (по названию) по его email
    List<Category> findByUserIdOrderByTitleAsc(String id);

    //поиск значений по названию для конкретного пользователя
    @Query("""
SELECT c FROM Category c WHERE\s
(:title is null or :title=''\s
or lower(c.title) like lower(concat('%', :title, '%')))\s
and c.userId=:id\s
order by c.title asc
""")
    List<Category> findByTitle(@Param("title") String title, @Param("id") String id);
}
