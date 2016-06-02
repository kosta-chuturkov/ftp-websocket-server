package ftp.core.service.impl;

import com.google.gson.Gson;
import ftp.core.common.model.dto.FileDto;
import ftp.core.websocket.dto.JsonResponse;
import ftp.core.websocket.handler.Handlers;
import org.springframework.stereotype.Service;
import reactor.bus.Event;
import reactor.bus.EventBus;
import reactor.fn.Consumer;

import javax.annotation.Resource;

import static reactor.bus.selector.Selectors.$;

/**
 * Created by kosta on 2.6.2016 г..
 */
@Service
public class EventService {

    @Resource
    private Gson gson;

    @Resource
    private EventBus eventBus;


    public void fireSharedFileEvent(final String topic, final FileDto fileDto, final Handlers handlers) {
        this.eventBus.notify(topic, Event.wrap(new JsonResponse(handlers, this.gson.toJson(fileDto))));

    }

    public <T> void listen(final String topic, final Consumer<Event<T>> consumer) {
        this.eventBus.on($(topic), consumer);
    }

    public void unregisterConsumer(final String topic) {
        this.eventBus.getConsumerRegistry().unregister(topic);
    }

}
