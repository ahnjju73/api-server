package helmet.bikelab.apiserver.services.leases;

import helmet.bikelab.apiserver.domain.bike.Bikes;
import helmet.bikelab.apiserver.domain.bikelab.BikeUser;
import helmet.bikelab.apiserver.domain.client.Clients;
import helmet.bikelab.apiserver.domain.lease.*;
import helmet.bikelab.apiserver.domain.types.BikeUserLogTypes;
import helmet.bikelab.apiserver.domain.types.ExtraTypes;
import helmet.bikelab.apiserver.domain.types.LeaseStatusTypes;
import helmet.bikelab.apiserver.objects.BikeDto;
import helmet.bikelab.apiserver.objects.BikeSessionRequest;
import helmet.bikelab.apiserver.objects.UnpaidExcelDto;
import helmet.bikelab.apiserver.objects.bikelabs.clients.ClientDto;
import helmet.bikelab.apiserver.objects.bikelabs.leases.AddUpdateLeaseRequest;
import helmet.bikelab.apiserver.objects.bikelabs.leases.FetchUnpaidLeasesResponse;
import helmet.bikelab.apiserver.objects.bikelabs.leases.PayLeaseRequest;
import helmet.bikelab.apiserver.objects.bikelabs.leases.UploadExcelDto;
import helmet.bikelab.apiserver.repositories.BikeUserLogRepository;
import helmet.bikelab.apiserver.repositories.LeaseExtraRepository;
import helmet.bikelab.apiserver.repositories.LeasePaymentsRepository;
import helmet.bikelab.apiserver.repositories.LeaseRepository;
import helmet.bikelab.apiserver.services.internal.SessService;
import helmet.bikelab.apiserver.utils.AutoKey;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static helmet.bikelab.apiserver.domain.bikelab.BikeUserLog.addLog;

