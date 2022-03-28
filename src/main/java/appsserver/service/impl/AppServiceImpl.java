package appsserver.service.impl;

import appsserver.entity.App;
import appsserver.entity.Principal;
import appsserver.entity.Role;
import appsserver.enums.Roles;
import appsserver.helper.MapstructMapper;
import appsserver.helper.Session;
import appsserver.helper.Utils;
import appsserver.model.reqAndRespDto.JavaApp;
import appsserver.model.reqDto.AppUpdateDto;
import appsserver.model.respDto.ApiResponse;
import appsserver.model.respDto.AppDto;
import appsserver.repo.AppRepo;
import appsserver.repo.PermissionPrincipalAppRepo;
import appsserver.service.AppService;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static appsserver.model.respDto.ApiResponse.response;

@Service
public class AppServiceImpl implements AppService {

    @Value("${apps.path}")
    private String appsPath;

    @Autowired
    AppRepo appRepo;

    @Autowired
    Session session;

    @Autowired
    MapstructMapper mapstructMapper;

    @Autowired
    PermissionPrincipalAppRepo permissionPrincipalAppRepo;


    @Override
    public ResponseEntity<ApiResponse<AppDto>> create(String customName, MultipartFile multipartFile) {

        Principal active = session.getPrincipal();

        if (active.getRoles().size() < 1)
            return response(false, MessageService.getMessage("NOT_PERMISSION"), HttpStatus.FORBIDDEN);
        if (!Utils.hasAccessToCreateApp(active.getRoles()))
            return response(false, MessageService.getMessage("NOT_PERMISSION"), HttpStatus.FORBIDDEN);


        //check file
        if (multipartFile == null)
            return response(false, MessageService.getMessage("FILE_CANT_BE_NULL"), HttpStatus.BAD_REQUEST);

        //check customName
        if (customName == null)
            return response(false, MessageService.getMessage("CUSTOM_NAME_CANT_BE_NULL"), HttpStatus.BAD_REQUEST);


        //check customName exist
        if (appRepo.existsByCustomName(customName))
            return response(false, MessageService.getMessage("CUSTOM_NAME_ALREADY_EXIST"), HttpStatus.BAD_REQUEST);

        //check file .jar application/x-java-archive
        if (!Objects.equals(multipartFile.getContentType(), "application/x-java-archive") &&
                !Objects.equals(multipartFile.getContentType(), "application/java-archive")) {
            return response(false, MessageService.getMessage("ITS_NOT_JAR"), HttpStatus.BAD_REQUEST);
        }


        File file = writeApp(multipartFile, appsPath);

        //check file write ?
        if (file == null)
            return response(false, MessageService.getMessage("FILE_CANT_WRITE"), HttpStatus.INTERNAL_SERVER_ERROR);


        App app = new App();
        app.setAbsPath(file.getAbsolutePath());
        app.setCustomName(customName);
        app.setIsAvailable(true);
        app.setStatus(false);
        app.setByteSize(file.length());


        appRepo.save(app);

        AppDto appDto = mapstructMapper.toAppDto(app);

        return response(appDto);
    }

    @Override
    public ResponseEntity<ApiResponse<AppDto>> updateApp(AppUpdateDto appUpdateDto, Long appId) {
        Principal active = session.getPrincipal();

        Optional<App> appOptional = appRepo.findById(appId);
        if (appOptional.isEmpty())
            return response(false, MessageService.getMessage("APP_NOT_FOUND"), HttpStatus.BAD_REQUEST);


        App app = appOptional.get();

        if (active.getRoles().size() < 1)
            return response(false, MessageService.getMessage("NOT_PERMISSION"), HttpStatus.FORBIDDEN);
        if (!Utils.hasAccessToChangeStatusApp(app, active))
            return response(false, MessageService.getMessage("NOT_PERMISSION"), HttpStatus.FORBIDDEN);


        //check customName exist
        if (appUpdateDto.getCustomName() == null)
            return response(false, MessageService.getMessage("CUSTOM_NAME_CANT_BE_NULL"), HttpStatus.BAD_REQUEST);

        if (appRepo.existsByCustomName(appUpdateDto.getCustomName()))
            return response(false, MessageService.getMessage("CUSTOM_NAME_ALREADY_EXIST"), HttpStatus.BAD_REQUEST);


        app.setCustomName(appUpdateDto.getCustomName());
        appRepo.save(app);

        return response(mapstructMapper.toAppDto(app));
    }

