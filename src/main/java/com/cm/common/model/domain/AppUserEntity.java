package com.cm.common.model.domain;

import com.cm.common.adapter.CertificateKeysListAdapter;
import com.cm.common.adapter.UserRoleAdapter;
import com.cm.common.classifiers.Searchable;
import com.cm.common.model.enumeration.UserRole;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@NamedNativeQueries(value = {
        @NamedNativeQuery(name = "getUserAuthorityForCourse", query = " SELECT cua.authorities " +
                " FROM management.course_user_authority cua WHERE cua.user_id = :userId AND course_id = :courseId")
})


@Getter
@Setter
@Accessors(chain = true)
@Entity(name = "app_user")
@Table(schema = "management")
@FilterDef(name = "activeFilter", parameters = @ParamDef(name = "isActive", type = "boolean"))
@Filter(name = "activeFilter", condition = "active=:isActive")
public class AppUserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "first_name")
    private String firstName;
    @Column(name = "last_name")
    private String lastName;
    @Column(name = "email")
    private String email;
    @Column(name = "password")
    private String password;
    @Column(name = "role")
    @Convert(converter = UserRoleAdapter.class)
    private UserRole userRole;
    @Convert(converter = CertificateKeysListAdapter.class)
    @Column(name = "certificate_keys")
    private List<String> certificateKeys;
    @Column(name = "active")
    private boolean active;
    @Column(name = "email_verified")
    private boolean emailVerified;
    @Column(name = "created_date")
    private LocalDateTime createdDate;
    @Column(name = "updated_date")
    private LocalDateTime updatedDate;


    @PrePersist
    void prePersist() {
        if (Objects.isNull(createdDate)) {
            createdDate = LocalDateTime.now();
        }
    }


    @PreUpdate
    void preUpdate() {
        if (Objects.isNull(updatedDate)) {
            updatedDate = LocalDateTime.now();
        }
    }


}
