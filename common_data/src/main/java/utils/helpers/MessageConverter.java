package utils.helpers;

import com.google.gson.Gson;
import models.Message;

public class MessageConverter {

    private final Gson gson = new Gson();

    public String extractDataFromJson(String data) {
        return gson.fromJson(data, Message.class).getMessageInfo();
    }

    public <T extends Message> T extractMessageFromJson(String data, Class<T> clazz) {
        return gson.fromJson(data, clazz);
    }

    public String toJsonFromMessage(Object data) {
        return gson.toJson(data);
    }

}
