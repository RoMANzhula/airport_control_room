package utils.processors;

import models.Message;

public interface MessageProcessor<T extends Message> {

    void processMessageFromJson(String jsonMessage);

}
