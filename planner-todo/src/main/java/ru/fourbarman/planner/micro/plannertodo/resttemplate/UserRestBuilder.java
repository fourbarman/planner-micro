package ru.fourbarman.planner.micro.plannertodo.resttemplate;

import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.fourbarman.planner.micro.plannerentity.entity.User;

@Component
public class UserRestBuilder {
    private static final String BASE_URL = "http://localhost:8765/planner-users/user";

    public boolean userExists(Long userId) {
        // пример использования RestTemplate (он уже deprecated)

        // создаем заголовки, чтобы указать что это будет JSON
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        //создаем объект RestTemplate
        RestTemplate restTemplate = new RestTemplate();
        // помещаем в HttpEntity параметр userId
        HttpEntity<Long> request = new HttpEntity<>(userId, headers);
        //создаем объект ответа (если нужно, можно из него получить объект User, но нам нужен только ответ сервера)
        ResponseEntity<User> response = null;

        try{
            response = restTemplate.exchange(BASE_URL + "/id", HttpMethod.POST, request, User.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
