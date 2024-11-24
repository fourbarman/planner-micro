package ru.fourbarman.planner.micro.plannertodo.mq;

import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Component;
import ru.fourbarman.planner.micro.plannertodo.service.TestDataService;

@Component
@EnableBinding(TodoBinding.class) // входящий канал
public class MessageConsumer {

    private final TestDataService testDataService;

    public MessageConsumer(TestDataService testDataService) {
        this.testDataService = testDataService;
    }

    @StreamListener(target = TodoBinding.INPUT_CHANNEL) //считываем любое сообщение через этот канал
    public void initTestData(Long userId) throws Exception {
        throw new Exception("test dead letter queue");
        //testDataService.initTestData(userId);
    }
}
