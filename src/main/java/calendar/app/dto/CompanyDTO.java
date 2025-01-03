package calendar.app.dto;

import lombok.Data;
import java.util.List;

@Data
public class CompanyDTO {
    private Integer mId;
    private String name;
    private String logoUrl;
    private String location;
    private String linkedInProfile;
    private List<String> emails;
    private List<String> phoneNumbers;
    private String comments;
    private String communicationPeriodicity;
    private List<SimplifiedUserDTO> users;
    private List<SimplifiedMessageDTO> messages;
}