@Service
@RequiredArgsConstructor
public class LeasePaymentService  extends SessService {
    private final LeaseRepository leaseRepository;
    private final LeasePaymentsRepository leasePaymentsRepository;
    private final LeaseExtraRepository leaseExtraRepository;
    private final BikeUserLogRepository bikeUserLogRepository;
    private final AutoKey autoKey;


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
        Map param = request.getParam();
        PayLeaseRequest payLeaseRequest = map(param, PayLeaseRequest.class);
        Leases lease = leaseRepository.findByLeaseId(payLeaseRequest.getLeaseId());
        if (lease.getStatus() != LeaseStatusTypes.CONFIRM)
            withException("900-001");
        int paidFee = payLeaseRequest.getPaidFee();
        List<LeasePayments> payments = leasePaymentsRepository.findAllByLease_LeaseId(payLeaseRequest.getLeaseId());
        for (int i = 0; i < payments.size() && paidFee > 0; i++) {
            int unpaidFee = payments.get(i).getLeaseFee() - payments.get(i).getPaidFee();
            if (unpaidFee > 0) {
                if (paidFee > unpaidFee) {
                    paymentLog(request.getSessionUser(), lease, payments.get(i), unpaidFee, true);
                    payments.get(i).setPaidFee(payments.get(i).getLeaseFee());
                    paidFee -= unpaidFee;
                    leasePaymentsRepository.save(payments.get(i));
                } else {
                    paymentLog(request.getSessionUser(), lease, payments.get(i), paidFee, false);
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
                        extraPaymentLog(request.getSessionUser(), lease, le, unpaidExtra, true);
                        le.setPaidFee(le.getExtraFee());
                        paidFee -= unpaidExtra;
                        leaseExtraRepository.save(le);
                    } else {
                        extraPaymentLog(request.getSessionUser(), lease, le, paidFee, false);
                        le.setPaidFee(le.getPaidFee() + paidFee);
                        leaseExtraRepository.save(le);
                        paidFee = 0;
                        break;
                    }
                }
            }
        }
        if (paidFee > 0) {
            String extraId = autoKey.makeGetKey("lease_extra");
            LeaseExtras leaseExtra = new LeaseExtras();
            leaseExtra.setPaymentNo(payments.get(payments.size() - 1).getPaymentNo());
            leaseExtra.setLeaseNo(payments.get(0).getLeaseNo());
            leaseExtra.setExtraFee(-paidFee);
            leaseExtra.setExtraId(extraId);
            leaseExtra.setExtraTypes(ExtraTypes.ETC);
            leaseExtra.setDescription("초과 금액");
            leaseExtraRepository.save(leaseExtra);
        }
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
        try {
            File newFile = new File("./newfile");
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
        }
    }

    @Transactional
    public BikeSessionRequest payWithExcel(BikeSessionRequest request) {
        Map param = request.getParam();
        UploadExcelDto uploadExcelDto = map(param, UploadExcelDto.class);
        for (PayLeaseRequest payLeaseRequest : uploadExcelDto.getPayments()) {
            Leases lease = leaseRepository.findByLeaseId(payLeaseRequest.getLeaseId());
            int paidFee = payLeaseRequest.getPaidFee();
            List<LeasePayments> payments = leasePaymentsRepository.findAllByLease_LeaseId(payLeaseRequest.getLeaseId());
            for (int i = 0; i < payments.size() && paidFee > 0; i++) {
                int unpaidFee = payments.get(i).getLeaseFee() - payments.get(i).getPaidFee();
                if (unpaidFee > 0) {
                    if (paidFee > unpaidFee) {
                        paymentLog(request.getSessionUser(), lease, payments.get(i), unpaidFee, true);
                        payments.get(i).setPaidFee(payments.get(i).getLeaseFee());
                        paidFee -= unpaidFee;
                        leasePaymentsRepository.save(payments.get(i));
                    } else {
                        paymentLog(request.getSessionUser(), lease, payments.get(i), paidFee, false);
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
                            extraPaymentLog(request.getSessionUser(), lease, le, unpaidExtra, true);
                            le.setPaidFee(le.getExtraFee());
                            paidFee -= unpaidExtra;
                            leaseExtraRepository.save(le);
                        } else {
                            extraPaymentLog(request.getSessionUser(), lease, le, paidFee, false);
                            le.setPaidFee(le.getPaidFee() + paidFee);
                            leaseExtraRepository.save(le);
                            paidFee = 0;
                            break;
                        }
                    }
                }
            }
            if (paidFee > 0) {
                String extraId = autoKey.makeGetKey("lease_extra");
                LeaseExtras leaseExtra = new LeaseExtras();
                leaseExtra.setPaymentNo(payments.get(payments.size() - 1).getPaymentNo());
                leaseExtra.setLeaseNo(payments.get(0).getLeaseNo());
                leaseExtra.setExtraFee(-paidFee);
                leaseExtra.setExtraId(extraId);
                leaseExtra.setExtraTypes(ExtraTypes.ETC);
                leaseExtra.setDescription("초과 금액");
                leaseExtraRepository.save(leaseExtra);
            }
        }

        return request;
    }

    private void paymentLog(BikeUser session, Leases leases, LeasePayments payment, Integer changedFee, boolean isFull) {
        List<String> stringList = new ArrayList<>();
        if(isFull)
            stringList.add(payment.getPaymentDate().getMonthValue() + "월 납부한 금액 " + payment.getPaidFee() + "에서 " + (changedFee + payment.getPaidFee()) + "로 완납하였습니다.");
        else
            stringList.add(payment.getPaymentDate().getMonthValue() + "월 납부한 금액 " + payment.getPaidFee() + "에서 " + (changedFee + payment.getPaidFee()) + "로 잔여금액 " + (payment.getLeaseFee() - (changedFee + payment.getPaidFee())) + "원 있습니다.");

        bikeUserLogRepository.save(addLog(BikeUserLogTypes.LEASE_PAYMENT, session.getUserNo(), leases.getLeaseNo().toString(), stringList));
    }

    private void extraPaymentLog(BikeUser session, Leases leases, LeaseExtras extra, Integer changedFee, boolean isFull) {
        List<String> stringList = new ArrayList<>();
        if(isFull)
            stringList.add(extra.getPayment().getPaymentDate().getMonthValue() + "월 추가금 납부한 금액 " + extra.getPaidFee() + "에서 " + (extra.getPaidFee() + changedFee) + "로 완납하였습니다.");
        else
            stringList.add(extra.getPayment().getPaymentDate().getMonthValue() + "월 추가금 납부한 금액 " + extra.getPaidFee() + "에서 " + (extra.getPaidFee() + changedFee) + "로 잔여금액 " + (extra.getExtraFee() - (extra.getPaidFee() + changedFee)) + "원 있습니다.");

        bikeUserLogRepository.save(addLog(BikeUserLogTypes.LEASE_PAYMENT, session.getUserNo(), leases.getLeaseNo().toString(), stringList));
    }
}
