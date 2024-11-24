package ru.fourbarman.planner.micro.plannertodo.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.fourbarman.planner.micro.plannertodo.service.TestDataService;


/*
Для заполнения БД сервисными данными
 */
@RestController
@RequestMapping("/data")
public class TestDataController {
    private final TestDataService testDataService;

    public TestDataController(TestDataService testDataService) {
        this.testDataService = testDataService;
    }

    @PostMapping("/init")
    public ResponseEntity<Boolean> init(@RequestBody Long userId) {
        testDataService.initTestData(userId);
        return ResponseEntity.ok(true);
    }
}
