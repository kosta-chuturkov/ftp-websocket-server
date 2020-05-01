package ftp.core.kafka;

import ftp.core.BootLoader;
import ftp.core.api.MessagePublishingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class KafkaMessagePublisher implements MessagePublishingService {
    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaMessagePublisher.class);

    @Override
    public void publish(String topic, Object data) {
        LOGGER.info("Message received. Topic: " + topic + " data:" + data);
    }
}
