package calendar.app.dto;

import jakarta.persistence.Column;
import lombok.Builder;
import lombok.Data;
import java.util.List;
import java.util.Set;

@Data
public class UserDTO {
    private Integer uId;
    private String firstname;
    private String secondname;
    private String email;
    private String password; // Added password
    private String role;
    @Column(nullable = true)
    private List<SimplifiedCompanyDTO> companies;
}
