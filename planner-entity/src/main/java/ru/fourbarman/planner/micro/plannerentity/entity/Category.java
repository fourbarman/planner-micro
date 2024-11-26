package ru.fourbarman.planner.micro.plannerentity.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.util.Objects;

/*

справочноное значение - категория пользователя
может использовать для своих задач

*/

@Entity
@Table(name = "category", schema = "todo", catalog = "planner-todo")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Category {

    // указываем, что поле заполняется в БД
    // нужно, когда добавляем новый объект и он возвращается уже с новым id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    private String title;

    @Column(name = "completed_count", updatable = false) // т.к. это поле высчитывается автоматически в триггерах - вручную его не обновляем (updatable = false)
    private Long completedCount;

    @Column(name = "uncompleted_count", updatable = false) // т.к. это поле высчитывается автоматически в триггерах - вручную его не обновляем (updatable = false)
    private Long uncompletedCount;

//    //@JsonIgnore //для того, чтобы в ответном json не было объекта user - при добавлении будет ошибка userid = null, поэтому ставим:
//    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY) //доступ к полю будет только в режиме write only
//    @ManyToOne(fetch = FetchType.LAZY) //чтобы не делать доп запрос к БД на user
//    @JoinColumn(name = "user_id", referencedColumnName = "id") // по каким полям связаны эти 2 объекта (foreign key)
//    private User user;

    @Column(name = "user_id")
    private String userId;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        return id.equals(category.id);
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
