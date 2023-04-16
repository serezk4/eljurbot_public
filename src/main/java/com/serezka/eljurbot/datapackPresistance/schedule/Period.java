package com.serezka.eljurbot.datapackPresistance.schedule;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.Calendar;
import java.util.Date;

@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
@Getter @AllArgsConstructor
public class Period {
    int id;
    Calendar startDate,endDate;
    String name;
}
