package helmet.bikelab.apiserver.services;

import helmet.bikelab.apiserver.repositories.LeaseExtraRepository;
import helmet.bikelab.apiserver.repositories.LeaseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LeaseExtraService {
    private final LeaseExtraRepository leaseExtraRepository;
    private final LeaseRepository leaseRepository;



}
