package helmet.bikelab.apiserver.workers;

import helmet.bikelab.apiserver.domain.SectionAxis;
import helmet.bikelab.apiserver.domain.SectionAxisParts;
import helmet.bikelab.apiserver.domain.Sections;
import helmet.bikelab.apiserver.domain.bike.Parts;
import helmet.bikelab.apiserver.objects.responses.FetchSections;
import helmet.bikelab.apiserver.objects.responses.SectionDetailResponse;
import helmet.bikelab.apiserver.repositories.SectionAxisPartsRepository;
import helmet.bikelab.apiserver.repositories.SectionAxisRepository;
import helmet.bikelab.apiserver.repositories.SectionsRepository;
import helmet.bikelab.apiserver.services.internal.SessService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SectionWorker extends SessService {

    private final SectionsRepository sectionsRepository;
    private final SectionAxisRepository sectionAxisRepository;
    private final SectionAxisPartsRepository sectionAxisPartsRepository;

    public Sections getSectionById(Integer sectionNo){
        Sections bySectionNo = sectionsRepository.findBySectionNo(sectionNo);
        if(!bePresent(bySectionNo)) withException("506-001");
        return bySectionNo;
    }

    public SectionAxis getSectionAxisBySectionNoAndAxisNo(Integer sectionNo, Integer axisNo){
        SectionAxis bySectionNoAndAxisNo = sectionAxisRepository.findBySectionNoAndAxisNo(sectionNo, axisNo);
        if(!bePresent(bySectionNoAndAxisNo)) withException("506-002");
        return bySectionNoAndAxisNo;
    }

    public SectionAxis getSectionAxisByAxisNo(Integer axisNo){
        SectionAxis bySectionNoAndAxisNo = sectionAxisRepository.findByAxisNo(axisNo);
        if(!bePresent(bySectionNoAndAxisNo)) withException("506-002");
        return bySectionNoAndAxisNo;
    }

    public List<FetchSections> getSectionsByBikeModel(String bikeModel){
        List<Sections> allByCarModel_code = sectionsRepository.findAllByCarModel_Code(bikeModel);
        List<FetchSections> sections = new ArrayList<>();
        for(Sections section : allByCarModel_code){
            FetchSections fetchSections = new FetchSections();
            fetchSections.setPartsCnt(sectionAxisPartsRepository.countAllBySectionNo(section.getSectionNo()));
            fetchSections.setSections(section);
            sections.add(fetchSections);
        }
        return sections;
    }

    public SectionDetailResponse fetchSectionDetail(Integer sectionNo){
        Sections bySectionNo = sectionsRepository.findBySectionNo(sectionNo);
        List<SectionAxis> allBySectionNo = sectionAxisRepository.findAllBySectionNo(sectionNo);
        SectionDetailResponse sectionDetailResponse = new SectionDetailResponse();
        sectionDetailResponse.setSection(bySectionNo);
        sectionDetailResponse.setSectionAxisList(allBySectionNo);
        return sectionDetailResponse;
    }

    public List<Parts> getPartsBySectionAxis(Integer axisNo){
        SectionAxis byAxisNo = sectionAxisRepository.findByAxisNo(axisNo);
        if(!bePresent(byAxisNo))
            withException("");
        List<SectionAxisParts> allByAxisNo = sectionAxisPartsRepository.findAllByAxisNo(byAxisNo.getAxisNo());
        List<Parts> parts = new ArrayList<>();
        for(SectionAxisParts sap : allByAxisNo){
            parts.add(sap.getPart());
        }
        return parts;
    }

    public void deleteSectionAxis(Integer axisNo){
        SectionAxis byAxisNo = sectionAxisRepository.findByAxisNo(axisNo);
        if(!bePresent(byAxisNo))
            withException("");
        sectionAxisPartsRepository.deleteAllByAxisNo(byAxisNo.getAxisNo());
        sectionAxisRepository.deleteByAxisNo(byAxisNo.getAxisNo());
    }

    public void deleteSection(Integer sectionNo){
        Sections sections = sectionsRepository.findBySectionNo(sectionNo);
        if(!bePresent(sections))
            withException("");
        List<SectionAxis> allBySectionNo = sectionAxisRepository.findAllBySectionNo(sectionNo);
        for(int i = 0; i < allBySectionNo.size(); i++){
            Integer axisNo = allBySectionNo.get(i).getAxisNo();
            SectionAxis byAxisNo = sectionAxisRepository.findByAxisNo(axisNo);
            sectionAxisPartsRepository.deleteAllByAxisNo(byAxisNo.getAxisNo());
            sectionAxisRepository.deleteByAxisNo(byAxisNo.getAxisNo());
        }
        sectionsRepository.deleteById(sectionNo);
    }


}
