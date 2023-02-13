package com.cm.common.repository;

import com.cm.common.model.domain.AppUserEntity;
import com.cm.common.model.enumeration.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface AppUserRepository extends JpaRepository<AppUserEntity, Long> {

    @Query(value = "SELECT * FROM management.app_user au WHERE au.first_name LIKE :firstName", nativeQuery = true)
    Set<AppUserEntity> searchByFirstNameLike(final String firstName);

    @Query(value = "SELECT * FROM management.app_user au WHERE au.last_name LIKE :lastName", nativeQuery = true)
    Set<AppUserEntity> searchByLastNameLike(final String lastName);

    @Query(value = "SELECT * FROM management.app_user au WHERE au.email LIKE :email", nativeQuery = true)
    Set<AppUserEntity> searchByEmailLike(final String email);


    @Query(value = "SELECT * FROM management.app_user au WHERE au.active = :active", nativeQuery = true)
    Set<AppUserEntity> searchByActive(final Boolean active);

    @Query(value = "SELECT * FROM management.app_user au WHERE au.email_verified = :active", nativeQuery = true)
    Set<AppUserEntity> searchByEmailVerified(final Boolean active);

    @Query(value = "SELECT * FROM management.app_user au WHERE au.role = :#{#userRole.getCode()}", nativeQuery = true)
    Set<AppUserEntity> searchByUserRole(final UserRole userRole);


    List<AppUserEntity> findAllByEmailVerified(final Boolean emailVerified);

    @Query(value = "SELECT * FROM management.app_user au " +
            " LEFT JOIN management.app_user_course_reference aucr ON au.id = aucr.app_user_id WHERE au.role = :#{#userRole.getCode()} AND aucr.course_id = :courseId", nativeQuery = true)
    List<AppUserEntity> getCourseUsersByCourseIdAndUserRole(@Param("courseId") final Long courseId, @Param("userRole") final UserRole userRole);

    AppUserEntity findByEmail(final String email);

    @Query(name = "getUserAuthorityForCourse", nativeQuery = true)
    String getUserAuthorityForCourse(@Param("userId") final Long userId, @Param("courseId") final Long courseId);

    @Modifying
    @Query(value = "UPDATE management.app_user SET active = :activeFlag, email_verified = :activeFlag WHERE id = :userId", nativeQuery = true)
    void updateAccountActiveFlag(@Param("userId") final Long userId, @Param("activeFlag") final Boolean activeFlag);

    @Modifying
    @Query(value = "UPDATE management.app_user SET password = :newPassword WHERE id = :userId", nativeQuery = true)
    void updateUserPasswordById(@Param("newPassword") final String newPassword, @Param("userId") final Long userId);


}