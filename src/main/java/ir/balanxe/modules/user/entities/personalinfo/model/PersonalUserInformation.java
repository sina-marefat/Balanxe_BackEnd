package ir.balanxe.modules.user.entities.personalinfo.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import ir.balanxe.enums.Gender;
import ir.balanxe.modules.user.model.User;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity(name = "personal_user_information")
public class PersonalUserInformation implements Serializable {

    @SequenceGenerator(
            name = "personal_user_information_sequence",
            sequenceName = "personal_user_information_sequence",
            allocationSize = 1
    )
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "personal_user_information_sequence"
    )
    private Long id;

    @Size(max = 20, message = "invalid firstname")
    @Column(nullable = false, name = "first_name")
    private String firstName;

    @Size(max = 20, message = "invalid lastname")
    @Column(nullable = false, name = "last_name")
    private String lastName;


    @Column(nullable = false, name = "gender")
    @Enumerated(value = EnumType.STRING)
    private Gender gender;

    @Field(name = "date_of_birthday")
    @Column(nullable = false, name = "date_of_birthday")
    private LocalDate dateOfBirthday;

    @Size(min = 11, max = 11, message = "invalid phone number")
    @Column(nullable = false, name = "phone_number")
    private String phone;

    @JsonIgnore
    @OneToOne
    @JoinColumn(nullable = false, name = "user_id")
    private User user;

    @JsonIgnore
    @Field(name = "created_at")
    @Column(updatable = false, name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;

    @JsonIgnore
    @Field(name = "updated_at")
    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;


    public PersonalUserInformation() {
    }

    public PersonalUserInformation(String firstName, String lastName, @NotNull Gender gender, LocalDate dateOfBirthday, @Size(min = 11, max = 11, message = "mobile must be 11 character") String phone, User user) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.dateOfBirthday = dateOfBirthday;
        this.user = user;
        this.phone= phone;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }


    public Gender getGender() {
        return gender;
    }

    public LocalDate getDateOfBirthday() {
        return dateOfBirthday;
    }

    public String getPhone() {
        return phone;
    }

    public User getUser() {
        return user;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
