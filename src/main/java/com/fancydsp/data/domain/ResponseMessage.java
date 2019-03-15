package com.fancydsp.data.domain;

public class ResponseMessage {
    private String msg;
    transient int code ;
    public ResponseMessage(String msg){
        this.msg = msg;
    }

    public ResponseMessage(String msg,int code){
        this.msg= msg;
        this.code = code;
    }


    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
