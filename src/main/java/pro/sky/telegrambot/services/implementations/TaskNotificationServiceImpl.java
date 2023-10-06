package pro.sky.telegrambot.services.implementations;

import org.apache.commons.lang3.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.models.domain.TaskNotification;
import pro.sky.telegrambot.services.interfaces.BotService;
import pro.sky.telegrambot.services.interfaces.TaskNotificationService;
import pro.sky.telegrambot.services.repositories.TaskNotificationRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.NoSuchElementException;

@Service
public class TaskNotificationServiceImpl implements TaskNotificationService {
    private final Logger logger = LoggerFactory.getLogger(TaskNotificationService.class);
    private final TaskNotificationRepository repository;

    public TaskNotificationServiceImpl(TaskNotificationRepository repository) {
        this.repository = repository;
    }

    @Override
    public TaskNotification addTask(TaskNotification task) {
        var result = repository.saveAndFlush(task);
        logger.debug("Added new notification for task: {}", task);
        return result;
    }

    @Override
    public TaskNotification editTask(TaskNotification task) {
        throw new NotImplementedException();
    }

    @Override
    public TaskNotification removeTask(long id) {
        var result = repository.findById(id);
        repository.delete(result.orElseThrow(() -> new NoSuchElementException("Task not found")));
        logger.debug("removed task {}", result.get());
        return result.get();
    }

    @Override
    public Collection<TaskNotification> getTasksNow() {
        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        var result = repository.findByNotificationDateTime(now);
        return result;
    }
}
