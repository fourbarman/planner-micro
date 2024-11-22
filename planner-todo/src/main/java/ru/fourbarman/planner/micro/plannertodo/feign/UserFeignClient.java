package ru.fourbarman.planner.micro.plannertodo.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.fourbarman.planner.micro.plannerentity.entity.User;

@FeignClient(name = "planner-users")
public interface UserFeignClient {

    // методы, которые будем вызывать с помощью feign

    @PostMapping("/user/id")
    ResponseEntity<User> findUserById(@RequestBody Long userId);
}
