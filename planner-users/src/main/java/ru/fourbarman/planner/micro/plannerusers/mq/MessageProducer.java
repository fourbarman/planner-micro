package ru.fourbarman.planner.micro.plannerusers.mq;

import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

/*

 */
@Component
@EnableBinding(TodoBinding.class) //связывает классы для использования каналов
public class MessageProducer {

    private TodoBinding todoBinding; // содержит все описанные каналы


    public MessageProducer(TodoBinding todoBinding) {
        this.todoBinding = todoBinding;
    }

    // отправка сообщений при создании нового пользователя
    public void newUserAction(Long userId) {
        //контейнер для добавления данных и Header
        Message message = MessageBuilder.withPayload(userId).build();
        //выбираем канал и отправляем сообщение
        todoBinding.todoOutputChannel().send(message);
    }
}
