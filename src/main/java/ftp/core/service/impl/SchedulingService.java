package ftp.core.service.impl;

import static reactor.bus.selector.Selectors.$;

import com.google.gson.Gson;
import ftp.core.model.dto.DataTransferObject;
import ftp.core.model.dto.DeletedFileDto;
import ftp.core.websocket.dto.JsonResponse;
import ftp.core.websocket.handler.Handlers;
import java.util.Collection;
import java.util.function.Supplier;
import javax.annotation.Resource;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.async.DeferredResult;
import reactor.bus.Event;
import reactor.bus.EventBus;
import reactor.fn.Consumer;

/**
 * Created by kosta on 2.6.2016 г..
 */
@Service
public class SchedulingService {

  @Resource
  private Gson gson;

  @Resource
  private EventBus eventBus;


  public void fireSharedFileEvent(final String topic, final DataTransferObject fileDto) {
    publish(topic, Event
        .wrap(new JsonResponse(this.gson.toJson(fileDto), Handlers.FILES_SHARED_WITH_ME_HANDLER.getHandlerName())));

  }

  private void publish(String topic, Event<JsonResponse> data) {
    this.eventBus.notify(topic, data);
  }

  public <T> DeferredResult<T> scheduleTask(Supplier<T> supplier, Long timeOut) {
    DeferredResult<T> deferredResult = new DeferredResult<>(timeOut);
    SecurityContext securityContext = SecurityContextHolder.getContext();
    this.eventBus.schedule((data) -> {
      SecurityContextHolder.setContext(securityContext);
      data.setResult(supplier.get());
    }, deferredResult);
    return deferredResult;
  }

  public void fireSharedFileEvent(Collection<String> topics, final DataTransferObject fileDto) {
    topics.forEach(s -> fireSharedFileEvent(s, fileDto));
  }

  public void fireRemovedFileEvent(final Collection<String> usersToBeNotified,
      final DeletedFileDto deletedFileDto) {
    for (final String userNickName : usersToBeNotified) {
      publish(userNickName, Event
          .wrap(new JsonResponse(this.gson.toJson(deletedFileDto), Handlers.DELETED_FILE.getHandlerName())));
    }
  }

  public <T> void subscribe(final String topic, final Consumer<Event<T>> consumer) {
    this.eventBus.on($(topic), consumer);
  }

  public void unsubscribe(final String topic) {
    this.eventBus.getConsumerRegistry().unregister(topic);
  }

}
