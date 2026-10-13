package web.eventbookingapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import web.eventbookingapp.model.UserEntity;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity,Long> {
    Optional<UserEntity> getByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);


}
