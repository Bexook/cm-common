package com.cm.common.service.user.impl;

import com.cm.common.classifiers.SearchCriteria;
import com.cm.common.exception.SystemException;
import com.cm.common.mapper.OrikaBeanMapper;
import com.cm.common.model.domain.AppUserEntity;
import com.cm.common.model.dto.AppUserDTO;
import com.cm.common.model.enumeration.*;
import com.cm.common.repository.AppUserRepository;
import com.cm.common.service.SearchCriteriaExecutor;
import com.cm.common.service.notification.NotificationService;
import com.cm.common.service.token.TokenService;
import com.cm.common.service.user.AppUserService;
import com.cm.common.util.AuthorizationUtil;
import com.cm.common.util.JsonUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.MapUtils;
import org.thymeleaf.util.StringUtils;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.cm.common.util.SqlUtils.wrapToSqlLikeStatement;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppUserServiceImpl implements AppUserService {

    private final OrikaBeanMapper mapper;
    private final AppUserRepository appUserRepository;
    private final EntityManager entityManager;
    private final PasswordEncoder passwordEncoder;

    private final NotificationService notificationService;
    private final TokenService tokenService;

    private static final Map<SearchCriteria, SearchCriteriaExecutor<AppUserDTO>> searchCriteriaExecutorMap = new HashMap<>();


    {
        searchCriteriaExecutorMap.put(UserSearchCriteria.BY_USER_ROLE, this::searchByUserRole);
        searchCriteriaExecutorMap.put(UserSearchCriteria.BY_EMAIL, this::searchByEmail);
        searchCriteriaExecutorMap.put(UserSearchCriteria.BY_EMAIL_VERIFIED, this::searchByEmailVerified);
        searchCriteriaExecutorMap.put(UserSearchCriteria.BY_LASTNAME, this::searchByLastName);
        searchCriteriaExecutorMap.put(UserSearchCriteria.BY_FIRSTNAME, this::searchByFirstName);
        searchCriteriaExecutorMap.put(UserSearchCriteria.BY_ACTIVE, this::searchByActive);
    }

    @Override
    @PreAuthorize("@userAccessValidation.isAdmin()")
    public void createUserByAdmin(final AppUserDTO appUser) {
        if (!appUserRepository.exists(Example.of(new AppUserEntity().setEmail(appUser.getEmail())))) {
            appUser.setEmailVerified(false);
            appUser.setActive(false);
            final AppUserDTO notActiveNewUser = mapper.map(appUserRepository.save(mapper.map(appUser, AppUserEntity.class)), AppUserDTO.class);
            notificationService.generateAndSendTokenMessage(notActiveNewUser, NotificationType.ACCOUNT_ACTIVATION);
            log.info("New user is created by admin. Admin name: {}\n New user email: {}", AuthorizationUtil.getCurrentUserNullable().getUsername(), appUser.getEmail());
        }
        throw new SystemException("User already exists", HttpStatus.BAD_REQUEST);
    }

    @Override
    public List<AppUserDTO> getCourseUsersByCourseIdAndUserRole(final Long courseId, final UserRole userRole) {
        return mapper.mapAsList(appUserRepository.getCourseUsersByCourseIdAndUserRole(courseId, userRole), AppUserDTO.class);
    }

    @Override
    @Transactional
    @PreAuthorize("@userAccessValidation.isAnonymous()")
    public boolean activateUserAccount(final String token) {
        final AppUserDTO appUser = tokenService.getUserByToken(token, TokenType.ACCOUNT_ACTIVATION_TOKEN);
        final boolean valid = tokenService.isTokenValid(token, TokenType.ACCOUNT_ACTIVATION_TOKEN);
        appUserRepository.updateAccountActiveFlag(appUser.getId(), valid);
        log.info("User account activation result: {} for user: {}", valid ? "SUCCESSFULLY" : "FAILED", appUser.getId());
        return valid;
    }

    @Override
    @Transactional
    @PreAuthorize("@userAccessValidation.isAdmin()")
    public void deactivateUserAccount(final Long userId) {
        log.info("User account: {} deactivated", userId);
        appUserRepository.updateAccountActiveFlag(userId, false);
    }

    @Override
    @PreAuthorize("@userAccessValidation.isCurrentUser(#userId) || @userAccessValidation.isAdmin()")
    @Transactional(readOnly = true)
    public AppUserDTO getUserById(final Long userId) {
        final Optional<AppUserEntity> appUser = appUserRepository.findById(userId);
        return mapper.map(appUser.orElseThrow(() -> new SystemException("User does not exist", HttpStatus.BAD_REQUEST)), AppUserDTO.class);
    }

    @Override
    public List<CourseAuthorities> getUserAuthorityForCourse(final Long userId, final Long courseId) {
        final String userAuthorityForCourse = appUserRepository.getUserAuthorityForCourse(userId, courseId);
        if (StringUtils.isEmptyOrWhitespace(userAuthorityForCourse)) {
            return List.of();
        }
        return JsonUtils.deserialize(userAuthorityForCourse, new TypeReference<List<CourseAuthorities>>() {
        });
    }

    @Override
    @Transactional
    public void userRegistration(@NonNull AppUserDTO user) {
        user.setUserRole(UserRole.STUDENT); // default
        if (existsByEmail(user.getEmail())) {
            throw new SystemException("User already exists", HttpStatus.BAD_REQUEST);
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        final AppUserDTO appUser = mapper.map(appUserRepository.save(mapper.map(user, AppUserEntity.class)), AppUserDTO.class);
        notificationService.generateAndSendTokenMessage(appUser, NotificationType.ACCOUNT_ACTIVATION);
    }

    @Override
    @Transactional
    @PreAuthorize("@userAccessValidation.isAdmin() || @userAccessValidation.isCurrentUser(#userId)")
    public void deleteById(final Long userId) {
        final AppUserEntity userEntity = appUserRepository.findById(userId)
                .orElseThrow(() -> new SystemException("User with such id does not exist", HttpStatus.BAD_REQUEST));
        appUserRepository.delete(userEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public AppUserDTO findByEmail(final String email) {
        return mapper.map(appUserRepository.findByEmail(email), AppUserDTO.class);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@userAccessValidation.isAdmin()")
    public List<AppUserDTO> findAll(final boolean isActive) {
        Session session = entityManager.unwrap(Session.class);
        session.enableFilter("activeFilter").setParameter("isActive", isActive);
        List<AppUserEntity> userEntityList = appUserRepository.findAll();
        session.disableFilter("activeFilter");
        return mapper.mapAsList(userEntityList, AppUserDTO.class);
    }


    @Override
    @PreAuthorize("@userAccessValidation.isCurrentUser(#userDTO.id)")
    public AppUserDTO update(final AppUserDTO userDTO) {
        userDTO.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        return mapper.map(appUserRepository.save(mapper.map(userDTO, AppUserEntity.class)), AppUserDTO.class);
    }


    @Override
    @PreAuthorize("@userAccessValidation.onlyScheduledJob()")
    public void dropNotVerifiedUsers() {
        log.info("=================== Fetching not verified accounts ===================");
        final Set<AppUserEntity> notVerifiedAccounts = appUserRepository.findAllByEmailVerified(false).stream()
                .filter(a -> a.getCreatedDate().plusDays(7).isBefore(LocalDateTime.now()))
                .collect(Collectors.toSet());
        log.info("=================== Deleting not verified accounts ===================");
        appUserRepository.deleteAll(notVerifiedAccounts);
        log.info("=================== Deleted {} accounts ===================", notVerifiedAccounts.size());
    }


    @Override
    @PreAuthorize("@userAccessValidation.onlyScheduledJob()")
    public void sendAccountDeletionWarningNotification() {
        final Set<AppUserDTO> userToReceiveWarningNotify = getListOfAboutToExpireActivationLink();
        userToReceiveWarningNotify.forEach(u -> {
            try {
                notificationService.generateAndSendTokenMessage(u, NotificationType.ACCOUNT_DELETION_WARNING);
            } catch (final RuntimeException e) {
                log.info("Account deletion warning notify sending: FAILED");
                throw new SystemException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        });
        log.info("Account deletion warning notify sending: SUCCEED");
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@userAccessValidation.isAdmin()")
    public Set<AppUserDTO> searchUserByCriteria(final Map<? extends SearchCriteria, Object> criteria) {
        if (MapUtils.isEmpty(criteria)) {
            return mapper.mapAsSet(appUserRepository.findAll(), AppUserDTO.class);
        }
        return searchByCriteria(criteria, searchCriteriaExecutorMap);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(final String email) {
        return appUserRepository.exists(Example.of(new AppUserEntity().setEmail(email), ExampleMatcher.matching()));
    }

    @Override
    public boolean updatePassword(final String resetToken, final String newPassword) {
        final boolean valid = tokenService.isTokenValid(resetToken, TokenType.PASSWORD_RESET_TOKEN);
        if (valid) {
            final AppUserDTO appUser = tokenService.getUserByToken(resetToken, TokenType.PASSWORD_RESET_TOKEN);
            if (!Objects.equals(appUser.getPassword(), newPassword)) {
                appUserRepository.updateUserPasswordById(passwordEncoder.encode(newPassword), appUser.getId());
                return true;
            }
            throw new SystemException("You can not set the same password", HttpStatus.BAD_REQUEST);
        }
        throw new SystemException("Invalid reset token", HttpStatus.BAD_REQUEST);
    }

    @Override
    public void resetPassword(final String email) {
        final Optional<AppUserEntity> appUser = appUserRepository.findOne(Example.of(new AppUserEntity().setEmail(email)));
        appUser.ifPresentOrElse(appUserEntity -> notificationService.generateAndSendTokenMessage(mapper.map(appUserEntity, AppUserDTO.class), NotificationType.PASSWORD_REST),
                () -> {
                    log.error("Not able to send reset password email for user: {}", email);
                    throw new SystemException("User does npt exist.", HttpStatus.BAD_REQUEST);
                });
    }

    private Set<AppUserDTO> getListOfAboutToExpireActivationLink() {
        return mapper.mapAsSet(appUserRepository.findAllByEmailVerified(false).stream()
                .filter(a -> a.getCreatedDate().plusDays(6).isBefore(LocalDateTime.now()))
                .collect(Collectors.toSet()), AppUserDTO.class);
    }

    private Set<AppUserDTO> searchByFirstName(final Object firstName) {
        return mapper.mapAsSet(appUserRepository.searchByFirstNameLike(wrapToSqlLikeStatement((String) firstName)), AppUserDTO.class);
    }

    private Set<AppUserDTO> searchByLastName(final Object lastName) {
        return mapper.mapAsSet(appUserRepository.searchByLastNameLike(wrapToSqlLikeStatement((String) lastName)), AppUserDTO.class);
    }

    private Set<AppUserDTO> searchByEmail(final Object email) {
        return mapper.mapAsSet(appUserRepository.searchByEmailLike(wrapToSqlLikeStatement((String) email)), AppUserDTO.class);
    }

    private Set<AppUserDTO> searchByUserRole(final Object userRole) {
        return mapper.mapAsSet(appUserRepository.searchByUserRole(UserRole.valueOf((String) userRole)), AppUserDTO.class);
    }

    private Set<AppUserDTO> searchByActive(final Object active) {
        return mapper.mapAsSet(appUserRepository.searchByActive(Boolean.valueOf((String) active)), AppUserDTO.class);
    }

    private Set<AppUserDTO> searchByEmailVerified(final Object emailVerified) {
        return mapper.mapAsSet(appUserRepository.searchByEmailVerified(Boolean.valueOf((String) emailVerified)), AppUserDTO.class);
    }
}
