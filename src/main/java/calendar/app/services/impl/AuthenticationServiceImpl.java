package calendar.app.services.impl;

import calendar.app.dto.JwtAuthenticationResponse;
import calendar.app.dto.RefreshTokenRequest;
import calendar.app.dto.SignInRequest;
import calendar.app.dto.SignUpRequest;
import calendar.app.dto.UserDTO;
import calendar.app.entities.Role;
import calendar.app.entities.User;
import calendar.app.repository.UserRepository;
import calendar.app.services.AuthenticationService;
import calendar.app.services.JWTService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;

    @Override
    public UserDTO signup(SignUpRequest signUpRequest) {
        if (userRepository.findByEmail(signUpRequest.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email is already registered");
        }

        User user = new User();
        user.setEmail(signUpRequest.getEmail());
        user.setFirstname(signUpRequest.getFirstname());
        user.setSecondname(signUpRequest.getLastname());
        user.setRole(Role.USER);
        user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));

        User savedUser = userRepository.save(user);

        return convertToDTO(savedUser);
    }

    @Override
    public JwtAuthenticationResponse signin(SignInRequest signInRequest) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                signInRequest.getEmail(), signInRequest.getPassword()));

        User user = userRepository.findByEmail(signInRequest.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid Email or Password"));

        String jwt = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(new HashMap<>(), user);

        return JwtAuthenticationResponse.builder()
                .token(jwt)
                .refreshtoken(refreshToken)
                .build();
    }

    @Override
    public JwtAuthenticationResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {
        String userEmail = jwtService.extractUserName(refreshTokenRequest.getToken());
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (jwtService.isTokenValid(refreshTokenRequest.getToken(), user)) {
            String jwt = jwtService.generateToken(user);

            return JwtAuthenticationResponse.builder()
                    .token(jwt)
                    .refreshtoken(refreshTokenRequest.getToken())
                    .build();
        }

        throw new IllegalArgumentException("Invalid refresh token");
    }

    private UserDTO convertToDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setUId(user.getUId());
        userDTO.setFirstname(user.getFirstname());
        userDTO.setSecondname(user.getSecondname());
        userDTO.setEmail(user.getEmail());
        userDTO.setPassword(null); // Do not expose the password
        userDTO.setRole(user.getRole().name());
        return userDTO;
    }
}
