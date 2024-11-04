package possible_messages;

import lombok.Getter;
import lombok.Setter;
import models.Message;
import models.enums.MessageType;
import models.enums.Source;

@Getter
@Setter
public class OfficeStateMessage extends Message {

    public OfficeStateMessage() {
        this.source = Source.OFFICE;
        this.messageType = MessageType.STATE;
    }

}
