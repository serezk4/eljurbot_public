package com.serezka.eljurbot.telergam.bot.util;

import org.telegram.telegrambots.meta.api.objects.Update;

public class UpdateUtil {
    public static int getMessageId(Update update) {
        return update.hasCallbackQuery() ?
                update.getCallbackQuery().getMessage().getMessageId() :
                update.getMessage().getMessageId();
    }

    public static String getChatId(Update update) {
        return String.valueOf(update.hasCallbackQuery() ?
                update.getCallbackQuery().getMessage().getChatId() :
                update.getMessage().getChatId());
    }

    public static String getUsername(Update update) {
        return update.hasCallbackQuery() ?
                update.getCallbackQuery().getMessage().getChat().getUserName() :
                update.getMessage().getChat().getUserName();
    }

    public static String getText(Update update) {
        return update.hasCallbackQuery() ?
                update.getCallbackQuery().getData() :
                update.getMessage().getText();
    }
}
