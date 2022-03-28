package appsserver.config;

import appsserver.entity.Principal;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class AuditorAwareImpl implements AuditorAware<Principal> {
    @Override
    public Optional<Principal> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !authentication.getPrincipal().equals("anonymousUser")) {
            Principal principal = (Principal) authentication.getPrincipal();
            return Optional.of(principal);
        }
        return Optional.empty();
    }
}