    @Transactional
    @Override
    public ResponseEntity<ApiResponse<AppDto>> startApp(Long appId) {
        Principal active = session.getPrincipal();

        Optional<App> appOptional = appRepo.findById(appId);
        if (appOptional.isEmpty())
            return response(false, MessageService.getMessage("APP_NOT_FOUND"), HttpStatus.BAD_REQUEST);

        App app = appOptional.get();

        if (active.getRoles().size() < 1)
            return response(false, MessageService.getMessage("NOT_PERMISSION"), HttpStatus.FORBIDDEN);

        if (!Utils.hasAccessToChangeStatusApp(app, active))
            return response(false, MessageService.getMessage("NOT_PERMISSION"), HttpStatus.FORBIDDEN);


        if (app.getStatus())
            return response(false, MessageService.getMessage("APP_IS_ALREADY_RUN"), HttpStatus.BAD_REQUEST);

        Long pid = Utils.startJavaApp(app);

        if (pid == null)
            return response(false, MessageService.getMessage("APP_CANT_RUN"), HttpStatus.INTERNAL_SERVER_ERROR);


        app.setStartTime(LocalDateTime.now());
        app.setStatus(true);
        app.setPid(pid);
        app.setIsAvailable(true);


        appRepo.save(app);

        return response(mapstructMapper.toAppDto(app));

    }


    @Transactional
    @Override
    public ResponseEntity<ApiResponse<AppDto>> stopApp(Long appId) {
        Principal active = session.getPrincipal();

        Optional<App> appOptional = appRepo.findById(appId);
        if (appOptional.isEmpty())
            return response(false, MessageService.getMessage("APP_NOT_FOUND"), HttpStatus.BAD_REQUEST);

        App app = appOptional.get();

        if (active.getRoles().size() < 1)
            return response(false, MessageService.getMessage("NOT_PERMISSION"), HttpStatus.FORBIDDEN);
        if (!Utils.hasAccessToChangeStatusApp(app, active))
            return response(false, MessageService.getMessage("NOT_PERMISSION"), HttpStatus.FORBIDDEN);

        if (app.getPid() == null)
            return response(false, MessageService.getMessage("NOT_PID"), HttpStatus.INTERNAL_SERVER_ERROR);


        if (!app.getStatus())
            return response(false, MessageService.getMessage("APP_IS_DOWN"), HttpStatus.BAD_REQUEST);


        List<JavaApp> javaAppList = Utils.getProcessJavaApps();

        for (JavaApp javaApp : javaAppList) {
            if (javaApp.getPID().equals(app.getPid())) {
                boolean killed = Utils.stopJavaAppSimple(javaApp);
                if (!killed)
                    return response(false, MessageService.getMessage("CANT_KILL"), HttpStatus.INTERNAL_SERVER_ERROR);

                // kill app with -9
                javaAppList = Utils.getProcessJavaApps();
                for (JavaApp javaApp1 : javaAppList) {
                    if (javaApp1.getPID().equals(app.getPid())){
                        boolean killed1 = Utils.stopJavaAppWith9(javaApp1);
                        if (!killed1)
                            return response(false, MessageService.getMessage("CANT_KILL"), HttpStatus.INTERNAL_SERVER_ERROR);
                    }
                }

               break;
            }
        }

        app.setStatus(false);
        app.setPid(null);
        app.setStartTime(null);
        appRepo.save(app);

        return response(mapstructMapper.toAppDto(app));

    }


