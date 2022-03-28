package appsserver.common;

import appsserver.entity.Permission;
import appsserver.entity.Role;
import appsserver.enums.Permissions;
import appsserver.enums.Roles;
import appsserver.repo.PermissionRepo;
import appsserver.repo.RoleRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    PermissionRepo permissionRepo;

    @Autowired
    RoleRepo roleRepo;


    @Value("${spring.datasource.initialization-mode}")
    private String initialMode;

    @Override
    public void run(String... args) {
        if (initialMode.equals("always")) {
            savePermissions();
            saveRoles();
        }
    }


    private void savePermissions() {
        for (Permissions value : Permissions.values()) {
            permissionRepo.save(new Permission(value));
        }
    }

    private void saveRoles() {
        for (Roles role : Roles.values()) {
            roleRepo.save(new Role(role));
        }
    }
}
