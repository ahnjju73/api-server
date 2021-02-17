package helmet.bikelab.apiserver.domain.bikelab;

import helmet.bikelab.apiserver.domain.types.converters.YesNoTypeConverter;
import helmet.bikelab.apiserver.domain.types.YesNoTypes;
import helmet.bikelab.apiserver.utils.keys.SESSION;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "system_program_mst", catalog = SESSION.SCHEME_SERVICE)
@Getter
@Setter
@NoArgsConstructor
public class Program {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "program_no", length = 11)
    private Integer programNo;

    @Column(name = "program_id", unique = true, nullable = false)
    private String programId;

    @Column(name = "program_path", length = 255)
    private String programPath;

    @Column(name = "default_tp", columnDefinition = "ENUM")
    @Convert(converter = YesNoTypeConverter.class)
    private YesNoTypes defaultType;

    @Column(name = "use_yn", columnDefinition = "ENUM")
    @Convert(converter = YesNoTypeConverter.class)
    private YesNoTypes usable;

    @Column(name = "created_at", columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime updatedAt = LocalDateTime.now();

}
