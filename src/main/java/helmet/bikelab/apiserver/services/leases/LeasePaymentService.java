package helmet.bikelab.apiserver.services.leases;

import helmet.bikelab.apiserver.domain.bike.Bikes;
import helmet.bikelab.apiserver.domain.bikelab.BikeUser;
import helmet.bikelab.apiserver.domain.client.ClientOverpay;
import helmet.bikelab.apiserver.domain.client.Clients;
import helmet.bikelab.apiserver.domain.lease.*;
import helmet.bikelab.apiserver.domain.types.BikeUserLogTypes;
import helmet.bikelab.apiserver.domain.types.LeaseStatusTypes;
import helmet.bikelab.apiserver.objects.BikeDto;
import helmet.bikelab.apiserver.objects.BikeSessionRequest;
import helmet.bikelab.apiserver.objects.UnpaidExcelDto;
import helmet.bikelab.apiserver.objects.bikelabs.clients.ClientDto;
import helmet.bikelab.apiserver.objects.bikelabs.leases.FetchUnpaidLeasesResponse;
import helmet.bikelab.apiserver.objects.bikelabs.leases.PayLeaseRequest;
import helmet.bikelab.apiserver.objects.bikelabs.leases.UploadExcelDto;
import helmet.bikelab.apiserver.repositories.*;
import helmet.bikelab.apiserver.objects.requests.LeasePaymentsRequestListDto;
import helmet.bikelab.apiserver.objects.responses.ResponseListDto;
import helmet.bikelab.apiserver.services.internal.SessService;
import helmet.bikelab.apiserver.utils.AutoKey;
import helmet.bikelab.apiserver.utils.Utils;
import helmet.bikelab.apiserver.workers.CommonWorker;
import helmet.bikelab.apiserver.workers.LeasePaymentWorker;
import lombok.RequiredArgsConstructor;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static helmet.bikelab.apiserver.domain.bikelab.BikeUserLog.addLog;

@Service
@RequiredArgsConstructor
public class LeasePaymentService  extends SessService {

    private final LeaseRepository leaseRepository;
    private final LeasePaymentsRepository leasePaymentsRepository;
    private final LeaseExtraRepository leaseExtraRepository;
    private final BikeUserLogRepository bikeUserLogRepository;
    private final ClientsRepository clientsRepository;
    private final ClientOverpayRepository clientOverpayRepository;
    private final BikesRepository bikesRepository;
    private final AutoKey autoKey;
    private final CommonWorker commonWorker;
    private final LeasePaymentWorker leasePaymentWorker;

    public BikeSessionRequest fetchLeaseExtrasGroupByClient(BikeSessionRequest request){
        Map param = request.getParam();
        LeasePaymentsRequestListDto requestListDto = map(param, LeasePaymentsRequestListDto.class);
        ResponseListDto responseListDto = commonWorker.fetchItemListByNextToken(requestListDto, "leases.leases-payments.fetchLeaseExtrasGroupByClient", "leases.leases-payments.countAllLeaseExtrasGroupByClient", "client_id");
        request.setResponse(responseListDto);
        return request;
    }

    public BikeSessionRequest fetchLeasePaymentsByClient(BikeSessionRequest request){
        Map param = request.getParam();
        LeasePaymentsRequestListDto requestListDto = map(param, LeasePaymentsRequestListDto.class);
        ResponseListDto responseListDto = commonWorker.fetchItemListByNextToken(requestListDto, "leases.leases-payments.fetchLeasePaymentsGroupByClient", "leases.leases-payments.countAllLeasePaymentsGroupByClient", "client_id");
        request.setResponse(responseListDto);
        return request;
    }

    @Transactional
    public BikeSessionRequest payLeaseExtraFeeByExtraId(BikeSessionRequest request){
        Map param = request.getParam();
        BikeUser session = request.getSessionUser();
        String extraId = (String)param.get("extra_id");
        leasePaymentWorker.readLeaseExtraFeeByExtraId(extraId, session);
//        leasePaymentWorker.payLeaseExtraFeeByExtraId(extraId, session);
        return request;
    }

