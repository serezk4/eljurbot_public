package com.serezka.eljurbot.telergam.bot.util;

import com.serezka.eljurbot.api.school.EljurApi;

import java.util.*;
import java.util.concurrent.*;

public class GetMarksUtil {
    public static Map<String, Map<EljurApi.Marks.Lesson, EljurApi.Marks.Mark>> findAllMarks(
            String eljurDevKey, String vendor, String authorizationToken) throws InterruptedException, ExecutionException, TimeoutException {
        Map<String, Map<EljurApi.Marks.Lesson, EljurApi.Marks.Mark>> allMarks = new HashMap<>();

        Calendar threeMonthsAgo = GregorianCalendar.getInstance();
        threeMonthsAgo.add(Calendar.DATE, -80);

        Calendar current=  GregorianCalendar.getInstance();


        return allMarks;
    }

    public static Date getDateThreeMonthsAgo(Date date) {
        return new Date(date.getTime() - 7789400000L);
    }
}
