package appsserver.service;

import appsserver.model.reqDto.AppUpdateDto;
import appsserver.model.respDto.ApiResponse;
import appsserver.model.respDto.AppDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface AppService {
    ResponseEntity<ApiResponse<AppDto>> getApp(Long appId);

    ResponseEntity<ApiResponse<List<AppDto>>> getApps();

    ResponseEntity<ApiResponse<AppDto>> create(String customName, MultipartFile multipartFile);

    ResponseEntity<ApiResponse<AppDto>> updateApp(AppUpdateDto appUpdateDto, Long appId);

    ResponseEntity<ApiResponse<AppDto>> deleteApp(Long appId);

    ResponseEntity<ApiResponse<AppDto>> startApp(Long appId);

    ResponseEntity<ApiResponse<AppDto>> stopApp(Long appId);

    ResponseEntity<ApiResponse<AppDto>> restartApp(Long appId);

    ResponseEntity<ApiResponse<AppDto>> updateAppFile(String customName, MultipartFile multipartFile);
}
