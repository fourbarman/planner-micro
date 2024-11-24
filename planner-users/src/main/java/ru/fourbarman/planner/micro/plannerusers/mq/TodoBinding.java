package ru.fourbarman.planner.micro.plannerusers.mq;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

/*
Для работы с mq
Создаем нужные каналы
 */

public interface TodoBinding {

    String OUTPUT_CHANNEL = "todoOutputChannel";//нужен, чтобы на него ссылаться

    //создает канал для отправки сообщений (@Output)
    @Output(OUTPUT_CHANNEL)
    MessageChannel todoOutputChannel();//название канала будет браться из анотации, а не названия метода (default)
}
