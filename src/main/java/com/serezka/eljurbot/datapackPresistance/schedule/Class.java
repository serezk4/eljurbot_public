package com.serezka.eljurbot.datapackPresistance.schedule;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Getter @RequiredArgsConstructor
public class Class {
    String className;
    List<Day> days;
}
