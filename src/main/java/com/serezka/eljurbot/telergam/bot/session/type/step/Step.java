package com.serezka.eljurbot.telergam.bot.session.type.step;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;

import java.util.List;

@RequiredArgsConstructor
@Getter
public class Step {
    private final ReplyKeyboard replyKeyboard;
    private final String text;

    public String canSkip(String chatId, List<String> data) { return "";}
}
