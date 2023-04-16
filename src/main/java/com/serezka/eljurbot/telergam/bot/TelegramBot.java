package com.serezka.eljurbot.telergam.bot;

import com.serezka.eljurbot.telergam.bot.services.CleanerService;
import com.serezka.eljurbot.telergam.bot.util.KeyboardUtil;
import com.serezka.eljurbot.telergam.bot.util.MessageUtil;
import com.serezka.eljurbot.telergam.bot.util.UpdateUtil;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

@Component
@PropertySource("classpath:telegram.properties")
@Log4j2
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TelegramBot extends TelegramLongPollingBot {
    String botUsername, botToken, commandsPrefix, helpMessage;
    boolean enableAutoDelete;

    CleanerService cleanerService = new CleanerService(this);

    @NonFinal
    @Setter
    TelegramHandler telegramHandler;

    public TelegramBot(@Value("${telegram.bot.username}") String botUsername,
                       @Value("${telegram.bot.token}") String botToken,
                       @Value("${telegram.bot.commands.prefix}") String commandsPrefix,
                       @Value("${telegram.bot.commands.help}") String helpMessage,
                       @Value("${telegram.bot.enableAutoDelete}") boolean enableAutoDelete) {
        this.botUsername = botUsername;
        this.botToken = botToken;

        this.commandsPrefix = commandsPrefix;
        this.helpMessage = helpMessage;

        this.enableAutoDelete = enableAutoDelete;
    }

    @Override
    public void onUpdateReceived(Update update) {
        TelegramUpdate tgUpdate = new TelegramUpdate(update);

        if (update.hasMessage() && enableAutoDelete)
            cleanerService.addToRemove(new CleanerService.MessageData(tgUpdate.getMessageId(), tgUpdate.getChatId(), new Date()));
        telegramHandler.process(this, tgUpdate);
    }

    @Override
    public <T extends Serializable, Method extends BotApiMethod<T>> T execute(Method method) {
        try {
            if (SendMessage.class.equals(method.getClass())) {
                Message result = super.execute((SendMessage) method);

                if (((SendMessage) method).getReplyMarkup() == null)
                    ((SendMessage) method).setReplyMarkup(KeyboardUtil.getDefaultReplyKeyboardMarkup());

                if (enableAutoDelete) {
                    int messageId = result.getMessageId();
                    String chatId = String.valueOf(result.getChatId());

                    Calendar current = GregorianCalendar.getInstance();
                    current.add(Calendar.MINUTE, 1);

                    cleanerService.addToRemove(new CleanerService.MessageData(messageId, chatId, new Date(current.getTimeInMillis())));
                }

                log.info(String.format("Message Sent: to {%s} with text {'%s'}",
                        ((SendMessage) method).getChatId(), ((SendMessage) method).getText().replace("\n", " ")));
                return (T) result;
            } else return super.execute(method);
        } catch (TelegramApiException e) {
            log.warn("Error method execution: {}", e.getMessage());
            return null;
        }
    }

    public <T extends Serializable, Method extends BotApiMethod<T>> T execute(Method method, int minutesForRemove) {
        try {
            if (SendMessage.class.equals(method.getClass())) {
                Message result = super.execute((SendMessage) method);

                if (((SendMessage) method).getReplyMarkup() == null)
                    ((SendMessage) method).setReplyMarkup(KeyboardUtil.getDefaultReplyKeyboardMarkup());

                if (enableAutoDelete) {
                    int messageId = result.getMessageId();
                    String chatId = String.valueOf(result.getChatId());

                    Calendar current = GregorianCalendar.getInstance();
                    current.add(Calendar.MINUTE, minutesForRemove);

                    if (minutesForRemove >= 0)
                        cleanerService.addToRemove(new CleanerService.MessageData(messageId, chatId, new Date(current.getTimeInMillis())));
                }

                log.info(String.format("Message Sent: to {%s} with text {'%s'}",
                        ((SendMessage) method).getChatId(), ((SendMessage) method).getText().replace("\n", " ")));
                return (T) result;
            } else return super.execute(method);
        } catch (TelegramApiException e) {
            log.warn("Error method execution: {}", e.getMessage());
            return null;
        }
    }

    // send methods
    public Message sendMessage(String chatId, String text, int minutesToRemove) {
        return execute(MessageUtil.sendMessage(chatId, text), minutesToRemove);
    }
    public Message sendMessage(String chatId, String text) {
        return execute(MessageUtil.sendMessage(chatId, text));
    }

    public Message sendMessage(String chatId, String text, ReplyKeyboardMarkup keyboardMarkup) {
        return execute(MessageUtil.sendMessage(chatId, text, keyboardMarkup));
    }

    public boolean deleteMessage(String chatId, int messageId) {
        return execute(MessageUtil.delete(chatId, messageId));
    }

    public Message sendSticker(String chatId, String stickerId) {
        try {
            return execute(MessageUtil.sendSticker(chatId, stickerId));
        } catch (TelegramApiException e) {
            log.warn(e.getMessage());
            return null;
        }
    }
}
