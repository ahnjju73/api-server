package helmet.bikelab.apiserver.services;

import helmet.bikelab.apiserver.domain.bikelab.BikeUser;
import helmet.bikelab.apiserver.domain.bikelab.Program;
import helmet.bikelab.apiserver.domain.bikelab.ProgramUser;
import helmet.bikelab.apiserver.domain.types.BikeUserStatusTypes;
import helmet.bikelab.apiserver.domain.types.YesNoTypes;
import helmet.bikelab.apiserver.objects.AuthDto;
import helmet.bikelab.apiserver.objects.BikeSessionRequest;
import helmet.bikelab.apiserver.objects.bikelabs.systems.HandlePermissionRequest;
import helmet.bikelab.apiserver.objects.bikelabs.systems.MenuDto;
import helmet.bikelab.apiserver.repositories.BikeLabUserRepository;
import helmet.bikelab.apiserver.repositories.ProgramRepository;
import helmet.bikelab.apiserver.repositories.ProgramUserRepository;
import helmet.bikelab.apiserver.services.internal.SessService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SystemService extends SessService {

    private final ProgramUserRepository programUserRepository;
    private final ProgramRepository programRepository;
    private final BikeLabUserRepository bikeLabUserRepository;

    public BikeSessionRequest fetchMyLeftMenu(BikeSessionRequest request){
        Map param = request.getParam();
        param.put("up_menu_id", "L_MENU");
        List menu = getList("comm.menu.getMyMenu", param);
        if(menu == null) menu = new ArrayList();
        List<MenuDto> menuResponseDto = map(menu, List.class);
        request.setResponse(menuResponseDto);
        return request;
    }

    public BikeSessionRequest fetchAllMenus(BikeSessionRequest request){
        Map param = request.getParam();
        Map response = new HashMap();
        response.put("list", getList("comm.menu.fetchAllMenus", param));
        request.setResponse(response);
        return request;
    }

    public BikeSessionRequest fetchUsersMenu(BikeSessionRequest request){
        Map param = request.getParam();
        Map response = new HashMap();
        response.put("auth", getList("comm.menu.getUsersInMenu", param));
        response.put("noauth", getList("comm.menu.getOthersInMenu", param));
        request.setResponse(response);
        return request;
    }

    @Transactional
    public BikeSessionRequest handlePermissionToUser(BikeSessionRequest request){
        Map param = request.getParam();
        HandlePermissionRequest handlePermissionRequest = map(param, HandlePermissionRequest.class);

        programUserRepository
                .findByBikeUserNoAndProgram_ProgramIdAndProgram_Usable(handlePermissionRequest.getUserNo(), handlePermissionRequest.getPgmId(), YesNoTypes.YES)
                .ifPresentOrElse(programUser -> {
                    programUserRepository.delete(programUser);
                }, () -> {
                    Program program = programRepository.findByProgramIdAndAndUsable(handlePermissionRequest.getPgmId(), YesNoTypes.YES);
                    BikeUser byUserIdAndUserStatusTypes = bikeLabUserRepository.findByUserNoAndUserStatusTypes(handlePermissionRequest.getUserNo(), BikeUserStatusTypes.COMPLETED);
                    if(!bePresent(program) || !bePresent(byUserIdAndUserStatusTypes)) withException("600-001");
                    ProgramUser programUser = new ProgramUser();
                    programUser.setProgramNo(program.getProgramNo());
                    programUser.setBikeUserNo(byUserIdAndUserStatusTypes.getUserNo());
                    programUserRepository.save(programUser);
                });
        return request;
    }

    public BikeSessionRequest checkAuthorization(BikeSessionRequest request){
        Map param = request.getParam();
        BikeUser bikeSession = request.getSessionUser();
        String programId = (String)param.get("program_id");
        ProgramUser programUser = programUserRepository.findByProgram_ProgramIdAndProgram_UsableAndBikeUserNo(programId, YesNoTypes.YES, bikeSession.getUserNo());
        if(bePresent(programUser)){
            AuthDto authDto = new AuthDto();
            authDto.setAuthType(programUser.getReadWriting());
            authDto.setAuthTypeCode(programUser.getReadWriting().getAuth());
            request.setResponse(authDto);
        }else withException("010-001");

        return request;
    }
}
