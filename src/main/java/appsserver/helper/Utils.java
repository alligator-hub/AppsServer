package appsserver.helper;

import appsserver.entity.App;
import appsserver.entity.Principal;
import appsserver.entity.Role;
import appsserver.enums.Roles;
import appsserver.model.reqAndRespDto.JavaApp;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class Utils {


    public static boolean hasAccessToCreateApp(List<Role> roles) {
        for (Role role : roles) {
            if (role.getRoleName().equals(Roles.ROOT) || role.getRoleName().equals(Roles.ADMIN)) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasAccessToChangeStatusApp(App app, Principal principal) {
        return Objects.equals(app.getCreatedBy().getId(), principal.getId()) ||
                isRoot(principal);
    }

    public static boolean isRoot(Principal principal) {
        for (Role role : principal.getRoles()) {
            if (role.getRoleName().equals(Roles.ROOT)) {
                return true;
            }
        }
        return false;
    }

    public static List<JavaApp> getProcessJavaApps() {
        List<JavaApp> processJavaAppList = new ArrayList<>();
        String[] command = {"ps", "-fC", "java"};
        try {
            Process process = new ProcessBuilder(command).start();
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("PID    PPID  C STIME TTY          TIME CMD")) continue;
                JavaApp javaApp = new JavaApp();
                javaApp.setUID(line.substring(0, line.indexOf(" ")));
                line = line.substring(line.indexOf(" "));
                line = line.trim();
                javaApp.setPID(Long.parseLong(line.substring(0, line.indexOf(" "))));
                line = line.substring(line.indexOf(" "));
                line = line.trim();
                javaApp.setPPID(line.substring(0, line.indexOf(" ")));
                line = line.substring(line.indexOf(" "));
                line = line.trim();
                javaApp.setC(line.substring(0, line.indexOf(" ")));
                line = line.substring(line.indexOf(" "));
                line = line.trim();
                javaApp.setSTIME(line.substring(0, line.indexOf(" ")));
                line = line.substring(line.indexOf(" "));
                line = line.trim();
                javaApp.setTTY(line.substring(0, line.indexOf(" ")));
                line = line.substring(line.indexOf(" "));
                line = line.trim();
                javaApp.setTIME(line.substring(0, line.indexOf(" ")));
                line = line.substring(line.indexOf(" "));
                line = line.trim();
                javaApp.setCMD(line.trim());
                processJavaAppList.add(javaApp);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return processJavaAppList;
    }

    public static boolean stopJavaAppSimple(JavaApp javaApp) {
        String[] command = {"kill", String.valueOf(javaApp.getPID())};
        try {
            Process process = new ProcessBuilder(command).start();
            process.waitFor();
            return true;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static Long startJavaApp(App app) {
        String[] command = {"java", "-jar", app.getAbsPath(), "&"};
        try {
            Process process = new ProcessBuilder(command).start();
            return process.pid();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean stopJavaAppWith9(JavaApp javaApp) {
        String[] command = {"kill","-9", String.valueOf(javaApp.getPID())};
        try {
            Process process = new ProcessBuilder(command).start();
            process.waitFor();
            return true;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }
}
