package com.serezka.eljurbot.telergam.bot.commands.user;

import com.serezka.eljurbot.Lang;
import com.serezka.eljurbot.db.model.Mark;
import com.serezka.eljurbot.db.model.User;
import com.serezka.eljurbot.db.services.MarkService;
import com.serezka.eljurbot.db.services.UserService;
import com.serezka.eljurbot.telergam.bot.TelegramBot;
import com.serezka.eljurbot.telergam.bot.TelegramUpdate;
import com.serezka.eljurbot.telergam.bot.commands.Command;
import com.serezka.eljurbot.telergam.bot.util.MessageUtil;
import com.serezka.eljurbot.telergam.bot.util.UpdateUtil;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.java.Log;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.*;

@Service
@Log
@PropertySource("classpath:eljur.properties")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NeedToReachAverage extends Command {
    UserService userService;
    MarkService markService;

    public NeedToReachAverage(UserService userService, MarkService markService) {
        super(Lang.NEED_TO_REACH_AVERAGE_USE, Lang.NEED_TO_REACH_AVERAGE_HELP, false);

        this.userService = userService;
        this.markService = markService;
    }

    @Override
    public void execute(TelegramBot bot, TelegramUpdate update, List<String> data) {
        try {
            String chatId = update.getChatId();
            if (!userService.userExistsByChatId(chatId)) {
                bot.execute(MessageUtil.sendMessage(chatId, Lang.NEED_TO_REACH_AVERAGE_USER_NOT_AUTHORIZED));
                return;
            }

            User user = userService.findUserByChatId(chatId);
            if (user == null) {
                bot.execute(MessageUtil.sendMessage(chatId, Lang.FIND_AVERAGE_USER_NOT_AUTHORIZED));
                bot.execute(MessageUtil.sendSticker(chatId, Lang.STICKER_MASUNYA_FEAR));
                return;
            }

            List<Mark> allMarks = markService.findAllByUserId(user.getId());

            Map<String, List<Integer>> marksForSubject = new HashMap<>();
            allMarks
                    .stream()
                    .map(Mark::getSubject)
                    .forEach(subject -> marksForSubject.put(subject, allMarks.stream()
                            .filter(mark -> mark.getSubject().equals(subject))
                            .filter(mark -> !mark.getComment().startsWith("ДЗ (Не идет в журнал)")) // for school 158
                            .map(Mark::getMark).map(markValue -> markValue.replaceAll("\\D+", ""))
                            .filter(markValue -> !markValue.isBlank())
                            .map(Integer::parseInt).toList()));

            StringBuilder sb = new StringBuilder();
            marksForSubject.forEach((subject, marks) -> {
                if (marks.isEmpty()) return;
                if (findAverage(marks) >= 4) return;

                sb.append(String.format(Lang.NEED_TO_REACH_AVERAGE, subject, 4.00)).append("\n");
                sb.append("<b>").append("  5: </b>").append(needMarksToReachAverage(5, marks, 4.0));
                sb.append("\n\n");

            });

            if (sb.isEmpty()) sb.append(String.format(Lang.NEED_TO_REACH_AVERAGE_AVERAGE_NORMAL, 4.00));
            bot.execute(MessageUtil.sendMessage(chatId, sb.toString()));

        } catch (Exception e) {
            log.warning(e.getMessage());
        }
    }

    private long needMarksToReachAverage(int mark, List<Integer> marks, double needAverage) {
        if (mark < findAverage(marks) || findAverage(marks) > needAverage) return 0;

        long marksSum = marks.stream().mapToInt(Integer::intValue).summaryStatistics().getSum();
        for (long x = 0; x < 20; x++)
            if (needAverage < ((double) (marksSum + (mark * x)) / (marks.size() + x))) return x;

        return -1;
    }

    public static double findAverage(List<Integer> marks) {
        return marks.stream().mapToInt(Integer::intValue).summaryStatistics().getAverage();
    }
}