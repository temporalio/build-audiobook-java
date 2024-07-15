package ttspackage;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(as = InputPayload.class)
public class InputPayload {
    public String path;

    public InputPayload() { } // support Jackson deserialization

    public InputPayload(String path) {
        this.path = path;
    }
}
