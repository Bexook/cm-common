package com.cm.common.model.dto;

import com.cm.common.model.enumeration.UserRole;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.List;

import static com.cm.common.constant.ApplicationValidationConstants.*;

@Data
public class AppUserDTO {

    private Long id;
    @Pattern(regexp = USER_NAMES_REGEX, message = "First name does not match requirements")
    @NotBlank(message = "User first name is mandatory")
    private String firstName;
    @Pattern(regexp = USER_NAMES_REGEX,  message = "Last name does not match requirements")
    @NotBlank(message = "User last name is mandatory")
    private String lastName;
    @Pattern(regexp = EMAIL_REGEX, message = "Email does not match requirements")
    @NotBlank(message = "User email is mandatory")
    private String email;
    @Pattern(regexp = PASSWORD_REGEX, message = "Password does not match requirements")
    @NotBlank(message = "User password is mandatory")
    private String password;
    private UserRole userRole;
    private List<String> certificateKeys;
    private boolean active;
    private boolean emailVerified;

}
