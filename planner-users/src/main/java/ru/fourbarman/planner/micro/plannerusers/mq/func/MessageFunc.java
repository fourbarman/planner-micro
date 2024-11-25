package ru.fourbarman.planner.micro.plannerusers.mq.func;

import lombok.Getter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import reactor.util.concurrent.Queues;

import java.util.function.Supplier;

/*
описываются все каналы
 */
@Configuration
@Getter
public class MessageFunc {

    // для считывания данных по требованию а не постоянно - создается поток откуда будут отправляться в канал
    // все настройки трубы (шины)
    private Sinks.Many<Message<Long>> innerBus = Sinks.many().multicast()
            .onBackpressureBuffer(Queues.SMALL_BUFFER_SIZE, false);

    // Supplier - отправитель
    // отправляет в канал id пользователя для кот нужно создать тестовые данные
    // название метода д-но совпадать с настройками definition и binding в properties
    @Bean
    public Supplier<Flux<Message<Long>>> newUserActionProduce() {
        //Flux исп-ся для отправки по требованию. Иначе будет пытаться отправлять каждую секунду
        return () -> innerBus.asFlux();
    }

}
