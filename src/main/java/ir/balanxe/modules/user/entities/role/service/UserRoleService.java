package ir.balanxe.modules.user.entities.role.service;

import ir.balanxe.modules.user.entities.role.model.UserRole;
import ir.balanxe.modules.user.entities.role.repository.UserRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class UserRoleService {

    private final static String ROLE_NOT_FOUND_MSG = "role not found";
    private final UserRoleRepository userRoleRepository;

    @Autowired
    public UserRoleService(UserRoleRepository userRoleRepository) {
        this.userRoleRepository = userRoleRepository;
    }

    public UserRole findRoleByName(String name) {
        return userRoleRepository.findByName(name)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST
                        , ROLE_NOT_FOUND_MSG));
    }

    public UserRole findRoleById(Long id) {
        return userRoleRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST
                        , ROLE_NOT_FOUND_MSG));
    }

    public List<UserRole> findAll() {
        return userRoleRepository.findAll();
    }

    public void seedTbl() {
        if (userRoleRepository.count() > 0) return;


        // change role of users that on registration & seeder of finearz user after change this section
        userRoleRepository.saveAll(List.of(
                new UserRole(1L, "USER"),
                new UserRole(2L, "VERIFIED_USER"),
                new UserRole(3L, "SUPPORT"),
                new UserRole(4L, "MARKETING_SPECIALIST"),
                new UserRole(5L, "ACCOUNTANT"),
                new UserRole(6L, "FP&A"),
                new UserRole(7L, "ANALYZER"),
                new UserRole(8L, "ADMIN"),
                new UserRole(9L, "SUPER_ADMIN")
        ));
    }
}
