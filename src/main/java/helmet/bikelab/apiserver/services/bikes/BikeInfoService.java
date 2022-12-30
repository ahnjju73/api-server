package helmet.bikelab.apiserver.services.bikes;

import com.amazonaws.services.s3.AmazonS3;
import helmet.bikelab.apiserver.domain.CommonBikes;
import helmet.bikelab.apiserver.domain.Manufacturers;
import helmet.bikelab.apiserver.domain.bike.*;
import helmet.bikelab.apiserver.domain.bikelab.BikeUser;
import helmet.bikelab.apiserver.domain.bikelab.SystemParameter;
import helmet.bikelab.apiserver.domain.client.Clients;
import helmet.bikelab.apiserver.domain.demands.DemandLeases;
import helmet.bikelab.apiserver.domain.embeds.ModelBikeTransaction;
import helmet.bikelab.apiserver.domain.embeds.ModelTransaction;
import helmet.bikelab.apiserver.domain.lease.LeaseExpense;
import helmet.bikelab.apiserver.domain.lease.Leases;
import helmet.bikelab.apiserver.domain.riders.*;
import helmet.bikelab.apiserver.domain.types.*;
import helmet.bikelab.apiserver.objects.BikeDto;
import helmet.bikelab.apiserver.objects.BikeSessionRequest;
import helmet.bikelab.apiserver.objects.CarModel;
import helmet.bikelab.apiserver.objects.PresignedURLVo;
import helmet.bikelab.apiserver.objects.bikelabs.bikes.*;
import helmet.bikelab.apiserver.objects.bikelabs.clients.ClientDto;
import helmet.bikelab.apiserver.objects.bikelabs.leases.LeasesDto;
import helmet.bikelab.apiserver.objects.requests.*;
import helmet.bikelab.apiserver.objects.responses.FetchBikeTransactionResponse;
import helmet.bikelab.apiserver.objects.responses.ResponseListDto;
import helmet.bikelab.apiserver.repositories.*;
import helmet.bikelab.apiserver.services.internal.SessService;
import helmet.bikelab.apiserver.utils.AutoKey;
import helmet.bikelab.apiserver.utils.Utils;
import helmet.bikelab.apiserver.utils.amazon.AmazonUtils;
import helmet.bikelab.apiserver.utils.keys.ENV;
import helmet.bikelab.apiserver.workers.BikeWorker;
import helmet.bikelab.apiserver.workers.CommonWorker;
import lombok.RequiredArgsConstructor;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileInputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.*;

import static helmet.bikelab.apiserver.domain.bikelab.BikeUserLog.addLog;

@RequiredArgsConstructor
@Service
public class BikeInfoService extends SessService {

    private final BikeWorker bikeWorker;
    private final BikeInfoRepository bikeInfoRepository;

    public BikeSessionRequest getBikeInfoListByBikeId(BikeSessionRequest request){
        BikeByIdRequest bikeByIdRequest = map(request.getParam(), BikeByIdRequest.class);
        Bikes bikeById = bikeWorker.getBikeById(bikeByIdRequest.getBikeId());
        List<BikeInfo> allByBikeNo = bikeInfoRepository.findAllByBikeNo(bikeById.getBikeNo());
        request.setResponse(bePresent(allByBikeNo) ? allByBikeNo : new ArrayList<>());
        return request;
    }

    @Transactional
    public BikeSessionRequest addBikeInfo(BikeSessionRequest request){
        AddBikeInfoRequest addBikeInfoRequest = map(request.getParam(), AddBikeInfoRequest.class);
        addBikeInfoRequest.checkValidation();
        BikeByIdRequest bikeByIdRequest = map(request.getParam(), BikeByIdRequest.class);
        Bikes bikeById = bikeWorker.getBikeById(bikeByIdRequest.getBikeId());
        BikeInfo bikeInfo = new BikeInfo(bikeById, addBikeInfoRequest);
        bikeInfoRepository.save(bikeInfo);
        request.setResponse(bikeInfo);
        return request;
    }

    @Transactional
    public BikeSessionRequest updateBikeInfo(BikeSessionRequest request){
        UpdateBikeInfoRequest updateBikeInfoRequest = map(request.getParam(), UpdateBikeInfoRequest.class);
        BikeByIdRequest bikeByIdRequest = map(request.getParam(), BikeByIdRequest.class);
        BikeInfo bikeInfo = bikeWorker.getBikeInfoByBikeNoAndInfoNo(bikeByIdRequest.getBikeId(), updateBikeInfoRequest.getInfoNo());
        bikeInfo.updateBikeInfo(updateBikeInfoRequest);
        bikeInfoRepository.save(bikeInfo);
        request.setResponse(bikeInfo);
        return request;
    }

    @Transactional
    public BikeSessionRequest deleteBikeInfo(BikeSessionRequest request){
        BikeByIdRequest bikeByIdRequest = map(request.getParam(), BikeByIdRequest.class);
        BikeInfoByIdRequest bikeInfoByIdRequest = map(request.getParam(), BikeInfoByIdRequest.class);
        bikeInfoRepository.deleteByBike_BikeIdAndInfoNo(bikeByIdRequest.getBikeId(), bikeInfoByIdRequest.getInfoNo());
        return request;
    }

}
