package com.cm.common.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import static com.cm.common.constant.ApplicationConstants.EMAIL_REGEX;
import static com.cm.common.constant.ApplicationConstants.PASSWORD_REGEX;

@Data
public class UserCredentialsDTO {

    @NotNull
    @JsonProperty("login")
    private String login;
    @NotNull
    @JsonProperty("password")
    private String password;

}
