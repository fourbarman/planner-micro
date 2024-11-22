package ru.fourbarman.planner.micro.plannerusers.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.fourbarman.planner.micro.plannerentity.entity.User;


public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);

    void deleteByEmail(String email);

    @Query("""
SELECT u FROM User u\s
WHERE (:email IS NULL OR :email='' OR lower(u.email) LIKE lower(concat('%', :email, '%')))\s
OR (:username IS NULL OR :username='' OR lower(u.username) LIKE lower(concat('%', :username, '%')))
""")
    Page<User> findByParams(@Param("email") String email,
                            @Param("username") String username,
                            Pageable pageable);
}
