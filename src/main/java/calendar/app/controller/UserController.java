package calendar.app.controller;

import calendar.app.dto.CompanyDTO;
import calendar.app.dto.MessageDTO;
import calendar.app.dto.UserDTO;
import calendar.app.entities.PriorityLevel;
import calendar.app.services.CompanyService;
import calendar.app.services.MessageService;
import calendar.app.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;


@RestController
@RequestMapping("/calendarapp/userReq")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final CompanyService companyService;
    private final MessageService messageService;

    // Fetch user profile by ID
    @GetMapping("/profile/{id}")
    public ResponseEntity<UserDTO> getUserProfile(@PathVariable Integer id) {
        UserDTO userDTO = userService.getUserById(id);
        return ResponseEntity.ok(userDTO);
    }

    // Get all Users
    @GetMapping("/users")
    public ResponseEntity<List<UserDTO>> getAllUsersforuser() {
        List<UserDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    // Get all Companies
    @GetMapping("/companies")
    public ResponseEntity<List<CompanyDTO>> getAllCompaniesforuser() {
        List<CompanyDTO> companies = companyService.getAllCompanies();
        return ResponseEntity.ok(companies);
    }

    // Get all messages
    @GetMapping("/messages")
    public ResponseEntity<List<MessageDTO>> getAllMessagesforuser() {
        List<MessageDTO> messages = messageService.getAllMessages();
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/user/id")
    public ResponseEntity<Integer> getUserIdByEmailforuser(@RequestParam String email) {
        System.out.println("Received email: " + email);
        UserDTO userDTO = userService.findByEmail(email);
            return ResponseEntity.ok(userDTO.getUId());
    }

    @GetMapping("/companies/{id}")
    public ResponseEntity<CompanyDTO> getCompanyByIdforuser(@PathVariable Integer id) {
        CompanyDTO company = companyService.getCompanyById(id);
        return ResponseEntity.ok(company);
    }

    @GetMapping("/users/{userId}/companies/{companyId}/messages")
    public ResponseEntity<List<MessageDTO>> getUserMessagesByCompanyforuser(
            @PathVariable Integer userId,
            @PathVariable Integer companyId) {
        List<MessageDTO> messages = userService.getUserMessagesByCompany(userId, companyId);
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/companies/{companyId}/users")
    public ResponseEntity<List<UserDTO>> getCompanyUsersforuser(@PathVariable Integer companyId) {
        List<UserDTO> users = companyService.getCompanyUsers(companyId);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/messages/{id}")
    public ResponseEntity<MessageDTO> getMessageByIdforuser(@PathVariable Integer id) {
        MessageDTO message = messageService.getMessageById(id);
        return ResponseEntity.ok(message);
    }

    // Get messages by user ID
    @GetMapping("message/user/{userId}")
    public ResponseEntity<List<MessageDTO>> getMessagesByUserIdforuser(@PathVariable Integer userId) {
        List<MessageDTO> messages = messageService.getMessagesByUserId(userId);
        return ResponseEntity.ok(messages);
    }

    // Get messages by company ID
    @GetMapping("message/company/{companyId}")
    public ResponseEntity<List<MessageDTO>> getMessagesByCompanyIdforuser(@PathVariable Integer companyId) {
        List<MessageDTO> messages = messageService.getMessagesByCompanyId(companyId);
        return ResponseEntity.ok(messages);
    }

    // Get messages by priority level
    @GetMapping("message/priority")
    public ResponseEntity<List<MessageDTO>> getMessagesByPriorityLevelforuser(@RequestParam PriorityLevel priorityLevel) {
        List<MessageDTO> messages = messageService.getMessagesByPriorityLevel(priorityLevel);
        return ResponseEntity.ok(messages);
    }

    // Get messages by seen/unseen status
    @GetMapping("message/seen")
    public ResponseEntity<List<MessageDTO>> getMessagesBySeenStatusforuser(@RequestParam boolean seen) {
        List<MessageDTO> messages = messageService.getMessagesBySeenStatus(seen);
        return ResponseEntity.ok(messages);
    }

    // Get messages within a specific date range
    @GetMapping("message/date-range")
    public ResponseEntity<List<MessageDTO>> getMessagesWithinDateRangeforuser(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {
        List<MessageDTO> messages = messageService.getMessagesWithinDateRange(startDate, endDate);
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/messages/user-company-not-seen")
    public ResponseEntity<List<MessageDTO>> getMessagesNotSeenByUserForCompanyforuser(
            @RequestParam Integer userId,
            @RequestParam Integer companyId) {

        List<MessageDTO> messages = messageService.getMessagesNotSeenByUserForCompany(userId, companyId);

        return ResponseEntity.ok(messages);
    }

    @PutMapping("/messages/{messageId}/seen")
    public ResponseEntity<Void> markMessageAsSeen(
            @PathVariable Integer messageId,
            @RequestParam Integer userId
    ) {
        messageService.markMessageAsSeen(messageId, userId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/messages/unseen/{userId}")
    public ResponseEntity<List<MessageDTO>> getUnseenMessagesByUser(@PathVariable Integer userId) {
        if (userId == null) {
            return ResponseEntity.badRequest().build();
        }
        try {
            List<MessageDTO> unseenMessages = messageService.getMessagesNotSeenByUser(userId);
            return ResponseEntity.ok(unseenMessages);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.emptyList());
        }
    }

    @GetMapping("/messages/seenstatus")
    public boolean getSeenStatusByMessageIDAndUserID(
            @RequestParam Integer messageId,
            @RequestParam Integer userId) {
        return messageService.isMessageSeenByUser(messageId, userId);
    }

    @PutMapping("/messages/status")
    public ResponseEntity<Void> updateSeenStatus(
            @RequestParam Integer messageId,
            @RequestParam Integer userId,
            @RequestParam boolean seen) {
        messageService.markMessageAsSeenOrUnseen(messageId, userId, seen);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/users/{userId}/companies")
    public ResponseEntity<List<CompanyDTO>> getUserCompaniesforuser(@PathVariable Integer userId) {
        List<CompanyDTO> companies = userService.getUserCompanies(userId);
        return ResponseEntity.ok(companies);
    }

}
