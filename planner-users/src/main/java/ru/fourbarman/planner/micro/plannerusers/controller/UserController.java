package ru.fourbarman.planner.micro.plannerusers.controller;


import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.fourbarman.planner.micro.plannerentity.entity.User;
import ru.fourbarman.planner.micro.plannerusers.search.UserSearchValues;
import ru.fourbarman.planner.micro.plannerusers.service.UserService;
import ru.fourbarman.planner.micro.plannerusers.webclient.UserWebClientBuilder;

import java.util.NoSuchElementException;
import java.util.Optional;

@RequestMapping("/user")
@RestController
public class UserController {
    private static final String ID_COLUMN = "id";
    private final UserService userService;
    private final UserWebClientBuilder userWebClientBuilder;

    public UserController(UserService userService, UserWebClientBuilder userWebClientBuilder) {
        this.userService = userService;
        this.userWebClientBuilder = userWebClientBuilder;
    }

    @PostMapping("/add")
    public ResponseEntity<User> add(@RequestBody User user) {
        if (user.getId() != null && user.getId() != 0) {
            return new ResponseEntity("Redundunt param: id", HttpStatus.NOT_ACCEPTABLE);
        }

        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            return new ResponseEntity("Missing param: email", HttpStatus.NOT_ACCEPTABLE);
        }

        if(user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            return new ResponseEntity("Missing param: password", HttpStatus.NOT_ACCEPTABLE);
        }

        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            return new ResponseEntity("Missing param: username", HttpStatus.NOT_ACCEPTABLE);
        }

        user = userService.add(user);

        // асинхронно вызываем сервис для заполнения тестовыми данными
        if (user != null) {
            userWebClientBuilder.initUserData(user.getId()).subscribe(result -> System.out.println("user populated: " + result));
        }

        return ResponseEntity.ok(user);
    }

    @PutMapping("/update")
    public ResponseEntity update(@RequestBody User user) {
        if (user.getId() == null && user.getId() == 0) {
            return new ResponseEntity("Missing param: id", HttpStatus.NOT_ACCEPTABLE);
        }
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            return new ResponseEntity("Missing param: email", HttpStatus.NOT_ACCEPTABLE);
        }
        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            return new ResponseEntity("Missing param: password", HttpStatus.NOT_ACCEPTABLE);
        }
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            return new ResponseEntity("Missing param: username", HttpStatus.NOT_ACCEPTABLE);
        }
        userService.update(user);
        return new ResponseEntity(HttpStatus.OK);
    }

    @DeleteMapping("/deleteById")
    public ResponseEntity deleteById(@RequestBody Long userId) {
        try {
            userService.deleteByUserId(userId);
        } catch (EmptyResultDataAccessException e) {
            e.printStackTrace();
            return new ResponseEntity("userId = " + userId + " not found", HttpStatus.NOT_ACCEPTABLE);
        }
        return new ResponseEntity(HttpStatus.OK);
    }

    @DeleteMapping("/deleteByEmail")
    public ResponseEntity deleteByEmail(@RequestBody String email) {
        try{
            userService.deleteByUserEmail(email);
        } catch (EmptyResultDataAccessException e) {
            e.printStackTrace();
            return new ResponseEntity("userEmail = " + email + " not found", HttpStatus.NOT_ACCEPTABLE);
        }
        return new ResponseEntity(HttpStatus.OK);
    }

    @PostMapping(value = "/id", consumes = "application/json", produces = "application/json")
    public ResponseEntity<User> findById(@RequestBody Long userId) {
        Optional<User> userOptional = userService.findById(userId);
        try{
            if (userOptional.isPresent()) {
                return ResponseEntity.ok(userOptional.get());
            }
        } catch (NoSuchElementException e) {
            e.printStackTrace();
        }
        return new ResponseEntity("User with id = " + userId + " not found", HttpStatus.NOT_ACCEPTABLE);
    }

    @PostMapping("/email")
    public ResponseEntity<User> findByEmail(@RequestBody String email) {
        User user = null;
        try{
            user = userService.findByEmail(email);
        } catch (NoSuchElementException e) {
            e.printStackTrace();
            return new ResponseEntity("User with email = " + email + " not found", HttpStatus.NOT_ACCEPTABLE);
        }
        return ResponseEntity.ok(user);
    }

    @PostMapping("/search")
    public ResponseEntity<Page<User>> search(@RequestBody UserSearchValues userSearchValues) {
        String email = userSearchValues.getEmail() != null ? userSearchValues.getEmail() : null;
        String username = userSearchValues.getUsername() != null ? userSearchValues.getUsername() : null;
        if(email == null || username.trim().isEmpty()) {
            return new ResponseEntity("Missing param: email", HttpStatus.NOT_ACCEPTABLE);
        }
        if(username == null || username.trim().isEmpty()) {
            return new ResponseEntity("Missing param: username", HttpStatus.NOT_ACCEPTABLE);
        }
        String sortColumn = userSearchValues.getSortColumn() != null ? userSearchValues.getSortColumn() : null;
        String sortDirection = userSearchValues.getSortDirection() != null ? userSearchValues.getSortDirection() : null;
        Integer pageNumber = userSearchValues.getPageNumber() != null ? userSearchValues.getPageNumber() : null;
        Integer pageSize = userSearchValues.getPageSize() != null ? userSearchValues.getPageSize() : null;

        Sort.Direction direction = sortDirection == null
                || sortDirection.trim().length() == 0
                || sortDirection.trim().equals("asc")
                ? Sort.Direction.ASC : Sort.Direction.DESC;
        //объект сортировки который содержит столбец и направление
        Sort sort = Sort.by(direction, sortColumn, ID_COLUMN);
        // объект постраничности
        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize, sort);
        // результат запроса с постраничным выводом
        Page<User> result = userService.findByParams(email, username, pageRequest);

        return ResponseEntity.ok(result);
    }
}
