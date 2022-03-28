package appsserver.repo;

import appsserver.entity.PermissionPrincipalApp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PermissionPrincipalAppRepo extends JpaRepository<PermissionPrincipalApp, Long> {

    List<PermissionPrincipalApp> findAllByApp_Id(Long app_id);
}
