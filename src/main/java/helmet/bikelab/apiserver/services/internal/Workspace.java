package helmet.bikelab.apiserver.services.internal;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import helmet.bikelab.apiserver.config.SqlMaster;
import helmet.bikelab.apiserver.objects.exceptions.BusinessException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class Workspace extends OriginObject{

    @Autowired
    private SqlMaster sm;

    @Autowired
    private ModelMapper modelMapper;

    // Database Settings >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    public Object insertObject(String path, Object param){
        try{
            return this.sm.insert(path, param);
        }catch (Exception e){
            throw getErrString(e, param);
        }
    }

    public Object updateObject(String path, Object param){
        try{
            return this.sm.update(path, param);
        }catch (Exception e){
            throw getErrString(e, param);
        }
    }

    public Object deleteObject(String path, Object param){
        try{
            return this.sm.delete(path, param);
        }catch (Exception e){
            throw getErrString(e, param);
        }
    }

    public Object getItem(String path, Object param){
        try{
            return this.sm.selectOne(path, param);
        }catch (Exception e){
            throw getErrString(e, param);
        }
    }

    public List getList(String path, Object param){
        try{
            List list = (List)this.sm.selectList(path, param);
            return list == null ? new ArrayList() : list;
        }catch (Exception e){
            e.printStackTrace();
            throw getErrString(e, param);
        }
    }

    protected Object procedureObject(String path, Object param){
        try{
            return this.sm.selectOne(path, param);
        }catch (Exception e){
            throw getErrString(e, param);
        }
    }

    private RuntimeException getErrString(String json) {
        throw new RuntimeException(json);
    }

    private RuntimeException getErrString(Exception e, Object param){
        Map rtn = new HashMap();
        rtn.put("result", "n");
        rtn.put("message", getMessage((Map)param));
        throw new RuntimeException(getJson(rtn));
    }
    // Database Settings end

    protected Map write(){
        return write(new HashMap());
    }

    protected Map write(Object o){
        if(o instanceof Map) {
            ((Map)o).put("result", "y");
            return (Map)o;
        }else if(o instanceof List){
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("result", "y");
            map.put("list", o);
            return map;
        }
        return (Map)o;
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

    protected void writeError(Map map, String lang_code){
        writeError(map, lang_code, HttpStatus.BAD_REQUEST);
    }

    protected void writeError(Map map, String lang_code, HttpStatus httpStatus){
        lang_code = lang_code == null || lang_code.equals("") ? "900-001" : lang_code;
        map.put("lang_code", lang_code);
        String err = (String)getItem("comm.common.getMessage", map);

        BusinessException businessException = new BusinessException();
        businessException.setErr_code(lang_code);
        businessException.setMsg(err == null ? "" : err);
        businessException.setErrHttpStatus(httpStatus);
        throw businessException;
    }

    protected String getMessage(Map map){
        return getMessage(map, "900-000");
    }

    public String getMessage(Map map, String lang_code){
        map.put("lang_code", lang_code);
        return (String)getItem("comm.common.getMessage", map);
    }

    protected <T, K> T map(K o, Class<T> cls){
        if(o == null) {
            try {
                return cls.getConstructor().newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
            return objectMapper.convertValue(o, cls);
//            return modelMapper.map(o, cls);
        }catch (Exception e){
            e.printStackTrace();
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
                e.printStackTrace();
            }
            return null;
        }
        return mapper.convertValue(o, typeReference);
    }
}