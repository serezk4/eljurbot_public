package com.serezka.eljurbot.telergam.bot;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.telegram.telegrambots.meta.api.objects.Update;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor @Getter
public class TelegramUpdate {
    Update self;
    public int getMessageId() {
        return self.hasCallbackQuery() ?
                self.getCallbackQuery().getMessage().getMessageId() :
                self.getMessage().getMessageId();
    }

    public String getChatId() {
        return String.valueOf(self.hasCallbackQuery() ?
                self.getCallbackQuery().getMessage().getChatId() :
                self.getMessage().getChatId());
    }

    public String getUsername() {
        return self.hasCallbackQuery() ?
                self.getCallbackQuery().getMessage().getChat().getUserName() :
                self.getMessage().getChat().getUserName();
    }

    public String getText() {
        return self.hasCallbackQuery() ?
                self.getCallbackQuery().getData() :
                self.getMessage().getWebAppData() != null ? self.getMessage().getWebAppData().getData() : self.getMessage().getText();
    }
}
