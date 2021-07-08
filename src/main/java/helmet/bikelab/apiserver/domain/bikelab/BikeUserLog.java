package helmet.bikelab.apiserver.domain.bikelab;

import helmet.bikelab.apiserver.domain.types.BikeUserLogTypes;
import helmet.bikelab.apiserver.domain.types.BikeUserSessionPK;
import helmet.bikelab.apiserver.domain.types.UserSessionTypes;
import helmet.bikelab.apiserver.domain.types.converters.BikeUserLogTypesConverter;
import helmet.bikelab.apiserver.domain.types.converters.UserSessionTypeConverter;
import helmet.bikelab.apiserver.repositories.BikeUserLogRepository;
import helmet.bikelab.apiserver.services.internal.OriginObject;
import helmet.bikelab.apiserver.utils.keys.SESSION;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Entity
@Table(name = "bike_user_log", catalog = SESSION.SCHEME_SERVICE)
@Getter
@Setter
@NoArgsConstructor
public class BikeUserLog extends OriginObject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_no")
    private Long logNo;

    @Column(name = "log_type")
    @Convert(converter = BikeUserLogTypesConverter.class)
    private BikeUserLogTypes logType;

    @ManyToOne
    @JoinColumn(name = "from_user_no", insertable = false, updatable = false)
    private BikeUser fromUser;

    @Column(name = "from_user_no", length = 21)
    private Integer fromUserNo;

    @ManyToOne
    @JoinColumn(name = "to_user_no", insertable = false, updatable = false)
    private BikeUser toUser;

    @Column(name = "to_user_no", length = 21)
    private Integer toUserNo;

    @Column(name = "ref_id", length = 45)
    private String referenceId;

    @Column(name = "content", columnDefinition = "MEDIUMTEXT")
    private String content;

    @Column(name = "created_at", columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt = LocalDateTime.now();

    public static BikeUserLog addLog(BikeUserLogTypes bikeUserLogTypes, Integer fromUserNo, String referenceId){
        return addLog(bikeUserLogTypes, fromUserNo, null, null, referenceId);
    }

    public static BikeUserLog addLog(BikeUserLogTypes bikeUserLogTypes, Integer fromUserNo, String referenceId, List<String> content){
        return addLog(bikeUserLogTypes, fromUserNo, null, content, referenceId);
    }

    public static BikeUserLog addLog(BikeUserLogTypes bikeUserLogTypes, Integer fromUserNo, Integer toUserNo, List<String> content, String referenceId){
        BikeUserLog bikeUserLog = new BikeUserLog();
        bikeUserLog.setLogType(bikeUserLogTypes);
        bikeUserLog.setFromUserNo(fromUserNo);
        bikeUserLog.setToUserNo(toUserNo);
        if(content != null){
            String collect = content.stream().collect(Collectors.joining("\\n"));
            bikeUserLog.setContent(collect);
        }
        bikeUserLog.setReferenceId(referenceId);
        return bikeUserLog;

    }

}
