package calendar.app.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.awt.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "message")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer messageId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MessageType name;

    private String description;

    private LocalDate date;

    private boolean mandatoryFlag;

    private String clientname;

    private String designation;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PriorityLevel priorityLevel;

    private boolean seen; // To track seen/unseen status

    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company company;

    @ManyToMany
    @JoinTable(
            name = "user_message",
            joinColumns = @JoinColumn(name = "message_id", referencedColumnName = "messageId"),
            inverseJoinColumns = @JoinColumn(name = "user_id", referencedColumnName = "uId")
    )
    private List<User> users = new ArrayList<>();

    @OneToMany(mappedBy = "message", cascade = CascadeType.ALL,orphanRemoval = true)
    private List<MessageUser> messageUsers = new ArrayList<>();

    public void markAsSeen() {
        this.seen = true;
    }

    public void markAsUnseen() {
        this.seen = false;
    }
}