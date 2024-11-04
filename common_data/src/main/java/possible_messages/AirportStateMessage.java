package possible_messages;

import lombok.Getter;
import lombok.Setter;
import models.Airport;
import models.Message;
import models.enums.MessageType;
import models.enums.Source;

@Getter
@Setter
public class AirportStateMessage extends Message {

    private Airport airport;

    public AirportStateMessage() {
        this.source = Source.AIRPORT;
        this.messageType = MessageType.STATE;
    }

    public AirportStateMessage(Airport airport) {
        this();
        this.airport = airport;
    }
}
