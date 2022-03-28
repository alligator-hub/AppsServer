package appsserver.model.reqDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PrincipalLoginDto {
    private String username;
    private String password;
}
