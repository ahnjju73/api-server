package helmet.bikelab.apiserver.services.insurance;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CopyObjectRequest;
import helmet.bikelab.apiserver.domain.embeds.ModelInsuranceImage;
import helmet.bikelab.apiserver.domain.embeds.ModelPassword;
import helmet.bikelab.apiserver.domain.ins_companies.InsuranceCompanies;
import helmet.bikelab.apiserver.domain.ins_companies.InsuranceCompanyPasswords;
import helmet.bikelab.apiserver.domain.types.Chasoo;
import helmet.bikelab.apiserver.domain.types.InsCompanyTypes;
import helmet.bikelab.apiserver.domain.types.InsuranceCompanyStatusTypes;
import helmet.bikelab.apiserver.domain.types.RoleTypes;
import helmet.bikelab.apiserver.objects.BikeSessionRequest;
import helmet.bikelab.apiserver.objects.PresignedURLVo;
import helmet.bikelab.apiserver.objects.requests.AddUpdateInsuranceCompanyRequest;
import helmet.bikelab.apiserver.objects.requests.PageableRequest;
import helmet.bikelab.apiserver.repositories.InsuranceCompanyPasswordRepository;
import helmet.bikelab.apiserver.repositories.InsuranceCompanyRepository;
import helmet.bikelab.apiserver.repositories.InsuranceCompanySessionRepository;
import helmet.bikelab.apiserver.services.internal.SessService;
import helmet.bikelab.apiserver.utils.AutoKey;
import helmet.bikelab.apiserver.utils.amazon.AmazonUtils;
import helmet.bikelab.apiserver.utils.keys.ENV;
import helmet.bikelab.apiserver.workers.CommonWorker;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InsuranceCompanyService extends SessService {
    private final InsuranceCompanyRepository insuranceCompanyRepository;
    private final InsuranceCompanyPasswordRepository insuranceCompanyPasswordRepository;
    private final CommonWorker commonWorker;
    private final AutoKey autoKey;

    @Transactional
    public BikeSessionRequest addCompany(BikeSessionRequest request) {
        Map param = request.getParam();
        AddUpdateInsuranceCompanyRequest addUpdateInsuranceCompanyRequest = map(param, AddUpdateInsuranceCompanyRequest.class);
        addUpdateInsuranceCompanyRequest.validationCheck();
        InsuranceCompanies insuranceCompanies = new InsuranceCompanies();
        String companyId = autoKey.makeGetKey("ins_company");
        insuranceCompanies.setCompanyId(companyId);
        insuranceCompanies.setName(addUpdateInsuranceCompanyRequest.getName());
        insuranceCompanies.setEmail(addUpdateInsuranceCompanyRequest.getEmail());
        insuranceCompanies.setPhone(addUpdateInsuranceCompanyRequest.getPhone());
        insuranceCompanies.setCompanyId(addUpdateInsuranceCompanyRequest.getId());
        insuranceCompanies.setCompanyNameType(InsCompanyTypes.getCompanyType(addUpdateInsuranceCompanyRequest.getCompanyName()));
        insuranceCompanies.setRoleType(RoleTypes.getRole(addUpdateInsuranceCompanyRequest.getRole()));
        insuranceCompanies.setDeptNm(addUpdateInsuranceCompanyRequest.getDeptName());
        insuranceCompanies.setDeptCenter(addUpdateInsuranceCompanyRequest.getDeptCenter());
        insuranceCompanies.setPosition(addUpdateInsuranceCompanyRequest.getPosition());
        insuranceCompanies.setPositionRole(addUpdateInsuranceCompanyRequest.getPositionRole());
        insuranceCompanies.setChasooStatus(Chasoo.getStatus(addUpdateInsuranceCompanyRequest.getChasoo()));
        //logo
        List<ModelInsuranceImage> collect = addUpdateInsuranceCompanyRequest
                .getImages()
                .stream().map(presignedURLVo -> {
                    AmazonS3 amazonS3 = AmazonUtils.amazonS3();
                    String fileKey = "ins-company/logos/" + insuranceCompanies.getName() + "/" + presignedURLVo.getFileKey();
                    CopyObjectRequest objectRequest = new CopyObjectRequest(presignedURLVo.getBucket(), presignedURLVo.getFileKey(), ENV.AWS_S3_ORIGIN_BUCKET, fileKey);
                    amazonS3.copyObject(objectRequest);
                    ModelInsuranceImage insLogos = new ModelInsuranceImage();
                    insLogos.setFileName(presignedURLVo.getFilename());
                    insLogos.setUri("/" + fileKey);
                    insLogos.setDomain(ENV.AWS_S3_ORIGIN_DOMAIN);
                    return insLogos;
                }).collect(Collectors.toList());
        insuranceCompanies.setLogoImageList(collect);
        insuranceCompanyRepository.save(insuranceCompanies);
        InsuranceCompanyPasswords passwords = new InsuranceCompanyPasswords();
        passwords.setCompanyNo(insuranceCompanies.getCompanyNo());
        passwords.setCompany(insuranceCompanies);
        passwords.makePassword();
        insuranceCompanyPasswordRepository.save(passwords);
        return request;
    }

    public BikeSessionRequest generatePresignedUrl(BikeSessionRequest request){
        Map param = request.getParam();
        String fileName = (String) param.get("file_name");
        PresignedURLVo presignedURLVo = commonWorker.generatePreSignedUrl(fileName, null);
        request.setResponse(presignedURLVo);
        return request;
    }

    public BikeSessionRequest fetchInsCompanies(BikeSessionRequest request){
        Map param = request.getParam();
        String name = (String) param.get("name");
        PageableRequest pageableRequest = map(param, PageableRequest.class);
        Pageable pageable = PageRequest.of(pageableRequest.getPage(), pageableRequest.getSize(), Sort.by("companyNo").descending());
        pageable.getSort().descending();
        Page<InsuranceCompanies> result;
        if(name != null)
            result = insuranceCompanyRepository.findAllByNameContaining(name, pageable);
        else
            result = insuranceCompanyRepository.findAll(pageable);
        request.setResponse(result);
        return request;
    }

    public BikeSessionRequest fetchInsCompanyDetail(BikeSessionRequest request){
        Map param = request.getParam();
        String companyId = (String) param.get("company_id");
        InsuranceCompanies byCompanyId = insuranceCompanyRepository.findByCompanyId(companyId);
        request.setResponse(byCompanyId);
        return request;
    }

    @Transactional
    public BikeSessionRequest updateCompany(BikeSessionRequest request) {
        Map param = request.getParam();
        AddUpdateInsuranceCompanyRequest addUpdateInsuranceCompanyRequest = map(param, AddUpdateInsuranceCompanyRequest.class);
        addUpdateInsuranceCompanyRequest.validationCheck();
        String companyId = (String) param.get("company_id");
        if(bePresent(companyId))
            withException("");
        InsuranceCompanies insuranceCompanies = insuranceCompanyRepository.findByCompanyId(companyId);
        insuranceCompanies.setName(addUpdateInsuranceCompanyRequest.getName());
        insuranceCompanies.setEmail(addUpdateInsuranceCompanyRequest.getEmail());
        insuranceCompanies.setPhone(addUpdateInsuranceCompanyRequest.getPhone());
        insuranceCompanies.setCompanyNameType(InsCompanyTypes.getCompanyType(addUpdateInsuranceCompanyRequest.getCompanyName()));
        insuranceCompanies.setRoleType(RoleTypes.getRole(addUpdateInsuranceCompanyRequest.getRole()));
        insuranceCompanies.setDeptNm(addUpdateInsuranceCompanyRequest.getDeptName());
        insuranceCompanies.setDeptCenter(addUpdateInsuranceCompanyRequest.getDeptCenter());
        insuranceCompanies.setPosition(addUpdateInsuranceCompanyRequest.getPosition());
        insuranceCompanies.setPositionRole(addUpdateInsuranceCompanyRequest.getPositionRole());
        insuranceCompanies.setChasooStatus(Chasoo.getStatus(addUpdateInsuranceCompanyRequest.getChasoo()));
        //logo
        List<ModelInsuranceImage> logoImageList = insuranceCompanies.getLogoImageList() == null ? new ArrayList<>() : insuranceCompanies.getLogoImageList();
        AmazonS3 amazonS3 = AmazonUtils.amazonS3();
        for (ModelInsuranceImage logo : logoImageList)
            amazonS3.deleteObject(ENV.AWS_S3_ORIGIN_BUCKET, logo.getUri());
        List<PresignedURLVo> images = addUpdateInsuranceCompanyRequest.getImages();
        for(PresignedURLVo img : images){
            String fileKey = "ins-company/logos/" + insuranceCompanies.getName() + "/" + img.getFileKey();
            CopyObjectRequest objectRequest = new CopyObjectRequest(img.getBucket(), img.getFileKey(), ENV.AWS_S3_ORIGIN_BUCKET, fileKey);
            amazonS3.copyObject(objectRequest);
            ModelInsuranceImage insLogos = new ModelInsuranceImage();
            insLogos.setFileName(img.getFilename());
            insLogos.setUri("/" + fileKey);
            insLogos.setDomain(ENV.AWS_S3_ORIGIN_DOMAIN);
            logoImageList.add(insLogos);
        }
        insuranceCompanyRepository.save(insuranceCompanies);
        return request;
    }

    @Transactional
    public BikeSessionRequest resetPassword(BikeSessionRequest request){
        Map param = request.getParam();
        String companyId = (String) param.get("company_id");
        String pw = getRandomString();
        InsuranceCompanyPasswords password = insuranceCompanyPasswordRepository.findByCompany_CompanyId(companyId);
        ModelPassword modelPassword = password.getModelPassword();
        modelPassword.modifyPasswordWithoutSHA256(pw);
        password.setModelPassword(modelPassword);
        insuranceCompanyPasswordRepository.save(password);
        Map res = new HashMap();
        res.put("password", pw);
        request.setResponse(res);
        return request;
    }

    private String getRandomString(){
        char[] tmp = new char[10];
        for(int i=0; i<tmp.length; i++) {
            boolean div = Math.random()*2<1;
            if(div) {
                tmp[i] = (char) (Math.random() * 10 + '0') ;
            }else {
                tmp[i] = (char) (Math.random() * 26 + 'A') ;
            }
        }
        return new String(tmp);
    }
}
