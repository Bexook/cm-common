package com.cm.common.service.user;

import com.cm.common.classifiers.SearchCriteria;
import com.cm.common.classifiers.Searchable;
import com.cm.common.model.dto.AppUserDTO;
import com.cm.common.model.dto.ScheduledJobReportDTO;
import com.cm.common.model.enumeration.CourseAuthorities;
import com.cm.common.model.enumeration.UserRole;
import lombok.NonNull;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface AppUserService extends Searchable {

    void createUserByAdmin(final AppUserDTO appUser);

    boolean activateUserAccount(final String userToken);

    void deactivateUserAccount(final Long userId);

    List<CourseAuthorities> getUserAuthorityForCourse(final Long useId, final Long courseId);

    AppUserDTO getUserById(final Long id);

    void userRegistration(@NonNull AppUserDTO userEntity);

    void deleteById(Long id);

    List<AppUserDTO> getCourseUsersByCourseIdAndUserRole(final Long courseId, final UserRole userRole);

    AppUserDTO findByEmail(String email);

    List<AppUserDTO> findAll(boolean isActive);

    AppUserDTO update(final AppUserDTO userDTO);

    ScheduledJobReportDTO dropNotVerifiedUsers();

    ScheduledJobReportDTO sendAccountDeletionWarningNotification();

    Set<AppUserDTO> searchUserByCriteria(final Map<? extends SearchCriteria, Object> criteria);

    boolean existsByEmail(final String email);

    boolean updatePassword(final String resetToken, final String newPassword);

    void resetPassword(final String email);
}
