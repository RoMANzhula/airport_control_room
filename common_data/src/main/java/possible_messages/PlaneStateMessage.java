package possible_messages;

import lombok.Getter;
import lombok.Setter;
import models.Message;
import models.Plane;
import models.enums.MessageType;
import models.enums.Source;

@Getter
@Setter
public class PlaneStateMessage extends Message {

    private Plane plane;

    public PlaneStateMessage() {
        this.source = Source.PLANE;
        this.messageType = MessageType.STATE;
    }

    public PlaneStateMessage(Plane plane) {
        this();
        this.plane = plane;
    }

}
