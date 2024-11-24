package ru.fourbarman.planner.micro.plannertodo.mq;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.MessageChannel;

/*
Оаисываем каналы для работы с message broker
 */
public interface TodoBinding {
    //имя канала
    String INPUT_CHANNEL = "todo_output_channel";

    // канал для входящих сообщений (@Input)
    @Input(INPUT_CHANNEL)
    MessageChannel todoInputChannel();
}
