package ftp.core.reactor;

import com.google.gson.Gson;
import ftp.core.api.MessagePublishingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import reactor.bus.Event;
import reactor.bus.EventBus;

@Profile({"dev", "test"})
@Service
public class ReactorMessageProducer implements MessagePublishingService {

  private final EventBus eventBus;
  private final Gson gson;

  @Autowired
  public ReactorMessageProducer(EventBus eventBus, Gson gson) {
    this.eventBus = eventBus;
    this.gson = gson;
  }

  public void publish(String topic, Object data) {
    this.eventBus.notify(topic, Event.wrap(gson.toJson(data)));
  }
}
