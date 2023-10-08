package chatdemo.vo;

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import com.alibaba.fastjson.JSONObject;


/**
 * 响应对象，继承一个哈希表，用于动态存放响应数据的键值对
 */
public class ResponseJson extends HashMap<String, Object> {
    private static final long serialVersionUID = 1L;
    private static final Integer SUCCESS_STATUS = 200;
    private static final Integer ERROR_STATUS = -1;
    private static final String SUCCESS_MSG = "正常";

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    
    public ResponseJson() {
        super();
    }

    public ResponseJson(int code) {
        super();
        setStatus(code);
    }

    public ResponseJson(HttpStatus status) {
        super();
        setStatus(status.value());
        setMsg(status.getReasonPhrase());
    }
    
    public ResponseJson success() {
        put("msg", SUCCESS_MSG);
        put("status", SUCCESS_STATUS);
        return this;
    }
    
    public ResponseJson success(String msg) {
        put("msg", msg);
        put("status", SUCCESS_STATUS);
        return this;
    }
    
    public ResponseJson error(String msg) {
        put("msg", msg);
        put("status", ERROR_STATUS);
        return this;
    }

    public ResponseJson setData(String key, Object obj) {
        @SuppressWarnings("unchecked")
        HashMap<String, Object> data = (HashMap<String, Object>) get("data");
        if (data == null) {
            data = new HashMap<String, Object>();
            put("data", data);
        }
        data.put(key, obj);
        return this;
    }
    
    public void setStatus(int status) {
        put("status", status);
    }

    public ResponseJson setMsg(String msg) {
        put("msg", msg);
        return this;
    }

    public ResponseJson setValue(String key, Object val) {
        put(key, val);
        return this;
    }
    
    /**
     * 返回JSON字符串
     */
    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
