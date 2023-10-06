package pro.sky.telegrambot.helpers;

import pro.sky.telegrambot.models.domain.TaskNotification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class Parser {
    //todo - разобраться с вариантом с регэкспом
    public static TaskNotification tryParseTaskNotification(String message) {
        String[] parts = message.split(" ");
        if (parts.length < 3) throw new IllegalArgumentException("Failed to parse message! Not enough info.");
        var dateFormat = DateTimeFormatter.ofPattern("yyyy.MM.d");
        var timeFormat = DateTimeFormatter.ofPattern("HH:mm");
        var date = LocalDate.parse(parts[1], dateFormat);
        var time = LocalTime.parse(parts[2], timeFormat);
        var dateTime = LocalDateTime.of(date, time);

        TaskNotification result = new TaskNotification();
        result.setNotificationDateTime(dateTime);
        result.setMessage(Stream.of(parts).skip(3).collect(Collectors.joining(" ")));
        return result;
    }

    public static long tryParseTaskId(String message) {
        String[] parts = message.split(" ");
        if (parts.length < 2) throw new IllegalArgumentException("Failed to parse message! Not enough info.");
        return Long.parseLong(parts[1]);
    }
}
