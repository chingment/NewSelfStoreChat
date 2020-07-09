package com.hyphenate.chatuidemo.fanju.model;

/**
 * Created by chingment on 2017/12/18.
 */


public class ApiResultBean<T> {
    private int result;
    private int code;
    private String message;
    private T data;

    public int getResult() {
        return result;
    }
    public void setResult(int result) {
        this.result = result;
    }

    public int getCode() {
        return code;
    }
    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

}
