package com.solid.subscribe.web.vo;

/**
 * @author fanyongju
 * @Title: CountryVO
 * @date 2018/12/6 14:56
 */
public class CountryVO {
    private String code;
    private String name;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "CountryVO{" +
                "code='" + code + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
