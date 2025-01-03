package calendar.app.repository;

import calendar.app.entities.Message;
import calendar.app.entities.PriorityLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Integer> {

    // Find messages by seen/unseen status
    List<Message> findBySeen(boolean seen);

    // Find messages by priority level
    List<Message> findByPriorityLevel(PriorityLevel priorityLevel);

    // Find messages within a specific date range
    @Query("SELECT m FROM Message m WHERE m.date BETWEEN :startDate AND :endDate")
    List<Message> findMessagesWithinDateRange(LocalDate startDate, LocalDate endDate);

    // Find messages associated with a specific user
    @Query("SELECT m FROM Message m JOIN m.users u WHERE u.uId = :userId")
    List<Message> findByUserId(Integer userId);

    // Find messages associated with a specific company
    @Query("SELECT m FROM Message m WHERE m.company.mId = :companyId")
    List<Message> findByCompanyId(Integer companyId);

    // Find messages by seen status, user ID, and company ID
    @Query("SELECT m FROM Message m JOIN m.users u WHERE m.seen = :seen AND u.uId = :userId AND m.company.mId = :companyId")
    List<Message> findBySeenStatusAndUserAndCompany(boolean seen, Integer userId, Integer companyId);
}
