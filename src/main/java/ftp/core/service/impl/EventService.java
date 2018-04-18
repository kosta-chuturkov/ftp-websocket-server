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
public class EventService {

  @Resource
  private Gson gson;

  @Resource
  private EventBus eventBus;


  public void fireSharedFileEvent(final String topic, final DataTransferObject fileDto) {
    this.eventBus.notify(topic, Event
        .wrap(new JsonResponse(Handlers.FILES_SHARED_WITH_ME_HANDLER, this.gson.toJson(fileDto))));

  }

  public <T> DeferredResult<T> scheduleTaskToReactor(Supplier<T> supplier, Long timeOut) {
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
      this.eventBus.notify(userNickName,
          Event.wrap(new JsonResponse(Handlers.DELETED_FILE, this.gson.toJson(deletedFileDto))));
    }
  }

  public <T> void listen(final String topic, final Consumer<Event<T>> consumer) {
    this.eventBus.on($(topic), consumer);
  }

  public void unregisterConsumer(final String topic) {
    this.eventBus.getConsumerRegistry().unregister(topic);
  }

}
