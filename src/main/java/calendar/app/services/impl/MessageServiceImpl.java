package calendar.app.services.impl;

import calendar.app.dto.MessageDTO;
import calendar.app.entities.*;
import calendar.app.repository.CompanyRepository;
import calendar.app.repository.MessageRepository;
import calendar.app.repository.MessageUserRepository;
import calendar.app.repository.UserRepository;
import calendar.app.services.MessageService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final MessageUserRepository messageUserRepository;

    @Override
    public MessageDTO createMessage(MessageDTO messageDTO) {
        if (messageDTO.getName() == null) {
            throw new IllegalArgumentException("Message name cannot be null");
        }
        if (messageDTO.getPriorityLevel() == null) {
            throw new IllegalArgumentException("Priority level cannot be null");
        }

        Message message = new Message();
        message.setName(MessageType.valueOf(messageDTO.getName()));
        message.setDescription(messageDTO.getDescription());
        message.setDate(messageDTO.getDate());
        message.setMandatoryFlag(messageDTO.isMandatoryFlag());
        message.setClientname(messageDTO.getClientName());
        message.setDesignation(messageDTO.getDesignation());
        message.setPriorityLevel(PriorityLevel.valueOf(messageDTO.getPriorityLevel()));
        message.setSeen(messageDTO.isSeen());

        Message savedMessage = messageRepository.save(message);
        return convertToDTO(savedMessage);
    }

    @Override
    public MessageDTO getMessageById(Integer id) {
        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Message with ID " + id + " not found"));
        return convertToDTO(message);
    }

    @Override
    public MessageDTO updateMessage(Integer id, MessageDTO messageDetails) {
        Message existingMessage = messageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Message with ID " + id + " not found"));

        if (messageDetails.getName() != null) {
            existingMessage.setName(MessageType.valueOf(messageDetails.getName()));
        }
        if (messageDetails.getDescription() != null) {
            existingMessage.setDescription(messageDetails.getDescription());
        }
        if (messageDetails.getDate() != null) {
            existingMessage.setDate(messageDetails.getDate());
        }
        existingMessage.setMandatoryFlag(messageDetails.isMandatoryFlag());
        if (messageDetails.getPriorityLevel() != null) {
            existingMessage.setPriorityLevel(PriorityLevel.valueOf(messageDetails.getPriorityLevel()));
        }
        existingMessage.setSeen(messageDetails.isSeen());

        Message updatedMessage = messageRepository.save(existingMessage);
        return convertToDTO(updatedMessage);
    }

    @Override
    public void deleteMessage(Integer id) {
        if (!messageRepository.existsById(id)) {
            throw new RuntimeException("Message with ID " + id + " not found");
        }
        messageRepository.deleteById(id);
    }

    @Override
    public List<MessageDTO> getAllMessages() {
        return messageRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<MessageDTO> getMessagesByUserId(Integer userId) {
        return messageRepository.findByUserId(userId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<MessageDTO> getMessagesByCompanyId(Integer companyId) {
        return messageRepository.findByCompanyId(companyId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<MessageDTO> getMessagesByPriorityLevel(PriorityLevel priorityLevel) {
        return messageRepository.findByPriorityLevel(priorityLevel)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<MessageDTO> getMessagesBySeenStatus(boolean seen) {
        return messageRepository.findBySeen(seen)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<MessageDTO> getMessagesWithinDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Start date and end date cannot be null");
        }
        return messageRepository.findMessagesWithinDateRange(startDate, endDate)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

//    @Override
//    public MessageDTO assignUsersAndCompanyToMessage(Integer messageId, Integer companyId, List<Integer> userIds) {
//        Message message = messageRepository.findById(messageId)
//                .orElseThrow(() -> new RuntimeException("Message with ID " + messageId + " not found"));
//
//        if (companyId != null) {
//            Company company = companyRepository.findById(companyId)
//                    .orElseThrow(() -> new RuntimeException("Company with ID " + companyId + " not found"));
//            message.setCompany(company);
//        }
//
//        if (userIds != null && !userIds.isEmpty()) {
//            List<User> users = userRepository.findAllById(userIds);
//
//            if (users.isEmpty()) {
//                throw new RuntimeException("No valid users found for the provided IDs");
//            }
//
//            List<User> newUsers = users.stream()
//                    .filter(user -> !message.getUsers().contains(user))
//                    .collect(Collectors.toList());
//
//            if (newUsers.isEmpty()) {
//                throw new RuntimeException("All provided users are already associated with the message");
//            }
//
//            message.getUsers().addAll(newUsers);
//        }
//
//        Message updatedMessage = messageRepository.save(message);
//
//        return convertToDTO(updatedMessage);
//    }

    @Override
    public MessageDTO assignUsersAndCompanyToMessage(Integer messageId, Integer companyId, List<Integer> userIds) {
        // Fetch the message
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message with ID " + messageId + " not found"));

        // Assign the company to the message, if provided
        if (companyId != null) {
            Company company = companyRepository.findById(companyId)
                    .orElseThrow(() -> new RuntimeException("Company with ID " + companyId + " not found"));
            message.setCompany(company);
        }

        // Assign users to the message and update MessageUser table
        if (userIds != null && !userIds.isEmpty()) {
            List<User> users = userRepository.findAllById(userIds);

            if (users.isEmpty()) {
                throw new RuntimeException("No valid users found for the provided IDs");
            }

            // Filter out users already associated with the message
            List<User> newUsers = users.stream()
                    .filter(user -> !message.getUsers().contains(user))
                    .collect(Collectors.toList());

            if (newUsers.isEmpty()) {
                throw new RuntimeException("All provided users are already associated with the message");
            }

            // Add new users to the message
            message.getUsers().addAll(newUsers);

            // Update MessageUser table with seen = false
            for (User user : newUsers) {
                MessageUserId messageUserId = new MessageUserId();
                messageUserId.setMessageId(messageId);
                messageUserId.setUserId(user.getUId());

                MessageUser messageUser = new MessageUser();
                messageUser.setId(messageUserId);
                messageUser.setMessage(message);
                messageUser.setUser(user);
                messageUser.setSeen(false);

                messageUserRepository.save(messageUser);
            }
        }

        // Save updated message
        Message updatedMessage = messageRepository.save(message);

        // Convert to DTO for response
        return convertToDTO(updatedMessage);
    }



    // Map Message to MessageDTO
    private MessageDTO convertToDTO(Message message) {
        MessageDTO dto = new MessageDTO();
        dto.setMessageId(message.getMessageId());
        dto.setName(message.getName().name());
        dto.setDescription(message.getDescription());
        dto.setDate(message.getDate());
        dto.setMandatoryFlag(message.isMandatoryFlag());
        dto.setClientName(message.getClientname());
        dto.setDesignation(message.getDesignation());
        dto.setPriorityLevel(message.getPriorityLevel().name());
        dto.setSeen(message.isSeen());
        dto.setCompanyId(message.getCompany() != null ? message.getCompany().getMId() : null);
        dto.setUserIds(message.getUsers().stream()
                .map(User::getUId)
                .collect(Collectors.toList()));
        return dto;
    }

    @Override
    public List<MessageDTO> getMessagesNotSeenByUserForCompany(Integer userId, Integer companyId) {
        if (userId == null || companyId == null) {
            throw new IllegalArgumentException("User ID and Company ID cannot be null");
        }

        List<Message> messages = messageUserRepository.findMessagesByUserAndCompanyAndSeenStatus(userId, companyId, false);

        return messages.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public void markMessageAsSeen(Integer messageId, Integer userId) {
        MessageUserId messageUserId = new MessageUserId();
        messageUserId.setMessageId(messageId);
        messageUserId.setUserId(userId);

        MessageUser messageUser = messageUserRepository.findById(messageUserId)
                .orElseThrow(() -> new RuntimeException("MessageUser entry not found for messageId: " + messageId + " and userId: " + userId));

        messageUser.setSeen(true);
        messageUserRepository.save(messageUser);
    }


    @Transactional
    @Override
    public void markMessageAsSeenOrUnseen(Integer messageId, Integer userId, boolean seen) {
        MessageUserId messageUserId = new MessageUserId();
        messageUserId.setMessageId(messageId);
        messageUserId.setUserId(userId);

        MessageUser messageUser = messageUserRepository.findById(messageUserId)
                .orElseThrow(() -> new RuntimeException("MessageUser entry not found for messageId: " + messageId + " and userId: " + userId));

        messageUser.setSeen(seen);
        messageUserRepository.save(messageUser);
    }


    @Override
    public List<MessageDTO> getMessagesNotSeenByUser(Integer userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }

        // Fetch messages not seen by the user
        List<Message> messages = messageUserRepository.findMessagesByUserIdAndSeenStatus(userId);

        // Convert messages to DTOs
        return messages.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public boolean isMessageSeenByUser(Integer messageId, Integer userId) {
        return messageUserRepository.findSeenStatusByMessageIdAndUserId(messageId, userId);
    }
}
