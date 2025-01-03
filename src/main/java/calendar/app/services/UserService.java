package calendar.app.services;

import calendar.app.dto.CompanyDTO;
import calendar.app.dto.MessageDTO;
import calendar.app.dto.UserDTO;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserService {

    UserDetailsService userDetailsService();

    UserDTO createUser(UserDTO userDTO);

    UserDTO getUserById(Integer id);

    UserDTO updateUser(Integer id, UserDTO userDTO);

    void deleteUser(Integer id);

    List<UserDTO> getAllUsers();

    UserDTO findByEmail(String email);

    UserDTO assignCompanyToUser(Integer userId, Integer companyId);

    UserDTO removeCompanyFromUser(Integer userId, Integer companyId);

    List<MessageDTO> getUserMessagesByCompany(Integer userId, Integer companyId);

    List<CompanyDTO> getUserCompanies(Integer userId);

    UserDTO assignMultipleCompaniesToUser(Integer userId, List<Integer> companyIds);

    public void deleteUserWithMessages(Integer uId);
}
