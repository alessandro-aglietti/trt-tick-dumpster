package it.growbit.telegram.model;

import com.google.api.client.util.Key;

/**
 * Created by name on 26/06/17.
 */
public class SendMessage {

    @Key
    private String chat_id;

    @Key
    private String text;

    public SendMessage(String chat_id, String text) {
        this.chat_id = chat_id;
        this.text = text;
    }

    public String getChat_id() {
        return chat_id;
    }

    public void setChat_id(String chat_id) {
        this.chat_id = chat_id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
