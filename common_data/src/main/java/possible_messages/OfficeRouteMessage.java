package possible_messages;

import lombok.Getter;
import lombok.Setter;
import models.Message;
import models.Route;
import models.enums.MessageType;
import models.enums.Source;

@Getter
@Setter
public class OfficeRouteMessage extends Message {

    private Route route;

    public OfficeRouteMessage() {
        this.source = Source.OFFICE;
        this.messageType = MessageType.ROUTE;
    }

    public OfficeRouteMessage(Route route) {
        this();
        this.route = route;
    }

}
