package helmet.bikelab.apiserver.services.leases;

import helmet.bikelab.apiserver.domain.embeds.ModelTransaction;
import helmet.bikelab.apiserver.domain.lease.LeaseExpense;
import helmet.bikelab.apiserver.domain.lease.Leases;
import helmet.bikelab.apiserver.domain.types.ExpenseTypes;
import helmet.bikelab.apiserver.objects.BikeSessionRequest;
import helmet.bikelab.apiserver.objects.bikelabs.leases.ExpenseDto;
import helmet.bikelab.apiserver.repositories.LeaseExpenseRepository;
import helmet.bikelab.apiserver.repositories.LeaseRepository;
import helmet.bikelab.apiserver.services.bikes.BikesService;
import helmet.bikelab.apiserver.services.internal.SessService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class LeaseExpenseService extends SessService {
    private final LeaseExpenseRepository leaseExpenseRepository;
    private final LeaseRepository leaseRepository;
    private final BikesService bikesService;

    public BikeSessionRequest fetchLeaseExpenses(BikeSessionRequest request){
        Map param = request.getParam();
        String leaseId = (String) param.get("lease_id");
        List<LeaseExpense> leaseExpenseList = leaseExpenseRepository.findAllByLease_LeaseId(leaseId);
        List<ExpenseDto> expenseDtos = new ArrayList<>();
        for(LeaseExpense le : leaseExpenseList){
            ExpenseDto expenseDto = new ExpenseDto();
            expenseDto.setExpenseNo(le.getExpenseNo());
            expenseDto.setExpenseType(le.getExpenseTypes().getType());
            expenseDto.setCompanyName(le.getTransaction().getCompanyName());
            expenseDto.setPrice(le.getTransaction().getPrice());
            expenseDto.setNumber(le.getNumber());
            expenseDto.setRegNum(le.getTransaction().getRegNum());
            expenseDto.setDescription(le.getDescription());
            expenseDtos.add(expenseDto);
        }
        request.setResponse(expenseDtos);
        return request;
    }

    public BikeSessionRequest addLeaseExpense(BikeSessionRequest request){
        Map param = request.getParam();
        String leaseId = (String) param.get("lease_id");
        ExpenseDto expenseDto = map(param, ExpenseDto.class);
        if(expenseDto.getExpenseType().equals("0") || expenseDto.getExpenseType().equals("6")) withException("870-001");
        Leases lease = leaseRepository.findByLeaseId(leaseId);
        LeaseExpense leaseExpense = new LeaseExpense();
        leaseExpense.setLeaseNo(lease.getLeaseNo());
        leaseExpense.setExpenseTypes(ExpenseTypes.getType(expenseDto.getExpenseType()));
        leaseExpense.setNumber(expenseDto.getNumber());
        leaseExpense.setDescription(expenseDto.getDescription());
        ModelTransaction modelTransaction = new ModelTransaction();
        modelTransaction.setCompanyName(expenseDto.getCompanyName());
        modelTransaction.setPrice(expenseDto.getPrice());
        modelTransaction.setRegNum(expenseDto.getRegNum());
        leaseExpense.setTransaction(modelTransaction);
        leaseExpenseRepository.save(leaseExpense);
        return request;
    }
    public BikeSessionRequest updateLeaseExpense(BikeSessionRequest request){
        Map param = request.getParam();
        ExpenseDto expenseDto = map(param, ExpenseDto.class);
        if(expenseDto.getExpenseType().equals("0") || expenseDto.getExpenseType().equals("6")) withException("870-002");
        LeaseExpense leaseExpense = leaseExpenseRepository.findById(expenseDto.getExpenseNo()).get();
        leaseExpense.setExpenseTypes(ExpenseTypes.getType(expenseDto.getExpenseType()));
        leaseExpense.setNumber(expenseDto.getNumber());
        leaseExpense.setDescription(expenseDto.getDescription());
        ModelTransaction modelTransaction = new ModelTransaction();
        modelTransaction.setCompanyName(expenseDto.getCompanyName());
        modelTransaction.setPrice(expenseDto.getPrice());
        modelTransaction.setRegNum(expenseDto.getRegNum());
        leaseExpense.setTransaction(modelTransaction);
        leaseExpenseRepository.save(leaseExpense);
        return request;
    }
    public BikeSessionRequest deleteLeaseExpense(BikeSessionRequest request){
        leaseExpenseRepository.deleteById(Long.parseLong((String) request.getParam().get("expense_no")));
        return request;
    }

}
