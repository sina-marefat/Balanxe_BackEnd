package ir.balanxe.modules.user.repository;

import ir.balanxe.modules.user.entities.role.model.UserRole;
import ir.balanxe.modules.user.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByUserName(String username);

    Optional<User> findByEmail(String email);

    @Query("select u from users u where u.email = ?1 or u.mobile = ?2")
    Optional<User> findByEmailOrMobile(String email, String mobile);

    Optional<User> findByMobile(String mobile);

    List<User> findAllByUserRoleName(String roleName);

    @Transactional
    @Modifying
    @Query("update users u set u.userRole = ?2 where u = ?1 and u.userRole <> ?3 ")
    int updateUserRole(User user, UserRole role, UserRole preventedRole);

    @Transactional
    @Modifying
    @Query("update users u set u.password = ?2 where u.id = ?1")
    void updatePassword(UUID userId, String password);


}
