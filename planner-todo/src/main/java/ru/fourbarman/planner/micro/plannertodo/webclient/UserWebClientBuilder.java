package ru.fourbarman.planner.micro.plannertodo.webclient;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import ru.fourbarman.planner.micro.plannerentity.entity.User;

@Component
public class UserWebClientBuilder {
    private static final String BASE_URL = "http://localhost:8765/planner-users/user/";

    public boolean userExists(Long userId) {

        try {
            User user = WebClient.create(BASE_URL)
                    .post()
                    .uri("id")
                    .bodyValue(userId)
                    .retrieve()
                    .bodyToFlux(User.class)
                    .blockFirst(); //блокирует поток до получения 1 записи => выполняется синхронно

            if(user != null) {
                return true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // не блокирует поток
    public Flux<User> userExistsAsync(Long userId) {

        Flux<User> userFlux = WebClient.create(BASE_URL)
                .post()
                .uri("id")
                .bodyValue(userId)
                .retrieve()
                .bodyToFlux(User.class);
        return userFlux;
    }
}
