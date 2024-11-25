package ru.fourbarman.planner.micro.plannerusers.mq.func;

import lombok.Getter;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Sinks;

/*
Работа с каналами для отправки сообщения с пом. функционального кода
 */
@Service
@Getter
public class MessageFuncActions {
    //каналы для обмена сообщениями
    private final MessageFunc messageFunc;

    public MessageFuncActions(MessageFunc messageFunc) {
        this.messageFunc = messageFunc;
    }

    //отправка сообщения
    public void sendNewUserMessage(Long id) {
        //добавление в слушатель нового сообщения
        messageFunc.getInnerBus().emitNext(MessageBuilder.withPayload(id).build(), Sinks.EmitFailureHandler.FAIL_FAST);
        System.out.println("Request to emit: " + id);
    }
}
