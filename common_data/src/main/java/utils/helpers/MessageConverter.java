package utils.helpers;

import com.google.gson.Gson;
import models.Message;

import java.util.Objects;

public class MessageConverter {

    private final Gson gson = new Gson();

    public String extractDataFromGson(String data) {
        return gson.fromJson(data, Message.class).getMessageInfo();
    }

    public <T extends Message> T extractMessageFromJson(String data, Class<T> clazz) {
        return gson.fromJson(data, clazz);
    }

    public String toJsonFromMessage(Objects data) {
        return gson.toJson(data);
    }

}
