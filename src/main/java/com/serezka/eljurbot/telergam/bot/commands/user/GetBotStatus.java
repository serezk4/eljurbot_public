package com.serezka.eljurbot.telergam.bot.commands.user;

import com.serezka.eljurbot.Application;
import com.serezka.eljurbot.telergam.bot.TelegramBot;
import com.serezka.eljurbot.telergam.bot.TelegramUpdate;
import com.serezka.eljurbot.telergam.bot.commands.Command;
import com.serezka.eljurbot.telergam.bot.util.MessageUtil;
import com.serezka.eljurbot.telergam.bot.util.UpdateUtil;
import com.sun.management.OperatingSystemMXBean;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.lang.management.ManagementFactory;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

public class GetBotStatus extends Command {
    public GetBotStatus() {
        super(List.of("/status"), "bot status", false);
    }

    @Override
    public void execute(TelegramBot tgBot, TelegramUpdate tgUpdate, List<String> data) {
        OperatingSystemMXBean os = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();

        String sb = "<b>All System Data:</b>\n" +
                " | <b>Threads count</b>: <code>" + Thread.activeCount() + "</code>\n" +
                " | <b>Memory use</b>: <code>" + Runtime.getRuntime().freeMemory() / 1000000 + "mb of " + Runtime.getRuntime().totalMemory() / 1000000 + "mb</code>\n" +
                " | <b>Processor:</b> \n<code>" +
                " \\ " + "time: " + (os.getProcessCpuTime() / 1000000000 / 60) + " mins\n" +
                " / " + "load: " + String.format("%.2f", os.getCpuLoad()*100) + "%</code>\n" +
                " | <b>Uptime: </b> <code> " + (Application.applicationStart.get(Calendar.HOUR) - GregorianCalendar.getInstance().get(Calendar.HOUR)) + " hours </code>";

        tgBot.sendMessage(tgUpdate.getChatId(), sb);
    }
}
