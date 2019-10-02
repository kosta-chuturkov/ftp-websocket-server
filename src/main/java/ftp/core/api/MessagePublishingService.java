package ftp.core.api;

public interface MessagePublishingService {

    void publish(String topic, Object data);
}