    @Override
    public ResponseEntity<ApiResponse<AppDto>> deleteApp(Long appId) {
        Principal active = session.getPrincipal();

        Optional<App> appOptional = appRepo.findById(appId);
        if (appOptional.isEmpty())
            return response(false, MessageService.getMessage("APP_NOT_FOUND"), HttpStatus.BAD_REQUEST);

        App app = appOptional.get();

        if (active.getRoles().size() < 1)
            return response(false, MessageService.getMessage("NOT_PERMISSION"), HttpStatus.FORBIDDEN);
        if (!Utils.hasAccessToChangeStatusApp(app, active))
            return response(false, MessageService.getMessage("NOT_PERMISSION"), HttpStatus.FORBIDDEN);

        if (app.getPid() != null || app.getStatus())
            return response(false, MessageService.getMessage("APP_IS_UP"), HttpStatus.BAD_REQUEST);


        try {
            FileUtils.forceDelete(new File(app.getAbsPath()));

            permissionPrincipalAppRepo.deleteAll(
                    permissionPrincipalAppRepo.findAllByApp_Id(app.getId())
            );

            appRepo.delete(app);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return response(true, MessageService.getMessage("APP_DELETED"), HttpStatus.OK);
    }

    @Transactional
    @Override
    public ResponseEntity<ApiResponse<AppDto>> restartApp(Long appId) {
        Principal active = session.getPrincipal();

        Optional<App> appOptional = appRepo.findById(appId);
        if (appOptional.isEmpty())
            return response(false, MessageService.getMessage("APP_NOT_FOUND"), HttpStatus.BAD_REQUEST);

        App app = appOptional.get();

        if (active.getRoles().size() < 1)
            return response(false, MessageService.getMessage("NOT_PERMISSION"), HttpStatus.FORBIDDEN);
        if (!Utils.hasAccessToChangeStatusApp(app, active))
            return response(false, MessageService.getMessage("NOT_PERMISSION"), HttpStatus.FORBIDDEN);

        if (app.getPid() == null)
            return response(false, MessageService.getMessage("NOT_PID"), HttpStatus.INTERNAL_SERVER_ERROR);


        if (!app.getStatus())
            return response(false, MessageService.getMessage("APP_IS_DOWN"), HttpStatus.BAD_REQUEST);


        List<JavaApp> javaAppList = Utils.getProcessJavaApps();

        for (JavaApp javaApp : javaAppList) {
            if (javaApp.getPID().equals(app.getPid())) {
                boolean killed = Utils.stopJavaAppSimple(javaApp);
                if (!killed)
                    return response(false, MessageService.getMessage("CANT_KILL"), HttpStatus.INTERNAL_SERVER_ERROR);

                // kill app with -9
                javaAppList = Utils.getProcessJavaApps();
                for (JavaApp javaApp1 : javaAppList) {
                    if (javaApp1.getPID().equals(app.getPid())){
                        boolean killed1 = Utils.stopJavaAppWith9(javaApp1);
                        if (!killed1)
                            return response(false, MessageService.getMessage("CANT_KILL"), HttpStatus.INTERNAL_SERVER_ERROR);
                    }
                }
                break;
            }
        }

        app.setStatus(false);
        app.setPid(null);
        app.setStartTime(null);
        appRepo.save(app);

        Long pid = Utils.startJavaApp(app);

        if (pid == null)
            return response(false, MessageService.getMessage("APP_CANT_RUN"), HttpStatus.INTERNAL_SERVER_ERROR);


        app.setStartTime(LocalDateTime.now());
        app.setStatus(true);
        app.setPid(pid);
        app.setIsAvailable(true);

        appRepo.save(app);

        return response(mapstructMapper.toAppDto(app));
    }

    @Override
    public ResponseEntity<ApiResponse<AppDto>> updateAppFile(String customName, MultipartFile multipartFile) {
        Principal active = session.getPrincipal();

        if (active.getRoles().size() < 1)
            return response(false, MessageService.getMessage("NOT_PERMISSION"), HttpStatus.FORBIDDEN);
        if (!Utils.hasAccessToCreateApp(active.getRoles()))
            return response(false, MessageService.getMessage("NOT_PERMISSION"), HttpStatus.FORBIDDEN);


        //check file
        if (multipartFile == null)
            return response(false, MessageService.getMessage("FILE_CANT_BE_NULL"), HttpStatus.BAD_REQUEST);

        //check customName
        if (customName == null)
            return response(false, MessageService.getMessage("CUSTOM_NAME_CANT_BE_NULL"), HttpStatus.BAD_REQUEST);

        //check file .jar application/x-java-archive
        if (!Objects.equals(multipartFile.getContentType(), "application/x-java-archive") &&
                !Objects.equals(multipartFile.getContentType(), "application/java-archive")) {
            return response(false, MessageService.getMessage("ITS_NOT_JAR"), HttpStatus.BAD_REQUEST);
        }

        Optional<App> appOptional = appRepo.findByCustomName(customName);
        if (appOptional.isEmpty())
            return response(false, MessageService.getMessage("APP_NOT_FOUND"), HttpStatus.BAD_REQUEST);

        App app = appOptional.get();

        if (app.getPid() != null || app.getStatus())
            return response(false, MessageService.getMessage("APP_IS_UP"), HttpStatus.BAD_REQUEST);


        try {
            FileUtils.forceDelete(new File(app.getAbsPath()));

            permissionPrincipalAppRepo.deleteAll(
                    permissionPrincipalAppRepo.findAllByApp_Id(app.getId())
            );

            app.setAbsPath("");
            app.setIsAvailable(false);
            app.setStatus(false);
            app.setByteSize(0L);
            app.setPid(0L);
            appRepo.save(app);

        } catch (IOException e) {
            e.printStackTrace();
        }

        File file = writeApp(multipartFile, appsPath);
        app.setIsAvailable(true);
        app.setAbsPath(file.getAbsolutePath());
        app.setStatus(false);
        app.setByteSize(file.length());

        appRepo.save(app);


        return response(true, MessageService.getMessage("APP_UPDATED"), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<ApiResponse<AppDto>> getApp(Long appId) {
        Principal active = session.getPrincipal();

        Optional<App> appOptional = appRepo.findById(appId);
        if (appOptional.isEmpty())
            return response(false, MessageService.getMessage("APP_NOT_FOUND"), HttpStatus.BAD_REQUEST);

        App app = appOptional.get();

        if (active.getRoles().size() < 1)
            return response(false, MessageService.getMessage("NOT_PERMISSION"), HttpStatus.FORBIDDEN);
        if (!Utils.hasAccessToChangeStatusApp(app, active))
            return response(false, MessageService.getMessage("NOT_PERMISSION"), HttpStatus.FORBIDDEN);

        return response(mapstructMapper.toAppDto(app));
    }

    @Override
    public ResponseEntity<ApiResponse<List<AppDto>>> getApps() {
        Principal active = session.getPrincipal();

        List<App> appsList = appRepo.findAll();

        boolean access = false;

        for (Role role : active.getRoles()) {
            if (role.getRoleName().equals(Roles.ROOT)) {
                access = true;
                break;
            }
        }

        if (!access)
            return response(false, MessageService.getMessage("NOT_PERMISSION"), HttpStatus.FORBIDDEN);

        return response(mapstructMapper.toAppDto(appsList), (long) appsList.size());
    }


    public File writeApp(MultipartFile file, String path) {

        String fileName = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss-")) +
                file.getOriginalFilename();

        Path filepath = Paths.get(path, fileName);
        try (OutputStream os = Files.newOutputStream(filepath)) {
            os.write(file.getBytes());
            return new File(filepath.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
