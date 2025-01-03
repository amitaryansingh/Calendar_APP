package calendar.app.services;

import calendar.app.dto.MessageDTO;
import calendar.app.entities.PriorityLevel;

import java.time.LocalDate;
import java.util.List;

public interface MessageService {

    // Create a new message
    MessageDTO createMessage(MessageDTO messageDTO);

    // Get a message by ID
    MessageDTO getMessageById(Integer id);

    // Update a message by ID
    MessageDTO updateMessage(Integer id, MessageDTO messageDetails);

    // Delete a message by ID
    void deleteMessage(Integer id);

    // Get all messages
    List<MessageDTO> getAllMessages();

    // Get messages associated with a specific user
    List<MessageDTO> getMessagesByUserId(Integer userId);

    // Get messages associated with a specific company
    List<MessageDTO> getMessagesByCompanyId(Integer companyId);

    // Get messages by priority level
    List<MessageDTO> getMessagesByPriorityLevel(PriorityLevel priorityLevel);

    // Get messages by seen/unseen status
    List<MessageDTO> getMessagesBySeenStatus(boolean seen);

    // Get messages within a specific date range
    List<MessageDTO> getMessagesWithinDateRange(LocalDate startDate, LocalDate endDate);

    // Assign users and a company to a message
    MessageDTO assignUsersAndCompanyToMessage(Integer messageId, Integer companyId, List<Integer> userIds);

    List<MessageDTO> getMessagesNotSeenByUserForCompany(Integer userId, Integer companyId);

    void markMessageAsSeen(Integer messageId, Integer userId);

    List<MessageDTO> getMessagesNotSeenByUser(Integer userId);

    boolean isMessageSeenByUser(Integer messageId, Integer userId);

    void markMessageAsSeenOrUnseen(Integer messageId, Integer userId, boolean seen);
}
