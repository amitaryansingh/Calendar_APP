package calendar.app.entities;

import jakarta.persistence.Embeddable;
import lombok.Data;

import java.io.Serializable;
@Data
@Embeddable
public class MessageUserId implements Serializable {

    private Integer messageId;
    private Integer userId;

    // Default constructor, equals, hashCode
}