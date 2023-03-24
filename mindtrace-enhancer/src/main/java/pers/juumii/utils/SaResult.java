package pers.juumii.utils;

import java.io.Serial;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

public class SaResult extends LinkedHashMap<String, Object> implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    public SaResult() {}

    public SaResult(int code, String msg, Object data) {
        this.setCode(code);
        this.setMsg(msg);
        this.setData(data);
    }

    public SaResult(Map<String, ?> map) {
        this.setMap(map);
    }

    public Integer getCode() {
        return (Integer)this.get("code");
    }

    public String getMsg() {
        return (String)this.get("msg");
    }

    public Object getData() {
        return this.get("data");
    }

    public void setCode(int code) {
        this.put("code", code);
    }

    public void setMsg(String msg) {
        this.put("msg", msg);
    }

    public void setData(Object data) {
        this.put("data", data);
    }

    public SaResult set(String key, Object data) {
        this.put(key, data);
        return this;
    }

    public void setMap(Map<String, ?> map) {
        for (String key : map.keySet())
            this.put(key, map.get(key));
    }

    public static SaResult ok() {
        return new SaResult(200, "ok", null);
    }

    public static SaResult ok(String msg) {
        return new SaResult(200, msg, null);
    }

    public static SaResult code(int code) {
        return new SaResult(code, (String)null, null);
    }

    public static SaResult data(Object data) {
        return new SaResult(200, "ok", data);
    }

    public static SaResult error() {
        return new SaResult(500, "error", null);
    }

    public static SaResult error(String msg) {
        return new SaResult(500, msg, null);
    }

    public static SaResult get(int code, String msg, Object data) {
        return new SaResult(code, msg, data);
    }

    public String toString() {
        return "{\"code\": " + this.getCode() + ", \"msg\": " + this.transValue(this.getMsg()) + ", \"data\": " + this.transValue(this.getData()) + "}";
    }

    private String transValue(Object value) {
        return value instanceof String ? "\"" + value + "\"" : String.valueOf(value);
    }
}
