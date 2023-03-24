package com.cm.common.controller;

import com.cm.common.model.dto.AppUserDTO;
import com.cm.common.security.AppUserDetails;
import com.cm.common.service.user.AppUserService;
import com.cm.common.util.AuthorizationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.net.UnknownHostException;
import java.util.List;

import static com.cm.common.constant.ApplicationConstants.PASSWORD_REGEX;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user/account")
public class UserAccountOperationResource {

    @Autowired
    private final AppUserService userService;

    @GetMapping("/me")
    public ResponseEntity<AppUserDetails> getCurrentUser() {
        return ResponseEntity.ok().body((AppUserDetails) AuthorizationUtil.getCurrentUser());
    }

    @PostMapping("/register")
    public void registerUser(@RequestBody  final AppUserDTO user) {
        userService.userRegistration(user);
    }


    @PostMapping("/password/reset/{resetToken}")
    public ResponseEntity<Boolean> updatePassword(@PathVariable("resetToken") final String resetToken,
                                                  @RequestBody @Pattern(regexp = PASSWORD_REGEX, message = "Password does not match requirements")
                                                  @NotBlank(message = "User password is mandatory") final String newPassword) {
        return ResponseEntity.ok().body(userService.updatePassword(resetToken, newPassword));
    }

    @PostMapping("/password/reset")
    public void passwordRest(@RequestParam("email") final String email) {
        userService.resetPassword(email);
    }

    @PostMapping("/delete")
    public void deactivateUserAccount(@RequestParam(name = "userId") final Long userId) {
        userService.deactivateUserAccount(userId);
    }

    @PostMapping("/deactivate")
    public void deleteUser(@RequestParam(name = "userId") final Long id) {
        userService.deactivateUserAccount(id);
    }

    @GetMapping("/get")
    public ResponseEntity<AppUserDTO> getUserByEmail(@RequestParam(name = "email") final String email) {
        return ResponseEntity.ok().body(userService.findByEmail(email));
    }

    @PostMapping("/activate/{activationToken}")
    public ResponseEntity<Boolean> activateAccount(@PathVariable final String activationToken) {
        return ResponseEntity.ok().body(userService.activateUserAccount(activationToken));
    }


    @GetMapping("/list")
    public ResponseEntity<List<AppUserDTO>> getUsers() {
        return ResponseEntity.ok().body(userService.findAll(true));
    }


}
