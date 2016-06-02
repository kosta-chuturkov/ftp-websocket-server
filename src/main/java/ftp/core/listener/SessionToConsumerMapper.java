package ftp.core.listener;

import com.google.common.collect.Maps;
import ftp.core.service.impl.EventService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Kosta_Chuturkov on 2/23/2016.
 */

@Service("sessionToConsumerMapper")
public class SessionToConsumerMapper {

    private final Map<String, AtomicInteger> sessionToConsumerMap = Maps.newConcurrentMap();

    @Resource
    private EventService eventService;


    public final void addConsumer(final String topic) {
        final AtomicInteger userSessionsCount = this.sessionToConsumerMap.get(topic);
        if (userSessionsCount == null) {
            this.sessionToConsumerMap.put(topic, new AtomicInteger(1));
        } else {
            synchronized (userSessionsCount) {
                if ((this.sessionToConsumerMap.get(topic)) != null)
                    userSessionsCount.incrementAndGet();
            }
        }

    }

    public final void removeConsumer(final String topic) {
        final AtomicInteger atomicInteger = this.sessionToConsumerMap.get(topic);
        if (atomicInteger != null) {
            synchronized (atomicInteger) {
                if (atomicInteger.decrementAndGet() == 0) {
                    this.eventService.unregisterConsumer(topic);
                    this.sessionToConsumerMap.remove(topic);
                }
            }
        }
    }

}
