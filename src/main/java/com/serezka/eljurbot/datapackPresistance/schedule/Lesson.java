package com.serezka.eljurbot.datapackPresistance.schedule;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter @Setter
public class Lesson {
    int lessonPosition = 0;

    List<String> subjects = null;
    List<String> teachers = null;
    List<String> rooms = null;

    Calendar lessonStartTime = null;
    Calendar lessonEndTime = null;
}
