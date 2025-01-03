package calendar.app.dto;

import lombok.Data;

@Data
public class SignInRequest {

    private String email;
    private String password;

    public String getPassword() {
        return password;
    }


    public String getEmail() {
        return email;
    }



}
