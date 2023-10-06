package pro.sky.telegrambot.services.interfaces;

import com.pengrad.telegrambot.model.Message;
import pro.sky.telegrambot.models.domain.TaskNotification;

public interface BotService {
    void sayHello(long chatId);
    void addTask(Message message);
    void editTask(Message message);
    void removeTask(Message message);
}
