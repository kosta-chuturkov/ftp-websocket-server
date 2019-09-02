package ftp.core.service.impl;

import java.util.function.Supplier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.async.DeferredResult;
import reactor.bus.EventBus;

/**
 * Created by kosta on 2.6.2016 г..
 */
@Service
public class SchedulingService {

  private EventBus eventBus;

  @Autowired
  public SchedulingService(EventBus eventBus) {
    this.eventBus = eventBus;
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

}
