package appsserver.helper;

import appsserver.entity.Principal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class Session {

    public Principal getPrincipal() {
        Principal principal = null;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            principal = (Principal) authentication.getPrincipal();
        }
        return principal;
    }

    public Long getUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            Principal principal = (Principal) authentication.getPrincipal();
            return principal.getId();
        }
        return null;
    }

    public String getUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            Principal principal = (Principal) authentication.getPrincipal();
            return principal.getUsername();
        }
        return null;
    }
}
