package ir.balanxe.modules.user.entities.role.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;

@Entity(name = "user_roles")
public class UserRole implements Serializable {

    @Id
    private Long id;

    private String name;

    public UserRole() {
    }

    public UserRole(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
