package helmet.bikelab.apiserver.services.employees;

import helmet.bikelab.apiserver.domain.bikelab.Inquiries;
import helmet.bikelab.apiserver.domain.types.InquiryStatusTypes;
import helmet.bikelab.apiserver.objects.BikeSessionRequest;
import helmet.bikelab.apiserver.objects.requests.ClientListDto;
import helmet.bikelab.apiserver.objects.requests.InquiriesDto;
import helmet.bikelab.apiserver.objects.requests.RequestListDto;
import helmet.bikelab.apiserver.objects.responses.ResponseListDto;
import helmet.bikelab.apiserver.repositories.InquiriesRepository;
import helmet.bikelab.apiserver.services.internal.SessService;
import helmet.bikelab.apiserver.workers.CommonWorker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class InquiryService extends SessService {

    private final CommonWorker commonWorker;
    private final InquiriesRepository inquiriesRepository;

    public BikeSessionRequest fetchInquiries(BikeSessionRequest request){
        Map param = request.getParam();
        InquiriesDto requestListDto = map(param, InquiriesDto.class);
        ResponseListDto responseListDto = commonWorker.fetchItemListByNextToken(requestListDto, "bikelabs.inquiries.fetchInquiries", "bikelabs.inquiries.countAllInquiries", "inquiry_no");
        request.setResponse(responseListDto);
        return request;
    }

    @Transactional
    public BikeSessionRequest confirmInquiryByInquiryNo(BikeSessionRequest request){
        Map param = request.getParam();
        String inquiryNo = (String)param.get("inquiry_no");
        if(!bePresent(inquiryNo)) withException("110-001");
        Inquiries byInquiryNo = inquiriesRepository.findByInquiryNo(Long.parseLong(inquiryNo));
        if(!bePresent(byInquiryNo)) withException("110-001");
        byInquiryNo.setInquiryStatusTypes(InquiryStatusTypes.CONFIRMED);
        byInquiryNo.setConfirmedUserNo(request.getSessionUser().getUserNo());
        byInquiryNo.setConfirmedAt(LocalDateTime.now());
        inquiriesRepository.save(byInquiryNo);
        return request;
    }

    public BikeSessionRequest fetchDemandLeases(BikeSessionRequest request){
        RequestListDto requestListDto = map(request.getParam(), RequestListDto.class);
        ResponseListDto responseListDto = commonWorker.fetchItemListByNextToken(requestListDto, "bikelabs.inquiries.fetchDemandLeases", "bikelabs.inquiries.countAllDemandLeases", "demand_lease_id");
        request.setResponse(responseListDto);
        return request;
    }

}
