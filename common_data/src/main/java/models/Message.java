package models;

import lombok.NoArgsConstructor;
import models.enums.MessageType;
import models.enums.Source;

@NoArgsConstructor
public class Message {

    protected Source source;
    protected MessageType messageType;

    public String getMessageInfo() {
        return source.name() + " : " + messageType.name();
    }

}
