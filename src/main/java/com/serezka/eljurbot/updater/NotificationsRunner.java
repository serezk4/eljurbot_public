package com.serezka.eljurbot.updater;

import com.serezka.eljurbot.Application;
import com.serezka.eljurbot.Lang;
import com.serezka.eljurbot.api.school.EljurApi;
import com.serezka.eljurbot.db.model.AuthorizationToken;
import com.serezka.eljurbot.db.model.Mark;
import com.serezka.eljurbot.db.model.User;
import com.serezka.eljurbot.db.services.AuthorizationTokenService;
import com.serezka.eljurbot.db.services.MarkService;
import com.serezka.eljurbot.db.services.UserService;
import com.serezka.eljurbot.telergam.bot.TelegramBot;
import com.serezka.eljurbot.telergam.bot.util.MessageUtil;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Stream;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@PropertySource("classpath:updater.properties")
@PropertySource("classpath:eljur.properties")
@Log4j2
public class NotificationsRunner implements Runnable {
    TelegramBot telegramBot;

    UserService userService;
    AuthorizationTokenService authTokenService;
    MarkService markService;

    public NotificationsRunner(TelegramBot telegramBot,
                               UserService userService,
                               AuthorizationTokenService authTokenService,
                               MarkService markService) {

        this.telegramBot = telegramBot;

        this.userService = userService;
        this.authTokenService = authTokenService;
        this.markService = markService;
    }

    @Override
    public void run() {
        for (; ; ) {
            try {
                // get list of all users
                List<User> users = userService.findAll();

                // go throw all users and check updated marks
                for (User tempUser : users) {
                    new Thread(() -> {
                        AuthorizationToken authToken = authTokenService.findAuthorizationTokenByUserId(tempUser.getId());
                        if (authToken == null || authToken.getAuthToken() == null) {
                            telegramBot.execute(MessageUtil.sendMessage(tempUser.getChatId(), Lang.UPDATER_ERROR_AUTH));
                            return;
                        }

                        if (tempUser.getEljurId() == null) {
                            telegramBot.execute(MessageUtil.sendMessage(tempUser.getChatId(), Lang.UPDATER_RELOGIN));
                            return;
                        }

                        // TODO: 24.11.2022 make request for all study year !!!
                        Calendar threeMonthsAgo = GregorianCalendar.getInstance();
                        threeMonthsAgo.add(Calendar.MONTH, -3);

                        // -> get marks from api
                        List<Mark> apiMarks = new LinkedList<>();
                        EljurApi.getInstance()
                                .requestMarks(threeMonthsAgo, GregorianCalendar.getInstance(), authToken.getAuthToken(), tempUser.getEljurVendor()).get(tempUser.getEljurId())
                                .forEach(lesson -> lesson.getMarks().forEach(mark -> apiMarks.add(Mark.parse(mark, lesson.getName()))));

                        // -> get marks from database
                        List<Mark> databaseMarks = markService.findAllByUserId(tempUser.getId());

                        // -> compare
                        Map<Mark, Operation> updatedMarks = new HashMap<>();
                        Stream.concat(databaseMarks.stream(), apiMarks.stream()).forEach(mark -> {
                            mark.setUserId(tempUser.getId());

                            boolean containsInApi = apiMarks.stream().anyMatch(selected -> selected.equals(mark));
                            boolean containsInDatabase = databaseMarks.stream().anyMatch(selected -> selected.equals(mark));

                            if (containsInApi && containsInDatabase) return;
                            updatedMarks.put(mark, containsInDatabase ? Operation.REMOVED : Operation.ADDED);
                        });

                        // -> send info to user

                        if (updatedMarks.size() <= 5) { // turn off spam attack
                            updatedMarks.forEach((mark, operation) -> {
                                StringBuilder markText = new StringBuilder();

                                markText.append(operation == Operation.ADDED ? Lang.NOTIFICATIONS_NEW_MARK : Lang.NOTIFICATIONS_REMOVED_MARK);
                                markText.append("\n | <b>Предмет: </b>").append(mark.getSubject());
                                markText.append("\n | <b>Оценка: ").append("</b>").append(mark.getMark());
                                markText.append("\n | <b>Дата: </b>").append(Application.MARKS_DATE_FORMAT.format(mark.getDate().getTimeInMillis()));

                                if (mark.getComment() != null && !mark.getComment().isBlank())
                                    markText.append("\n | <b>Комментарий</b>: ").append(mark.getComment());
                                if (mark.getLesson_comment() != null)
                                    markText.append("\n | <b>Коммент. к уроку</b>: ").append(mark.getLesson_comment());

                                telegramBot.execute(MessageUtil.sendMessage(tempUser.getChatId(), markText.toString()));
                            });
                        }

                        // -> update marks
                        markService.saveMarks(updatedMarks.entrySet().stream().filter(entry -> entry.getValue() == Operation.ADDED).map(Map.Entry::getKey).toList());
                        markService.deleteMarks(updatedMarks.entrySet().stream().filter(entry -> entry.getValue() == Operation.REMOVED).map(Map.Entry::getKey).toList());

                        // -> re-check all marks
                        if (!new HashSet<>(apiMarks).containsAll(markService.findAllByUserId(tempUser.getId()))) {
                            markService.deleteAllByUserId(tempUser.getId());
                            markService.saveMarks(apiMarks);
                            log.warn("save marks error : not equals to api");
                        }

                        // -> log
                        log.info("UPDATED for USER [id={} | count={}]", tempUser.getEljurId(), updatedMarks.size());
                    }).start();

                    Thread.sleep(180000);
                }
            } catch (Exception e) {
                log.warn(e.getMessage());
            }
        }
    }

    private enum Operation {
        ADDED, REMOVED;
    }
}
