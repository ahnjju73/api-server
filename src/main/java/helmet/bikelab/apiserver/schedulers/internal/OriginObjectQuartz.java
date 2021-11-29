package helmet.bikelab.apiserver.schedulers.internal;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.Gson;
import helmet.bikelab.apiserver.objects.exceptions.BusinessExceptionWithMessage;
import org.quartz.InterruptableJob;
import org.quartz.UnableToInterruptJobException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.util.List;
import java.util.Map;

public abstract class OriginObjectQuartz extends QuartzJobBean implements InterruptableJob {

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

    @Override
    public void interrupt() throws UnableToInterruptJobException { }

}
