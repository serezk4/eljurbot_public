package com.serezka.eljurbot.datapackPresistance.schedule;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Getter @AllArgsConstructor
public class Day {
    Long dateInMillis;
    List<Lesson> lessons;
}
