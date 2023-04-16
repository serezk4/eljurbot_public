package com.serezka.eljurbot.telergam.bot.commands.user;

import com.serezka.eljurbot.telergam.bot.TelegramBot;
import com.serezka.eljurbot.telergam.bot.TelegramUpdate;
import com.serezka.eljurbot.telergam.bot.commands.Command;

import java.util.List;

public class GetHomework extends Command {
    public GetHomework() {
        super(List.of("/hw"), "hw", false);
    }

    @Override
    public void execute(TelegramBot bot, TelegramUpdate tgUpdate, List<String> data) {

    }
}
