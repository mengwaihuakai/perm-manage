package com.solid.subscribe.web.perm.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ZOOMY on 2018/8/23.
 */
public class ResultHandler {
    private int code=0;
    private String message="";
    private String version="";
    private Map<String,Object> resultMap=new HashMap<>();

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

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Map<String, Object> getResultMap() {
        return resultMap;
    }

    public void setResultMap(Map<String, Object> resultMap) {
        this.resultMap = resultMap;
    }
}
