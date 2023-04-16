package com.serezka.eljurbot.telergam.bot.session;

import com.serezka.eljurbot.telergam.bot.commands.Command;
import com.serezka.eljurbot.telergam.bot.session.type.step.StepSession;
import lombok.Synchronized;

import java.util.HashMap;
import java.util.Map;

public class StepSessionManager {
    private static final Map<String, StepSession> stepSessions = new HashMap<>();

    @Synchronized
    public static boolean containsSession(String chatId) {
        return stepSessions.containsKey(chatId);
    }

    @Synchronized
    public static StepSession getSession(String chatId) {
        return stepSessions.getOrDefault(chatId, null);
    }

    @Synchronized
    public static void addSession(String chatId, Command command) {
        stepSessions.put(chatId, new StepSession(command, chatId));
    }

    @Synchronized
    public static void removeSession(String chatId) {
        stepSessions.remove(chatId);
    }
}
