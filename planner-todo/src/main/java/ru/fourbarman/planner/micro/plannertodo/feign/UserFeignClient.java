package ru.fourbarman.planner.micro.plannertodo.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.fourbarman.planner.micro.plannerentity.entity.User;

/*
fallback - если name не будет доступен, то вызовется соотв. метод указанного класса
 */

@FeignClient(name = "planner-users", fallback = UserFeignClientFallback.class)
public interface UserFeignClient {

    // методы, которые будем вызывать с помощью feign

    @PostMapping("/user/id")
    ResponseEntity<User> findUserById(@RequestBody Long userId);
}

@Component
class UserFeignClientFallback implements UserFeignClient {

    @Override
    public ResponseEntity<User> findUserById(Long userId) {
        return null;
    }
}
