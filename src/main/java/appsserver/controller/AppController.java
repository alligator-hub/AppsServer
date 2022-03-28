package appsserver.controller;

import appsserver.model.reqDto.AppUpdateDto;
import appsserver.model.respDto.ApiResponse;
import appsserver.model.respDto.AppDto;
import appsserver.service.AppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
@RequestMapping("api/app")
public class AppController {

    @Autowired
    AppService appService;

    @GetMapping("/get")
    public ResponseEntity<ApiResponse<AppDto>> getApp(@RequestParam(value = "app_id") Long appId) {
        return appService.getApp(appId);
    }

    @GetMapping("/get/all")
    public ResponseEntity<ApiResponse<List<AppDto>>> getApps() {
        return appService.getApps();
    }


    @PostMapping(value = "/create")
    public ResponseEntity<ApiResponse<AppDto>> createApp(@RequestParam(value = "custom_name", required = false) String customName, @RequestParam(value = "file", required = false) MultipartFile multipartFile) {
        return appService.create(customName, multipartFile);
    }

    @PutMapping("/update")
    public ResponseEntity<ApiResponse<AppDto>> editApp(@RequestBody AppUpdateDto appUpdateDto, @RequestParam(value = "app_id") Long appId) {
        return appService.updateApp(appUpdateDto, appId);
    }

    @PutMapping("/update/file")
    public ResponseEntity<ApiResponse<AppDto>> updateAppFile(@RequestParam(value = "custom_name", required = false) String customName, @RequestParam(value = "file", required = false) MultipartFile multipartFile) {
        return appService.updateAppFile(customName, multipartFile);
    }

    @PostMapping("/delete")
    public ResponseEntity<ApiResponse<AppDto>> deleteApp(@RequestParam(value = "app_id") Long appId) {
        return appService.deleteApp(appId);
    }

    @PostMapping("/start")
    public ResponseEntity<ApiResponse<AppDto>> startApp(@RequestParam(value = "app_id") Long appId) {
        return appService.startApp(appId);
    }

    @PostMapping("/stop")
    public ResponseEntity<ApiResponse<AppDto>> stopApp(@RequestParam(value = "app_id") Long appId) {
        return appService.stopApp(appId);
    }

    @PostMapping("/restart")
    public ResponseEntity<ApiResponse<AppDto>> restartApp(@RequestParam(value = "app_id") Long appId) {
        return appService.restartApp(appId);
    }

}
