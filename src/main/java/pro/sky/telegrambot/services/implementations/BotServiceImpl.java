package pro.sky.telegrambot.services.implementations;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.services.interfaces.BotService;

@Service
public class BotServiceImpl implements BotService {
    private final Logger logger = LoggerFactory.getLogger(BotServiceImpl.class);
    TelegramBot telegramBot;

    public BotServiceImpl(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }

    @Override
    public void sayHello(long chatId) {
        SendMessage request = new SendMessage(chatId, Messages.HELLO)
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

    private static class Messages {
        private static final String HELLO = "Hello \uD83D\uDC4B\n This is Tavkel's bot speaking! Currently I can do following things:\n" +
                " - nothing";
    }
}
