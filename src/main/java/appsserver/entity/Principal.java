package appsserver.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor

@Entity
public class Principal extends AbsEntity implements UserDetails {

    @Column(nullable = false, length = 100, unique = true)
    private String fullName;

    @Column(nullable = false, unique = true, length = 100)
    private String username;

    @Column(nullable = false, length = 1000)
    private String password;

    @Column(nullable = false)
    private LocalDateTime lastActivityDate = LocalDateTime.now();

    @ManyToMany
    private List<Role> roles;
    @Column(nullable = false)
    private boolean isAccountNonExpired = true;
    @Column(nullable = false)
    private boolean isAccountNonLocked = true;
    @Column(nullable = false)
    private boolean isCredentialsNonExpired = true;
    @Column(nullable = false)
    private boolean isEnabled = true;

    public Principal(String fullName, String username, String password, LocalDateTime lastActivityDate, List<Role> roles) {
        this.fullName = fullName;
        this.username = username;
        this.password = password;
        this.lastActivityDate = lastActivityDate;
        this.roles = roles;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return isAccountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return isAccountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return isCredentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }
}
