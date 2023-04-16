package com.serezka.eljurbot.telergam.bot.commands.user;

import com.serezka.eljurbot.Application;
import com.serezka.eljurbot.Lang;
import com.serezka.eljurbot.db.model.Mark;
import com.serezka.eljurbot.db.model.User;
import com.serezka.eljurbot.db.services.MarkService;
import com.serezka.eljurbot.db.services.UserService;
import com.serezka.eljurbot.telergam.bot.TelegramBot;
import com.serezka.eljurbot.telergam.bot.TelegramUpdate;
import com.serezka.eljurbot.telergam.bot.commands.Command;
import com.serezka.eljurbot.telergam.bot.commands.user.NeedToReachAverage;
import com.serezka.eljurbot.telergam.bot.session.type.menu.Menu;
import com.serezka.eljurbot.telergam.bot.session.type.menu.Page;
import com.serezka.eljurbot.telergam.bot.util.KeyboardUtil;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.*;
import java.util.stream.Collectors;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
@Log
public class SendMarks extends Command {

    public SendMarks(SelectSubjectPage selectSubjectPage, GetMarksPage getMarksPage, GetMarkPage getMarkPage) {
        super(Lang.GET_MARKS_USE, Lang.GET_MARKS_HELP, false);

        setMenu(new Menu(selectSubjectPage, Map.of("marks", getMarksPage, "mark", getMarkPage)));
    }

    @Override
    public void execute(TelegramBot bot, TelegramUpdate tgUpdate, List<String> data) {
        // ...
    }


    @Service
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    @RequiredArgsConstructor
    private static class GetMarkPage extends Page {
        UserService userService;
        MarkService markService;

        @Override
        public Data generate(String chatId, String query) {
            String[] queryData = query.split(";");
            if (queryData.length != 4)
                return new Data("Ошибка!", List.of(new KeyboardUtil.InlineButtonData(Lang.go_back, "root")), 2);

            long markId = Long.parseLong(queryData[2]);
            List<Mark> foundedMarks = markService.findAllByUserId(userService.findUserByChatId(chatId).getId()).stream().filter(mark -> mark.getMarkId() == markId).toList();
            if (foundedMarks.isEmpty()) return new Data("Оценка не найдена", List.of(new KeyboardUtil.InlineButtonData(Lang.go_back, "root")), 2);

            Mark foundedMark = foundedMarks.iterator().next();

            StringBuilder messageText = new StringBuilder();
            messageText.append("<b>\uD83D\uDCDA Предмет:</b> ").append(foundedMark.getSubject()).append("\n");
            messageText.append("<b>✔ Оценка:</b> ").append(foundedMark.getMark()).append("\n");
            messageText.append("<b>\uD83D\uDCC5 Дата:</b> ").append(Application.MARKS_DATE_FORMAT.format(foundedMark.getDate().getTimeInMillis())).append("\n");
            if (!foundedMark.getComment().isBlank()) messageText.append("<b>\uD83D\uDCDD Комментарий:</b> ").append(foundedMark.getComment()).append("\n");

            return new Data(messageText.toString(), List.of(new KeyboardUtil.InlineButtonData(Lang.go_back, "marks;" + queryData[3])), 2);
        }
    }

    @Service
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    @RequiredArgsConstructor
    private static class GetMarksPage extends Page {
        UserService userService;
        MarkService markService;

