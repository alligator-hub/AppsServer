package appsserver.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor

@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"principal", "app", "permission"})
})
public class PermissionPrincipalApp extends AbsEntity {

    @JoinColumn(name = "principal")
    @ManyToOne
    private Principal principal;

    @JoinColumn(name = "app")
    @ManyToOne
    private App app;

    @JoinColumn(name = "permission")
    @ManyToOne
    private Permission permission;

    public PermissionPrincipalApp(Principal principal, App app, Permission permission) {
        this.principal = principal;
        this.app = app;
        this.permission = permission;
    }
}