    public BikeSessionRequest fetchLeasePaymentExtraByIndex(BikeSessionRequest request){
        Map param = request.getParam();
        LeasePaymentsRequestListDto requestListDto = map(param, LeasePaymentsRequestListDto.class);
        ResponseListDto responseListDto = commonWorker.fetchItemListByNextToken(requestListDto, "leases.leases-payments.fetchLeasePaymentExtraByIndex", "leases.leases-payments.countAllPaymentExtraByIndex", "rownum");
        request.setResponse(responseListDto);
        return request;
    }

    @Transactional
    public BikeSessionRequest payLeaseFeeByPaymentId(BikeSessionRequest request){
        Map param = request.getParam();
        BikeUser session = request.getSessionUser();
        String paymentId = (String)param.get("payment_id");
//        leasePaymentWorker.payLeaseFeeByPaymentId(paymentId, session);
        leasePaymentWorker.readLeaseFeeByPaymentId(paymentId, session);
        return request;
    }

    public BikeSessionRequest fetchLeasePaymentsByIndex(BikeSessionRequest request){
        Map param = request.getParam();
        LeasePaymentsRequestListDto requestListDto = map(param, LeasePaymentsRequestListDto.class);
        ResponseListDto responseListDto = commonWorker.fetchItemListByNextToken(requestListDto, "leases.leases-payments.fetchLeasePaymentsByIndex", "leases.leases-payments.countAllPaymentsByIndex", "rownum");
        request.setResponse(responseListDto);
        return request;
    }

    public BikeSessionRequest fetchUnpaidLeases(BikeSessionRequest request) {
        List<Leases> leases = leaseRepository.findAll();
        Map response = new HashMap();
        List<FetchUnpaidLeasesResponse> fetchUnpaidLeasesResponses = new ArrayList<>();
        for (Leases lease : leases) {
            if (lease.getStatus() != LeaseStatusTypes.CONFIRM)
                continue;
            List<LeasePayments> payments = leasePaymentsRepository.findAllByLease_LeaseId(lease.getLeaseId());
            int index = 0;
            int unpaidFee = 0;
            int unpaidExtraFee = 0;
            while (index < payments.size()) {
                if (!LocalDate.now().plusDays(1).isAfter(payments.get(index).getPaymentDate())) {
                    break;
                }
                index++;
            }
            for (int i = 0; i < index; i++) {
                unpaidFee += payments.get(i).getLeaseFee() - payments.get(i).getPaidFee();
                for (LeaseExtras le : leaseExtraRepository.findAllByPayment_PaymentId(payments.get(i).getPaymentId())) {
                    unpaidExtraFee += le.getExtraFee() - le.getPaidFee();
                }
            }
            if (unpaidFee > 0) {
                FetchUnpaidLeasesResponse fetchUnpaidLeasesResponse = new FetchUnpaidLeasesResponse();
                fetchUnpaidLeasesResponse.setUnpaidLeaseFee(unpaidFee);
                fetchUnpaidLeasesResponse.setLeaseId(lease.getLeaseId());
                fetchUnpaidLeasesResponse.setUnpaidExtraFee(unpaidExtraFee);
                Bikes bike = lease.getBike();
                Clients clients = lease.getClients();
                fetchUnpaidLeasesResponse.setBikeId(bike.getBikeId());
                fetchUnpaidLeasesResponse.setClientId(clients.getClientId());
                BikeDto bikeDto = new BikeDto();
                bikeDto.setBikeId(bike.getBikeId());
                bikeDto.setBikeNum(bike.getCarNum());
                bikeDto.setBikeModel(bike.getCarModelCode());
                ClientDto clientDto = new ClientDto();
                clientDto.setClientName(clients.getClientInfo().getName());
                clientDto.setClientId(clients.getClientId());
                fetchUnpaidLeasesResponse.setClient(clientDto);
                fetchUnpaidLeasesResponse.setBike(bikeDto);
                fetchUnpaidLeasesResponses.add(fetchUnpaidLeasesResponse);
            }
        }
        response.put("unpaid_leases", fetchUnpaidLeasesResponses);
        request.setResponse(response);
        return request;
    }

