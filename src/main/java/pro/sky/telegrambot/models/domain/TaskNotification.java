package pro.sky.telegrambot.models.domain;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "task_notifications")
public class TaskNotification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private long chatId;
    private String userName;
    private String message;
    private LocalDateTime notificationDateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getChatId() {
        return chatId;
    }

    public void setChatId(long chatId) {
        this.chatId = chatId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getNotificationDateTime() {
        return notificationDateTime;
    }

    public void setNotificationDateTime(LocalDateTime notificationDateTime) {
        this.notificationDateTime = notificationDateTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TaskNotification that = (TaskNotification) o;
        return chatId == that.chatId && Objects.equals(userName, that.userName) && Objects.equals(message, that.message) && Objects.equals(notificationDateTime, that.notificationDateTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(chatId, userName, message, notificationDateTime);
    }
}
