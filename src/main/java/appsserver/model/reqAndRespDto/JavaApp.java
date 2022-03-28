package appsserver.model.reqAndRespDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class JavaApp {

    private String UID;

    private Long PID;

    private String PPID;

    private String C;

    private String STIME;

    private String TTY;

    private String TIME;

    private String CMD;

}
