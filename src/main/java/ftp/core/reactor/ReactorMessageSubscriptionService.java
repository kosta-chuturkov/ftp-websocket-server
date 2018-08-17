package ftp.core.reactor;

import static reactor.bus.selector.Selectors.$;

import com.google.gson.Gson;
import ftp.core.api.MessageConsumer;
import ftp.core.api.MessageSubscriptionService;
import java.lang.reflect.Field;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;
import reactor.bus.EventBus;
import reactor.bus.registry.Registration;

@Profile("dev")
@Service
public class ReactorMessageSubscriptionService implements MessageSubscriptionService {

  private EventBus eventBus;
  private Gson gson;
  private static final Logger log = LoggerFactory
      .getLogger(ReactorMessageSubscriptionService.class);

  @Autowired
  public ReactorMessageSubscriptionService(EventBus eventBus, Gson gson) {
    this.eventBus = eventBus;
    this.gson = gson;
  }

  public MessageConsumer subscribe(String topic, WebSocketSession session) {
    final ReactorMessageConsumer reactorMessageConsumer = new ReactorMessageConsumer(session
    );
    this.eventBus.on($(topic), reactorMessageConsumer);
    return reactorMessageConsumer;
  }

  public void unsubscribe(final String topic, MessageConsumer messageConsumer) {
    this.eventBus
        .getConsumerRegistry()
        .select(topic)
        .stream()
        .filter(registration -> {
          Object object = registration.getObject();
          try {
            Field field = object.getClass().getDeclaredField("val$consumer");
            field.setAccessible(true);
            Object consumer = field.get(object);
            return consumer.equals(messageConsumer);
          } catch (NoSuchFieldException | IllegalAccessException e) {
            log.error("Unable to unregister consumer", e);
          }
          return false;
        })
        .findFirst().ifPresent(Registration::cancel);
  }
}
