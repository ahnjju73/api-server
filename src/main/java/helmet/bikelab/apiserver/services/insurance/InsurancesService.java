package helmet.bikelab.apiserver.services.insurance;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CopyObjectRequest;
import helmet.bikelab.apiserver.domain.CommonCodeInsurances;
import helmet.bikelab.apiserver.domain.bike.Bikes;
import helmet.bikelab.apiserver.domain.embeds.ModelAttachment;
import helmet.bikelab.apiserver.domain.lease.Insurances;
import helmet.bikelab.apiserver.domain.riders.RiderInsuranceHistories;
import helmet.bikelab.apiserver.domain.riders.RiderInsurances;
import helmet.bikelab.apiserver.domain.riders.RiderInsurancesDtl;
import helmet.bikelab.apiserver.domain.riders.Riders;
import helmet.bikelab.apiserver.domain.types.*;
import helmet.bikelab.apiserver.objects.*;
import helmet.bikelab.apiserver.objects.bikelabs.insurance.DeleteInsuranceRequest;
import helmet.bikelab.apiserver.objects.bikelabs.insurance.FetchInsuranceResponse;
import helmet.bikelab.apiserver.objects.bikelabs.leases.LeasesDto;
import helmet.bikelab.apiserver.objects.requests.AddUpdateRiderInsuranceRequest;
import helmet.bikelab.apiserver.objects.requests.FetchRiderInsuranceRequest;
import helmet.bikelab.apiserver.repositories.*;
import helmet.bikelab.apiserver.services.internal.SessService;
import helmet.bikelab.apiserver.utils.AutoKey;
import helmet.bikelab.apiserver.utils.Utils;
import helmet.bikelab.apiserver.utils.amazon.AmazonUtils;
import helmet.bikelab.apiserver.utils.keys.ENV;
import helmet.bikelab.apiserver.workers.BikeWorker;
import helmet.bikelab.apiserver.workers.CommonWorker;
import helmet.bikelab.apiserver.workers.RiderWorker;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InsurancesService extends SessService {

    private final InsurancesRepository insurancesRepository;
    private final AutoKey autoKey;
    private final RiderWorker riderWorker;
    private final BikeWorker bikeWorker;
    private final InsuranceOptionlRepository insuranceOptionlRepository;
    private final LeaseRepository leaseRepository;
    private final CommonWorker commonWorker;
    private final RiderInsuranceRepository riderInsuranceRepository;
    private final RiderInsuranceDtlRepository riderInsuranceDtlRepository;
    private final RiderInsuranceHistoryRepository riderInsuranceHistoryRepository;


    public BikeSessionRequest fetchInsurances(BikeSessionRequest request) {
        Map response = new HashMap();
        List<Insurances> insurancesList = insurancesRepository.findAll();
        response.put("insurances", insurancesList);
        request.setResponse(response);
        return request;
    }

    @Transactional
    public BikeSessionRequest addInsurance(BikeSessionRequest request) {
        Map param = request.getParam();
        Insurances insurance = map(param, Insurances.class);
        insurance.checkValidation();
        String insuranceId = autoKey.makeGetKey("insurance");
        insurance.setInsuranceId(insuranceId);
        insurancesRepository.save(insurance);
        return request;
    }

    @Transactional
    public BikeSessionRequest updateInsurance(BikeSessionRequest request) {
        Map param = request.getParam();
        Insurances newInsurance = map(param, Insurances.class);
        newInsurance.checkValidation();
        Insurances insurance = insurancesRepository.findByInsuranceId(newInsurance.getInsuranceId());
        insurance.setInsuranceTypeCode(newInsurance.getInsuranceTypeCode());
        insurance.setCompanyName(newInsurance.getCompanyName());
        insurance.setAge(newInsurance.getAge());
        insurance.setBmCare(newInsurance.getBmCare());
        insurance.setLiabilityCar(newInsurance.getLiabilityCar());
        insurance.setLiabilityMan(newInsurance.getLiabilityMan());
        insurance.setSelfCoverCar(newInsurance.getSelfCoverCar());
        insurance.setSelfCoverMan(newInsurance.getSelfCoverMan());
        insurance.setLiabilityMan2(newInsurance.getLiabilityMan2());
        insurance.setNoInsuranceCover(newInsurance.getNoInsuranceCover());
        insurance.setType(newInsurance.getType());
        insurance.setInsuranceName(newInsurance.getInsuranceName());
        insurancesRepository.save(insurance);

        return request;
    }

    @Transactional
    public BikeSessionRequest deleteInsurance(BikeSessionRequest request) {
        Map param = request.getParam();
        DeleteInsuranceRequest deleteInsuranceRequest = map(param, DeleteInsuranceRequest.class);
        Insurances insurances = insurancesRepository.findByInsuranceId(deleteInsuranceRequest.getInsuranceId());
        if (leaseRepository.existsAllByInsuranceNoEquals(insurances.getInsuranceNo()))
            writeMessage("사용중인 보험입니다 삭제할 수 없습니다.");
        insurancesRepository.delete(insurances);
        return request;
    }

    public BikeSessionRequest fetchInsuranceOption(BikeSessionRequest request) {
        Map param = request.getParam();
        Map response = new HashMap();
        List<CommonCodeInsurances> insuranceOptions = insuranceOptionlRepository.findAll();
        List<FetchInsuranceResponse> fetchInsuranceResponses = new ArrayList<>();
        for (CommonCodeInsurances insurance : insuranceOptions) {
            if (insurance.getUpperCode() == null) {
                FetchInsuranceResponse fetchInsuranceResponse = new FetchInsuranceResponse();
                fetchInsuranceResponse.setUpCode(insurance.getCode());
                fetchInsuranceResponse.setUpCodeName(insurance.getName());
                fetchInsuranceResponse.setList(new ArrayList<>());
                fetchInsuranceResponses.add(fetchInsuranceResponse);
            } else {
                for (int i = 0; i < fetchInsuranceResponses.size(); i++) {
                    if (fetchInsuranceResponses.get(i).getUpCode().equals(insurance.getUpperCode())) {
                        InsuranceOptionDto optionDto = new InsuranceOptionDto();
                        optionDto.setValue(insurance.getName());
                        optionDto.setComCode(insurance.getCode());
                        fetchInsuranceResponses.get(i).getList().add(optionDto);
                    }
                }
            }
        }
        response.put("insurances", fetchInsuranceResponses);
        request.setResponse(response);
        return request;
    }

    @Transactional
    public BikeSessionRequest addRiderInsurance(BikeSessionRequest request) {
        AddUpdateRiderInsuranceRequest addUpdateRiderInsuranceRequest = map(request.getParam(), AddUpdateRiderInsuranceRequest.class);
        String riderInsId = autoKey.makeGetKey("rider_ins");
        RiderInsurances riderInsurances = new RiderInsurances();
        riderInsurances.setRiderInsId(riderInsId);
        Riders rider = null;
        if (bePresent(addUpdateRiderInsuranceRequest.getRiderInfoDto().getRiderId())) {
            rider = riderWorker.getRiderById(addUpdateRiderInsuranceRequest.getRiderInfoDto().getRiderId());
            riderInsurances.setRiderNo(rider.getRiderNo());
        }
        if (bePresent(rider)) {
            riderInsurances.setRiderEmail(rider.getEmail());
            riderInsurances.setRiderPhone(rider.getPhone());
            riderInsurances.setRiderName(rider.getRiderInfo().getName());
            riderInsurances.setRiderSsn(addUpdateRiderInsuranceRequest.getSsn());
        } else {
            riderInsurances.setRiderEmail(addUpdateRiderInsuranceRequest.getRiderInfoDto().getRiderEmail());
            riderInsurances.setRiderPhone(addUpdateRiderInsuranceRequest.getRiderInfoDto().getRiderPhone());
            riderInsurances.setRiderName(addUpdateRiderInsuranceRequest.getRiderInfoDto().getRiderName());
            riderInsurances.setRiderSsn(addUpdateRiderInsuranceRequest.getSsn());
        }
        riderInsurances.setBikeNum(addUpdateRiderInsuranceRequest.getBikeNum());
        riderInsurances.setVimNum(addUpdateRiderInsuranceRequest.getVimNum());
        riderInsurances.setBikeTypes(InsuranceBikeTypes.getType(addUpdateRiderInsuranceRequest.getBikeType()));
        riderInsurances.setRiderAddress(new AddressDto().setByModelAddress(addUpdateRiderInsuranceRequest.getAddress()));
        List<ModelAttachment> attachments = new ArrayList<>();
        if(bePresent(addUpdateRiderInsuranceRequest.getNewAttachments())) {
            List<ModelAttachment> newAttachments = addUpdateRiderInsuranceRequest.getNewAttachments()
                    .stream().map(presignedURLVo -> {
                        AmazonS3 amazonS3 = AmazonUtils.amazonS3();
                        String fileKey = "rider-insurance/" + riderInsurances.getRiderNo() + "/" + presignedURLVo.getFileKey();
                        CopyObjectRequest objectRequest = new CopyObjectRequest(presignedURLVo.getBucket(), presignedURLVo.getFileKey(), ENV.AWS_S3_ORIGIN_BUCKET, fileKey);
                        amazonS3.copyObject(objectRequest);
                        ModelAttachment leaseAttachment = new ModelAttachment();
                        leaseAttachment.setUuid(UUID.randomUUID().toString().replaceAll("-", ""));
                        leaseAttachment.setDomain(ENV.AWS_S3_ORIGIN_DOMAIN);
                        leaseAttachment.setUri("/" + fileKey);
                        leaseAttachment.setFileName(presignedURLVo.getFilename());
                        return leaseAttachment;
                    }).collect(Collectors.toList());
            attachments.addAll(newAttachments);
        }
        riderInsurances.setAttachmentsList(attachments);
        riderInsuranceRepository.save(riderInsurances);

        RiderInsurancesDtl insurancesDtl = new RiderInsurancesDtl();
        insurancesDtl.setAge(InsAgeTypes.getAge(addUpdateRiderInsuranceRequest.getAge()));
        insurancesDtl.setRiderInsNo(riderInsurances.getRiderInsNo());
        insurancesDtl.setInsCompany(InsCompanyTypes.getCompanyType(addUpdateRiderInsuranceRequest.getInsCompany()));
        insurancesDtl.setInsNum(addUpdateRiderInsuranceRequest.getInsNum());
        insurancesDtl.setInsRangeType(InsRangeTypes.getType(addUpdateRiderInsuranceRequest.getInsRange()));
        insurancesDtl.setLiabilityMan(addUpdateRiderInsuranceRequest.getLiabilityMan());
        insurancesDtl.setLiabilityCar(addUpdateRiderInsuranceRequest.getLiabilityCar());
        insurancesDtl.setLiabilityMan2(addUpdateRiderInsuranceRequest.getLiabilityMan2());
        insurancesDtl.setSelfCoverMan(addUpdateRiderInsuranceRequest.getSelfCoverMan());
        insurancesDtl.setSelfCoverCar(addUpdateRiderInsuranceRequest.getSelfCoverCar());
        insurancesDtl.setNoInsCover(addUpdateRiderInsuranceRequest.getNoInsuranceCover());
        insurancesDtl.setRiderInsuranceStatus(RiderInsuranceStatus.PENDING);
        if (bePresent(addUpdateRiderInsuranceRequest.getBankInfoDto()))
            insurancesDtl.setBankInfo(addUpdateRiderInsuranceRequest.getBankInfoDto());
        insurancesDtl.setUsageTypes(UsageTypes.getType(addUpdateRiderInsuranceRequest.getUsage()));
        insurancesDtl.setAdditionalStandardTypes(AdditionalStandardTypes.getType(addUpdateRiderInsuranceRequest.getAdditionalStandard()));
        insurancesDtl.setStartDt(addUpdateRiderInsuranceRequest.getStartDt());
        insurancesDtl.setEndDt(addUpdateRiderInsuranceRequest.getEndDt());
        insurancesDtl.setInsFee(addUpdateRiderInsuranceRequest.getInsFee());
        riderInsuranceDtlRepository.save(insurancesDtl);
        return request;
    }

    @Transactional
    public BikeSessionRequest updateRiderInsuranceDtls(BikeSessionRequest request){
        UpdateRiderInsuranceDtlRequest updateRiderInsuranceDtlRequest = map(request.getParam(), UpdateRiderInsuranceDtlRequest.class);
        RiderInsurancesDtl insurancesDtl = riderInsuranceDtlRepository.findByDtlNo(updateRiderInsuranceDtlRequest.getDtlNo());
        insurancesDtl.setInsCompany(InsCompanyTypes.getCompanyType(updateRiderInsuranceDtlRequest.getInsCompany()));
        insurancesDtl.setInsNum(updateRiderInsuranceDtlRequest.getInsNum());
        insurancesDtl.setCreatedBy(request.getSessionUser().getUserNo());
        insurancesDtl.setInsRangeType(InsRangeTypes.getType(updateRiderInsuranceDtlRequest.getInsRange()));
        insurancesDtl.setLiabilityMan(updateRiderInsuranceDtlRequest.getLiabilityMan());
        insurancesDtl.setLiabilityCar(updateRiderInsuranceDtlRequest.getLiabilityCar());
        insurancesDtl.setLiabilityMan2(updateRiderInsuranceDtlRequest.getLiabilityMan2());
        insurancesDtl.setSelfCoverMan(updateRiderInsuranceDtlRequest.getSelfCoverMan());
        insurancesDtl.setSelfCoverCar(updateRiderInsuranceDtlRequest.getSelfCoverCar());
        insurancesDtl.setNoInsCover(updateRiderInsuranceDtlRequest.getNoInsuranceCover());
        insurancesDtl.setRiderInsuranceStatus(RiderInsuranceStatus.PENDING);
        if (bePresent(updateRiderInsuranceDtlRequest.getBankInfoDto()))
            insurancesDtl.setBankInfo(updateRiderInsuranceDtlRequest.getBankInfoDto());
        insurancesDtl.setUsageTypes(UsageTypes.getType(updateRiderInsuranceDtlRequest.getUsage()));
        insurancesDtl.setAdditionalStandardTypes(AdditionalStandardTypes.getType(updateRiderInsuranceDtlRequest.getAdditionalStandard()));
        insurancesDtl.setStartDt(updateRiderInsuranceDtlRequest.getStartDt());
        insurancesDtl.setEndDt(updateRiderInsuranceDtlRequest.getEndDt());
        insurancesDtl.setInsFee(updateRiderInsuranceDtlRequest.getInsFee());
        insurancesDtl.setAge(InsAgeTypes.getAge(updateRiderInsuranceDtlRequest.getAge()));
        riderInsuranceDtlRepository.save(insurancesDtl);
        return request;
    }

    public BikeSessionRequest fetchRiderInsurances(BikeSessionRequest request) {
        FetchRiderInsuranceRequest fetchRiderInsuranceRequest = map(request.getParam(), FetchRiderInsuranceRequest.class);
        Pageable pageable = PageRequest.of(fetchRiderInsuranceRequest.getPage(), fetchRiderInsuranceRequest.getSize(), Sort.by("riderInsNo").descending());
        if (bePresent(fetchRiderInsuranceRequest.getRiderName()) && bePresent(fetchRiderInsuranceRequest.getStatus())) {
            Page<RiderInsurances> allByRiderInsurancesDtl_riderInfoDto_riderNameContaining = riderInsuranceRepository.findAllByRiderNameContaining(fetchRiderInsuranceRequest.getRiderName(), pageable);
            request.setResponse(allByRiderInsurancesDtl_riderInfoDto_riderNameContaining);
        } else if (bePresent(fetchRiderInsuranceRequest.getRiderName())) {
            Page<RiderInsurances> allByRiderInsurancesDtl_riderInfoDto_riderNameContaining = riderInsuranceRepository.findAllByRiderNameContaining(fetchRiderInsuranceRequest.getRiderName(), pageable);
            request.setResponse(allByRiderInsurancesDtl_riderInfoDto_riderNameContaining);
        } else if (bePresent(fetchRiderInsuranceRequest.getStatus())) {
////            Page<RiderInsurances> allByRiderInsurancesDtl_riderInfoDto_riderNameContaining = riderInsuranceRepository.findAllByRiderInsurancesDtl_RiderInsuranceStatus(RiderInsuranceStatus.getStatus(fetchRiderInsuranceRequest.getStatus()), pageable);
////            request.setResponse(allByRiderInsurancesDtl_riderInfoDto_riderNameContaining);
        } else {
            Page<RiderInsurances> allOrderByRiderInsNoDesc = riderInsuranceRepository.findAll(pageable);
            request.setResponse(allOrderByRiderInsNoDesc);
        }
        return request;
    }

    public BikeSessionRequest fetchRiderInsuranceDetail(BikeSessionRequest request) {
        String riderInsId = (String) request.getParam().get("rider_ins_id");
        RiderInsurances byRiderInsId = riderInsuranceRepository.findByRiderInsId(riderInsId);
        request.setResponse(byRiderInsId);
        return request;
    }

    @Transactional
    public BikeSessionRequest updateRiderInsurance(BikeSessionRequest request) {
        String riderInsId = (String) request.getParam().get("rider_ins_id");
        AddUpdateRiderInsuranceRequest addUpdateRiderInsuranceRequest = map(request.getParam(), AddUpdateRiderInsuranceRequest.class);
        RiderInsurances riderInsurances = riderInsuranceRepository.findByRiderInsId(riderInsId);
        String log = getChangeLog(riderInsurances, addUpdateRiderInsuranceRequest);
        if (!log.isBlank()) {
            log = "<>" + request.getSessionUser().getBikeUserInfo().getName() + "님</>이 수정하였습니다.\n" + log;
            RiderInsuranceHistories history = riderInsuranceHistoryRepository.findByRiderInsurance_RiderInsId(riderInsId);
            if (!bePresent(history)) {
                history = new RiderInsuranceHistories();
                history.setRiderInsNo(riderInsurances.getRiderInsNo());
            }
            RiderInsHistoriesDto riderInsHistoriesDto = new RiderInsHistoriesDto();
            riderInsHistoriesDto.setLog(log);
            riderInsHistoriesDto.setUpdatedAt(LocalDateTime.now());
            history.getHistories().add(0, riderInsHistoriesDto);
            riderInsuranceHistoryRepository.save(history);
        }
        Riders rider = null;
        if (bePresent(addUpdateRiderInsuranceRequest.getRiderInfoDto().getRiderId())) {
            rider = riderWorker.getRiderById(addUpdateRiderInsuranceRequest.getRiderInfoDto().getRiderId());
            riderInsurances.setRiderNo(rider.getRiderNo());
        }
        if (bePresent(rider)) {
            riderInsurances.setRiderEmail(rider.getEmail());
            riderInsurances.setRiderPhone(rider.getPhone());
            riderInsurances.setRiderName(rider.getRiderInfo().getName());
            riderInsurances.setRiderSsn(addUpdateRiderInsuranceRequest.getSsn());
        } else {
            riderInsurances.setRiderEmail(addUpdateRiderInsuranceRequest.getRiderInfoDto().getRiderEmail());
            riderInsurances.setRiderPhone(addUpdateRiderInsuranceRequest.getRiderInfoDto().getRiderPhone());
            riderInsurances.setRiderName(addUpdateRiderInsuranceRequest.getRiderInfoDto().getRiderName());
            riderInsurances.setRiderSsn(addUpdateRiderInsuranceRequest.getSsn());
        }
        riderInsurances.setBikeNum(addUpdateRiderInsuranceRequest.getBikeNum());
        riderInsurances.setVimNum(addUpdateRiderInsuranceRequest.getVimNum());
        riderInsurances.setBikeTypes(InsuranceBikeTypes.getType(addUpdateRiderInsuranceRequest.getBikeType()));
        riderInsurances.setRiderAddress(new AddressDto().setByModelAddress(addUpdateRiderInsuranceRequest.getAddress()));
        List<ModelAttachment> attachments = addUpdateRiderInsuranceRequest.getAttachments() != null ? addUpdateRiderInsuranceRequest.getAttachments() : new ArrayList<>();
        deletedAttachments(riderInsurances.getAttachmentsList(), attachments).stream().forEach(ma -> {
            AmazonS3 amazonS3 = AmazonUtils.amazonS3();
            amazonS3.deleteObject(ENV.AWS_S3_ORIGIN_BUCKET, ma.getUri());
        });
        List<ModelAttachment> newAttachments = addUpdateRiderInsuranceRequest.getNewAttachments()
                .stream().map(presignedURLVo -> {
                    AmazonS3 amazonS3 = AmazonUtils.amazonS3();
                    String fileKey = "rider-insurance/" + riderInsurances.getRiderNo() + "/" + presignedURLVo.getFileKey();
                    CopyObjectRequest objectRequest = new CopyObjectRequest(presignedURLVo.getBucket(), presignedURLVo.getFileKey(), ENV.AWS_S3_ORIGIN_BUCKET, fileKey);
                    amazonS3.copyObject(objectRequest);
                    ModelAttachment leaseAttachment = new ModelAttachment();
                    leaseAttachment.setUuid(UUID.randomUUID().toString().replaceAll("-", ""));
                    leaseAttachment.setDomain(ENV.AWS_S3_ORIGIN_DOMAIN);
                    leaseAttachment.setUri("/" + fileKey);
                    leaseAttachment.setFileName(presignedURLVo.getFilename());
                    return leaseAttachment;
                }).collect(Collectors.toList());
        attachments.addAll(newAttachments);
        riderInsurances.setAttachmentsList(attachments);
        riderInsuranceRepository.save(riderInsurances);
        return request;
    }

    @Transactional
    public BikeSessionRequest deleteRiderInsurance(BikeSessionRequest request) {
        String riderInsId = (String) request.getParam().get("rider_ins_id");
        riderInsuranceDtlRepository.deleteAllByRiderInsurances_RiderInsId(riderInsId);
        riderInsuranceHistoryRepository.deleteAllByRiderInsurance_RiderInsId(riderInsId);
        riderInsuranceRepository.deleteByRiderInsId(riderInsId);
        return request;
    }

    private List<ModelAttachment> deletedAttachments(List<ModelAttachment> origin, List<ModelAttachment> updated){
        List<ModelAttachment> deleted = new ArrayList<>();
        if(origin.size() == updated.size()){
            return deleted;
        }else{
            for(int i = 0; i < origin.size(); i++){
                if(updated.indexOf(origin.get(i)) < 0){
                    deleted.add(origin.get(i));
                }
            }
        }
        return deleted;
    }


    private String getChangeLog(RiderInsurances riderInsurances, AddUpdateRiderInsuranceRequest addUpdateRiderInsuranceRequest) {
        String change = "";
        Riders rider = null;
        if (bePresent(addUpdateRiderInsuranceRequest.getRiderInfoDto().getRiderId())) {
            rider = riderWorker.getRiderById(addUpdateRiderInsuranceRequest.getRiderInfoDto().getRiderId());
            riderInsurances.setRiderNo(rider.getRiderNo());
        }
        if (bePresent(rider)) {
            if (!riderInsurances.getRiderName().equals(rider.getRiderInfo().getName())) {
                change += "라이더를 <>" + riderInsurances.getRiderName() + "</>에서 <>" + rider.getRiderInfo().getName() + "</>로 수정하였습니다.";
            }
        } else if (!bePresent(rider)) {
            if (!riderInsurances.getRiderName().equals(addUpdateRiderInsuranceRequest.getRiderInfoDto().getRiderName())) {
                change += "라이더를 <>" + riderInsurances.getRiderName() + "</>에서 <>" + rider.getRiderInfo().getName() + "</>로 수정하였습니다.";
            }
            if (!riderInsurances.getRiderEmail().equals(addUpdateRiderInsuranceRequest.getRiderInfoDto().getRiderEmail())) {
                change += "라이더 이메일을 <>" + riderInsurances.getRiderEmail() + "</>에서 <>" + addUpdateRiderInsuranceRequest.getRiderInfoDto().getRiderEmail() + "</>로 수정하였습니다.";
            }
            if (!riderInsurances.getRiderPhone().equals(addUpdateRiderInsuranceRequest.getRiderInfoDto().getRiderPhone())) {
                change += "라이더 연락처를 <>" + riderInsurances.getRiderPhone() + "</>에서 <>" + addUpdateRiderInsuranceRequest.getRiderInfoDto().getRiderPhone() + "</>로 수정하였습니다.";
            }
        }
        if (bePresent(riderInsurances.getRiderSsn()) && !riderInsurances.getRiderSsn().equals(addUpdateRiderInsuranceRequest.getSsn())) {
            if (bePresent(riderInsurances.getRiderSsn())) {
                change += "라이더의 주민번호를 <>" + riderInsurances.getRiderSsn() + "</>에서 <>" + addUpdateRiderInsuranceRequest.getSsn() + "</>으로 수정하였습니다.\n";
            } else {
                change += "라이더의 주민번호를 <>" + addUpdateRiderInsuranceRequest.getSsn() + "</>으로 설정하였습니다.\n";
            }
        }
//        if (bePresent(riderInsurances.getAge()) && !riderInsurances.getAge().equals(InsAgeTypes.getAge(addUpdateRiderInsuranceRequest.getAge()))) {
//            if (bePresent(riderInsurances.getAge())) {
//                change += "라이더의 나이를 <>" + riderInsurances.getAge().getAge() + " 세</>에서 <>" + addUpdateRiderInsuranceRequest.getAge() + " 세</>로 수정하였습니다.\n";
//            } else {
//                change += "라이더의 나이를 <>" + addUpdateRiderInsuranceRequest.getAge() + " 세</>로 설정하였습니다.\n";
//            }
//        }
        if (bePresent(riderInsurances.getBikeNum()) && !riderInsurances.getBikeNum().equals(addUpdateRiderInsuranceRequest.getBikeNum())) {
            if (bePresent(riderInsurances.getBikeNum())) {
                change += "바이크 번호를 <>" + riderInsurances.getBikeNum() + "</>에서 <>" + addUpdateRiderInsuranceRequest.getBikeNum() + "</>로 수정하였습니다.\n";
            } else {
                change += "바이크 번호를 <>" + addUpdateRiderInsuranceRequest.getBikeNum() + "</>로 설정하였습니다.\n";
            }
        }
        if (bePresent(riderInsurances.getVimNum()) && !riderInsurances.getVimNum().equals(addUpdateRiderInsuranceRequest.getVimNum())) {
            if (bePresent(riderInsurances.getBikeNum())) {
                change += "차대 번호를 <>" + riderInsurances.getVimNum() + "</>에서 <>" + addUpdateRiderInsuranceRequest.getVimNum() + "</>로 수정하였습니다.\n";
            } else {
                change += "차대 번호를 <>" + addUpdateRiderInsuranceRequest.getVimNum() + "</>로 설정하였습니다.\n";
            }
        }
        if (bePresent(riderInsurances.getBikeTypes()) && !riderInsurances.getBikeTypes().equals(InsuranceBikeTypes.getType(addUpdateRiderInsuranceRequest.getBikeType()))) {
            if (bePresent(riderInsurances.getBikeNum())) {
                change += "바이크 종류를 <>" + riderInsurances.getBikeTypes() + "</>에서 <>" + InsuranceBikeTypes.getType(addUpdateRiderInsuranceRequest.getBikeType()) + "</>로 수정하였습니다.\n";
            } else {
                change += "바이크 종류를 <>" + InsuranceBikeTypes.getType(addUpdateRiderInsuranceRequest.getBikeType()) + "</>로 설정하였습니다.\n";
            }
        }
//        RiderInsurancesDtl topDetail = riderInsuranceDtlRepository.findTopByRiderInsurances_RiderInsIdOrderByDtlNoDesc(riderInsurances.getRiderInsId());
//        Integer dtlCnt = riderInsuranceDtlRepository.countAllByRiderInsurances_RiderInsId(riderInsurances.getRiderInsId());
//        if (!topDetail.getInsCompanyCode().equals(addUpdateRiderInsuranceRequest.getInsCompany())) {
//            change += dtlCnt + "번째 계약서 보험 회사를 <>" + topDetail.getInsCompany() + "</>에서 <>" + InsCompanyTypes.getCompanyType(addUpdateRiderInsuranceRequest.getInsCompany()) + "</>로 수정하였습니다.\n";
//        }
//        if (!topDetail.getInsCompanyCode().equals(addUpdateRiderInsuranceRequest.getInsCompany())) {
//            change += dtlCnt + "번째 계약서 보험 번호를 <>" + topDetail.getInsNum() + "</>에서 <>" + addUpdateRiderInsuranceRequest.getInsNum() + "</>로 수정하였습니다.\n";
//        }
//        if (bePresent(topDetail.getBankInfo()) && bePresent(addUpdateRiderInsuranceRequest.getBankInfoDto())) {
//            if (bePresent(topDetail.getBankInfo().getBankName()) && bePresent(addUpdateRiderInsuranceRequest.getBankInfoDto().getBankName()) && !topDetail.getBankInfo().getBankName().equals(addUpdateRiderInsuranceRequest.getBankInfoDto().getBankName())) {
//                change += dtlCnt + "번째 은행사를 <>" + topDetail.getBankInfo().getBankName() + "</>에서 <>" + addUpdateRiderInsuranceRequest.getBankInfoDto().getBankName() + "</>로 수정하였습니다.\n";
//            } else if (bePresent(topDetail.getBankInfo().getBankName()) && !bePresent(addUpdateRiderInsuranceRequest.getBankInfoDto().getBankName())) {
//                change += dtlCnt + "번째 은행사를 <>" + topDetail.getBankInfo().getBankName() + "</>에서 삭제하였습니다.\n";
//            } else if (!bePresent(topDetail.getBankInfo().getBankName()) && bePresent(addUpdateRiderInsuranceRequest.getBankInfoDto().getBankName())) {
//                change += dtlCnt + "번째 은행사를 <>" + addUpdateRiderInsuranceRequest.getBankInfoDto().getBankName() + "</>로 입력하였습니다.\n";
//            }
//            if (bePresent(topDetail.getBankInfo().getAccountNumber()) && bePresent(addUpdateRiderInsuranceRequest.getBankInfoDto().getAccountNumber()) && !topDetail.getBankInfo().getAccountNumber().equals(addUpdateRiderInsuranceRequest.getBankInfoDto().getAccountNumber())) {
//                change += dtlCnt + "번째 계좌번호를 <>" + topDetail.getBankInfo().getAccountNumber() + "</>에서 <>" + addUpdateRiderInsuranceRequest.getBankInfoDto().getAccountNumber() + "</>로 수정하였습니다.\n";
//            } else if (bePresent(topDetail.getBankInfo().getAccountNumber()) && !bePresent(addUpdateRiderInsuranceRequest.getBankInfoDto().getAccountNumber())) {
//                change += dtlCnt + "번째 계좌번호를 <>" + topDetail.getBankInfo().getAccountNumber() + "</>에서 삭제하였습니다.\n";
//            } else if (!bePresent(topDetail.getBankInfo().getAccountNumber()) && bePresent(addUpdateRiderInsuranceRequest.getBankInfoDto().getAccountNumber())) {
//                change += dtlCnt + "번째 계좌번호를 <>" + addUpdateRiderInsuranceRequest.getBankInfoDto().getAccountNumber() + "</>로 입력하였습니다.\n";
//            }
//            if (bePresent(topDetail.getBankInfo().getName()) && bePresent(addUpdateRiderInsuranceRequest.getBankInfoDto().getName()) && !topDetail.getBankInfo().getName().equals(addUpdateRiderInsuranceRequest.getBankInfoDto().getName())) {
//                change += dtlCnt + "번째 게좌명을 <>" + topDetail.getBankInfo().getName() + "</>에서 <>" + addUpdateRiderInsuranceRequest.getBankInfoDto().getName() + "</>로 수정하였습니다.\n";
//            } else if (bePresent(topDetail.getBankInfo().getName()) && !bePresent(addUpdateRiderInsuranceRequest.getBankInfoDto().getName())) {
//                change += dtlCnt + "번째 게좌명을 <>" + topDetail.getBankInfo().getName() + "</>에서 삭제하였습니다.\n";
//            } else if (!bePresent(topDetail.getBankInfo().getName()) && bePresent(addUpdateRiderInsuranceRequest.getBankInfoDto().getName())) {
//                change += dtlCnt + "번째 게좌명을 <>" + addUpdateRiderInsuranceRequest.getBankInfoDto().getName() + "</>로 입력하였습니다.\n";
//            }
//        }
//        if (!topDetail.getUsageTypeCode().equals(addUpdateRiderInsuranceRequest.getUsage())) {
//            change += dtlCnt + "번째 계약서 용도를 <>" + topDetail.getUsageTypes() + "</>에서 <>" + InsCompanyTypes.getCompanyType(addUpdateRiderInsuranceRequest.getUsage()) + "</>로 수정하였습니다.\n";
//        }
//        if (!topDetail.getAdditionalStandardTypeCode().equals(addUpdateRiderInsuranceRequest.getAdditionalStandard())) {
//            change += dtlCnt + "번째 할증 기준금액을 <>" + topDetail.getUsageTypeCode() + " 만원</>에서 <>" + addUpdateRiderInsuranceRequest.getAdditionalStandard() + " 만원</>으로 수정하였습니다.\n";
//        }
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//        if (!topDetail.getStartDt().equals(addUpdateRiderInsuranceRequest.getStartDt())) {
//            change += dtlCnt + "번째 계약 시작 날짜를 <>" + topDetail.getStartDt().format(formatter) + "</>에서 <>" + addUpdateRiderInsuranceRequest.getStartDt().format(formatter) + "</>로 수정하였습니다.\n";
//        }
//        if (!topDetail.getEndDt().equals(addUpdateRiderInsuranceRequest.getEndDt())) {
//            change += dtlCnt + "번째 계약 종료 날짜를 <>" + topDetail.getEndDt().format(formatter) + "</>에서 <>" + addUpdateRiderInsuranceRequest.getEndDt().format(formatter) + "</>로 수정하였습니다.\n";
//        }
//        if (bePresent(topDetail.getInsFee()) && bePresent(addUpdateRiderInsuranceRequest.getInsFee()) && topDetail.getInsFee() != addUpdateRiderInsuranceRequest.getInsFee()) {
//            change += dtlCnt + "번째 계약 금액을 <>" + Utils.getCurrencyFormat(topDetail.getInsFee()) + "원</>에서 <>" + Utils.getCurrencyFormat(addUpdateRiderInsuranceRequest.getInsFee()) + "원</>으로 수정하였습니다.\n";
//        }
//        if (bePresent(topDetail.getLiabilityMan()) && bePresent(addUpdateRiderInsuranceRequest.getLiabilityMan()) && !topDetail.getLiabilityMan().equals(addUpdateRiderInsuranceRequest.getLiabilityMan())) {
//            change += dtlCnt + "번째 대인 금액을 <>" + Utils.getCurrencyFormat(topDetail.getLiabilityMan()) + "원</>에서 <>" + Utils.getCurrencyFormat(addUpdateRiderInsuranceRequest.getLiabilityMan()) + "원</>으로 수정하였습니다.\n";
//        }
//        if (bePresent(topDetail.getLiabilityCar()) && bePresent(addUpdateRiderInsuranceRequest.getLiabilityCar()) && topDetail.getInsFee() != addUpdateRiderInsuranceRequest.getLiabilityCar()) {
//            change += dtlCnt + "번째 대물 금액을 <>" + Utils.getCurrencyFormat(topDetail.getLiabilityCar()) + "원</>에서 <>" + Utils.getCurrencyFormat(addUpdateRiderInsuranceRequest.getLiabilityCar()) + "원</>으로 수정하였습니다.\n";
//        }
//        if (bePresent(topDetail.getLiabilityMan2()) && bePresent(addUpdateRiderInsuranceRequest.getLiabilityMan2()) && topDetail.getLiabilityMan2() != addUpdateRiderInsuranceRequest.getLiabilityMan2()) {
//            change += dtlCnt + "번째 대인2 금액을 <>" + Utils.getCurrencyFormat(topDetail.getLiabilityMan2()) + "원</>에서 <>" + Utils.getCurrencyFormat(addUpdateRiderInsuranceRequest.getLiabilityMan2()) + "원</>으로 수정하였습니다.\n";
//        }
//        if (bePresent(topDetail.getNoInsCover()) && bePresent(addUpdateRiderInsuranceRequest.getNoInsuranceCover()) && topDetail.getNoInsCover() != addUpdateRiderInsuranceRequest.getNoInsuranceCover()) {
//            change += dtlCnt + "번째 무보험 상해 보험 금액을 <>" + Utils.getCurrencyFormat(topDetail.getNoInsCover()) + "원</>에서 <>" + Utils.getCurrencyFormat(addUpdateRiderInsuranceRequest.getNoInsuranceCover()) + "원</>으로 수정하였습니다.\n";
//        }
//        if (bePresent(topDetail.getSelfCoverMan()) && bePresent(addUpdateRiderInsuranceRequest.getSelfCoverMan()) && topDetail.getSelfCoverMan() != addUpdateRiderInsuranceRequest.getSelfCoverMan()) {
//            change += dtlCnt + "번째 자손 금액을 <>" + Utils.getCurrencyFormat(topDetail.getSelfCoverMan()) + "원</>에서 <>" + Utils.getCurrencyFormat(addUpdateRiderInsuranceRequest.getSelfCoverMan()) + "원</>으로 수정하였습니다.\n";
//        }
//        if (bePresent(topDetail.getSelfCoverCar()) && bePresent(addUpdateRiderInsuranceRequest.getSelfCoverCar()) && topDetail.getSelfCoverCar() != addUpdateRiderInsuranceRequest.getSelfCoverCar()) {
//            change += dtlCnt + "번째 자차 금액을 <>" + Utils.getCurrencyFormat(topDetail.getSelfCoverCar()) + "원</>에서 <>" + Utils.getCurrencyFormat(addUpdateRiderInsuranceRequest.getSelfCoverCar()) + "원</>으로 수정하였습니다.\n";
//        }
        return change;
    }

    @Transactional
    public BikeSessionRequest renewInsurance(BikeSessionRequest request) {
        String riderInsId = (String) request.getParam().get("rider_ins_id");
        UpdateRiderInsuranceDtlRequest addUpdateRiderInsuranceRequest = map(request.getParam(), UpdateRiderInsuranceDtlRequest.class);
        RiderInsurances riderInsurances = riderInsuranceRepository.findByRiderInsId(riderInsId);
        RiderInsurancesDtl insurancesDtl = new RiderInsurancesDtl();
        insurancesDtl.setRiderInsNo(riderInsurances.getRiderInsNo());
        insurancesDtl.setInsCompany(InsCompanyTypes.getCompanyType(addUpdateRiderInsuranceRequest.getInsCompany()));
        insurancesDtl.setInsNum(addUpdateRiderInsuranceRequest.getInsNum());
        insurancesDtl.setInsRangeType(InsRangeTypes.getType(addUpdateRiderInsuranceRequest.getInsRange()));
        insurancesDtl.setRiderInsuranceStatus(RiderInsuranceStatus.PENDING);
        insurancesDtl.setLiabilityMan(addUpdateRiderInsuranceRequest.getLiabilityMan());
        insurancesDtl.setLiabilityCar(addUpdateRiderInsuranceRequest.getLiabilityCar());
        insurancesDtl.setLiabilityMan2(addUpdateRiderInsuranceRequest.getLiabilityMan2());
        insurancesDtl.setSelfCoverMan(addUpdateRiderInsuranceRequest.getSelfCoverMan());
        insurancesDtl.setSelfCoverCar(addUpdateRiderInsuranceRequest.getSelfCoverCar());
        insurancesDtl.setNoInsCover(addUpdateRiderInsuranceRequest.getNoInsuranceCover());
        insurancesDtl.setRiderInsuranceStatus(RiderInsuranceStatus.PENDING);
        if (bePresent(addUpdateRiderInsuranceRequest.getBankInfoDto()))
            insurancesDtl.setBankInfo(addUpdateRiderInsuranceRequest.getBankInfoDto());
        insurancesDtl.setUsageTypes(UsageTypes.getType(addUpdateRiderInsuranceRequest.getUsage()));
        insurancesDtl.setAge(InsAgeTypes.getAge(addUpdateRiderInsuranceRequest.getAge()));
        insurancesDtl.setAdditionalStandardTypes(AdditionalStandardTypes.getType(addUpdateRiderInsuranceRequest.getAdditionalStandard()));
        insurancesDtl.setStartDt(addUpdateRiderInsuranceRequest.getStartDt());
        insurancesDtl.setEndDt(addUpdateRiderInsuranceRequest.getEndDt());
        insurancesDtl.setInsFee(addUpdateRiderInsuranceRequest.getInsFee());
        riderInsuranceDtlRepository.save(insurancesDtl);
        return request;
    }

    @Transactional
    public BikeSessionRequest confirmInsurance(BikeSessionRequest request) {
        String riderInsId = (String) request.getParam().get("rider_ins_id");
        RiderInsurancesDtl topDtl = riderInsuranceDtlRepository.findTopByRiderInsurances_RiderInsIdOrderByDtlNoDesc(riderInsId);
        if (topDtl.getRiderInsuranceStatus() == RiderInsuranceStatus.COMPLETE)
            withException("");
        topDtl.setRiderInsuranceStatus(RiderInsuranceStatus.COMPLETE);
        riderInsuranceDtlRepository.save(topDtl);
        return request;
    }

    public BikeSessionRequest generatePresignedUrl(BikeSessionRequest request) {
        String filename = (String) request.getParam().get("filename");
        String name = filename.substring(0, filename.lastIndexOf("."));
        String extension = filename.substring(filename.lastIndexOf(".") + 1);
        PresignedURLVo presignedURLVo = commonWorker.generatePreSignedUrl(name, extension);
        request.setResponse(presignedURLVo);
        return request;
    }

    @Transactional
    public BikeSessionRequest stopInsurance(BikeSessionRequest request) {
        String riderInsId = (String) request.getParam().get("rider_ins_id");
        RiderInsurancesDtl topDtl = riderInsuranceDtlRepository.findTopByRiderInsurances_RiderInsIdOrderByDtlNoDesc(riderInsId);
        if (topDtl.getRiderInsuranceStatus() != RiderInsuranceStatus.COMPLETE)
            withException("");
        topDtl.setStopDt(LocalDateTime.now());
        riderInsuranceDtlRepository.save(topDtl);
        return request;
    }
}