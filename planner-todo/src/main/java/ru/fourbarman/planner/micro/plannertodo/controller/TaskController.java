package ru.fourbarman.planner.micro.plannertodo.controller;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.expression.ParseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.fourbarman.planner.micro.plannerentity.entity.Task;
import ru.fourbarman.planner.micro.plannertodo.resttemplate.UserRestBuilder;
import ru.fourbarman.planner.micro.plannertodo.search.TaskSearchValues;
import ru.fourbarman.planner.micro.plannertodo.service.TaskService;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/task")
public class TaskController {
    public static final String ID_COLUMN = "id";
    private final TaskService taskService;
    private final UserRestBuilder userRestBuilder;

    public TaskController(TaskService taskService, UserRestBuilder userRestBuilder) {
        this.taskService = taskService;
        this.userRestBuilder = userRestBuilder;
    }

    @PostMapping("/all")
    public ResponseEntity<List<Task>> findAll(@RequestBody Long userId) {
        return ResponseEntity.ok(taskService.findAll(userId));
    }

    @PostMapping("/add")
    public ResponseEntity<Task> add(@RequestBody Task task) {
        if (task.getId() != null && task.getId() != 0) {
            return new ResponseEntity("Redundant param: id must be null", HttpStatus.NOT_ACCEPTABLE);
        }

        if (task.getTitle() == null && task.getTitle().trim().isEmpty()) {
            return new ResponseEntity("Missed param: title", HttpStatus.NOT_ACCEPTABLE);
        }

        //вызываем микросервис из другого модуля
        // если пользователь существует, то создаем запись
        if(userRestBuilder.userExists(task.getUserId())) {
            return ResponseEntity.ok(taskService.add(task));
        }
        // пользователя не существует => ошибка
        return new ResponseEntity("user id = " + task.getUserId() + " not found", HttpStatus.NOT_ACCEPTABLE);
    }

    @PutMapping("/update")
    public ResponseEntity<Task> update(@RequestBody Task task) {
        if (task.getId() == null && task.getId() == 0) {
            return new ResponseEntity("Missed param: id", HttpStatus.NOT_ACCEPTABLE);
        }
        if (task.getTitle() == null && task.getTitle().trim().isEmpty()) {
            return new ResponseEntity("Missed param: title", HttpStatus.NOT_ACCEPTABLE);
        }
        taskService.update(task);

        return new ResponseEntity(HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity delete(@PathVariable Long id) {
        try{
            taskService.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            e.printStackTrace();
            return new ResponseEntity("id = " + id + " not found", HttpStatus.NOT_ACCEPTABLE);
        }
        return new ResponseEntity(HttpStatus.OK);
    }

    @PostMapping("/id")
    public ResponseEntity<Task> findById(@RequestBody Long id) {
        Task task = null;
        try{
            task = taskService.findById(id);
        } catch (EmptyResultDataAccessException e) {
            e.printStackTrace();
            return new ResponseEntity("id = " + id + " not found", HttpStatus.NOT_ACCEPTABLE);
        }
        return ResponseEntity.ok(task);
    }

    //поиск по любым параметрам TaskSearchValues
    // 1. получаем значения из TaskSearchValues: если значение пустое, то вернем null. иначе берем значение
    // 2.
    @PostMapping("/search")
    public ResponseEntity<Page<Task>> search(@RequestBody TaskSearchValues taskSearchValues) throws ParseException {
        //исключаем Null
        String title = taskSearchValues.getTitle() != null ? taskSearchValues.getTitle() : null;
        //конвертировать boolean в Integer
        Boolean completed = taskSearchValues.getCompleted() != null && taskSearchValues.getCompleted() == 1 ? true : false;
        Long priorityId = taskSearchValues.getPriorityId() != null ? taskSearchValues.getPriorityId() : null;
        Long categoryId = taskSearchValues.getCategoryId() != null ? taskSearchValues.getCategoryId() : null;
        String sortColumn = taskSearchValues.getSortColumn() != null ? taskSearchValues.getSortColumn() : null;
        String sortDirection = taskSearchValues.getSortDirection() != null ? taskSearchValues.getSortDirection() : null;
        Integer pageNumber = taskSearchValues.getPageNumber() != null ? taskSearchValues.getPageNumber() : null;
        Integer pageSize = taskSearchValues.getPageSize() != null ? taskSearchValues.getPageSize() : null;

        //для того, чтобы показать задачи только текущего пользователя
        Long userId = taskSearchValues.getUserId() != null ? taskSearchValues.getUserId() : null;

        //проверить на обязательные поля
        if (userId == null || userId == 0) {
            return new ResponseEntity("Missed param: userId", HttpStatus.NOT_ACCEPTABLE);
        }

        //чтобы захватить в выборке все задачи по датам, независимо от времени
        Date dateFrom = null;
        Date dateTo = null;

        //выставляем 0:00 для начальной даты (если она указана)
        if (taskSearchValues.getDateFrom() != null) {
            Calendar calendarFrom = Calendar.getInstance();
            calendarFrom.setTime(taskSearchValues.getDateFrom());
            calendarFrom.set(Calendar.HOUR_OF_DAY, 0);
            calendarFrom.set(Calendar.MINUTE, 0);
            calendarFrom.set(Calendar.SECOND, 0);
            calendarFrom.set(Calendar.MILLISECOND, 1);

            dateFrom = calendarFrom.getTime();
        }

        //выставляем 23:59 для конечной даты (если она указана)
        if (taskSearchValues.getDateTo() != null) {
            Calendar calendarTo = Calendar.getInstance();
            calendarTo.setTime(taskSearchValues.getDateTo());
            calendarTo.set(Calendar.HOUR_OF_DAY, 23);
            calendarTo.set(Calendar.MINUTE, 59);
            calendarTo.set(Calendar.SECOND, 59);
            calendarTo.set(Calendar.MILLISECOND, 999);

            dateTo = calendarTo.getTime();
        }

        //задаем направление сортировки
        Sort.Direction direction =
                sortDirection == null || sortDirection.trim().length() == 0
                || sortDirection.trim().equalsIgnoreCase("asc")
                        ? Sort.Direction.ASC : Sort.Direction.DESC;

        //объект сортировки, который содержит столбец и направление
        Sort sort = Sort.by(direction, sortColumn, ID_COLUMN);

        //объект постраничности
        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize, sort);

        //результат запроса с постраничным выводом
        Page<Task> result = taskService.findByParams(title, completed, priorityId, categoryId, userId, dateFrom, dateTo, pageRequest);

        //результат запроса
        return ResponseEntity.ok(result);
    }
}
