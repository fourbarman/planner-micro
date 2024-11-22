package ru.fourbarman.planner.micro.plannerusers.webclient;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import ru.fourbarman.planner.micro.plannerentity.entity.User;

@Component
public class UserWebClientBuilder {
    private static final String BASE_URL_DATA = "http://localhost:8765/planner-todo/data/";

    // init тестовых данных
    public Flux<Boolean> initUserData(Long userId) {

        Flux<Boolean> userFlux = WebClient.create(BASE_URL_DATA)
                .post()
                .uri("init")
                .bodyValue(userId)
                .retrieve()
                .bodyToFlux(Boolean.class);

        return userFlux;
    }
}
