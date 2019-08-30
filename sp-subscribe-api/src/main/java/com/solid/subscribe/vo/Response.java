package com.solid.subscribe.vo;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * @author fanyongju
 * @Title: Response
 * @date 2018/11/2617:39
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Response<T> {
    private Integer code;
    private String msg;
    private T data;


    public static Response getInstance(){
        return new Response();
    }

    public static Response error() {
        Response response = new Response();
        response.setCode(1);
        response.setMsg("failed");
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

    public Response success(T data) {
        this.code = 0;
        this.msg = "success";
        this.data = data;
        return this;
    }
}
