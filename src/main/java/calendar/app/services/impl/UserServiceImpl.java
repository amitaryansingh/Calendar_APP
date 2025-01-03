package calendar.app.services.impl;

import calendar.app.dto.CompanyDTO;
import calendar.app.dto.MessageDTO;
import calendar.app.dto.SimplifiedCompanyDTO;
import calendar.app.dto.UserDTO;
import calendar.app.entities.Company;
import calendar.app.entities.Message;
import calendar.app.entities.Role;
import calendar.app.entities.User;
import calendar.app.repository.CompanyRepository;
import calendar.app.repository.UserRepository;
import calendar.app.services.UserService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final PasswordEncoder passwordEncoder;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public UserDetailsService userDetailsService() {
        return username -> {
            User user = userRepository.findByEmail(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));
            return new org.springframework.security.core.userdetails.User(
                    user.getEmail(),
                    user.getPassword(),
                    user.getAuthorities()
            );
        };
    }

    @Override
    public UserDTO findByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return convertToDTO(user);
    }

    @Override
    public UserDTO createUser(UserDTO userDTO) {
        User user = convertToEntity(userDTO);
        if (user.getRole() == null) {
            user.setRole(Role.USER);
        }

        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return convertToDTO(userRepository.save(user));
    }

    @Override
    public UserDTO getUserById(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return convertToDTO(user);
    }

    @Override
    public UserDTO updateUser(Integer id, UserDTO userDetails) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (userDetails.getFirstname() != null) {
            existingUser.setFirstname(userDetails.getFirstname());
        }
        if (userDetails.getSecondname() != null) {
            existingUser.setSecondname(userDetails.getSecondname());
        }
        if (userDetails.getEmail() != null) {
            existingUser.setEmail(userDetails.getEmail());
        }
        if (userDetails.getPassword() != null) {
            existingUser.setPassword(passwordEncoder.encode(userDetails.getPassword()));
        }
        if (userDetails.getRole() != null) {
            existingUser.setRole(Role.valueOf(userDetails.getRole()));
        }

        return convertToDTO(userRepository.save(existingUser));
    }

    @Override
    public void deleteUser(Integer id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found");
        }
        userRepository.deleteById(id);
    }


    @Transactional
    @Override
    public void deleteUserWithMessages(Integer uId) {
        String deleteQuery = "DELETE FROM user_message WHERE user_id = :userId";
        Query query = entityManager.createNativeQuery(deleteQuery);
        query.setParameter("userId", uId);
        query.executeUpdate();

        // Now delete the user
        userRepository.deleteById(uId);
    }
    @Override
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public UserDTO assignCompanyToUser(Integer userId, Integer companyId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company not found"));

        user.getCompanies().add(company);
        userRepository.save(user);

        if(!user.getCompanies().contains(company)){
            user.getCompanies().add(company);
            company.getUsers().add(user);
        }
        else{
            throw new RuntimeException("User is already associated with this company");
        }
        companyRepository.save(company);
        return convertToDTO(user);
    }

    @Override
    public UserDTO removeCompanyFromUser(Integer userId, Integer companyId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company not found"));

        user.getCompanies().remove(company);
        userRepository.save(user);

        return convertToDTO(user);
    }

    private UserDTO convertToDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setUId(user.getUId());
        userDTO.setFirstname(user.getFirstname());
        userDTO.setSecondname(user.getSecondname());
        userDTO.setEmail(user.getEmail());
        userDTO.setPassword(user.getPassword());
        userDTO.setRole(user.getRole().name());
        userDTO.setCompanies(user.getCompanies().stream()
                .map(this::convertToSimplifiedCompanyDTO)
                .collect(Collectors.toList()));
        return userDTO;
    }

    private SimplifiedCompanyDTO convertToSimplifiedCompanyDTO(Company company) {
        SimplifiedCompanyDTO simplifiedCompanyDTO = new SimplifiedCompanyDTO();
        simplifiedCompanyDTO.setMId(company.getMId());
        simplifiedCompanyDTO.setName(company.getName());
        return simplifiedCompanyDTO;
    }

    private User convertToEntity(UserDTO userDTO) {
        User user = new User();
        user.setUId(userDTO.getUId());
        user.setFirstname(userDTO.getFirstname());
        user.setSecondname(userDTO.getSecondname());
        user.setEmail(userDTO.getEmail());
        user.setPassword(userDTO.getPassword());
        user.setRole(Role.valueOf(userDTO.getRole()));
        return user;
    }

    private CompanyDTO convertToCompanyDTO(Company company) {
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
        return companyDTO;
    }

    @Override
    public List<CompanyDTO> getUserCompanies(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return user.getCompanies().stream()
                .map(this::convertToCompanyDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<MessageDTO> getUserMessagesByCompany(Integer userId, Integer companyId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company not found"));

        return company.getMessages().stream()
                .filter(message -> message.getUsers().contains(user))
                .map(this::convertToMessageDTO)
                .collect(Collectors.toList());
    }

    private MessageDTO convertToMessageDTO(Message message) {
        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setMessageId(message.getMessageId());
        messageDTO.setName(message.getName().name());
        messageDTO.setDescription(message.getDescription());
        messageDTO.setDate(message.getDate());
        messageDTO.setMandatoryFlag(message.isMandatoryFlag());
        messageDTO.setClientName(message.getClientname());
        messageDTO.setDesignation(message.getDesignation());
        messageDTO.setPriorityLevel(message.getPriorityLevel().name());
        messageDTO.setSeen(message.isSeen());
        messageDTO.setCompanyId(message.getCompany().getMId());
        messageDTO.setUserIds(message.getUsers().stream().map(User::getUId).collect(Collectors.toList()));
        return messageDTO;
    }

//    public UserDTO assignMultipleCompaniesToUser(Integer userId, List<Integer> companyIds) {
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
//        List<Company> companies = companyRepository.findAllById(companyIds);
//        if (companies.isEmpty()) {
//            throw new RuntimeException("No companies found");
//        }
//
//        user.getCompanies().addAll(companies);
//        return convertToDTO(userRepository.save(user));
//    }

    public UserDTO assignMultipleCompaniesToUser(Integer userId, List<Integer> companyIds) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Company> companies = companyRepository.findAllById(companyIds);
        if (companies.isEmpty()) {
            throw new RuntimeException("No companies found");
        }

        // Filter out companies that are already assigned to the user
        List<Company> newCompanies = companies.stream()
                .filter(company -> !user.getCompanies().contains(company))
                .collect(Collectors.toList());

        if (newCompanies.isEmpty()) {
            throw new RuntimeException("User is already associated with all the provided companies");
        }

        // Add only the companies that are not already associated
        user.getCompanies().addAll(newCompanies);

        return convertToDTO(userRepository.save(user));
    }


}
