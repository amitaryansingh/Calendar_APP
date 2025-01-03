package calendar.app.services.impl;

import calendar.app.dto.*;
import calendar.app.entities.Company;
import calendar.app.entities.Message;
import calendar.app.entities.User;
import calendar.app.repository.CompanyRepository;
import calendar.app.repository.UserRepository;
import calendar.app.services.CompanyService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public CompanyDTO createCompany(CompanyDTO companyDTO) {
        Company company = convertToEntity(companyDTO);
        return convertToDTO(companyRepository.save(company));
    }

    @Override
    public CompanyDTO getCompanyById(Integer id) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Company not found"));
        return convertToDTO(company);
    }

    @Override
    public CompanyDTO updateCompany(Integer id, CompanyDTO companyDetails) {
        Company existingCompany = companyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Company not found"));

        if (companyDetails.getName() != null) {
            existingCompany.setName(companyDetails.getName());
        }
        if (companyDetails.getLogoUrl() != null) {
            existingCompany.setLogoUrl(companyDetails.getLogoUrl());
        }
        if (companyDetails.getLocation() != null) {
            existingCompany.setLocation(companyDetails.getLocation());
        }
        if (companyDetails.getLinkedInProfile() != null) {
            existingCompany.setLinkedInProfile(companyDetails.getLinkedInProfile());
        }
        if (companyDetails.getEmails() != null) {
            existingCompany.setEmails(companyDetails.getEmails());
        }
        if (companyDetails.getPhoneNumbers() != null) {
            existingCompany.setPhoneNumbers(companyDetails.getPhoneNumbers());
        }
        if (companyDetails.getComments() != null) {
            existingCompany.setComments(companyDetails.getComments());
        }
        if (companyDetails.getCommunicationPeriodicity() != null) {
            existingCompany.setCommunicationPeriodicity(companyDetails.getCommunicationPeriodicity());
        }

        return convertToDTO(companyRepository.save(existingCompany));
    }

    @Override
    public void deleteCompany(Integer id) {
        if (!companyRepository.existsById(id)) {
            throw new RuntimeException("Company not found");
        }
        companyRepository.deleteById(id);
    }

    @Transactional // Ensure the method runs within a transaction
    @Override
    public void deleteCompanyWithMessages(Integer id) {
        // First, remove the associations in the user_message table
        String deleteQuery = "DELETE FROM user_company WHERE company_id =:companyId";

        Query query = entityManager.createNativeQuery(deleteQuery);
        query.setParameter("companyId", id);
        query.executeUpdate();

        // Now delete the user
        companyRepository.deleteById(id);
    }

    @Override
    public List<CompanyDTO> getAllCompanies() {
        return companyRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public CompanyDTO assignUserToCompany(Integer companyId, Integer userId) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

//        company.getUsers().add(user);
//        companyRepository.save(company);

        // Check if the user is already associated with the company
        if (!company.getUsers().contains(user)) {
            company.getUsers().add(user); // Add user to company
            user.getCompanies().add(company); // Add company to user (bidirectional update)
        } else {
            throw new RuntimeException("User is already associated with this company");
        }

        // Save both entities
        userRepository.save(user); // Save user to update the association
        return convertToDTO(companyRepository.save(company)); // Save company and return updated DTO
        //return convertToDTO(company);
    }

    @Override
    public CompanyDTO removeUserFromCompany(Integer companyId, Integer userId) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        company.getUsers().remove(user);
        user.getCompanies().remove(company);
        userRepository.save(user);
        companyRepository.save(company);

        return convertToDTO(company);
    }

    private CompanyDTO convertToDTO(Company company) {
        CompanyDTO companyDTO = new CompanyDTO();
        companyDTO.setMId(company.getMId());
        companyDTO.setName(company.getName());
        companyDTO.setLogoUrl(company.getLogoUrl());
        companyDTO.setLocation(company.getLocation());
        companyDTO.setLinkedInProfile(company.getLinkedInProfile());
        companyDTO.setEmails(company.getEmails());
        companyDTO.setPhoneNumbers(company.getPhoneNumbers());
        companyDTO.setComments(company.getComments());
        companyDTO.setCommunicationPeriodicity(company.getCommunicationPeriodicity());
//        companyDTO.setUsers(company.getUsers());
        //companyDTO.setMessages(company.getMessages());

        // Instead of adding full UserDTOs, add SimplifiedUserDTOs
        companyDTO.setUsers(company.getUsers().stream()
                .map(this::convertToSimplifiedUserDTO)
                .collect(Collectors.toList()));

        companyDTO.setMessages(company.getMessages().stream()
                        .map(this::convertToSimplifiedMessageDTO)
                                .collect(Collectors.toList()));

        //companyDTO.setMessages(company.getMessages());
        return companyDTO;
    }

    private SimplifiedUserDTO convertToSimplifiedUserDTO(User user) {
        SimplifiedUserDTO simplifiedUserDTO = new SimplifiedUserDTO();
        simplifiedUserDTO.setUId(user.getUId());
        simplifiedUserDTO.setEmail(user.getEmail());
        simplifiedUserDTO.setFirstname(user.getFirstname());
        simplifiedUserDTO.setSecondname(user.getSecondname());
        return simplifiedUserDTO;
    }

    private SimplifiedMessageDTO convertToSimplifiedMessageDTO(Message message){
        SimplifiedMessageDTO simplifiedMessageDTO = new SimplifiedMessageDTO();
        simplifiedMessageDTO.setMessageId(message.getMessageId());
        return simplifiedMessageDTO;
    }

    private Company convertToEntity(CompanyDTO companyDTO) {
        Company company = new Company();
        company.setMId(companyDTO.getMId());
        company.setName(companyDTO.getName());
        company.setLogoUrl(companyDTO.getLogoUrl());
        company.setLocation(companyDTO.getLocation());
        company.setLinkedInProfile(companyDTO.getLinkedInProfile());
        company.setEmails(companyDTO.getEmails());
        company.setPhoneNumbers(companyDTO.getPhoneNumbers());
        company.setComments(companyDTO.getComments());
        company.setCommunicationPeriodicity(companyDTO.getCommunicationPeriodicity());

        // Handle users null check
        if (companyDTO.getUsers() != null) {
            List<User> users = companyDTO.getUsers().stream()
                    .map(this::convertToUserEntity)
                    .collect(Collectors.toList());
            company.setUsers(users);
        } else {
            company.setUsers(new ArrayList<>()); // Initialize with an empty list if null
        }

        // Handle messages null check
        if (companyDTO.getMessages() != null) {
            List<Message> messages = companyDTO.getMessages().stream()
                    .map(this::convertToMessageEntity)
                    .collect(Collectors.toList());
            company.setMessages(messages);
        } else {
            company.setMessages(new ArrayList<>()); // Initialize with an empty list if null
        }

        return company;
    }


    private Message convertToMessageEntity(SimplifiedMessageDTO simplifiedMessageDTO){
        Message message = new Message();
        message.setMessageId(simplifiedMessageDTO.getMessageId());
        return message;
    }
    private User convertToUserEntity(SimplifiedUserDTO simplifiedUserDTO) {
        User user = new User();
        user.setUId(simplifiedUserDTO.getUId());
        user.setEmail(simplifiedUserDTO.getEmail());
        return user;
    }

    @Override
    public List<UserDTO> getCompanyUsers(Integer companyId) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company not found"));

        return company.getUsers().stream()
                .map(this::convertToUserDTO)
                .collect(Collectors.toList());
    }

    private UserDTO convertToUserDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setUId(user.getUId());
        userDTO.setFirstname(user.getFirstname());
        userDTO.setSecondname(user.getSecondname());
        userDTO.setEmail(user.getEmail());
        userDTO.setRole(user.getRole().name());
        // Exclude sensitive fields like password if not required
        return userDTO;
    }


    @Override
    public CompanyDTO assignMultipleUsersToCompany(Integer companyId, List<Integer> userIds) {
        // Fetch company
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company not found"));

        // Fetch users by their IDs
        List<User> users = userRepository.findAllById(userIds);
        if (users.isEmpty()) {
            throw new RuntimeException("No users found");
        }

        // Filter users that are not already associated with the company
        List<User> newUsers = users.stream()
                .filter(user -> !company.getUsers().contains(user))
                .collect(Collectors.toList());

        if (newUsers.isEmpty()) {
            throw new RuntimeException("All provided users are already associated with this company");
        }

        // Add new users to the company
        company.getUsers().addAll(newUsers);

        // Update the inverse relationship: add the company to each new user's list of companies
        newUsers.forEach(user -> user.getCompanies().add(company));

        // Save changes
        userRepository.saveAll(newUsers); // Save the users with updated relationships
        return convertToDTO(companyRepository.save(company)); // Save the company and return the updated DTO
    }


}