    @Transactional
    public BikeSessionRequest payLeaseFee(BikeSessionRequest request) {
        ArrayList<String> logList = new ArrayList<>();
        Map param = request.getParam();
        PayLeaseRequest payLeaseRequest = map(param, PayLeaseRequest.class);
        Leases lease = leaseRepository.findByLeaseId(payLeaseRequest.getLeaseId());
        if (lease.getStatus() != LeaseStatusTypes.CONFIRM)
            withException("900-001");
        int paidFee = payLeaseRequest.getPaidFee();
        List<LeasePayments> payments = leasePaymentsRepository.findAllByLease_LeaseId(payLeaseRequest.getLeaseId());
        for (int i = 0; i < payments.size() && paidFee > 0 && payments.get(i).getPaymentDate().isBefore(LocalDate.now().plusDays(1)); i++) {

            int unpaidFee = payments.get(i).getLeaseFee() - payments.get(i).getPaidFee();
            if (unpaidFee > 0) {
                if (paidFee > unpaidFee) {
                    logList.add(paymentLog(payments.get(i), unpaidFee, true));
                    payments.get(i).setPaidFee(payments.get(i).getLeaseFee());
                    paidFee -= unpaidFee;
                    leasePaymentsRepository.save(payments.get(i));
                } else {
                    logList.add(paymentLog(payments.get(i), paidFee, false));
                    payments.get(i).setPaidFee(payments.get(i).getPaidFee() + paidFee);
                    leasePaymentsRepository.save(payments.get(i));
                    paidFee = 0;
                    break;
                }
            }
            List<LeaseExtras> extras = leaseExtraRepository.findAllByPayment_PaymentId(payments.get(i).getPaymentId());
            for (LeaseExtras le : extras) {
                int unpaidExtra = le.getExtraFee() - le.getPaidFee();
                if (unpaidExtra > 0) {
                    if (unpaidExtra < paidFee) {
                        logList.add(extraPaymentLog(le, unpaidExtra, true));
                        le.setPaidFee(le.getExtraFee());
                        paidFee -= unpaidExtra;
                        leaseExtraRepository.save(le);
                    } else {
                        logList.add(extraPaymentLog(le, paidFee, false));
                        le.setPaidFee(le.getPaidFee() + paidFee);
                        leaseExtraRepository.save(le);
                        paidFee = 0;
                        break;
                    }
                }
            }
        }
        if (paidFee > 0) {
            saveOverpayLog(request.getSessionUser(), lease, paidFee);
        }
        bikeUserLogRepository.save(addLog(BikeUserLogTypes.LEASE_PAYMENT, request.getSessionUser().getUserNo(), lease.getLeaseNo().toString(), logList));
        return request;
    }

