package com.solid.subscribe.web.vo;


public class Response<T> {
    private Integer code;
    private String msg;
    private T result;

    public static Response getInstance(){
        return new Response();
    }

    public static Response error() {
        Response response = new Response();
        response.setCode(1);
        response.setMsg("failed");
        return response;
    }

    public static Response keyExistError() {
        Response response = new Response();
        response.setCode(2);
        response.setMsg("Key already exists");
        return response;
    }

    public static Response error(String msg) {
        Response response = new Response();
        response.setCode(1);
        response.setMsg(msg);
        return response;
    }

    public static Response success() {
        Response response = new Response();
        response.setCode(0);
        response.setMsg("success");
        return response;
    }

    public Response success(T result) {
        this.code = 0;
        this.msg = "success";
        this.result = result;
        return this;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }
}
