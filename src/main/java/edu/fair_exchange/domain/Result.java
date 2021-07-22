package edu.fair_exchange.domain;

public class Result {

    private Integer code;

    private Object data;

    public Result(Integer code, Object data) {
        this.code = code;
        this.data = data;
    }
    public Result() {
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
