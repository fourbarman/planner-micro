package ru.fourbarman.planner.micro.plannerusers.controller;


import jakarta.ws.rs.core.Response;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.fourbarman.planner.micro.plannerentity.entity.User;
import ru.fourbarman.planner.micro.plannerusers.dto.UserDTO;
import ru.fourbarman.planner.micro.plannerusers.keycloak.KeycloakUtil;
import ru.fourbarman.planner.micro.plannerusers.search.UserSearchValues;
import ru.fourbarman.planner.micro.plannerusers.service.UserService;

import java.util.NoSuchElementException;
import java.util.Optional;

@RequestMapping("/admin/user")
@RestController
public class AdminController {
    private static final String ID_COLUMN = "id";
    private final UserService userService;
    private final KeycloakUtil keycloakUtil;

    public AdminController(UserService userService, KeycloakUtil keycloakUtil) {
        this.userService = userService;
        this.keycloakUtil = keycloakUtil;
    }

    @PostMapping("/add")
    public ResponseEntity add(@RequestBody UserDTO userDTO) {
        if (userDTO.getId() != null && userDTO.getId() != 0) {
            return new ResponseEntity("Redundunt param: id", HttpStatus.NOT_ACCEPTABLE);
        }

        if (userDTO.getEmail() == null || userDTO.getEmail().isEmpty()) {
            return new ResponseEntity("Missing param: email", HttpStatus.NOT_ACCEPTABLE);
        }

        if(userDTO.getPassword() == null || userDTO.getPassword().trim().isEmpty()) {
            return new ResponseEntity("Missing param: password", HttpStatus.NOT_ACCEPTABLE);
        }

        if (userDTO.getUsername() == null || userDTO.getUsername().trim().isEmpty()) {
            return new ResponseEntity("Missing param: username", HttpStatus.NOT_ACCEPTABLE);
        }

//        //добавить пользователя в БД
//        user = userService.add(user);

//        // проверить что User существует и отправить сообщение с id через mq: 1 вариант legacy
//        if (user != null) {
//            messageProducer.newUserAction(user.getId());
//        }

        //отправляем сообщение с помощью функц. кода
        //messageFuncActions.sendNewUserMessage(user.getId());

        Response response = keycloakUtil.createKeycloakUser(userDTO);

        //если пользователь существует, отправить ошибку
        if (response.getStatus() == HttpStatus.CONFLICT.value()) {
            return new ResponseEntity("User or email already exists", HttpStatus.CONFLICT);
        }

        //CreatedResponseUtil - готовый класс для получения id из response!!
        String userId = CreatedResponseUtil.getCreatedId(response);
        System.out.println("User created with id: " + userId);

        String responseBody = response.readEntity(String.class);
        return ResponseEntity.status(response.getStatus()).body(responseBody);

        //return ResponseEntity.status(response.getStatus()).body(response);
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
