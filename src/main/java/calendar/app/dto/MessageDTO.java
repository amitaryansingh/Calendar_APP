package calendar.app.dto;

import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class MessageDTO {
    private Integer messageId;
    private String name;
    private String description;
    private LocalDate date;
    private boolean mandatoryFlag;
    private String clientName;
    private String designation;
    private String priorityLevel;
    private boolean seen;
    private Integer companyId;
    private List<Integer> userIds;

}
