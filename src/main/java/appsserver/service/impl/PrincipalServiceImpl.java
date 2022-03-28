package appsserver.service.impl;

import appsserver.entity.Principal;
import appsserver.entity.Role;
import appsserver.enums.Roles;
import appsserver.model.reqDto.PrincipalLoginDto;
import appsserver.model.reqDto.PrincipalRegisterDto;
import appsserver.model.reqDto.PrincipalUpdateDto;
import appsserver.model.respDto.ApiResponse;
import appsserver.model.respDto.PrincipalDto;
import appsserver.repo.PrincipalRepo;
import appsserver.repo.RoleRepo;
import appsserver.security.JwtProvider;
import appsserver.service.PrincipalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static appsserver.model.respDto.ApiResponse.response;

@Service
public class PrincipalServiceImpl implements PrincipalService, UserDetailsService {

    @Autowired
    PrincipalRepo principalRepo;

    @Autowired
    RoleRepo roleRepo;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtProvider jwtProvider;

    @Override
    public Principal loadUserByUsername(String username) throws UsernameNotFoundException {
        return principalRepo.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Override
    @Transactional
    public ResponseEntity<ApiResponse<PrincipalDto>> register(PrincipalRegisterDto principalRegisterDto) {
        Optional<Principal> principalOptional = principalRepo.findByUsername(principalRegisterDto.getUsername());
        if (principalOptional.isPresent()) {
            return response(false, MessageService.getMessage("USERNAME_ALREADY_EXIST"), HttpStatus.BAD_REQUEST);
        }


        //todo check roles for add
        List<Role> roles = new ArrayList<>();
        List<Roles> rolesByNames = Roles.getRolesByNames(principalRegisterDto.getRoles());

        if (rolesByNames.size() != principalRegisterDto.getRoles().size())
            return response(false, MessageService.getMessage("ROLES_NOT_FOUND_ON_ENUM"), HttpStatus.BAD_REQUEST);


        for (Roles roleEnum : rolesByNames) {
            Optional<Role> byRoleName = roleRepo.findByRoleName(roleEnum);
            if (byRoleName.isEmpty())
                return response(false, MessageService.getMessage("ROLES_NOT_FOUND_ON_DB"), HttpStatus.BAD_REQUEST);

            roles.add(byRoleName.get());
        }

        Principal principal = new Principal();
        principal.setFullName(principalRegisterDto.getFullName());
        principal.setUsername(principalRegisterDto.getUsername());
        principal.setPassword(passwordEncoder.encode(principalRegisterDto.getPassword()));
        principal.setRoles(roles);

        principalRepo.save(principal);

        return response(new PrincipalDto(principal.getId(), principal.getFullName(), principal.getUsername()));
    }

    @Override
    @Transactional
    public ResponseEntity<ApiResponse<PrincipalDto>> update(PrincipalUpdateDto dto) {
        //check id
        if (dto.getId() == null)
            return response(false, MessageService.getMessage("PRINCIPAL_NOT_FOUND"), HttpStatus.BAD_REQUEST);

        //check id
        Optional<Principal> principalOptional = principalRepo.findById(dto.getId());
        if (principalOptional.isEmpty())
            return response(false, MessageService.getMessage("PRINCIPAL_NOT_FOUND"), HttpStatus.BAD_REQUEST);


        //check username
        boolean existUsername = principalRepo.existsByUsername(dto.getUsername());
        if (existUsername)
            return response(false, MessageService.getMessage("USERNAME_ALREADY_EXIST"), HttpStatus.BAD_REQUEST);


        //check fullName length
        if (dto.getFullName() != null) {
            if (dto.getFullName().length() > 99)
                return response(false, MessageService.getMessage("FULL_NAME_IS_LARGE"), HttpStatus.BAD_REQUEST);

        }

        //check username length
        if (dto.getUsername() != null) {
            if (dto.getUsername().length() > 99)
                return response(false, MessageService.getMessage("USERNAME_IS_LARGE"), HttpStatus.BAD_REQUEST);

        }

        //check username length
        if (dto.getPassword() != null) {
            if (dto.getPassword().length() > 99)
                return response(false, MessageService.getMessage("PASSWORD_IS_LARGE"), HttpStatus.BAD_REQUEST);

        }


        Principal principal = principalOptional.get();

        principal.setFullName(dto.getFullName() == null ? principal.getFullName() : dto.getFullName());
        principal.setUsername(dto.getUsername() == null ? principal.getUsername() : dto.getUsername());
        principal.setPassword(dto.getPassword() == null ? principal.getPassword() : passwordEncoder.encode(dto.getPassword()));

        principalRepo.save(principal);

        return response(new PrincipalDto(principal.getId(), principal.getFullName(), principal.getUsername()));
    }

    @Override
    public ResponseEntity<ApiResponse<PrincipalDto>> getPrincipals() {
        return null;
    }

    @Override
    public ResponseEntity<ApiResponse<String>> login(PrincipalLoginDto principalLoginDto) {
        try {

            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(principalLoginDto.getUsername(), principalLoginDto.getPassword())
            );

            String generate = jwtProvider.generate(principalLoginDto.getUsername());
            return response(generate);
        } catch (BadCredentialsException exception) {
            return response(false, MessageService.getMessage("USERNAME_OR_PASSWORD_WRONG"), HttpStatus.FORBIDDEN);
        }
    }
}
