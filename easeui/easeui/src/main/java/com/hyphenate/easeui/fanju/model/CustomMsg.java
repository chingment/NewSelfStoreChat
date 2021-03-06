package com.hyphenate.easeui.fanju.model;

public class CustomMsg<T> {
    public String type;
    public String message;

    public T content;
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public T getContent() {
        return content;
    }
    public void setContent(T content) {
        this.content = content;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}