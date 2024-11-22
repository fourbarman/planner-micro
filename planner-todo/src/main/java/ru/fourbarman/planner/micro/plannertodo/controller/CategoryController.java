package ru.fourbarman.planner.micro.plannertodo.controller;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.fourbarman.planner.micro.plannerentity.entity.Category;
import ru.fourbarman.planner.micro.plannertodo.resttemplate.UserRestBuilder;
import ru.fourbarman.planner.micro.plannertodo.search.CategorySearchValues;
import ru.fourbarman.planner.micro.plannertodo.service.CategoryService;
import ru.fourbarman.planner.micro.plannertodo.webclient.UserWebClientBuilder;

import java.util.List;
import java.util.NoSuchElementException;

/*

Используем @RestController вместо обычного @Controller, чтобы все ответы сразу оборачивались в JSON,
иначе пришлось бы добавлять лишние объекты в код, использовать @ResponseBody для ответа, указывать тип отправки JSON

Названия методов могут быть любыми, главное не дублировать их имена внутри класса и URL mapping

*/

@RestController
@RequestMapping("/category") // базовый URI
public class CategoryController {

    // доступ к данным из БД
    private final CategoryService categoryService;
    private final UserRestBuilder userRestBuilder;
    private final UserWebClientBuilder userWebClientBuilder;

    // автоматическое внедрение экземпляра класса через конструктор
    // не используем @Autowired ля переменной класса, т.к. "Field injection is not recommended "
    public CategoryController(CategoryService categoryService, UserWebClientBuilder userWebClientBuilder) {
        this.categoryService = categoryService;
        this.userWebClientBuilder = userWebClientBuilder;
        this.userRestBuilder = new UserRestBuilder();
    }

    //можно без try-catch, тогда будет созвращаться полная ошибка (stacktrace)
    @PostMapping("/id")
    public ResponseEntity<Category> findById(@RequestBody Long id) {

        Category category = null;

        try{
            category = categoryService.findById(id);
        } catch (EmptyResultDataAccessException | NoSuchElementException e) {
            e.printStackTrace();
            return new ResponseEntity("id = " + id + " not found", HttpStatus.NOT_FOUND);
        }

        return ResponseEntity.ok(category);
    }
    //этот метод дублирует search(@RequestBody CategorySearchValues categorySearchValues)
    @PostMapping("/all")
    public List<Category> findAll(@RequestBody Long userId) {
        return categoryService.findAll(userId);
    }

    @PostMapping(value = "/add", consumes = "application/json", produces = "application/json")
    public ResponseEntity<Category> add(@RequestBody Category category) {
        //проверяем на наличие id у категории
        if (category.getId() != null && category.getId() != 0) {
            //не нужно передавать id, т.к. id создается в БД
            return new ResponseEntity("Redundant param: id must be null", HttpStatus.NOT_ACCEPTABLE);
        }

        //если передаем пустой title, то ошибка
        if (category.getTitle() == null || category.getTitle().trim().isEmpty()) {
            return new ResponseEntity("Missed param: title must be not null", HttpStatus.NOT_ACCEPTABLE);
        }
        //вызываем микросервис из другого модуля
        // если пользователь существует, то создаем запись

        // используем синхронный RestClient

//        if(userRestBuilder.userExists(category.getUserId())) {
//            return ResponseEntity.ok(categoryService.add(category));
//        }

        // используем синхронный WebClient

//        if(userWebClientBuilder.userExists(category.getUserId())) {
//            return ResponseEntity.ok(categoryService.add(category));
//        }

        // используем асинхронный WebClient => Результат не будет ожилаться и выполнение продолжится!
        userWebClientBuilder.userExistsAsync(category.getUserId())
                .subscribe(user -> System.out.println("user = " + user));

        // пользователя не существует => ошибка
        return new ResponseEntity("user id = " + category.getUserId() + " not found", HttpStatus.NOT_ACCEPTABLE);
    }

    @PutMapping("/update")
    public ResponseEntity update(@RequestBody Category category) {
        //id должен быть заполнен
        if (category.getId() == null && category.getId() == 0) {
            return new ResponseEntity("Missed param: id", HttpStatus.NOT_ACCEPTABLE);
        }

        if (category.getTitle() == null || category.getTitle().trim().isEmpty()) {
            return new ResponseEntity("Missed param: title", HttpStatus.NOT_ACCEPTABLE);
        }

        categoryService.update(category);

        return new ResponseEntity(HttpStatus.OK);
    }

    //вместо DELETE можно использовать POST и передавать id в теле запроса
    @DeleteMapping("/delete/{id}")
    public ResponseEntity delete(@PathVariable("id") Long id) {

        try{
            categoryService.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            e.printStackTrace();
            return new ResponseEntity("id = " + id + " not found", HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity(HttpStatus.OK);
    }

    @PostMapping("/search")
    public ResponseEntity<List<Category>> search(@RequestBody CategorySearchValues categorySearchValues) {

        // 1. если title == null, то получим все категории пользователя по его email
        // 2. если email == null, вернуть ошибку
        if (categorySearchValues.getUserId() == null || categorySearchValues.getUserId() == 0) {
            return new ResponseEntity("Missed param: email", HttpStatus.NOT_ACCEPTABLE);
        }

        List<Category> list = categoryService.findByTitle(categorySearchValues.getTitle(),
                categorySearchValues.getUserId());

        return ResponseEntity.ok(list);
    }
}
