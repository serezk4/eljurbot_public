package com.serezka.eljurbot.telergam.bot.session;

import com.serezka.eljurbot.telergam.bot.session.type.menu.Menu;
import com.serezka.eljurbot.telergam.bot.session.type.menu.MenuSession;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Synchronized;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Log4j2
public class MenuSessionManager {
    static List<MenuSession> menuSessions = new ArrayList<>();

    @Synchronized
    public static boolean containsSession(String chatId, String menuUUID) {
        // todo
        return menuSessions.stream().anyMatch(session -> session.getMenuUUID().equals(menuUUID) && session.getChatId().equals(chatId));
    }

    @Synchronized
    public static MenuSession getSession(String chatId, String menuUUID) {

        if (!containsSession(chatId, menuUUID)) return null;

        // we use Optional checked  !!
        return menuSessions.stream().filter(session -> session.getMenuUUID().equals(menuUUID) && session.getChatId().equals(chatId)).findFirst().get();
    }

    @Synchronized
    public static MenuSession addSession(MenuSession menuSession) {
        menuSessions.add(menuSession);
        return menuSession;
    }

    @Synchronized
    public static void removeSession(MenuSession menuSession) {
        menuSessions.remove(menuSession);
    }
}
