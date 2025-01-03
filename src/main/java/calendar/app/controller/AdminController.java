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
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/calendarapp/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;
    private final CompanyService companyService;
    private final MessageService messageService;

    // CRUD operations for User

    // Create a new User
    @PostMapping("/users")
    public ResponseEntity<UserDTO> createUser(@RequestBody UserDTO userDTO) {
        UserDTO createdUser = userService.createUser(userDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    // Get all Users
    @GetMapping("/users")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }


    // Get a User by ID
    @GetMapping("/users/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Integer id) {
        UserDTO user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    // Update a User by ID
    @PutMapping("/users/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Integer id, @RequestBody UserDTO userDTO) {
        UserDTO updatedUser = userService.updateUser(id, userDTO);
        return ResponseEntity.ok(updatedUser);
    }

    // Delete a User by ID
    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Integer id) {
        userService.deleteUserWithMessages(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    // CRUD operations for Company

    // Create a new Company
    @PostMapping("/companies")
    public ResponseEntity<CompanyDTO> createCompany(@RequestBody CompanyDTO companyDTO) {
        CompanyDTO createdCompany = companyService.createCompany(companyDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCompany);
    }

    // Get all Companies
    @GetMapping("/companies")
    public ResponseEntity<List<CompanyDTO>> getAllCompanies() {
        List<CompanyDTO> companies = companyService.getAllCompanies();
        return ResponseEntity.ok(companies);
    }

    // Get a Company by ID
    @GetMapping("/companies/{id}")
    public ResponseEntity<CompanyDTO> getCompanyById(@PathVariable Integer id) {
        CompanyDTO company = companyService.getCompanyById(id);
        return ResponseEntity.ok(company);
    }

    // Update an existing Company
    @PutMapping("/companies/{id}")
    public ResponseEntity<CompanyDTO> updateCompany(@PathVariable Integer id, @RequestBody CompanyDTO companyDTO) {
        CompanyDTO updatedCompany = companyService.updateCompany(id, companyDTO);
        return ResponseEntity.ok(updatedCompany);
    }

    // Delete a Company
    @DeleteMapping("/companies/{id}")
    public ResponseEntity<Void> deleteCompany(@PathVariable Integer id) {
        companyService.deleteCompanyWithMessages(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    // Assign a User to a Company
    @PostMapping("/companies/{companyId}/assignUser/{userId}")
    public ResponseEntity<CompanyDTO> assignUserToCompany(
            @PathVariable Integer companyId,
            @PathVariable Integer userId
    ) {
        CompanyDTO updatedCompany = companyService.assignUserToCompany(companyId, userId);
        return ResponseEntity.ok(updatedCompany);
    }

    // Remove a User from a Company
    @DeleteMapping("/companies/{companyId}/removeUser/{userId}")
    public ResponseEntity<CompanyDTO> removeUserFromCompany(
            @PathVariable Integer companyId,
            @PathVariable Integer userId
    ) {
        CompanyDTO updatedCompany = companyService.removeUserFromCompany(companyId, userId);
        return ResponseEntity.ok(updatedCompany);
    }

    // Assign a Company to a User
    @PostMapping("/users/{userId}/assignCompany/{companyId}")
    public ResponseEntity<UserDTO> assignCompanyToUser(
            @PathVariable Integer userId,
            @PathVariable Integer companyId
    ) {
        UserDTO updatedUser = userService.assignCompanyToUser(userId, companyId);
        return ResponseEntity.ok(updatedUser);
    }

    // Remove a Company from a User
    @DeleteMapping("/users/{userId}/removeCompany/{companyId}")
    public ResponseEntity<UserDTO> removeCompanyFromUser(
            @PathVariable Integer userId,
            @PathVariable Integer companyId
    ) {
        UserDTO updatedUser = userService.removeCompanyFromUser(userId, companyId);
        return ResponseEntity.ok(updatedUser);
    }

    @GetMapping("/users/{userId}/companies")
    public ResponseEntity<List<CompanyDTO>> getUserCompanies(@PathVariable Integer userId) {
        List<CompanyDTO> companies = userService.getUserCompanies(userId);
        return ResponseEntity.ok(companies);
    }

    @GetMapping("/users/{userId}/companies/{companyId}/messages")
    public ResponseEntity<List<MessageDTO>> getUserMessagesByCompany(
            @PathVariable Integer userId,
            @PathVariable Integer companyId) {
        List<MessageDTO> messages = userService.getUserMessagesByCompany(userId, companyId);
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/companies/{companyId}/users")
    public ResponseEntity<List<UserDTO>> getCompanyUsers(@PathVariable Integer companyId) {
        List<UserDTO> users = companyService.getCompanyUsers(companyId);
        return ResponseEntity.ok(users);
    }

    @PostMapping("/companies/{companyId}/assignUsers")
    public ResponseEntity<CompanyDTO> assignMultipleUsersToCompany(
            @PathVariable Integer companyId,
            @RequestBody List<Integer> userIds
    ) {
        CompanyDTO updatedCompany = companyService.assignMultipleUsersToCompany(companyId, userIds);
        return ResponseEntity.ok(updatedCompany);
    }

    @PostMapping("/users/{userId}/assignCompanies")
    public ResponseEntity<UserDTO> assignMultipleCompaniesToUser(
            @PathVariable Integer userId,
            @RequestBody List<Integer> companyIds
    ) {
        UserDTO updatedUser = userService.assignMultipleCompaniesToUser(userId, companyIds);
        return ResponseEntity.ok(updatedUser);
    }


@PostMapping("/messages")
public ResponseEntity<MessageDTO> createMessage(@RequestBody MessageDTO messageDTO) {
    MessageDTO createdMessage = messageService.createMessage(messageDTO);
    return ResponseEntity.status(201).body(createdMessage);
}

// Get a message by ID
@GetMapping("/messages/{id}")
public ResponseEntity<MessageDTO> getMessageById(@PathVariable Integer id) {
    MessageDTO message = messageService.getMessageById(id);
    return ResponseEntity.ok(message);
}

// Update a message by ID
@PutMapping("messages/{id}")
public ResponseEntity<MessageDTO> updateMessage(@PathVariable Integer id, @RequestBody MessageDTO messageDTO) {
    MessageDTO updatedMessage = messageService.updateMessage(id, messageDTO);
    return ResponseEntity.ok(updatedMessage);
}

// Delete a message by ID
@DeleteMapping("messages/{id}")
public ResponseEntity<Void> deleteMessage(@PathVariable Integer id) {
    messageService.deleteMessage(id);
    return ResponseEntity.noContent().build();
}

// Get all messages
@GetMapping("/messages")
public ResponseEntity<List<MessageDTO>> getAllMessages() {
    List<MessageDTO> messages = messageService.getAllMessages();
    return ResponseEntity.ok(messages);
}

// Assign users and a company to a message
@PostMapping("messages/{messageId}/assign")
public ResponseEntity<MessageDTO> assignUsersAndCompanyToMessage(
        @PathVariable Integer messageId,
        @RequestParam(required = false) Integer companyId,
        @RequestBody(required = false) List<Integer> userIds) {
    MessageDTO updatedMessage = messageService.assignUsersAndCompanyToMessage(messageId, companyId, userIds);
    return ResponseEntity.ok(updatedMessage);
}

// Get messages by user ID
@GetMapping("message/user/{userId}")
public ResponseEntity<List<MessageDTO>> getMessagesByUserId(@PathVariable Integer userId) {
    List<MessageDTO> messages = messageService.getMessagesByUserId(userId);
    return ResponseEntity.ok(messages);
}

// Get messages by company ID
@GetMapping("message/company/{companyId}")
public ResponseEntity<List<MessageDTO>> getMessagesByCompanyId(@PathVariable Integer companyId) {
    List<MessageDTO> messages = messageService.getMessagesByCompanyId(companyId);
    return ResponseEntity.ok(messages);
}

// Get messages by priority level
@GetMapping("message/priority")
public ResponseEntity<List<MessageDTO>> getMessagesByPriorityLevel(@RequestParam PriorityLevel priorityLevel) {
    List<MessageDTO> messages = messageService.getMessagesByPriorityLevel(priorityLevel);
    return ResponseEntity.ok(messages);
}

// Get messages by seen/unseen status
@GetMapping("message/seen")
public ResponseEntity<List<MessageDTO>> getMessagesBySeenStatus(@RequestParam boolean seen) {
    List<MessageDTO> messages = messageService.getMessagesBySeenStatus(seen);
    return ResponseEntity.ok(messages);
}

// Get messages within a specific date range
@GetMapping("message/date-range")
public ResponseEntity<List<MessageDTO>> getMessagesWithinDateRange(
        @RequestParam LocalDate startDate,
        @RequestParam LocalDate endDate) {
    List<MessageDTO> messages = messageService.getMessagesWithinDateRange(startDate, endDate);
    return ResponseEntity.ok(messages);
}

    @GetMapping("/messages/user-company-not-seen")
    public ResponseEntity<List<MessageDTO>> getMessagesNotSeenByUserForCompany(
            @RequestParam Integer userId,
            @RequestParam Integer companyId) {

        List<MessageDTO> messages = messageService.getMessagesNotSeenByUserForCompany(userId, companyId);

        return ResponseEntity.ok(messages);
    }

}