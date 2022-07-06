package helmet.bikelab.apiserver.workers;

import helmet.bikelab.apiserver.domain.SectionAxis;
import helmet.bikelab.apiserver.domain.Sections;
import helmet.bikelab.apiserver.repositories.SectionAxisRepository;
import helmet.bikelab.apiserver.repositories.SectionsRepository;
import helmet.bikelab.apiserver.services.internal.SessService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SectionWorker extends SessService {

    private final SectionsRepository sectionsRepository;
    private final SectionAxisRepository sectionAxisRepository;

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




}
