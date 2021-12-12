package ir.balanxe.modules.user.service;

import ir.balanxe.configs.customresponse.GenericOkHttpResponse;
import ir.balanxe.modules.log.model.Log;
import ir.balanxe.modules.registration.entities.otp.model.ConfirmationCode;
import ir.balanxe.modules.registration.entities.otp.service.ConfirmationCodeService;
import ir.balanxe.modules.user.entities.role.model.UserRole;
import ir.balanxe.modules.user.entities.role.service.UserRoleService;
import ir.balanxe.modules.user.model.User;
import ir.balanxe.modules.user.repository.UserRepository;
import ir.balanxe.providers.message.MessageUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {

    @Value("${balanxe.username:temp}")
    private String BALANXE_USERNAME;

    @Value("${balanxe.password:temp}")
    private String BALANXE_PASSWORD;

    private final static int UPPER_BOUND = 99999;
    private final static int LOWER_BOUND = 10000;
    private final UserRepository userRepository;
    private final UserRoleService userRoleService;
    private final EntityManager entityManager;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final ConfirmationCodeService confirmationCodeService;
    private final Logger logger = LoggerFactory.getLogger(UserService.class);


    @Autowired
    public UserService(UserRepository userRepository,ConfirmationCodeService confirmationCodeService, UserRoleService userRoleService, EntityManager entityManager , BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.userRoleService = userRoleService;
        this.entityManager = entityManager;
        this.confirmationCodeService=confirmationCodeService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(
            String userName
    ) throws UsernameNotFoundException {
        return userRepository.findByUserName(userName)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                        MessageUtil.getDesiredMessage("incorrect.credentials.msg")));
    }

    public Optional<User> findByUsername(
            String username
    ) {
        return userRepository.findByUserName(username);
    }

    public List<User> findSuperAdminUsers() {
        return userRepository.findAllByUserRoleName("SUPER_ADMIN");
    }

    @Transactional
    public String registerUser(
            User user
    ) {
        boolean userExists = userRepository.findByUserName(user.getUsername()).isPresent();

        if (user.isEnabled())
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, MessageUtil.getDesiredMessage("user.already.exists.msg"));

        String encodedPassword = bCryptPasswordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);

        return createOtp(userRepository.save(user), userExists);
    }

    @Transactional
    public String createOtp(
            User user,
            boolean isRenew
    ) {
        ConfirmationCode confirmationCode;

        int code = (int) (Math.random() * (UPPER_BOUND - LOWER_BOUND) + LOWER_BOUND);

        if (isRenew) {
            confirmationCode = confirmationCodeService.findConfirmationCodeByUserId(user.getId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                            MessageUtil.getDesiredMessage("incorrect.credentials.msg")));

            if (confirmationCode.getCreatedAt().plusSeconds(90).isAfter(LocalDateTime.now()))
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, MessageUtil.getDesiredMessage("wait.90.seconds"));

            confirmationCode.setCode(code);
            confirmationCode.setCreatedAt(LocalDateTime.now());
            confirmationCode.setExpiresAt(LocalDateTime.now().plusMinutes(15));
            confirmationCode.setConfirmedAt(null);
        } else {

            confirmationCode = new ConfirmationCode(
                    code,
                    LocalDateTime.now(),
                    LocalDateTime.now().plusMinutes(15),
                    user
            );
        }

        confirmationCodeService.saveConfirmationCode(confirmationCode);

        logger.info(new Log("register", user.getId()).toString());
        return String.valueOf(code);
    }

    public Optional<User> findByEmail(
            String email
    ) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> findByEmailOrMobile(String email,String mobile) {
        return userRepository.findByEmailOrMobile(email,mobile);
    }

    public Optional<User> findByMobile(
            String mobile
    ) {
        return userRepository.findByMobile(mobile);
    }

    public void enableUser(
            User user
    ) {
        user.setEnabled(true);
        userRepository.save(user);
        logger.info(new Log("user enabled", user.getId()).toString());
    }

    public boolean checkEqualPasswords(
            String rawPassword,
            String encodedPassword
    ) {
        return bCryptPasswordEncoder.matches(rawPassword, encodedPassword);
    }

    public void updatePassword(
            User user,
            String password
    ) {
        String encodedPassword = bCryptPasswordEncoder.encode(password);
        userRepository.updatePassword(user.getId(), encodedPassword);
    }


    public User findById(
            UUID userId
    ) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        MessageUtil.getDesiredMessage("not.confirmed.account.msg")));
    }

    //todo : get user profile


    public void seedBalanxeUsers() {
        if (userRepository.findByUserName(BALANXE_USERNAME).isEmpty()) {
            List<User> userList = List.of(
                    new User(
                            BALANXE_USERNAME,
                            null,
                            BALANXE_USERNAME,
                            bCryptPasswordEncoder.encode(BALANXE_PASSWORD),
                            userRoleService.findRoleByName("ADMIN")
                    )
            );

            userList.get(0).setEnabled(true);
            userList.get(0).setVerifiedAt(LocalDateTime.now());
            userRepository.saveAll(userList);
        }
    }




    public GenericOkHttpResponse changeUserRole(
            UUID userId,
            Long roleId
    ) {
        String preventedRole = "SUPER_ADMIN";
        UserRole role = userRoleService.findRoleById(roleId);

        if (role.getName().equals(preventedRole) || userRepository.updateUserRole(findById(userId), role, userRoleService.findRoleByName(preventedRole)) == 0)
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, MessageUtil.getDesiredMessage("change.role.forbidden.msg"));

        return new GenericOkHttpResponse(MessageUtil.getDesiredMessage("change.role.msg"));
    }

    public Page<User> findAllUsers(
            String fromDate,
            String toDate,
            Long roleId,
            String email,
            String mobile,
            int page,
            int size
    ) {
        PageRequest pageRequest = PageRequest.of(page, size);

        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> criteria = generateCriteriaQuery(fromDate, toDate, roleId);

        // add predicate per given field
        List<Predicate> predicates = new ArrayList<>();

        List<User> userList = entityManager.createQuery(criteria)
                .setFirstResult((int) pageRequest.getOffset()).setMaxResults(pageRequest.getPageSize())
                .getResultList();

        if (email != null && email.length() > 2)
            userList = userList.stream().filter(user -> user.getEmail() != null && user.getEmail().toLowerCase().contains(email.toLowerCase())).collect(Collectors.toList());
        if (mobile != null && mobile.length() > 2)
            userList = userList.stream().filter(user -> user.getMobile() != null && user.getMobile().contains(mobile)).collect(Collectors.toList());

        // Create Count Query
        CriteriaQuery<Long> countQuery = builder.createQuery(Long.class);
        Root<User> booksRootCount = countQuery.from(User.class);
        countQuery.select(builder.count(booksRootCount)).where(builder.and(predicates.toArray(new Predicate[0])));

        // Fetches the count of all Books as per given criteria
        Long count = entityManager.createQuery(countQuery).getSingleResult();

        return new PageImpl<>(userList, pageRequest, count);
    }

    private CriteriaQuery<User> generateCriteriaQuery(
            String fromDate,
            String toDate,
            Long roleId
    ) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> criteria = builder.createQuery(User.class);
        Root<User> root = criteria.from(User.class);

        // add predicate per given field
        List<Predicate> predicates = new ArrayList<>();
        if (roleId != null)
            predicates.add(builder.equal(root.get("userRole"), roleId));
        if (fromDate != null)
            predicates.add(builder.greaterThan(root.get("createdAt"), LocalDate.parse(fromDate).atStartOfDay()));
        if (toDate != null)
            predicates.add(builder.lessThan(root.get("createdAt"), LocalDate.parse(toDate).plusDays(1).atStartOfDay()));

        criteria.where(builder.and(predicates.toArray(new Predicate[0])));
        criteria.orderBy(builder.desc(root.get("createdAt")));
        return criteria;
    }



}
