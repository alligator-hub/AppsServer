package appsserver.model.respDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AppDto {

    private Long id;

    private LocalDateTime startTime;

    private String customName;

    private Long pid;

    private Boolean status;

    private String absPath;

    private Boolean isAvailable;

    private Long byteSize;
}
