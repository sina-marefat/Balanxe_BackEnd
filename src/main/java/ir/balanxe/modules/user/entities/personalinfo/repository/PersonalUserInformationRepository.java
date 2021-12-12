package ir.balanxe.modules.user.entities.personalinfo.repository;

import ir.balanxe.modules.user.entities.personalinfo.model.PersonalUserInformation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PersonalUserInformationRepository extends JpaRepository<PersonalUserInformation, Long> {

    Optional<PersonalUserInformation> findByUserId(UUID userId);

}
