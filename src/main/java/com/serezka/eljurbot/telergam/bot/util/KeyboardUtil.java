package com.serezka.eljurbot.telergam.bot.util;

import com.serezka.eljurbot.Lang;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.log4j.Log4j2;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.api.objects.webapp.WebAppInfo;

import java.util.ArrayList;
import java.util.List;

@Log4j2
public class KeyboardUtil {

    // TODO: 11.02.2023  
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    @Getter
    public static class ReplyButtonData {
        WebAppInfo webAppInfo;
        String text;

        public ReplyButtonData(String text, WebAppInfo webAppInfo) {
            this.webAppInfo = webAppInfo;
            this.text = text;
        }

        public ReplyButtonData(String text) {
            this.text = text;
            this.webAppInfo = null;
        }
    }

    public static ReplyKeyboardMarkup getDefaultReplyKeyboardMarkup() {
        return getCustomReplyKeyboardMarkup(new String[][]{
                {Lang.GET_MARKS_USE.get(1), Lang.LINK_ACCOUNTS_USE.get(1)},
                {Lang.NEED_TO_REACH_AVERAGE_USE.get(1), /*Lang.SKIP_LESSONS_COUNT_USE.get(1),*/ Lang.GET_HOMEWORK_USE.get(0)}
        });
    }

    private static KeyboardButton getReplyKeyboardButton(String text) {
        KeyboardButton tempButton = new KeyboardButton();
        tempButton.setText(text);

        return tempButton;
    }

    private static KeyboardButton getReplyKeyboardButton(String text, WebAppInfo webAppInfo) {
        KeyboardButton tempButton = new KeyboardButton();
        tempButton.setText(text);
        tempButton.setWebApp(webAppInfo);

        return tempButton;
    }


    public static ReplyKeyboardMarkup getResizableReplyKeyboardMarkup(List<ReplyButtonData> buttonsString, int rowSize) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> mainRow = new ArrayList<>();

        KeyboardRow tempRow = new KeyboardRow();
        for (ReplyButtonData buttonData : buttonsString) {
            KeyboardButton keyboardButton;

            if (buttonData.getWebAppInfo() != null) keyboardButton = getReplyKeyboardButton(buttonData.getText(), buttonData.getWebAppInfo());
            else keyboardButton = getReplyKeyboardButton(buttonData.getText());

            if (tempRow.size() > rowSize) {
                mainRow.add(tempRow);
                tempRow = new KeyboardRow();
            }

            tempRow.add(keyboardButton);
        }
        if (!tempRow.isEmpty()) mainRow.add(tempRow);

        replyKeyboardMarkup.setKeyboard(mainRow);
        replyKeyboardMarkup.setResizeKeyboard(true);

        return replyKeyboardMarkup;
    }

    @Getter
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    @RequiredArgsConstructor
    public static class InlineButtonData {
        String text;
        String data;
        WebAppInfo webAppInfo;

        public InlineButtonData(String text, String data) {
            this.text = text;
            this.data = data;
            this.webAppInfo = null;
        }

        public InlineButtonData(String text, WebAppInfo webAppInfo) {
            this.text = text;
            this.webAppInfo = webAppInfo;
            this.data = null;
        }
    }

    public static InlineKeyboardMarkup getResizableInlineKeyboardMarkup(List<InlineButtonData> buttonsData, int rowSize) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> allRows = new ArrayList<>();

        List<InlineKeyboardButton> tempRow = new ArrayList<>();
        for (InlineButtonData buttonData : buttonsData) {
            InlineKeyboardButton keyboardButton;

            if (buttonData.getData() != null) keyboardButton = getInlineKeyboardButton(buttonData.getText(), buttonData.getData());
            else keyboardButton = getInlineKeyboardButton(buttonData.getText(), buttonData.getWebAppInfo());

            if (tempRow.size() >= rowSize) {
                allRows.add(tempRow);
                tempRow = new ArrayList<>();
            }

            tempRow.add(keyboardButton);
        }

        if (!tempRow.isEmpty()) allRows.add(tempRow);

        inlineKeyboardMarkup.setKeyboard(allRows);
        return inlineKeyboardMarkup;
    }

    // TODO: 11.02.2023 make private
    private static InlineKeyboardButton getInlineKeyboardButton(String text, String callbackData) {
        InlineKeyboardButton tempInlineButton = new InlineKeyboardButton();
        tempInlineButton.setText(text);
        tempInlineButton.setCallbackData(callbackData);

        return tempInlineButton;
    }

    private static InlineKeyboardButton getInlineKeyboardButton(String text, WebAppInfo webAppInfo) {
        InlineKeyboardButton tempInlineButton = new InlineKeyboardButton();
        tempInlineButton.setText(text);
        tempInlineButton.setWebApp(webAppInfo);

        return tempInlineButton;
    }

    public static ReplyKeyboardMarkup getCustomReplyKeyboardMarkup(List<List<String>> buttonsText) {
        String[][] converted = new String[buttonsText.size()][100];
        for (int i = 0; i < buttonsText.size(); i++)
            for (int j = 0; j < buttonsText.get(i).size(); j++)
                converted[i][j] = buttonsText.get(i).get(j);
        return getCustomReplyKeyboardMarkup(converted);
    }

    public static ReplyKeyboardMarkup getCustomReplyKeyboardMarkup(String[][] buttonsText) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();

        List<KeyboardRow> mainRow = new ArrayList<>();

        for (String[] buttonsTextRow : buttonsText) {
            KeyboardRow tempRow = new KeyboardRow();
            for (String buttonText : buttonsTextRow)
                if (buttonText != null) tempRow.add(getReplyKeyboardButton(buttonText));

            mainRow.add(tempRow);
        }

        replyKeyboardMarkup.setKeyboard(mainRow);
        replyKeyboardMarkup.setResizeKeyboard(true);

        return replyKeyboardMarkup;
    }

    @Deprecated
    public static InlineKeyboardMarkup getCustomInlineKeyboardMarkup(String[][] buttonsText) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();

        for (String[] buttonsTextRow : buttonsText) {
            List<InlineKeyboardButton> tempList = new ArrayList<>();
            for (String buttonText : buttonsTextRow)
                if (buttonText != null) tempList.add(getInlineKeyboardButton(buttonText, buttonText));

            buttons.add(tempList);
        }

        inlineKeyboardMarkup.setKeyboard(buttons);
        return inlineKeyboardMarkup;
    }

    public static InlineKeyboardMarkup getCustomInlineKeyboardMarkup(List<List<String>> buttonsText) {
        String[][] temp = new String[buttonsText.size()][];
        for (int i = 0; i < buttonsText.size(); i++) temp[i] = buttonsText.get(i).toArray(String[]::new);
        return getCustomInlineKeyboardMarkup(temp);
    }
}
