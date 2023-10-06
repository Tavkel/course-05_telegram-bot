package pro.sky.telegrambot.services.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pro.sky.telegrambot.models.domain.TaskNotification;

import java.time.LocalDateTime;
import java.util.Collection;

@Repository
public interface TaskNotificationRepository extends JpaRepository<TaskNotification, Long> {
    Collection<TaskNotification> findByNotificationDateTime(LocalDateTime dateTime);
}
