package ir.balanxe.modules.user.model;
import com.fasterxml.jackson.annotation.JsonIgnore;
import ir.balanxe.modules.user.entities.role.model.UserRole;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

@Entity(name = "users")
public class User implements UserDetails, Serializable {

    @Id
    private UUID id;

    @Email(message = "invalid email")
    @Column(unique = true)
    private String email;

    @Size(min = 11, max = 11, message = "mobile must be 11 character")
    @Column(unique = true)
    private String mobile;

    @JsonIgnore
    @Column(unique = true)
    private String userName;

    @JsonIgnore
    @NotNull
    @Size(min = 8, message = "password must be greater than 7 character")
    private String password;


    @NotNull
    @ManyToOne
    @JoinColumn(name = "user_role_id")
    private UserRole userRole;


    @JsonIgnore
    private Boolean locked = false;

    @JsonIgnore
    private Boolean enabled = false;

    @JsonIgnore
    @Field(name = "created_at")
    @Column(name = "created_at", updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @JsonIgnore
    @Field(name = "updated_at")
    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @JsonIgnore
    @Field(name = "verified_at")
    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;

    public User(@Email(message = "invalid email") String email,
                @Size(min = 11, max = 11, message = "mobile must be 11 character") String mobile,
                String userName,
                @NotNull @Size(min = 8, message = "password must be greater than 7 character") String password,
                @NotNull UserRole userRole) {
        this.id = UUID.randomUUID();
        this.email = email;
        this.mobile = mobile;
        this.userName = userName;
        this.password = password;
        this.userRole = userRole;
    }

    public User() {

    }

    public UUID getId() {
        return id;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    @JsonIgnore
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(userRole.getName());
        return Collections.singleton(authority);
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String getUsername() {
        return userName;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonLocked() {
        return !locked;
    }

    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public UserRole getRole() {
        return userRole;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setUserRole(UserRole userRole) {
        this.userRole = userRole;
    }

    public void setLocked(Boolean locked) {
        this.locked = locked;
    }


    public LocalDateTime getVerifiedAt() {
        return verifiedAt;
    }

    public void setVerifiedAt(LocalDateTime verifiedAt) {
        this.verifiedAt = verifiedAt;
    }



}
