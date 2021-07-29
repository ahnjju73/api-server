package helmet.bikelab.apiserver.domain.client;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import helmet.bikelab.apiserver.domain.types.BusinessTypes;
import helmet.bikelab.apiserver.domain.types.ClientReceiptTypes;
import helmet.bikelab.apiserver.domain.types.converters.BusinessTypesConverter;
import helmet.bikelab.apiserver.domain.types.converters.ClientReceiptTypesConverter;
import helmet.bikelab.apiserver.utils.keys.SESSION;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@Table(name = "client_info", catalog = SESSION.SCHEME_SERVICE)
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ClientInfo {

    @Id
    @JsonIgnore
    @Column(name = "client_no")
    private Integer clientNo;

    @JsonIgnore
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "client_no", insertable = false, updatable = false)
    private Clients client;

    @Column(name = "thumbnail")
    private String thumbnail;

    // 사업자명
    @Column(name = "name",  length =  100)
    private String name;

    @Column(name = "phone", length = 45)
    private String phone;

    /**
     * 대표자명이 들어가고 담당자정보는 business_managers 에 저장된다.
     */
    @Column(name = "manager_name")
    private String managerName;

    /**
     * 대표자 연락처가 들어가고 담당자정보는 business_managers 에 저장된다.
     */
    @Column(name = "manager_phone")
    private String managerPhone;

    /**
     * 대표자 이메일정보가 들어가고 담당자정보는 business_managers 에 저장된다.
     */
    @Column(name = "manager_email")
    private String managerEmail;

    // 사업자등록일자
    @JsonSerialize
    @JsonDeserialize
    @DateTimeFormat
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Column(name = "reg_dt")
    private LocalDate regDate;

    // 사업자 업종
    @Column(name = "reg_busi_type")
    private String regBusinessType;

    // 사업자 업태
    @Column(name = "reg_sector_type")
    private String regSectorType;

    @Column(name = "description", columnDefinition = "MEDIUMTEXT")
    private String description;

    @Column(name = "receipt_type", columnDefinition = "ENUM", nullable = false)
    @Convert(converter = ClientReceiptTypesConverter.class)
    private ClientReceiptTypes receiptType = ClientReceiptTypes.EMAIL;

    @Column(name = "receipt_type", insertable = false, updatable = false)
    private String receiptTypeCode;

    public void setRegDate(String regDate) {
        this.regDate = LocalDate.parse(regDate);
    }

    public void setRegDate(LocalDate regDate) {
        this.regDate = regDate;
    }
}
