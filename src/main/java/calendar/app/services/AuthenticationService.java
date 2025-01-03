package calendar.app.services;

import calendar.app.dto.JwtAuthenticationResponse;
import calendar.app.dto.RefreshTokenRequest;
import calendar.app.dto.SignInRequest;
import calendar.app.dto.SignUpRequest;
import calendar.app.dto.UserDTO;

public interface AuthenticationService {

    UserDTO signup(SignUpRequest signUpRequest);

    JwtAuthenticationResponse signin(SignInRequest signInRequest);

    JwtAuthenticationResponse refreshToken(RefreshTokenRequest refreshTokenRequest);
}
