package com.serezka.eljurbot.telergam.bot.util;

import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendSticker;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;

public class MessageUtil {
    public static DeleteMessage delete(String chatId, int messageId) {
        DeleteMessage deleteMessage = new DeleteMessage();
        deleteMessage.setMessageId(messageId);
        deleteMessage.setChatId(String.valueOf(chatId));

        return deleteMessage;
    }

    public static SendDocument sendDocument(String chatId, InputFile file) {
        SendDocument sendDocument = new SendDocument();
        sendDocument.setChatId(String.valueOf(chatId));
        sendDocument.setDocument(file);
        sendDocument.setDisableContentTypeDetection(true);

        return sendDocument;
    }

    public static SendPhoto sendPhoto(String chatId, InputFile file) {
        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setChatId(String.valueOf(chatId));
        sendPhoto.setPhoto(file);

        return sendPhoto;
    }

    public static EditMessageText edit(String chatId, int messageId) {
        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setMessageId(messageId);
        editMessageText.setChatId(String.valueOf(chatId));
        editMessageText.setParseMode(ParseMode.HTML);
        editMessageText.setDisableWebPagePreview(true);

        return editMessageText;
    }

    public static EditMessageText edit(String chatId, int messageId, String newText) {
        EditMessageText editMessageText = edit(chatId,messageId);
        editMessageText.setText(newText);

        return editMessageText;
    }

    public static EditMessageText edit(String chatId, int messageId, String newText, InlineKeyboardMarkup inlineKeyboard) {
        EditMessageText editMessageText = edit(chatId,messageId,newText);
        editMessageText.setReplyMarkup(inlineKeyboard);
        editMessageText.setDisableWebPagePreview(true);

        return editMessageText;
    }

    public static SendMessage sendMessage(String chatId, String text) {
        SendMessage sm = new SendMessage();
        sm.setChatId(String.valueOf(chatId));
        sm.setText(text);
        sm.setParseMode(ParseMode.HTML);
        sm.setReplyMarkup(KeyboardUtil.getDefaultReplyKeyboardMarkup());
        sm.setDisableWebPagePreview(true);

        return sm;
    }

    public static SendMessage sendMessage(String chatId, String text, ReplyKeyboard replyKeyboard) {
        SendMessage sm = sendMessage(chatId,text);
        sm.setReplyMarkup(replyKeyboard);

        return sm;
    }

    public static SendSticker sendSticker(String chatId, String fileId) {
        SendSticker sendSticker = new SendSticker();
        sendSticker.setSticker(new InputFile(fileId));
        sendSticker.setChatId(String.valueOf(chatId));

        return sendSticker;
    }
}
