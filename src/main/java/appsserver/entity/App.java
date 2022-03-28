package appsserver.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor

@Entity
public class App extends AbsEntity {
    @Column
    private LocalDateTime startTime;

    @Column(nullable = false, length = 100, unique = true)
    private String customName;

    @Column
    private Long pid;

    @Column(nullable = false)
    private Boolean status;

    @Column(nullable = false, length = 500)
    private String absPath;

    @Column(nullable = false)
    private Boolean isAvailable;

    private Long byteSize;

}
