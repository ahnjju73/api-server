package helmet.bikelab.apiserver.services.leases;

import helmet.bikelab.apiserver.domain.embeds.ModelTransaction;
import helmet.bikelab.apiserver.domain.lease.LeaseExpense;
import helmet.bikelab.apiserver.domain.lease.Leases;
import helmet.bikelab.apiserver.domain.types.BikeUserLogTypes;
import helmet.bikelab.apiserver.domain.types.ExpenseOptionTypes;
import helmet.bikelab.apiserver.domain.types.ExpenseTypes;
import helmet.bikelab.apiserver.objects.BikeSessionRequest;
import helmet.bikelab.apiserver.objects.bikelabs.leases.ExpenseDto;
import helmet.bikelab.apiserver.repositories.BikeUserLogRepository;
import helmet.bikelab.apiserver.repositories.LeaseExpenseRepository;
import helmet.bikelab.apiserver.repositories.LeaseRepository;
import helmet.bikelab.apiserver.services.bikes.BikesService;
import helmet.bikelab.apiserver.services.internal.SessService;
import helmet.bikelab.apiserver.utils.Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static helmet.bikelab.apiserver.domain.bikelab.BikeUserLog.addLog;

@RequiredArgsConstructor
@Service
public class LeaseExpenseService extends SessService {
    private final LeaseExpenseRepository leaseExpenseRepository;
    private final LeaseRepository leaseRepository;
    private final BikesService bikesService;
    private final BikeUserLogRepository bikeUserLogRepository;

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
            expenseDto.setExpenseOptionType(le.getExpenseOptionTypes() != null ? le.getExpenseOptionTypes().getType() : ExpenseOptionTypes.OFF.getType());
            expenseDtos.add(expenseDto);
        }
        request.setResponse(expenseDtos);
        return request;
    }

    @Transactional
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
        leaseExpense.setExpenseOptionTypes(ExpenseOptionTypes.getType(expenseDto.getExpenseOptionType()));
        leaseExpenseRepository.save(leaseExpense);
        String expenseLog = getExpenseLog(expenseDto, null);
        bikeUserLogRepository.save(addLog(BikeUserLogTypes.LEASE_UPDATED, request.getSessionUser().getUserNo(), lease.getLeaseNo().toString(), expenseLog));
        return request;
    }

    @Transactional
    public BikeSessionRequest updateLeaseExpense(BikeSessionRequest request){
        Map param = request.getParam();
        ExpenseDto expenseDto = map(param, ExpenseDto.class);
        if(expenseDto.getExpenseType().equals("0")) withException("870-002");
        LeaseExpense leaseExpense = leaseExpenseRepository.findById(expenseDto.getExpenseNo()).get();
        leaseExpense.setExpenseTypes(ExpenseTypes.getType(expenseDto.getExpenseType()));
        leaseExpense.setNumber(expenseDto.getNumber());
        leaseExpense.setDescription(expenseDto.getDescription());
        ModelTransaction modelTransaction = new ModelTransaction();
        modelTransaction.setCompanyName(expenseDto.getCompanyName());
        modelTransaction.setPrice(expenseDto.getPrice());
        modelTransaction.setRegNum(expenseDto.getRegNum());
        leaseExpense.setTransaction(modelTransaction);
        leaseExpense.setExpenseOptionTypes(ExpenseOptionTypes.getType(expenseDto.getExpenseOptionType()));
        leaseExpenseRepository.save(leaseExpense);
        String expenseLog = getExpenseLog(expenseDto, leaseExpense);
        bikeUserLogRepository.save(addLog(BikeUserLogTypes.LEASE_UPDATED, request.getSessionUser().getUserNo(), leaseExpense.getLeaseNo().toString(), expenseLog));
        return request;
    }

    @Transactional
    public BikeSessionRequest deleteLeaseExpense(BikeSessionRequest request){
        leaseExpenseRepository.deleteById(Long.parseLong((String) request.getParam().get("expense_no")));
        return request;
    }

    private String getExpenseLog(ExpenseDto expense, LeaseExpense leaseExpense){
        String log = "";
        if(leaseExpense == null){
            if(expense.getExpenseType() != null && !expense.getExpenseType().equals("")){
                log += "지출항목으로 <>" + ExpenseTypes.getType(expense.getExpenseType()).getTypeName() + "</>로 설정되었습니다.\n";
            }
            if(expense.getCompanyName() != null && !expense.getCompanyName().equals("")){
                log += "구입처는 <>" + expense.getCompanyName() + "</>로 설정되었습니다.\n";
            }
            if(expense.getRegNum() != null && !expense.getRegNum().equals("")){
                log += "구입처 사업자 번호는 <>" + expense.getRegNum() + "</>로 설정되었습니다.\n";
            }
            if(expense.getPrice() != null){
                log += "지출가격이 <>" + Utils.getCurrencyFormat(expense.getPrice()) + "원</>으로 설정되었습니다.\n";
            }
            if(expense.getCompanyName() != null){
                log += "구입 갯수는 <>" + expense.getNumber() + "</>개로 설정되었습니다.\n";
            }
            if(expense.getDescription() != null && !expense.getDescription().equals("")){
                log += "상세설명이 <>\"" + expense.getDescription() + "\"</>로 입력되었습니다.\n";
            }
            if(expense.getExpenseOptionType() != null && expense.getExpenseOptionType() == ExpenseOptionTypes.ON.getType()){
                log += "실비 / 확보 물량 옵션을 켬으로 설정되었습니다.\n";
            } else{
                log += "실비 / 확보 물량 옵션을 끔으로 설정되었습니다.\n";
            }

        } else {
            if (leaseExpense.getExpenseTypes() != ExpenseTypes.getType(expense.getExpenseType())) {
                log += "지출항목이 <>" + leaseExpense.getExpenseTypes().getTypeName() + "</>에서 <>" + ExpenseTypes.getType(expense.getExpenseType()).getTypeName() + "</>로 변경하였습니다.\n";
            }

            if ((leaseExpense.getTransaction().getCompanyName() == null || leaseExpense.getTransaction().getCompanyName().equals(""))) {
                if (expense.getCompanyName() != null && !expense.getCompanyName().equals("")) {
                    log += "구입처는 <>" + expense.getCompanyName() + "</>로 설정되었습니다.\n";
                }
            } else {
                if (expense.getCompanyName() != null && !expense.getCompanyName().equals("")) {
                    log += "구입처는 <>" + leaseExpense.getTransaction().getCompanyName() + "</>에서 <>" + expense.getCompanyName() + "</>으로 변경되었습니다.\n";
                } else {
                    log += "구입처 정보를 삭제하였습니다.";
                }
            }
            if ((leaseExpense.getTransaction().getRegNum() == null || leaseExpense.getTransaction().getRegNum().equals(""))) {
                if (expense.getRegNum() != null && !expense.getRegNum().equals("")) {
                    log += "구입처 사업자 번호는 <>" + expense.getRegNum() + "</>로 설정되었습니다.\n";
                }
            } else {
                if (expense.getRegNum() != null && !expense.getRegNum().equals("")) {
                    log += "구입처 사업자 번호가 <>" + leaseExpense.getTransaction().getRegNum() + "</>에서 <>" + expense.getRegNum() + "</>으로 변경되었습니다.\n";
                } else {
                    log += "구입처 사업자 번호 정보를 삭제하였습니다.\n";
                }
            }
            if (leaseExpense.getTransaction().getPrice() != expense.getPrice()) {
                log += "지출가격이 <>" + Utils.getCurrencyFormat(leaseExpense.getTransaction().getPrice()) + "원</>에서 <>" + Utils.getCurrencyFormat(expense.getPrice()) + "원</>으로 변경되었습니다.\n";
            }
            if (leaseExpense.getTransaction().getPrice() != expense.getPrice()) {
                log += "구입 갯수가 <>" + leaseExpense.getNumber() + "</>개에서 <>" + expense.getNumber() + "</>개로 변경되었습니다.\n";
            }
            if ((leaseExpense.getDescription() == null || leaseExpense.getDescription().equals(""))) {
                if (expense.getDescription() != null && !expense.getDescription().equals("")) {
                    log += "상세 설명이 <>\"" + expense.getDescription() + "\"</>로 설정되었습니다.\n";
                }
            } else {
                if (expense.getCompanyName() != null && !expense.getCompanyName().equals("")) {
                    log += "상세 설명이 <>" + leaseExpense.getDescription() + "</>에서 <>" + expense.getDescription() + "</>으로 변경되었습니다.\n";
                } else {
                    log += "상세 설명을 삭제하였습니다.\n";
                }
            }
        }
        return log;
    }

    @Transactional
    public BikeSessionRequest changeExpenseOption(BikeSessionRequest request){
        Map param = request.getParam();
        Integer expenseNo = (Integer)param.get("expense_no");
        LeaseExpense leaseExpense = leaseExpenseRepository.findById(expenseNo.longValue()).get();
        String expenseLog;
        if(leaseExpense.getExpenseOptionTypes() == ExpenseOptionTypes.ON)
            expenseLog = "실비 / 확보 물량 옵션을 끔으로 변경되었습니다";
        else
            expenseLog = "실비 / 확보 물량 옵션을 켬으로 변견되었습니다";
        leaseExpense.setExpenseOptionTypes((leaseExpense.getExpenseOptionTypes() != ExpenseOptionTypes.ON ? ExpenseOptionTypes.ON : ExpenseOptionTypes.OFF));
        leaseExpenseRepository.save(leaseExpense);
        bikeUserLogRepository.save(addLog(BikeUserLogTypes.LEASE_UPDATED, request.getSessionUser().getUserNo(), leaseExpense.getLeaseNo().toString(), expenseLog));
        return request;
    }
}
