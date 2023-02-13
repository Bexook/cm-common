package com.cm.common.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import static com.cm.common.constant.ApplicationValidationConstants.EMAIL_REGEX;
import static com.cm.common.constant.ApplicationValidationConstants.PASSWORD_REGEX;

@Data
public class UserCredentialsDTO {

    @NotNull
    @Pattern(regexp = EMAIL_REGEX)
    @JsonProperty("login")
    private String login;
    @NotNull
    @Pattern(regexp = PASSWORD_REGEX)
    @JsonProperty("password")
    private String password;

}
