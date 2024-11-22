package ru.fourbarman.planner.micro.plannertodo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.fourbarman.planner.micro.plannerentity.entity.Stat;
import ru.fourbarman.planner.micro.plannertodo.service.StatService;

@RestController
public class StatController {

    private final StatService statService;

    public StatController(StatService statService) {
        this.statService = statService;
    }

    @PostMapping("/stat")
    public ResponseEntity<Stat> findByEmail(@RequestBody Long userId) {
        return ResponseEntity.ok(statService.findStat(userId));
    }
}
