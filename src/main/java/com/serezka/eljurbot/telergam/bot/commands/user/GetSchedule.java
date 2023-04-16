package com.serezka.eljurbot.telergam.bot.commands.user;

import com.serezka.eljurbot.api.school.NikasoftApi;
import com.serezka.eljurbot.datapackPresistance.schedule.Class;
import com.serezka.eljurbot.datapackPresistance.schedule.Schedule;
import com.serezka.eljurbot.db.model.User;
import com.serezka.eljurbot.db.services.UserService;
import com.serezka.eljurbot.telergam.bot.TelegramBot;
import com.serezka.eljurbot.telergam.bot.TelegramUpdate;
import com.serezka.eljurbot.telergam.bot.commands.Command;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Log4j2
@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GetSchedule extends Command {
    UserService userService;

    public GetSchedule(UserService userService) {
        super(List.of("/getschedule"), "получить расписание", false);

        this.userService = userService;
    }

    @Override
    public void execute(TelegramBot tgBot, TelegramUpdate tgUpdate, List<String> data) {
        User selectedUser = userService.findUserByChatId(tgUpdate.getChatId());

        Calendar cal = GregorianCalendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DAY_OF_MONTH, -7);

        Class selectedClass = Schedule.findClassByName(
                NikasoftApi.Utils.getRefactoredSchedule(NikasoftApi.getSchedule(selectedUser.getNikasoftUrl())), selectedUser.getEljurSchoolClass());


        if (selectedClass != null) {
            selectedClass.getDays().forEach(day->{
                System.out.println("Day #"+day);
                day.getLessons().forEach(lesson -> System.out.println(lesson.getLessonPosition() + " " + lesson.getSubjects()));
                System.out.println();
            });
        } else tgBot.sendMessage(tgUpdate.getChatId(), "ошибка запроса");
    }
}
