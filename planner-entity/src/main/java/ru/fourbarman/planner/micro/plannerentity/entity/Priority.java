package ru.fourbarman.planner.micro.plannerentity.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.util.Objects;

/*

справочноное значение - приоритет пользователя
может использовать для своих задач

 */

@Entity
@Table(name = "priority", schema = "todo", catalog = "planner-todo")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Priority {

    // указываем, что поле заполняется в БД
    // нужно, когда добавляем новый объект и он возвращается уже с новым id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    private String title;
    private String color;

//    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY) //доступ к полю будет только в режиме write only
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "user_id", referencedColumnName = "id") // по каким полям связывать (foreign key)
//    private User user;

    @Column(name = "user_id")
    private String userId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Priority priority = (Priority) o;
        return id.equals(priority.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return title;
    }
}
