package pro.sky.telegrambot.services.implementations;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import org.apache.commons.lang3.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.helpers.Parser;
import pro.sky.telegrambot.models.domain.TaskNotification;
import pro.sky.telegrambot.services.interfaces.BotService;
import pro.sky.telegrambot.services.interfaces.TaskNotificationService;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

@Service
public class BotServiceImpl implements BotService {
    private final Logger logger = LoggerFactory.getLogger(BotServiceImpl.class);
    private final TelegramBot telegramBot;
    private final TaskNotificationService taskNotificationService;

    public BotServiceImpl(TelegramBot telegramBot, TaskNotificationService taskNotificationService) {
        this.telegramBot = telegramBot;
        this.taskNotificationService = taskNotificationService;
    }

    @Override
    public void sayHello(long chatId) {
        SendMessage request = new SendMessage(chatId, ResponseMessages.HELLO)
                .parseMode(ParseMode.HTML)
                .disableWebPagePreview(true)
                .disableNotification(true);
        SendResponse sendResponse = telegramBot.execute(request);
        if (sendResponse.isOk()) {
            logger.debug("sayHello() - message sent");
        } else {
            logger.warn("sayHello() - failed to send response");
        }
    }

    @Override
    public void addTask(Message message) {
        long chatId = message.chat().id();
        TaskNotification task;
        SendMessage request;

        //parse date/time
        try {
            task = Parser.tryParseTaskNotification(message.text());
        } catch (Exception e) {
            logger.warn("Error parsing message " + e.getMessage());
            request = createMessage(chatId, ResponseMessages.ADD_TASK_FAILURE + ResponseMessages.DATE_TIME_FORMAT_ISSUE);
            sendResponse(request);
            return;
        }

        //to db
        task.setChatId(chatId);
        task.setUserName(message.from().username());
        try {
            taskNotificationService.addTask(task);
        } catch (Exception e) {
            logger.warn("Failed to add new notification to db " + e.getMessage());
            request = createMessage(chatId, ResponseMessages.ADD_TASK_FAILURE + ResponseMessages.DB_INTERACTION_ISSUE);
            sendResponse(request);
            return;
        }

        //bot
        request = createMessage(chatId, ResponseMessages.getAddResponseMessageSuccess(
                task.getNotificationDateTime(),
                task.getMessage()));
        sendResponse(request);
    }

    @Override
    public void editTask(Message message) {
        throw new NotImplementedException();
        //todo date only? time only? message only? all together only?
    }

    @Override
    public void removeTask(Message message) {
        long chatId = message.chat().id();
        SendMessage request;
        long taskId;
        TaskNotification removedTask;

        try {
            taskId = Parser.tryParseTaskId(message.text());
        } catch (Exception e) {
            logger.warn("Error parsing message" + e.getMessage());
            request = createMessage(chatId, ResponseMessages.REMOVE_TASK_FAILURE + ResponseMessages.PARSING_ISSUE);
            sendResponse(request);
            return;
        }

        try {
            removedTask = taskNotificationService.removeTask(taskId);
        } catch (NoSuchElementException e) {
            logger.warn("Failed to remove notification from db " + e.getMessage());
            request = createMessage(chatId, ResponseMessages.REMOVE_TASK_FAILURE + ResponseMessages.NO_SUCH_ELEMENT);
            sendResponse(request);
            return;
        } catch (Exception e) {
            logger.warn("Failed to remove notification from db " + e.getMessage());
            request = createMessage(chatId, ResponseMessages.REMOVE_TASK_FAILURE + ResponseMessages.DB_INTERACTION_ISSUE);
            sendResponse(request);
            return;
        }

        request = createMessage(chatId, ResponseMessages.getRemoveResponseMessageSuccess(
                removedTask.getNotificationDateTime(),
                removedTask.getMessage()));
        sendResponse(request);
    }

    @Scheduled(cron = "0 0/1 * * * *")
    private void checkForTasks()
    {
        var result = taskNotificationService.getTasksNow();
        if(!result.isEmpty()) {
            result.forEach(this::notifyClient);
        }
    }

    private void notifyClient(TaskNotification task) {
        var request = createMessage(task.getChatId(), ResponseMessages.getNotificationMessage(
                task.getUserName(),
                task.getMessage()));
        sendResponse(request);
    }

    private SendMessage createMessage(long chatId, String text) {
        return new SendMessage(chatId, text).parseMode(ParseMode.HTML).disableWebPagePreview(true);
    }

    private void sendResponse(SendMessage message) {
        if (telegramBot.execute(message).isOk()) {
            logger.debug("addTask() - message sent");
        } else {
            logger.warn("addTask() - failed to send response");
        }
    }

    private static class ResponseMessages {
        private static final String HELLO = "Hello \uD83D\uDC4B\n This is Tavkel's bot speaking! Currently I can do following things:\n" +
                " - nothing";

        private static String getAddResponseMessageSuccess(LocalDateTime date, String description) {
            return String.format("Task added! Await notification for \"%s\" at %s", description, date);
        }

        private static String getRemoveResponseMessageSuccess(LocalDateTime date, String description) {
            return String.format("Task for \"%s\" at %s removed!", description, date);
        }

        private static String getNotificationMessage(String username, String text) {
            return String.format("Hey @%s! %s", username, text);
        }

        private static final String ADD_TASK_FAILURE = "Failed to add task! Reason:\n";
        private static final String REMOVE_TASK_FAILURE = "Failed to remove task! Reason:\n";
        private static final String DATE_TIME_FORMAT_ISSUE = "Wrong date or time format!\n" +
                "Please use following pattern:\n" +
                "/add yyyy.MM.d HH:mm [task description]";
        private static final String PARSING_ISSUE = "Failed to parse message";
        private static final String DB_INTERACTION_ISSUE = "Something went wrong while accessing database!";
        private static final String NO_SUCH_ELEMENT = "Task was not found";
    }
}
