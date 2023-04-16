package com.serezka.eljurbot.telergam.bot.commands.user;

import com.serezka.eljurbot.Lang;
import com.serezka.eljurbot.telergam.bot.TelegramBot;
import com.serezka.eljurbot.telergam.bot.TelegramUpdate;
import com.serezka.eljurbot.telergam.bot.commands.Command;
import com.serezka.eljurbot.telergam.bot.util.KeyboardUtil;
import com.serezka.eljurbot.telergam.bot.util.MessageUtil;
import com.serezka.eljurbot.telergam.bot.util.UpdateUtil;
import lombok.extern.log4j.Log4j2;
import org.telegram.telegrambots.meta.api.methods.pinnedmessages.PinChatMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@Log4j2
public class Start extends Command {
    public Start() {
        super(List.of("/start"), Lang.START_HELP, false);
    }

    @Override
    public void execute(TelegramBot tgBot, TelegramUpdate tgUpdate, List<String> data) {
        tgBot.sendMessage(tgUpdate.getChatId(), Lang.START_TEXT, -1);
        tgBot.sendMessage(tgUpdate.getChatId(), ".", -1);
        tgBot.sendMessage(tgUpdate.getChatId(), Lang.START_ABOUT, 100);
    }
}
