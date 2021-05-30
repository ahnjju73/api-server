package helmet.bikelab.apiserver.domain.bikelab;

import helmet.bikelab.apiserver.domain.types.BikeUserStatusTypes;
import helmet.bikelab.apiserver.domain.types.BikeUserTodoTypes;
import helmet.bikelab.apiserver.domain.types.YesNoTypes;
import helmet.bikelab.apiserver.domain.types.converters.BikeUserStatusTypesConverter;
import helmet.bikelab.apiserver.domain.types.converters.BikeUserTodoTypesConverter;
import helmet.bikelab.apiserver.domain.types.converters.YesNoTypeConverter;
import helmet.bikelab.apiserver.utils.keys.SESSION;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "bike_user_todo", catalog = SESSION.SCHEME_SERVICE)
public class BikeUserTodo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "todo_no")
    private Long todoNo;

    @Column(name = "todo_type")
    @Convert(converter = BikeUserTodoTypesConverter.class)
    private BikeUserTodoTypes todoTypes;

    @Column(name = "from_user_no")
    private Integer fromUserNo;

    @ManyToOne
    @JoinColumn(name = "from_user_no", insertable = false, updatable = false)
    private BikeUser fromUser;

    @Column(name = "to_user_no")
    private Integer toUserNo;

    @Column(name = "read_yn", columnDefinition = "ENUM")
    @Convert(converter = YesNoTypeConverter.class)
    private YesNoTypes readYn = YesNoTypes.NO;

    @Column(name = "ref_id")
    private String referenceId;

    @Column(name = "ref_uuid")
    private String referenceUuid;

    @ManyToOne
    @JoinColumn(name = "to_user_no", insertable = false, updatable = false)
    private BikeUser toUser;

    @Column(name = "created_at", columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt = LocalDateTime.now();

}

