package calendar.app.repository;

import calendar.app.entities.Message;
import calendar.app.entities.MessageUser;
import calendar.app.entities.MessageUserId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageUserRepository extends JpaRepository<MessageUser, MessageUserId> {

    @Query("SELECT mu.message FROM MessageUser mu WHERE mu.user.uId = :userId AND mu.seen = false")
    List<Message> findMessagesByUserIdAndSeenStatus(@Param("userId") Integer userId);

    @Query("SELECT mu.message FROM MessageUser mu WHERE mu.user.uId = :userId AND mu.message.company.mId = :companyId AND mu.seen = :seen")
    List<Message> findMessagesByUserAndCompanyAndSeenStatus(Integer userId, Integer companyId, boolean seen);

    @Query("SELECT mu.seen FROM MessageUser mu WHERE mu.message.messageId = :messageId AND mu.user.uId = :userId")
    boolean findSeenStatusByMessageIdAndUserId(@Param("messageId") Integer messageId, @Param("userId") Integer userId);

}