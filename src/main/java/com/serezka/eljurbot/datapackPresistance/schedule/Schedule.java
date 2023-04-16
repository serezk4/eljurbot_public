package com.serezka.eljurbot.datapackPresistance.schedule;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Getter
@AllArgsConstructor
public class Schedule {
    List<Class> schedule;

    // utils
    public static Class findClassByName(Schedule schedule, String className) {
        if (schedule == null || className == null) return null;
        System.out.println(schedule.getSchedule().stream().filter(tempClass->tempClass.getClassName().equalsIgnoreCase(className)).count());

        return schedule.getSchedule().stream().filter(tempClass->tempClass.getClassName().equalsIgnoreCase(className)).findFirst().orElse(null);
    }
}
