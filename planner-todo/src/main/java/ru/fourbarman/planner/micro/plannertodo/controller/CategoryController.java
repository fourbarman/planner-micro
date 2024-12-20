package ru.fourbarman.planner.micro.plannertodo.controller;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import ru.fourbarman.planner.micro.plannerentity.entity.Category;
import ru.fourbarman.planner.micro.plannertodo.search.CategorySearchValues;
import ru.fourbarman.planner.micro.plannertodo.service.CategoryService;

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

    // автоматическое внедрение экземпляра класса через конструктор
    // не используем @Autowired ля переменной класса, т.к. "Field injection is not recommended "
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
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
    public List<Category> findAll(@RequestBody String userId) {
        return categoryService.findAll(userId);
    }


    @PostMapping(value = "/add", consumes = "application/json", produces = "application/json")
    public ResponseEntity<Category> add(@RequestBody Category category, @AuthenticationPrincipal Jwt jwt) {

        //получить subject из jwt - uuid пользователя из keycloak
        category.setUserId(jwt.getSubject());


        //проверяем на наличие id у категории
        if (category.getId() != null && category.getId() != 0) {
            //не нужно передавать id, т.к. id создается в БД
            return new ResponseEntity("Redundant param: category id must be null", HttpStatus.NOT_ACCEPTABLE);
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
//        userWebClientBuilder.userExistsAsync(category.getUserId())
//                .subscribe(user -> System.out.println("user = " + user));

        //используем feign для проверки что User существует в другом микросервисе (planner-user)
//        ResponseEntity<User> response = userFeignClient.findUserById(category.getUserId());
//
//        if (response == null) {
//            return new ResponseEntity("User service not available", HttpStatus.NOT_FOUND);
//        }
//
//        if (userFeignClient.findUserById(category.getUserId()) != null) {
//            return ResponseEntity.ok(categoryService.add(category));
//        }

        // если UUID есть, то записываем в БД
        if (!category.getUserId().isBlank()) {
            return ResponseEntity.ok(categoryService.add(category));
        }
        //если нет, то ошибка
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
    public ResponseEntity<List<Category>> search(@RequestBody CategorySearchValues categorySearchValues,
                                                 @AuthenticationPrincipal Jwt jwt) {

        categorySearchValues.setUserId(jwt.getSubject());

        // 1. если title == null, то получим все категории пользователя по его email
        // 2. если email == null, вернуть ошибку
        if (categorySearchValues.getUserId().isBlank()) {
            return new ResponseEntity("Missed param: email", HttpStatus.NOT_ACCEPTABLE);
        }

        List<Category> list = categoryService.findByTitle(categorySearchValues.getTitle(),
                categorySearchValues.getUserId());

        return ResponseEntity.ok(list);
    }
}
