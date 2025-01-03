package calendar.app.services;

import calendar.app.dto.CompanyDTO;
import calendar.app.dto.UserDTO;
import calendar.app.entities.Company;
import java.util.List;

public interface CompanyService {

    CompanyDTO createCompany(CompanyDTO companyDTO);

    CompanyDTO getCompanyById(Integer id);

    CompanyDTO updateCompany(Integer id, CompanyDTO companyDetails);

    void deleteCompany(Integer id);

    List<CompanyDTO> getAllCompanies();

    CompanyDTO assignUserToCompany(Integer companyId, Integer userId);

    CompanyDTO removeUserFromCompany(Integer companyId, Integer userId);

    List<UserDTO> getCompanyUsers(Integer companyId);

    CompanyDTO assignMultipleUsersToCompany(Integer companyId, List<Integer> userIds);

    void deleteCompanyWithMessages(Integer id);

}
