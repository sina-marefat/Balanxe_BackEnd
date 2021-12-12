package ir.balanxe.modules.user.entities.personalinfo.service;

import ir.balanxe.modules.user.entities.personalinfo.model.PersonalUserInformation;
import ir.balanxe.modules.user.entities.personalinfo.repository.PersonalUserInformationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class PersonalUserInformationService {

    private final PersonalUserInformationRepository userInformationRepository;

    @Autowired
    public PersonalUserInformationService(PersonalUserInformationRepository userInformationRepository) {
        this.userInformationRepository = userInformationRepository;
    }

    public void savePersonalUserInformation(PersonalUserInformation userInformation) {
        userInformationRepository.save(userInformation);
    }

    public Optional<PersonalUserInformation> getPersonalUserInfoByUserId(UUID userId) {
        return userInformationRepository.findByUserId(userId);
    }

}
