package appsserver.service;

import appsserver.model.reqDto.PrincipalLoginDto;
import appsserver.model.reqDto.PrincipalRegisterDto;
import appsserver.model.reqDto.PrincipalUpdateDto;
import appsserver.model.respDto.ApiResponse;
import appsserver.model.respDto.PrincipalDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;

public interface PrincipalService {

    ResponseEntity<ApiResponse<PrincipalDto>> register(PrincipalRegisterDto principalRegisterDto);

    ResponseEntity<ApiResponse<String>> login(PrincipalLoginDto principalLoginDto);

    ResponseEntity<ApiResponse<PrincipalDto>> update(PrincipalUpdateDto principalUpdateDto);

    ResponseEntity<ApiResponse<PrincipalDto>> getPrincipals();


    UserDetails loadUserByUsername(String usernameFromToken);
}
