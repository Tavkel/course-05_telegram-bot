package pro.sky.telegrambot.services.interfaces;

import pro.sky.telegrambot.models.domain.TaskNotification;
import java.time.LocalDateTime;
import java.util.Collection;

public interface TaskNotificationService {
    TaskNotification addTask(TaskNotification task);
    TaskNotification editTask(TaskNotification task);
    TaskNotification removeTask(long id);

    Collection<TaskNotification> getTasksNow();
}
