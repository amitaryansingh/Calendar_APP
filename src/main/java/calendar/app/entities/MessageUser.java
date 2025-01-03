package calendar.app.entities;


import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "message_user")
public class MessageUser {

    @EmbeddedId
    private MessageUserId id;

    @ManyToOne
    @MapsId("messageId")
    private Message message;

    @ManyToOne
    @MapsId("userId")
    private User user;

    @Column(nullable = false)
    private boolean seen;
}
