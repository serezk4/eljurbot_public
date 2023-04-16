package com.serezka.eljurbot.telergam.bot.session.type.menu;

import com.serezka.eljurbot.telergam.bot.util.KeyboardUtil;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.List;

public abstract class Page {
    public abstract Data generate(String chatId, String query);

    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    @RequiredArgsConstructor @Getter
    public static class Data {
        String text;
        List<KeyboardUtil.InlineButtonData> buttonsData;
        int rowSize;
    }
}
