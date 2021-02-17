package helmet.bikelab.apiserver.services;

import helmet.bikelab.apiserver.objects.BikeSessionRequest;
import helmet.bikelab.apiserver.objects.bikelabs.systems.MenuDto;
import helmet.bikelab.apiserver.services.internal.SessService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SystemService extends SessService {

    public BikeSessionRequest fetchMyLeftMenu(BikeSessionRequest request){
        Map param = request.getParam();
        param.put("up_menu_id", "L_MENU");
        List menu = getList("comm.menu.getMyMenu", param);
        if(menu == null) menu = new ArrayList();
        List<MenuDto> menuResponseDto = map(menu, List.class);
        request.setResponse(menuResponseDto);
        return request;
    }
}
