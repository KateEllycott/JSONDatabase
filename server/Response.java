package server;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

public class Response {

    private String response;
    private String reason;
    private JsonElement value;

    public void setResponse(String response) {
        this.response = response;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public void setValue(JsonElement value) {
        this.value = value;
    }

}
