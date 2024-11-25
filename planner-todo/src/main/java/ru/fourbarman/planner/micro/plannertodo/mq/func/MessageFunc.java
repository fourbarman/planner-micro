package ru.fourbarman.planner.micro.plannertodo.mq.func;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import ru.fourbarman.planner.micro.plannertodo.service.TestDataService;

import java.util.function.Consumer;

/*
Получатель сообщения
 */
@Configuration
public class MessageFunc {
    private final TestDataService testDataService;

    public MessageFunc(TestDataService testDataService) {
        this.testDataService = testDataService;
    }

    // получает id пользователя и запускает создание тестовых данных
    // название метода д-но совпадать с настройками definition и binding в properties
    @Bean
    public Consumer<Message<Long>> newUserActionConsume() {
        return message -> {
            testDataService.initTestData(message.getPayload());
        };
    }
}
