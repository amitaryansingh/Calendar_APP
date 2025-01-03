package calendar.app.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "company")
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer mId;

    @Column(nullable = false)
    private String name;

    private String logoUrl;

    private String location;

    private String linkedInProfile;

    @ElementCollection
    private List<String> emails;

    @ElementCollection
    private List<String> phoneNumbers;

    private String comments;

    private String communicationPeriodicity;

    @ManyToMany(mappedBy = "companies")
    @JsonIgnoreProperties(value = "companies")
    private List<User> users=new ArrayList<>();

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL)
    @JsonIgnoreProperties(value = "company")
    private List<Message> messages;
}