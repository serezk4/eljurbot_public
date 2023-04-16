package com.serezka.eljurbot.telergam.bot.session.type.menu;

import com.serezka.eljurbot.Lang;
import com.serezka.eljurbot.telergam.bot.TelegramBot;
import com.serezka.eljurbot.telergam.bot.TelegramUpdate;
import com.serezka.eljurbot.telergam.bot.session.MenuSessionManager;
import com.serezka.eljurbot.telergam.bot.util.KeyboardUtil;
import com.serezka.eljurbot.telergam.bot.util.MessageUtil;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

@Log4j2
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Getter
public class MenuSession {
    private static final AtomicLong idCounter = new AtomicLong();

    // session data
    String chatId;
    String menuUUID = String.valueOf(idCounter.getAndIncrement());

    // menu data
    Menu menu;
    List<String> allCollectedData = new ArrayList<>();
    Set<Integer> messagesId = new LinkedHashSet<>();

    public void process(TelegramBot tgBot, TelegramUpdate tgUpdate) {
        String text = tgUpdate.getText();
        allCollectedData.add(text);

        if (text.contains("exit")) {
            tgBot.execute(MessageUtil.sendMessage(chatId,  Lang.MENU_SESSION_CLOSED));
            for (int messageId : messagesId) tgBot.execute(MessageUtil.delete(chatId, messageId)); // remove all messages
            MenuSessionManager.removeSession(this);
            return;
        }

        // get root page if collected data size = 1
        if (allCollectedData.size() == 1) {
            Page.Data selectedPageData = menu.getRootPage().generate(chatId, null);
            messagesId.add(tgBot.execute(MessageUtil.sendMessage(chatId,
                    selectedPageData.getText(),
                    KeyboardUtil.getResizableInlineKeyboardMarkup(getFormattedKeyboardData(selectedPageData.getButtonsData()),selectedPageData.getRowSize())), 60).getMessageId());
            return;
        }

        // get next page
        String[] queryData = text.split(";");

        if (!menu.getPages().containsKey(queryData[1]) && !queryData[1].equals("root")) {
            log.warn("can't find menu page! name: {}", queryData[1]);
            return;
        }

        Page.Data selectedPageData;
        if (queryData[1].equals("root")) selectedPageData = menu.getRootPage().generate(chatId, text);
        else selectedPageData = menu.getPages().get(queryData[1]).generate(chatId, text);

        if (tgUpdate.getSelf().hasCallbackQuery())
            tgBot.execute(MessageUtil.edit(chatId, (int) messagesId.toArray()[messagesId.size() - 1],
                    selectedPageData.getText(), KeyboardUtil.getResizableInlineKeyboardMarkup(getFormattedKeyboardData(selectedPageData.getButtonsData()),
                            selectedPageData.getRowSize())));
        else
            messagesId.add(tgBot.execute(MessageUtil.sendMessage(chatId, selectedPageData.getText(),
                    KeyboardUtil.getResizableInlineKeyboardMarkup(getFormattedKeyboardData(selectedPageData.getButtonsData()),selectedPageData.getRowSize()))).getMessageId());

    }

    private List<KeyboardUtil.InlineButtonData> getFormattedKeyboardData(List<KeyboardUtil.InlineButtonData> buttonsData) {
        return buttonsData.stream().map(buttonData -> new KeyboardUtil.InlineButtonData(buttonData.getText(), menuUUID + ";" + buttonData.getData())).toList();
    }
}
