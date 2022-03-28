package appsserver.repo;

import appsserver.entity.App;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AppRepo extends JpaRepository<App, Long> {


    boolean existsByCustomName(String customName);

    Optional<App> findByCustomName(String customName);
}
