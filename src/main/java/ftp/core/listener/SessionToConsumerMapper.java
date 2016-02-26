package ftp.core.listener;

import com.google.common.collect.Maps;
import ftp.core.exception.JsonException;
import reactor.bus.EventBus;
import reactor.fn.Consumer;

import javax.annotation.Resource;
import java.util.Map;

import static reactor.bus.selector.Selectors.$;

/**
 * Created by Kosta_Chuturkov on 2/23/2016.
 */

//@Service("sessionToConsumerMapper")
public class SessionToConsumerMapper {

    private final Map<String, Consumer> sessionToConsumerMap = Maps.newConcurrentMap();

    @Resource
    private EventBus eventBus;


    public final void addConsumer(final String sessionId, final Consumer consumer, final String topic) {
        if (this.sessionToConsumerMap.get(sessionId) == null) {
            this.sessionToConsumerMap.put(sessionId, consumer);
            this.eventBus.on($(topic), consumer);
        } else {
            throw new JsonException("Unable to start Session.", "default");
        }

    }

}
