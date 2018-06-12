package com.fancydsp.data.domain;

public class ResponseMessage {
    private String msg;
    public ResponseMessage(String msg){
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
