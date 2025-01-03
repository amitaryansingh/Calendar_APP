package calendar.app.repository;

import calendar.app.entities.Role;
import calendar.app.entities.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    // Find a user by email
    Optional<User> findByEmail(String email);

    // Find a user by role
    User findByRole(Role role);

}