        @Override
        public Data generate(String chatId, String query) {
            String[] queryData = query.split(";");
            if (queryData.length != 3)
                return new Data("Ошибка!", List.of(new KeyboardUtil.InlineButtonData(Lang.go_back, "root")), 2);

            String subjectName = queryData[2];

            List<Mark> userMarks = markService.findAllByUserId(userService.findUserByChatId(chatId).getId());
            if (userMarks.stream().map(Mark::getSubject).noneMatch(subjectName::equalsIgnoreCase) && !subjectName.equals("all_marks"))
                return new Data("Предмет не найден!", List.of(new KeyboardUtil.InlineButtonData(Lang.go_back, "root")), 2);

            List<Mark> marksForSubject = userMarks.stream().filter(mark -> mark.getSubject().equalsIgnoreCase(subjectName)).toList();

            List<Mark> parsedMarks = marksForSubject
                    .stream()
                    .filter(mark -> !mark.getComment().toLowerCase().contains("не идет в журнал")).toList();

            List<Integer> allNumericalMarks = parsedMarks
                    .stream()
                    .map(Mark::getMark)
                    .filter(markVal -> markVal.matches("\\d"))
                    .map(Integer::parseInt).toList();


            Calendar weekAgo = GregorianCalendar.getInstance();
            int delta = -weekAgo.get(GregorianCalendar.DAY_OF_WEEK) + 2;
            weekAgo.add(Calendar.DAY_OF_MONTH, delta);
            Date dateWeekAgo = new Date(weekAgo.getTimeInMillis());

            double weekAgoAverage = parsedMarks
                    .stream().filter(mark -> mark.getDate().before(dateWeekAgo))
                    .map(Mark::getMark).filter(markVal -> markVal.matches("\\d"))
                    .mapToInt(Integer::parseInt).summaryStatistics().getAverage();

            StringBuilder messageText = new StringBuilder();
            messageText.append("\uD83C\uDF1F<b>").append(subjectName).append("</b>\n");

            double currentAverage = allNumericalMarks.stream().mapToInt(i -> i).summaryStatistics().getAverage();

            messageText.append("| С/р балл: <b>").append(String.format("%.3f", NeedToReachAverage.findAverage(allNumericalMarks))).append("</b>\n");
            messageText.append("| Тенденция изменения: ").append(String.format("<code> %.2f -> %.2f </code>", weekAgoAverage, currentAverage)).append("\n");

            List<KeyboardUtil.InlineButtonData> buttonsData = new LinkedList<>();
            marksForSubject.forEach(mark -> buttonsData.add(new KeyboardUtil.InlineButtonData(Lang.GET_MARKS_TO_EMOJI.containsKey(mark.getMark()) ? Lang.GET_MARKS_TO_EMOJI.get(mark.getMark()) : mark.getMark().toUpperCase(), "mark;" + mark.getMarkId() + ";" + subjectName)));

            if (allNumericalMarks.isEmpty()) messageText.append(Lang.SEND_MARKS_NO_MARKS);

            buttonsData.add(new KeyboardUtil.InlineButtonData(Lang.go_back, "root"));

            return new Data(messageText.toString(), buttonsData, 6);
        }
    }

    @Service
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    @RequiredArgsConstructor
    private static class SelectSubjectPage extends Page {
        UserService userService;
        MarkService markService;

        @Override
        public Data generate(String chatId, String query) {
            if (!userService.userExistsByChatId(chatId))
                return new Data(Lang.GET_MARKS_USER_NOT_AUTHORIZED, List.of(new KeyboardUtil.InlineButtonData(Lang.go_back, "root")), 1);

            User currentUser = userService.findUserByChatId(chatId);
            Set<String> subjects = markService.findAllByUserId(currentUser.getId()).stream().map(Mark::getSubject).collect(Collectors.toSet());

            List<Integer> allNumericalMarks = markService.findAllByUserId(currentUser.getId())
                    .stream()
                    .filter(mark -> !mark.getComment().toLowerCase().contains("не идет в журнал"))
                    .map(Mark::getMark)
                    .filter(markVal -> markVal.matches("\\d"))
                    .map(Integer::parseInt).toList();


            List<KeyboardUtil.InlineButtonData> buttonsData = new LinkedList<>();
            for (String subject : subjects)
                buttonsData.add(new KeyboardUtil.InlineButtonData(
                        Lang.BOOKS.get((int) (Math.random() * (Lang.BOOKS.size()))) + String.format("%.12s", subject.toLowerCase()), "marks;" + subject));


            return new Data(String.format(Locale.UK, Lang.GET_MARKS_CHOOSE_ITEM_TEXT, NeedToReachAverage.findAverage(allNumericalMarks)), buttonsData, 2);
        }
    }
}