package ftp.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.Environment;
import reactor.bus.EventBus;
import reactor.core.dispatch.WorkQueueDispatcher;
import reactor.fn.Consumer;

/**
 * Created by Kosta_Chuturkov on 2/26/2016.
 */
@Configuration
public class ReactorEventsConfig {

  @Bean(name = "eventBus")
  EventBus createEventBus() {
    return EventBus.create(Environment.initializeIfEmpty().assignErrorJournal(),
        new WorkQueueDispatcher
            ("appWorkQueueDispatcher", Runtime.getRuntime().availableProcessors(), 8192, new ExceptionHandler()));
  }

  private class ExceptionHandler implements Consumer<Throwable> {

    @Override
    public void accept(final Throwable throwable) {

      throw new RuntimeException(throwable.getMessage());
    }
  }
}