    public File unpaidExcelDownload(BikeSessionRequest request) {
        List<Leases> leases = leaseRepository.findAll();
        List<UnpaidExcelDto> unpaidExcelDtos = new ArrayList<>();

        for (Leases lease : leases) {
            if (lease.getStatus() != LeaseStatusTypes.CONFIRM)
                continue;
            List<LeasePayments> payments = leasePaymentsRepository.findAllByLease_LeaseId(lease.getLeaseId());
            int index = 0;
            int unpaidFee = 0;
            int unpaidExtraFee = 0;
            while (index < payments.size()) {
                if (!LocalDate.now().isAfter(payments.get(index).getPaymentDate())) {
                    break;
                }
                index++;
            }
            for (int i = 0; i < index; i++) {
                unpaidFee += payments.get(i).getLeaseFee() - payments.get(i).getPaidFee();
                for (LeaseExtras le : leaseExtraRepository.findAllByPayment_PaymentId(payments.get(i).getPaymentId())) {
                    unpaidExtraFee += le.getExtraFee() - le.getPaidFee();
                }
            }
            if (unpaidFee > 0 || unpaidExtraFee > 0) {
                UnpaidExcelDto unpaidExcelDto = new UnpaidExcelDto();
                unpaidExcelDto.setLeaseId(lease.getLeaseId());
                unpaidExcelDto.setBikeId(lease.getBike().getBikeId());
                unpaidExcelDto.setBikeNumber(lease.getBike().getCarNum());
                unpaidExcelDto.setVimNumber(lease.getBike().getVimNum());
                unpaidExcelDto.setClientId(lease.getClients().getClientId());
                unpaidExcelDto.setClientName(lease.getClients().getClientInfo().getName());
                unpaidExcelDto.setUnpaidFee(unpaidFee > 0 ? unpaidFee : 0);
                unpaidExcelDto.setUnpaidExtra(unpaidExtraFee > 0 ? unpaidExtraFee : 0);
                unpaidExcelDto.setUnpaidTotal(unpaidFee + unpaidExtraFee);
                unpaidExcelDtos.add(unpaidExcelDto);
            }
        }
        File newFile = new File("./newfile");
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(newFile);
            Workbook wb = new XSSFWorkbook();
            Sheet sheet = wb.createSheet("첫번째 시트");
            Row row = null;
            Cell cell = null;
            int rowNum = 0;

            // Header
            CellStyle color = wb.createCellStyle();
            color.setFillForegroundColor(HSSFColor.LIGHT_YELLOW.index);
            color.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
            row = sheet.createRow(rowNum++);
            cell = row.createCell(0);
            cell.setCellValue("리스 아이디");
            cell.setCellStyle(color);
            cell = row.createCell(1);
            cell.setCellValue("바이크 아이디");
            cell.setCellStyle(color);
            cell = row.createCell(2);
            cell.setCellValue("고객 아이디");
            cell.setCellStyle(color);
            cell = row.createCell(3);
            cell.setCellValue("바이크 고유 번호");
            cell.setCellStyle(color);
            cell = row.createCell(4);
            cell.setCellValue("바이크 번호");
            cell.setCellStyle(color);
            cell = row.createCell(5);
            cell.setCellValue("고객명");
            cell.setCellStyle(color);
            cell = row.createCell(6);
            cell.setCellValue("미수금");
            cell.setCellStyle(color);
            cell = row.createCell(7);
            cell.setCellValue("추가 미수금");
            cell.setCellStyle(color);
            cell = row.createCell(8);
            cell.setCellValue("총 미수금");
            cell.setCellStyle(color);
            cell = row.createCell(9);
            cell.setCellValue("납부금");

            // Body
            for (UnpaidExcelDto unpaidExcelDto : unpaidExcelDtos) {
                row = sheet.createRow(rowNum++);
                cell = row.createCell(0);
                cell.setCellValue(unpaidExcelDto.getLeaseId());
                cell = row.createCell(1);
                cell.setCellValue(unpaidExcelDto.getBikeId());
                cell = row.createCell(2);
                cell.setCellValue(unpaidExcelDto.getClientId());
                cell = row.createCell(3);
                cell.setCellValue(unpaidExcelDto.getVimNumber());
                cell = row.createCell(4);
                cell.setCellValue(unpaidExcelDto.getBikeNumber());
                cell = row.createCell(5);
                cell.setCellValue(unpaidExcelDto.getClientName());
                cell = row.createCell(6);
                cell.setCellValue(unpaidExcelDto.getUnpaidFee());
                cell = row.createCell(7);
                cell.setCellValue(unpaidExcelDto.getUnpaidExtra());
                cell = row.createCell(8);
                cell.setCellValue(unpaidExcelDto.getUnpaidTotal());


            }
            // Excel File Output
            wb.write(fileOutputStream);
            fileOutputStream.flush();
            return newFile;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }finally {
            if(newFile != null && newFile.exists()) newFile.delete();
        }
    }

    @Transactional
    public BikeSessionRequest payWithExcel(BikeSessionRequest request) {
        Map param = request.getParam();
        UploadExcelDto uploadExcelDto = map(param, UploadExcelDto.class);
        for (PayLeaseRequest payLeaseRequest : uploadExcelDto.getPayments()) {
            ArrayList<String> logList = new ArrayList<>();
            Leases lease = leaseRepository.findByLeaseId(payLeaseRequest.getLeaseId());
            int paidFee = payLeaseRequest.getPaidFee();
            List<LeasePayments> payments = leasePaymentsRepository.findAllByLease_LeaseId(payLeaseRequest.getLeaseId());
            for (int i = 0; i < payments.size() && payments.get(i).getPaymentDate().isBefore(LocalDate.now().plusDays(1)); i++) {
                int unpaidFee = payments.get(i).getLeaseFee() - payments.get(i).getPaidFee();
                if (unpaidFee > 0) {
                    if (paidFee > unpaidFee) {
                        logList.add(paymentLog(payments.get(i), unpaidFee, true));
                        payments.get(i).setPaidFee(payments.get(i).getLeaseFee());
                        paidFee -= unpaidFee;
                        leasePaymentsRepository.save(payments.get(i));
                    } else {
                        logList.add(paymentLog(payments.get(i), paidFee, false));
                        payments.get(i).setPaidFee(payments.get(i).getPaidFee() + paidFee);
                        leasePaymentsRepository.save(payments.get(i));
                        paidFee = 0;
                        break;
                    }
                }

                List<LeaseExtras> extras = leaseExtraRepository.findAllByPayment_PaymentId(payments.get(i).getPaymentId());
                for (LeaseExtras le : extras) {
                    int unpaidExtra = le.getExtraFee() - le.getPaidFee();
                    if (unpaidExtra > 0) {
                        if (unpaidExtra < paidFee) {
                            logList.add(extraPaymentLog(le, unpaidExtra, true));
                            le.setPaidFee(le.getExtraFee());
                            paidFee -= unpaidExtra;
                            leaseExtraRepository.save(le);
                        } else {
                            logList.add(extraPaymentLog(le, paidFee, false));
                            le.setPaidFee(le.getPaidFee() + paidFee);
                            leaseExtraRepository.save(le);
                            paidFee = 0;
                            break;
                        }
                    }
                }
            }
            if (paidFee > 0) {
                saveOverpayLog(request.getSessionUser(), lease, paidFee);
            }
            bikeUserLogRepository.save(addLog(BikeUserLogTypes.LEASE_PAYMENT, request.getSessionUser().getUserNo(), lease.getLeaseNo().toString(), logList));
        }
        return request;
    }

    @Transactional
    public BikeSessionRequest payByClient(BikeSessionRequest request){
        Map param = request.getParam();
        PayLeaseRequest payLeaseRequest = map(param, PayLeaseRequest.class);
        Clients client = clientsRepository.findByClientId(payLeaseRequest.getClientId());
        List<Leases> leaseList = leaseRepository.findAllByClients_ClientIdAndStatusOrderByLeaseInfo_ContractDate(payLeaseRequest.getClientId(), LeaseStatusTypes.CONFIRM);
        int paidFee = payLeaseRequest.getPaidFee();
        for(Leases lease : leaseList){
            List<LeasePayments> payments = leasePaymentsRepository.findAllByLeaseNo(lease.getLeaseNo());
            ArrayList<String> logList = new ArrayList<>();
            if(payLeaseRequest.getPayType().equals("lease")) {
                for (int i = 0; i < payments.size() && payments.get(i).getPaymentDate().isBefore(LocalDate.parse(payLeaseRequest.getEndDt()).plusDays(1)); i++) {
                    int unpaidFee = payments.get(i).getLeaseFee() - payments.get(i).getPaidFee();
                    payments.get(i).setRead(true);
                    if (payments.get(i).getReadUserNo() == null)
                        payments.get(i).setReadUserNo(request.getSessionUser().getUserNo());
                    if (unpaidFee > 0) {
                        if (paidFee > unpaidFee) {
                            logList.add(paymentLog(payments.get(i), unpaidFee, true));
                            payments.get(i).setPaidFee(payments.get(i).getLeaseFee());
                            paidFee -= unpaidFee;
                            leasePaymentsRepository.save(payments.get(i));
                        } else {
                            logList.add(paymentLog(payments.get(i), paidFee, false));
                            payments.get(i).setPaidFee(payments.get(i).getPaidFee() + paidFee);
                            leasePaymentsRepository.save(payments.get(i));
                            paidFee = 0;
                            break;
                        }
                    }
                }
            }
            else{
                List<LeaseExtras> extras = leaseExtraRepository.findAllByPayment_Lease_Clients_ClientIdOrderByPaymentNo(payLeaseRequest.getClientId());
                for (LeaseExtras le : extras) {
                    le.setRead(true);
                    if(le.getReadUserNo() == null)
                        le.setReadUserNo(request.getSessionUser().getUserNo());
                    int unpaidExtra = le.getExtraFee() - le.getPaidFee();
                    if (unpaidExtra > 0) {
                        if (unpaidExtra < paidFee) {
                            logList.add(extraPaymentLog(le, unpaidExtra, true));
                            le.setPaidFee(le.getExtraFee());
                            paidFee -= unpaidExtra;
                            leaseExtraRepository.save(le);
                        } else {
                            logList.add(extraPaymentLog(le, paidFee, false));
                            le.setPaidFee(le.getPaidFee() + paidFee);
                            leaseExtraRepository.save(le);
                            paidFee = 0;
                            break;
                        }
                    }
                }
            }


            bikeUserLogRepository.save(addLog(BikeUserLogTypes.LEASE_PAYMENT, request.getSessionUser().getUserNo(), lease.getLeaseNo().toString(), logList));
            if(paidFee == 0)
                break;
        }
        if (paidFee > 0) {
            saveOverpayLog(request.getSessionUser(), leaseList.get(leaseList.size() - 1), paidFee);
        }
        return request;
    }

    @Transactional
    public BikeSessionRequest payByClientWithExcel(BikeSessionRequest request) {
        Map param = request.getParam();
        UploadExcelDto uploadExcelDto = map(param, UploadExcelDto.class);
        ArrayList<PayLeaseRequest> excludeBike = new ArrayList<>();
        ArrayList<PayLeaseRequest> sorted = new ArrayList<>();
        for (PayLeaseRequest payLeaseRequest : uploadExcelDto.getPayments()) {
            if (payLeaseRequest.getBikeNum() != null && !payLeaseRequest.getBikeNum().equals("")) {
                Bikes bike = bikesRepository.findByCarNum(payLeaseRequest.getBikeNum());
                ArrayList<String> logList = new ArrayList<>();
                Leases lease = leaseRepository.findByBikeNo(bike.getBikeNo());
                if (lease.getStatus() != LeaseStatusTypes.CONFIRM)
                    continue;
                int paidFee = payLeaseRequest.getPaidFee();
                List<LeasePayments> payments = leasePaymentsRepository.findAllByLease_LeaseId(lease.getLeaseId());
                for (int i = 0; i < payments.size() && payments.get(i).getPaymentDate().isBefore(LocalDate.now().plusDays(1)); i++) {
                    int unpaidFee = payments.get(i).getLeaseFee() - payments.get(i).getPaidFee();
                    if (unpaidFee > 0) {
                        if (paidFee > unpaidFee) {
                            logList.add(paymentLog(payments.get(i), unpaidFee, true));
                            payments.get(i).setPaidFee(payments.get(i).getLeaseFee());
                            paidFee -= unpaidFee;
                            leasePaymentsRepository.save(payments.get(i));
                        } else {
                            logList.add(paymentLog(payments.get(i), paidFee, false));
                            payments.get(i).setPaidFee(payments.get(i).getPaidFee() + paidFee);
                            leasePaymentsRepository.save(payments.get(i));
                            paidFee = 0;
                            break;
                        }
                    }
                    List<LeaseExtras> extras = leaseExtraRepository.findAllByPayment_PaymentId(payments.get(i).getPaymentId());
                    for (LeaseExtras le : extras) {
                        int unpaidExtra = le.getExtraFee() - le.getPaidFee();
                        if (unpaidExtra > 0) {
                            if (unpaidExtra < paidFee) {
                                logList.add(extraPaymentLog(le, unpaidExtra, true));
                                le.setPaidFee(le.getExtraFee());
                                paidFee -= unpaidExtra;
                                leaseExtraRepository.save(le);
                            } else {
                                logList.add(extraPaymentLog(le, paidFee, false));
                                le.setPaidFee(le.getPaidFee() + paidFee);
                                leaseExtraRepository.save(le);
                                paidFee = 0;
                                break;
                            }
                        }
                    }
                }
                if (paidFee > 0) {
                   saveOverpayLog(request.getSessionUser(), lease, paidFee);
                }
                bikeUserLogRepository.save(addLog(BikeUserLogTypes.LEASE_PAYMENT, request.getSessionUser().getUserNo(), lease.getLeaseNo().toString(), logList));
            }
            else
                excludeBike.add(payLeaseRequest);
        }
        Collections.sort(excludeBike);
        for(PayLeaseRequest payLeaseRequest : excludeBike) {
            if(!sorted.contains(payLeaseRequest)){
                sorted.add(payLeaseRequest);
            }else{
                int index = sorted.indexOf(payLeaseRequest);
                sorted.get(index).setPaidFee(sorted.get(index).getPaidFee() + payLeaseRequest.getPaidFee());
            }
        }
        for(PayLeaseRequest payLeaseRequest : sorted){
            Clients client = clientsRepository.findByRegNum(payLeaseRequest.getClientNum());
            List<Leases> leases = leaseRepository.findAllByClients_ClientIdOrderByLeaseInfo_ContractDate(client.getClientId());
            int paidFee = payLeaseRequest.getPaidFee();
            for(Leases lease : leases){
                List<String> logList = new ArrayList<>();
                if (lease.getStatus() != LeaseStatusTypes.CONFIRM)
                    continue;
                List<LeasePayments> payments = leasePaymentsRepository.findAllByLease_LeaseId(lease.getLeaseId());
                for (int i = 0; i < payments.size() && payments.get(i).getPaymentDate().isBefore(LocalDate.now().plusDays(1)); i++) {
                    int unpaidFee = payments.get(i).getLeaseFee() - payments.get(i).getPaidFee();
                    if (unpaidFee > 0) {
                        if (paidFee > unpaidFee) {
                            logList.add(paymentLog(payments.get(i), unpaidFee, true));
                            payments.get(i).setPaidFee(payments.get(i).getLeaseFee());
                            paidFee -= unpaidFee;
                            leasePaymentsRepository.save(payments.get(i));
                        } else {
                            logList.add(paymentLog(payments.get(i), paidFee, false));
                            payments.get(i).setPaidFee(payments.get(i).getPaidFee() + paidFee);
                            leasePaymentsRepository.save(payments.get(i));
                            paidFee = 0;
                            break;
                        }
                    }
                    List<LeaseExtras> extras = leaseExtraRepository.findAllByPayment_PaymentId(payments.get(i).getPaymentId());
                    for (LeaseExtras le : extras) {
                        int unpaidExtra = le.getExtraFee() - le.getPaidFee();
                        if (unpaidExtra > 0) {
                            if (unpaidExtra < paidFee) {
                                logList.add(extraPaymentLog(le, unpaidExtra, true));
                                le.setPaidFee(le.getExtraFee());
                                paidFee -= unpaidExtra;
                                leaseExtraRepository.save(le);
                            } else {
                                logList.add(extraPaymentLog(le, paidFee, false));
                                le.setPaidFee(le.getPaidFee() + paidFee);
                                leaseExtraRepository.save(le);
                                paidFee = 0;
                                break;
                            }
                        }
                    }
                }
                bikeUserLogRepository.save(addLog(BikeUserLogTypes.LEASE_PAYMENT, request.getSessionUser().getUserNo(), lease.getLeaseNo().toString(), logList));
                if(paidFee == 0)
                    break;
            }
            if(paidFee > 0){
                saveOverpayLog(request.getSessionUser(), leases.get(leases.size()-1), paidFee);
            }
        }
        return request;
    }



    private String paymentLog(LeasePayments payment, Integer changedFee, boolean isFull) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일");
        LocalDate paymentDate = payment.getPaymentDate();
        if(isFull)
            return ("<>" + payment.getIndex() + "회차 (" + paymentDate.format(dateTimeFormatter) + ")</> 리스료 <>" + Utils.getCurrencyFormat(payment.getLeaseFee()) + "원</>중에서 납부금액 (<>" + Utils.getCurrencyFormat(payment.getPaidFee()) + "원</>에서 <>" + Utils.getCurrencyFormat(changedFee + payment.getPaidFee()) + "원</>로) 완납하였습니다.");
        else
            return ("<>" + payment.getIndex() + "회차 (" + paymentDate.format(dateTimeFormatter) + ")</> 리스료 <>" + Utils.getCurrencyFormat(payment.getLeaseFee()) + "원</>중에서 납부금액 (<>" + Utils.getCurrencyFormat(payment.getPaidFee()) + "원</>에서 <>" + Utils.getCurrencyFormat(changedFee + payment.getPaidFee()) + "원</>) 납부하여서 잔여금액은 <>" + Utils.getCurrencyFormat(payment.getLeaseFee() - (changedFee + payment.getPaidFee())) + "원</>입니다.");
    }

    private String extraPaymentLog(LeaseExtras extra, Integer changedFee, boolean isFull) {
        LeasePayments payment = extra.getPayment();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일");
        LocalDate paymentDate = payment.getPaymentDate();
        if(isFull)
            return ("<>" + payment.getIndex() + "회차 (" + paymentDate.format(dateTimeFormatter) + ")</> 총추가납부금 <>" + Utils.getCurrencyFormat(extra.getExtraFee()) + "원</>중에서 납부금액 (<>" + Utils.getCurrencyFormat(extra.getPaidFee()) + "원</>에서 <>" + Utils.getCurrencyFormat(extra.getPaidFee() + changedFee) + "원</>)으로 완납하였습니다.");
        else
            return ("<>" + payment.getIndex() + "회차 (" + paymentDate.format(dateTimeFormatter) + ")</> 총추가납부금 <>" + Utils.getCurrencyFormat(extra.getExtraFee()) + "원</>중에서 납부금액 (<>" + Utils.getCurrencyFormat(extra.getPaidFee()) + "원</>에서 <>" + Utils.getCurrencyFormat(extra.getPaidFee() + changedFee) + "원</>)을 납부하여 잔여금액은 <>" + Utils.getCurrencyFormat(extra.getExtraFee() - (extra.getPaidFee() + changedFee)) + "원</>입니다.");
    }

    private void saveOverpayLog(BikeUser session, Leases lease, Integer overpayFee){
        ClientOverpay clientOverpay = new ClientOverpay();
        clientOverpay.setClientNo(lease.getClientNo());
        clientOverpay.setOverpayFee(overpayFee);
        clientOverpay.setDate(LocalDateTime.now());
        clientOverpayRepository.save(clientOverpay);
        String log = "리스료 <>" + overpayFee + "</>원 과납입 되었습니다.";
        bikeUserLogRepository.save(addLog(BikeUserLogTypes.LEASE_OVERPAY, session.getUserNo(), lease.getLeaseNo().toString(), log));
        bikeUserLogRepository.save(addLog(BikeUserLogTypes.COMM_CLIENT_OVERPAY, session.getUserNo(), lease.getClientNo().toString(), log));
    }
}
