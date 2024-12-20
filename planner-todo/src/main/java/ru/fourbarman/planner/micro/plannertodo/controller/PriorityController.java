package ru.fourbarman.planner.micro.plannertodo.controller;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import ru.fourbarman.planner.micro.plannerentity.entity.Priority;
import ru.fourbarman.planner.micro.plannertodo.resttemplate.UserRestBuilder;
import ru.fourbarman.planner.micro.plannertodo.search.PrioritySearchValues;
import ru.fourbarman.planner.micro.plannertodo.service.PriorityService;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/priority")
public class PriorityController {
    private final PriorityService priorityService;
    private final UserRestBuilder userRestBuilder;

    public PriorityController(PriorityService priorityService, UserRestBuilder userRestBuilder) {
        this.priorityService = priorityService;
        this.userRestBuilder = userRestBuilder;
    }

    @PostMapping("/all")
    public List<Priority> findAll(@RequestBody String userId) {
        return priorityService.findAll(userId);
    }

    @PostMapping("/add")
    public ResponseEntity<Priority> add(@RequestBody Priority priority, @AuthenticationPrincipal Jwt jwt) {

        priority.setUserId(jwt.getSubject());

        //id создфется автоматически в БД
        if (priority.getId() != null && priority.getId() != 0) {
            return new ResponseEntity("Redundunt param: priority id must be null", HttpStatus.NOT_ACCEPTABLE);
        }
        //если передали пустое значение title
        if (priority.getTitle() == null || priority.getTitle().trim().isEmpty()) {
            return new ResponseEntity("Missed param: title", HttpStatus.NOT_ACCEPTABLE);
        }
        //если передали пустое значение color
        if (priority.getColor() == null || priority.getColor().trim().isEmpty()) {
            return new ResponseEntity("Missed param: color", HttpStatus.NOT_ACCEPTABLE);
        }

        //вызываем микросервис из другого модуля
        // если пользователь существует, то создаем запись
//        if(userRestBuilder.userExists(priority.getUserId())) {
//            return ResponseEntity.ok(priorityService.add(priority));
//        }

        if (!priority.getUserId().isBlank()) {
            return ResponseEntity.ok(priorityService.add(priority));
        }
        // пользователя не существует => ошибка
        return new ResponseEntity("user id = " + priority.getUserId() + " not found", HttpStatus.NOT_ACCEPTABLE);
    }

    @PutMapping("/update")
    public ResponseEntity update(@RequestBody Priority priority) {
        if (priority.getId() == null && priority.getId() == 0) {
            return new ResponseEntity("Missed param: id", HttpStatus.NOT_ACCEPTABLE);
        }

        if (priority.getTitle() == null || priority.getTitle().trim().isEmpty()) {
            return new ResponseEntity("Missed param: title", HttpStatus.NOT_ACCEPTABLE);
        }

        if (priority.getColor() == null || priority.getColor().trim().isEmpty()) {
            return new ResponseEntity("Missed param: color", HttpStatus.NOT_ACCEPTABLE);
        }
        priorityService.update(priority);

        return new ResponseEntity(HttpStatus.OK);
    }

    @PostMapping("/id")
    public ResponseEntity<Priority> findById(@RequestBody Long id) {
        Priority priority = null;

        try{
            priority = priorityService.findById(id);
        } catch (NoSuchElementException e) {
            e.printStackTrace();
            return new ResponseEntity("id = " + id + " not found", HttpStatus.NOT_ACCEPTABLE);
        }
        return ResponseEntity.ok(priority);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity delete(@PathVariable("id") Long id) {
        try{
            priorityService.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            e.printStackTrace();
            return new ResponseEntity("id = " + id + " not found", HttpStatus.NOT_ACCEPTABLE);
        }
        return new ResponseEntity(HttpStatus.OK);
    }

    @PostMapping("/search")
    public ResponseEntity<List<Priority>> search(@RequestBody PrioritySearchValues prioritySearchValues,
                                                 @AuthenticationPrincipal Jwt jwt) {

        prioritySearchValues.setUserId(jwt.getSubject());

        if (prioritySearchValues.getUserId().isBlank()) {
            return new ResponseEntity("Missed param: userId", HttpStatus.NOT_ACCEPTABLE);
        }
        return ResponseEntity.ok(priorityService.find(prioritySearchValues.getTitle(), prioritySearchValues.getUserId()));
    }

}
