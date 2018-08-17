package ftp.core.websocket.factory.impl;

import com.google.common.collect.Maps;
import ftp.core.service.face.tx.FtpServerException;
import ftp.core.websocket.api.JsonTypedHandler;
import ftp.core.websocket.factory.JsonHandlerFactory;
import ftp.core.websocket.handler.Handlers;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Service;

/**
 * Created by Kosta_Chuturkov on 2/23/2016.
 */

@Service("jsonHandlerFactory")
public class JsonHandlerFactoryImpl implements JsonHandlerFactory, BeanPostProcessor {

  private final Map<String, JsonTypedHandler> typedHandlerMap = Maps.newHashMap();

  private final Logger logger = LoggerFactory.getLogger(JsonHandlerFactoryImpl.class);

  @Override
  public JsonTypedHandler getHandlerByType(final String type) {
    final JsonTypedHandler jsonTypedHandler = this.typedHandlerMap.get(type);
    if (jsonTypedHandler == null) {
      return this.typedHandlerMap.get(Handlers.DEFAULT_HANDLER);
    }
    return jsonTypedHandler;
  }

  @Override
  public Object postProcessBeforeInitialization(final Object bean, final String beanName)
      throws BeansException {
    return bean;
  }

  @Override
  public Object postProcessAfterInitialization(final Object bean, final String beanName)
      throws BeansException {
    if (!(bean instanceof JsonTypedHandler)) {
      return bean;
    }

    final JsonTypedHandler jsonTypedHandler = (JsonTypedHandler) bean;

    final String handlerType = jsonTypedHandler
        .getHandlerType().getHandlerName();

    if (this.typedHandlerMap.containsKey(handlerType)) {
      final Class<?> existClass = this.typedHandlerMap.get(handlerType).getClass();

      throw new FtpServerException(String.format(
          "Duplication of beans of type 'JsonTypedHandler' that implement logic for the same entity type, entityTypeName:%1$s, existingBean:%2$s, newBean:%3$s",
          handlerType,
          existClass,
          jsonTypedHandler.getClass()));
    }
    this.logger.debug("Registering handler with type: " + handlerType);
    this.typedHandlerMap.put(handlerType, jsonTypedHandler);
    return bean;
  }
}
