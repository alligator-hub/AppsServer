package appsserver.entity;

import appsserver.enums.Permissions;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor

@Entity
public class Permission extends AbsEntity{
    @Column(length = 100, nullable = false, unique = true)
    @Enumerated(value = EnumType.STRING)
    private Permissions permissions;

    public Permission(Permissions permissions) {
        this.permissions = permissions;
    }
}
