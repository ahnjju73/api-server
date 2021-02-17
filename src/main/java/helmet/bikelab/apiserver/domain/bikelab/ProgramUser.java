package helmet.bikelab.apiserver.domain.bikelab;

import helmet.bikelab.apiserver.domain.types.ReadWriteTypes;
import helmet.bikelab.apiserver.domain.types.ProgramUserPK;
import helmet.bikelab.apiserver.domain.types.converters.ReadWriteTypesConverter;
import helmet.bikelab.apiserver.utils.keys.SESSION;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "system_program_user", catalog = SESSION.SCHEME_SERVICE)
@IdClass(ProgramUserPK.class)
@Getter
@Setter
@NoArgsConstructor
public class ProgramUser {

    @Id
    @Column(name = "user_no")
    private Integer bikeUserNo;

    @Id
    @Column(name = "program_no")
    private Integer programNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_no", insertable = false, updatable = false)
    private BikeUser bikeUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "program_no", insertable = false, updatable = false)
    private Program program;

    @Column(name = "read_wrt", columnDefinition = "ENUM")
    @Convert(converter = ReadWriteTypesConverter.class)
    private ReadWriteTypes readWriting = ReadWriteTypes.ONLY_VIEW;

    @Column(name = "created_at", columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime updatedDate = LocalDateTime.now();

}
