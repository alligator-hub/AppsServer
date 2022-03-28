package appsserver.security;

import appsserver.service.PrincipalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    JwtProvider jwtProvider;

    @Autowired
    PrincipalService principalService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        //get authorization
        String authorization = request.getHeader("Authorization");

        if (authorization != null) {
            if (authorization.startsWith("Bearer")) {
                // get token
                authorization = authorization.substring(7);
                //validate token
                boolean validateToken = jwtProvider.validateToken(authorization);

                if (validateToken) {
                    //get username by token
                    String usernameFromToken = jwtProvider.getUsernameFromToken(authorization);
                    //get userDetails by username
                    UserDetails userDetails = principalService.loadUserByUsername(usernameFromToken);

                    //created auth
                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails, null,
                                    userDetails.getAuthorities()
                            );
                    //set current authUser
                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);

                }
            }
        }

        filterChain.doFilter(request, response);
    }
}
