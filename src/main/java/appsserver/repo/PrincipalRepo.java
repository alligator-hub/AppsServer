package appsserver.repo;

import appsserver.entity.Principal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PrincipalRepo extends JpaRepository<Principal, Long> {

    Optional<Principal> findByUsername(String username);

    boolean existsByUsername(String username);
}
