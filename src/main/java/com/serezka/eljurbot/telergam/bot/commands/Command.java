package com.serezka.eljurbot.telergam.bot.commands;

import com.serezka.eljurbot.telergam.bot.TelegramBot;
import com.serezka.eljurbot.telergam.bot.TelegramUpdate;
import com.serezka.eljurbot.telergam.bot.session.type.menu.Menu;
import com.serezka.eljurbot.telergam.bot.session.type.step.StepGenerator;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@Data @RequiredArgsConstructor
public abstract class Command {
    private final List<String> names;
    private final String help;
    private final boolean admin;

    public abstract void execute(TelegramBot bot, TelegramUpdate tgUpdate, List<String> data);

    private List<StepGenerator> stepGenerators = null;
    private Menu menu = null;
}
