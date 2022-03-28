package appsserver.controller;

import appsserver.model.reqDto.PrincipalLoginDto;
import appsserver.model.reqDto.PrincipalRegisterDto;
import appsserver.model.reqDto.PrincipalUpdateDto;
import appsserver.model.respDto.ApiResponse;
import appsserver.model.respDto.PrincipalDto;
import appsserver.service.PrincipalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("api/user")
public class PrincipalController {

    @Autowired
    PrincipalService principalService;

    @PostMapping("/auth/login")
    public ResponseEntity<ApiResponse<String>> login(@RequestBody PrincipalLoginDto principalLoginDto) {
        return principalService.login(principalLoginDto);
    }

    @PostMapping("/auth/register")
    public ResponseEntity<ApiResponse<PrincipalDto>> registerPrincipal(@RequestBody PrincipalRegisterDto principalRegisterDto) {
        return principalService.register(principalRegisterDto);
    }

    @PutMapping("/update")
    public ResponseEntity<ApiResponse<PrincipalDto>> updateUser(@RequestBody PrincipalUpdateDto principalUpdateDto) {
        return principalService.update(principalUpdateDto);
    }
}
