package ir.balanxe.configs;

import ir.balanxe.modules.user.entities.role.service.UserRoleService;
import ir.balanxe.modules.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataBaseSeeder {

    private final UserRoleService roleService;
    private final UserService userService;

    @Autowired
    public DataBaseSeeder(UserRoleService roleService,
                          UserService userService) {
        this.roleService = roleService;
        this.userService = userService;
    }

    @Bean
    public void seedRolesTable() {
        roleService.seedTbl();
    }

    @Bean
    public void seedFinearzUsers() {
        userService.seedBalanxeUsers();
    }


}
