package calendar.app.controller;

import calendar.app.dto.JwtAuthenticationResponse;
import calendar.app.dto.RefreshTokenRequest;
import calendar.app.dto.SignInRequest;
import calendar.app.dto.SignUpRequest;
import calendar.app.dto.UserDTO;
import calendar.app.services.AuthenticationService;
import calendar.app.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/calendarapp/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final UserService userService;
    private final AuthenticationService authenticationService;

    @PostMapping("/signup")
    public ResponseEntity<UserDTO> signup(@RequestBody SignUpRequest signUpRequest) {
        UserDTO userDTO = authenticationService.signup(signUpRequest);
        return ResponseEntity.ok(userDTO);
    }

    @PostMapping("/signin")
    public ResponseEntity<JwtAuthenticationResponse> signin(@RequestBody SignInRequest signInRequest) {
        return ResponseEntity.ok(authenticationService.signin(signInRequest));
    }

    @PostMapping("/refresh")
    public ResponseEntity<JwtAuthenticationResponse> refresh(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        return ResponseEntity.ok(authenticationService.refreshToken(refreshTokenRequest));
    }

    @GetMapping("/users/role")
    public ResponseEntity<String> getUserRoleByEmail(@RequestParam String email) {
        System.out.println("Received email: " + email);
        UserDTO userDTO = userService.findByEmail(email);
        if (userDTO != null) {
            return ResponseEntity.ok(userDTO.getRole());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
    }
}
