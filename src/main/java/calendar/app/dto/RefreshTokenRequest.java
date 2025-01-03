package calendar.app.dto;

public class RefreshTokenRequest {

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    private String token;
}
