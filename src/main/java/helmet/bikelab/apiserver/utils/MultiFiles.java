package helmet.bikelab.apiserver.utils;

import helmet.bikelab.apiserver.services.internal.SessService;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.codec.multipart.Part;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
public class MultiFiles extends SessService {
    public Mono<Map> multipartFile(MultiValueMap<String, Part> parts, String fileName){
        Map map = parts.toSingleValueMap();
        FilePart filePart = (FilePart)map.get(fileName);
        if(filePart != null && !"".equals(filePart.filename()))
            map.put("__file__", filePart);
        else map.put("__file__", null);
        return Mono.fromSupplier(() -> map);
    }

}
