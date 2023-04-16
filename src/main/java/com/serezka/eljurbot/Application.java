package com.serezka.eljurbot;

import com.serezka.eljurbot.api.ApiUtils;
import com.serezka.eljurbot.api.school.NikasoftApi;
import com.serezka.eljurbot.api.school.SibsauApi;
import com.serezka.eljurbot.datapackPresistance.schedule.Class;
import com.serezka.eljurbot.datapackPresistance.schedule.Schedule;
import com.serezka.eljurbot.db.services.AuthorizationTokenService;
import com.serezka.eljurbot.db.services.MarkService;
import com.serezka.eljurbot.db.services.UserService;
import com.serezka.eljurbot.telergam.bot.services.CleanerService;
import com.serezka.eljurbot.telergam.bot.TelegramBot;
import com.serezka.eljurbot.telergam.bot.TelegramHandler;
import com.serezka.eljurbot.telergam.bot.commands.user.*;
import com.serezka.eljurbot.updater.NotificationsRunner;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Optional;

@SpringBootApplication @RequiredArgsConstructor // todo remove
@AutoConfigureAfter @Log4j2
public class Application implements ApplicationRunner { // 2023-09-29 23:33:54
    // date formatters
    public static final SimpleDateFormat AUTHORIZATION_TOKEN_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    public static final SimpleDateFormat QUERY_DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");
    public static final SimpleDateFormat MARKS_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    public static final SimpleDateFormat MONTH_DATE_FORMAT = new SimpleDateFormat("dd.MM");

    // start app date
    public static final Calendar applicationStart = GregorianCalendar.getInstance();

    // telegram bot
    final TelegramBot telegramBot;
    final TelegramHandler telegramHandler;

    // database services
    final UserService userService;
    final MarkService markService;
    final AuthorizationTokenService authorizationTokenService;

    // bot commands
    final LinkAccounts linkAccounts;
    final GetSchedule getSchedule;
    final SendMarks sendMarks;
    final NeedToReachAverage needToReachAverage;

    // eljur telegram link updater
    final NotificationsRunner notificationsRunner;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
//        SibsauApi.getSchedule(13095);

        telegramHandler.addCommand(linkAccounts);
        telegramHandler.addCommand(new Start());
        telegramHandler.addCommand(new GetBotStatus());
        telegramHandler.addCommand(getSchedule);
        telegramHandler.addCommand(sendMarks);
        telegramHandler.addCommand(needToReachAverage);

        telegramBot.setTelegramHandler(telegramHandler);

        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        telegramBotsApi.registerBot(telegramBot);

        new Thread(notificationsRunner).start();
        
        Thread messageRemoveThread = new Thread(new CleanerService(telegramBot));
        messageRemoveThread.setDaemon(false);
        messageRemoveThread.start();
    }
}
