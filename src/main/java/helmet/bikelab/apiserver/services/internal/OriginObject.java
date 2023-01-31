package helmet.bikelab.apiserver.services.internal;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import helmet.bikelab.apiserver.objects.exceptions.BusinessException;
import helmet.bikelab.apiserver.objects.exceptions.BusinessExceptionWithMessage;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Map;

public class OriginObject {

    @JsonIgnore
    private final static Gson gson = new Gson();

    @JsonIgnore
    protected String getJson(Object o){
        try{
            return gson.toJson(o);
        }catch (Exception e){
            return "";
        }
    }

    @JsonIgnore
    protected Gson getGsonInstance(){
        return gson;
    }

    @JsonIgnore
    protected void withException(String langCode){
        throw new BusinessExceptionWithMessage(langCode);
    }

    @JsonIgnore
    protected static void staticWithException(String langCode){
        throw new BusinessExceptionWithMessage(langCode);
    }

    @JsonIgnore
    protected static void staticWithException(String langCode, Exception e){
        throw new BusinessExceptionWithMessage(e, langCode);
    }

    @JsonIgnore
    protected <T> boolean bePresent(T obj){
        if(obj instanceof String) return obj != null && !"".equals(obj);
        if(obj instanceof Long) return obj != null;
        if(obj instanceof Integer) return obj != null;
        if(obj instanceof Map) {
            if(obj == null) return false;
            if(((Map)obj).isEmpty()) return false;
        }
        if(obj instanceof List){
            if(obj == null) return false;
            if(((List)obj).isEmpty()) return false;
            if(((List)obj).size() <= 0) return false;
        }
        return obj != null;
    }

    protected void writeMessage(String message){
        writeMessage(message, HttpStatus.BAD_REQUEST);
    }

    protected void writeMessage(String message, HttpStatus httpStatus){
        BusinessException businessException = new BusinessException();
        businessException.setErr_code("");
        businessException.setMsg(message);
        businessException.setErrHttpStatus(httpStatus);
        throw businessException;

    }

    protected <T, K> T map(K o, Class<T> cls){
        if(o == null) {
            try {
                return cls.getConstructor().newInstance();
            } catch (Exception e) {
            }
        }
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
            return objectMapper.convertValue(o, cls);
        }catch (Exception e){
            return null;
        }
    }

    protected <T, K> T map(K o, TypeReference<T> typeReference){
        ObjectMapper mapper = new ObjectMapper();
        if(o == null){
            String genericSuperclass = typeReference.getType().getTypeName();
            genericSuperclass = genericSuperclass.replaceAll("<.*>", "");
            try {
                Class.forName(genericSuperclass).getConstructor().newInstance();
            } catch (Exception e) {

            }
            return null;
        }
        return mapper.convertValue(o, typeReference);
    }

}
