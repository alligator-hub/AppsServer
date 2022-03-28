package appsserver.entity;

import appsserver.enums.Roles;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

@Entity
public class Role extends AbsEntity implements GrantedAuthority {

    @Column(nullable = false, unique = true)
    @Enumerated(value = EnumType.STRING)
    private Roles roleName;


    @Override
    public String getAuthority() {
        return roleName.name();
    }
}
