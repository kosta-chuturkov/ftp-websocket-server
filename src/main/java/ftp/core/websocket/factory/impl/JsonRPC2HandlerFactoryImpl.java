package ftp.core.websocket.factory.impl;

import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Service;

import com.google.common.collect.Maps;

import ftp.core.service.face.tx.FtpServerException;
import ftp.core.websocket.api.JsonRPC2TypedHandler;
import ftp.core.websocket.factory.JsonRPC2HandlerFactory;
import ftp.core.websocket.handler.HandlerNames;

/**
 * Created by Kosta_Chuturkov on 2/23/2016.
 */

@Service("jsonRPC2HandlerFactory")
public class JsonRPC2HandlerFactoryImpl implements JsonRPC2HandlerFactory, BeanPostProcessor {

    private final Map<String,JsonRPC2TypedHandler> typedHandlerMap = Maps.newHashMap();

    private final Logger logger = Logger.getLogger(JsonRPC2HandlerFactoryImpl.class);

    @Override
    public JsonRPC2TypedHandler getHandlerByType(final String type) {
        final JsonRPC2TypedHandler jsonRPC2TypedHandler = this.typedHandlerMap.get(type);
        if(jsonRPC2TypedHandler == null){
            return this.typedHandlerMap.get(HandlerNames.DEFAULT_HANDLER_NAME);
        }
        return jsonRPC2TypedHandler;
    }

    @Override
    public Object postProcessBeforeInitialization(final Object bean, final String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(final Object bean, final String beanName) throws BeansException {
        for (final Class<?> declaredInterface : bean.getClass().getInterfaces()) {
            if (!declaredInterface.equals(JsonRPC2TypedHandler.class)) {
                return bean;
            }

            final JsonRPC2TypedHandler jsonRPC2TypedHandler = (JsonRPC2TypedHandler) bean;

            final String handlerType = jsonRPC2TypedHandler
                    .getHandlerType();

            if (this.typedHandlerMap.containsKey(handlerType)) {
                final Class<?> existClass = this.typedHandlerMap.get(handlerType).getClass();

                throw new FtpServerException(String.format(	"Duplication of beans of type 'JsonRPC2TypedHandler' that implement logic for the same entity type, entityTypeName:%1$s, existingBean:%2$s, newBean:%3$s",
                        handlerType,
                        existClass,
                        jsonRPC2TypedHandler.getClass()));
            }
            this.logger.debug("Registering handler with type: " + handlerType);
            this.typedHandlerMap.put(handlerType, jsonRPC2TypedHandler);
        }
        return bean;
    }
}
