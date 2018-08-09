package ftp.core.session;

import com.google.common.collect.Maps;
import ftp.core.api.UserSessionCounter;
import ftp.core.service.impl.SchedulingService;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.Resource;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

/**
 * Created by Kosta_Chuturkov on 2/23/2016.
 */
@Profile("dev")
@Service("localSessionCounter")
public class LocalSessionCounter implements UserSessionCounter {

  private final Map<String, AtomicInteger> sessionToConsumerMap = Maps.newConcurrentMap();

  @Resource
  private SchedulingService schedulingService;


  @Override
  public void addUserSession(String userName) {
    final AtomicInteger userSessionsCount = this.sessionToConsumerMap.get(userName);
    if (userSessionsCount == null) {
      this.sessionToConsumerMap.put(userName, new AtomicInteger(1));
    } else {
      synchronized (userSessionsCount) {
        if ((this.sessionToConsumerMap.get(userName)) != null) {
          userSessionsCount.incrementAndGet();
        }
      }
    }
  }

  @Override
  public void removeUserSession(String userName) {
    final AtomicInteger userSessionsCount = this.sessionToConsumerMap.get(userName);
    if (userSessionsCount != null) {
      synchronized (userSessionsCount) {
        if (userSessionsCount.decrementAndGet() == 0) {
          this.schedulingService.unsubscribe(userName);
          this.sessionToConsumerMap.remove(userName);
        }
      }
    }
  }
}
