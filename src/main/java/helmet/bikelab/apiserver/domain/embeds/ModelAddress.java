package helmet.bikelab.apiserver.domain.embeds;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Embeddable;


@Getter
@Setter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@Embeddable
public class ModelAddress {
    @Column(name = "country", length = 10)
    private String country;
    @Column(name = "country_code")
    private String countryCode;
    @Column(name = "address", length = 1000)
    private String address;
    @Column(name = "address_english", length = 1000)
    private String addressEnglish;
    @Column(name = "address_detail", length = 1000)
    private String addressDetail;
    @Column(name = "building_code")
    private String buildingCode;
    @Column(name = "building_name")
    private String buildingName;
    @Column(name = "jibun_address", length = 1000)
    private String jibunAddress;
    @Column(name = "jibun_address_english", length = 1000)
    private String jibunAddressEnglish;
    @Column(name = "road_address", length = 1000)
    private String roadAddress;
    @Column(name = "road_address_english", length = 1000)
    private String roadAddressEnglish;
    @Column(name = "roadname")
    private String roadname;
    @Column(name = "roadname_code", length = 13)
    private String roadnameCode;
    @Column(name = "sido")
    private String sido;
    @Column(name = "sigungu")
    private String sigungu;
    @Column(name = "sigungu_code", length = 13)
    private String sigunguCode;
    @Column(name = "zonecode", length = 13)
    private String zonecode;
    @JsonIgnore
    public void checkHasNull(){
    }
}