package com.serezka.eljurbot.telergam.bot.services;

import com.serezka.eljurbot.telergam.bot.util.MessageUtil;
import lombok.Synchronized;
import lombok.extern.log4j.Log4j2;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.*;

@Log4j2
public class CleanerService extends Thread {
    private final List<MessageData> toRemove = new ArrayList<>();
    public static final long wait = 300;
    private final TelegramLongPollingBot bot;

    public CleanerService(TelegramLongPollingBot bot) {
        setDaemon(false);
        setName("message-cleaner-service");
        this.bot = bot;
    }

    @Synchronized
    public void addToRemove(MessageData messageData) {
        toRemove.add(messageData);
    }

    @Override
    public void run() {
        for (; ; ) {
            try {
                for (MessageData messageData : new LinkedList<>(toRemove)) { // make immutable
                    if (new Date().after(messageData.removeDate())) {
                        try {
                            bot.execute(MessageUtil.delete(messageData.chatId(), messageData.messageId()));
                            toRemove.remove(messageData);
                            Thread.sleep(100); // wait 100 ms to get around telegram spam blocker
                        } catch (TelegramApiException e) {
                            System.out.println(toRemove.toString());
                            log.warn(e.getMessage());
                        }
                    }
                }

                while (toRemove.isEmpty()) {} // wait util list doesn't have messages
                Thread.sleep(300);
            } catch (ConcurrentModificationException | InterruptedException e) {
                log.warn(e.getMessage());
            }
        }
    }

    public record MessageData(int messageId, String chatId, Date removeDate){ /* empty */ }

}
