package pro.sky.telegrambot.services.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pro.sky.telegrambot.models.domain.TaskNotification;

@Repository
public interface TaskNotificationRepository extends JpaRepository<TaskNotification, Long> {
}
