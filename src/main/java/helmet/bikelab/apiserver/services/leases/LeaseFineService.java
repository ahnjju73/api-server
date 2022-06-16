package helmet.bikelab.apiserver.services.leases;

import helmet.bikelab.apiserver.repositories.BikeUserLogRepository;
import helmet.bikelab.apiserver.repositories.LeaseExtraRepository;
import helmet.bikelab.apiserver.repositories.LeasePaymentsRepository;
import helmet.bikelab.apiserver.repositories.LeaseRepository;
import helmet.bikelab.apiserver.utils.AutoKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class LeaseFineService {

    private final LeaseExtraRepository leaseExtraRepository;
    private final LeaseRepository leaseRepository;
    private final LeasePaymentsRepository leasePaymentsRepository;
    private final BikeUserLogRepository bikeUserLogRepository;
    private final AutoKey autoKey;



}
