package appsserver.model.reqDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class PrincipalUpdateDto {

    @NotNull
    private Long id;

    private String fullName;

    private String username;

    private String password;

}
