package com.serezka.eljurbot.telergam.bot.session.type.menu;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.Map;

@Getter @RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Menu {
    final Page rootPage;

    final Map<String,Page> pages;
}
