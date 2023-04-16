package com.serezka.eljurbot;

import java.util.List;
import java.util.Map;

public class Lang {
    public static final String BOT_CREATOR_LINK = "@serezkk";

    // root
    public static final String command_help_amd = " *> %s - %s";
    public static final String command_help = " > %s - %s";
    public static final String operation_canceled = "\uD83D\uDC48 <b>Операция отменена</b> \uD83D\uDC48";
    public static final String cancel = "\uD83D\uDC48 Отмена";
    public static final String exit = "закрыть";
    public static final String cancel_short = "\uD83D\uDC48";
    public static final String SEND_HW_NOT_AUTHORIZED = "Вы не авторизированы в боте. /link - авторизация";
    public static final String SEND_HW_UPDATE = "Обновить";
    public static final String SEND_HW_TEXT_ALL_HW = "<b>Вся домашняя работа:</b>";
    public static final List<String> SEND_ALL_DATA_USE = List.of("/all");
    public static final String SEND_ALL_DATA_HELP = "all data";
    public static final String SEND_HW_NEXT_WEEK = "след. неделя >>";
    public static final String SEND_HW_PRED_WEEK = "<< пред. неделя";
    public static final String MENU_SESSION_CLOSED = "меню закрыто";
    public static final String START_HELP = "запустить бота";
    public static final String START_TEXT = "Бот <b>использует</b> ваш <code>логин</code> и <code>пароль</code> для входа в журнал.\n" +
            "<code>Бот создан </code> @serezkk. <code> Все вопросы о боте или его исходном коде к нему. </code>";
    public static final List<String> GET_HOMEWORK_USE = List.of("ДЗ", "/hw");
    public static final String UPDATER_ERROR_AUTH = "Ошибка ключа авторизации. Напишите @serezkk";
    public static final String UPDATER_RELOGIN = "/link - войдите заного.";
    public static final String LINK_ACCOUNTS_INPUT_NIKASOFT_URL = "Укажите ссылку на школьное расписание, если есть: ";
    public static final String LINK_ACCOUNTS_SKIP_NIKASOFT_URL_BTN = "Укажите ссылку на школьное расписание, если есть: ";
    public static final String LINK_ACCOUNTS_USE_SITE = "<b>Используйте авторизацию через кнопку.</b>\n/link - повторить";
    public static final String LINK_ACCOUNTS_AUTH_ERROR = "Возникла <b>ошибка</b> во время авторизации. Попробуйте позже или напишите: " + BOT_CREATOR_LINK;
    public static final String LINK_ACCOUNTS_ENTER_ALL_FIELDS = "Введите <b>все</b> необходимые поля для авторизации. \n /link - повторить.";
    public static String go_back = "<<<";

    // commands help
    public static final String LINK_ACCOUNTS_HELP = "Войти в дневник";
    public static final String GET_MARKS_HELP = "Получить оценки";
    public static final String SKIP_LESSONS_COUNT_HELP = "сколько уроков пропущено";
    public static final String NEED_TO_REACH_AVERAGE_HELP = "не придумал";

    // commands use
    public static final List<String> LINK_ACCOUNTS_USE = List.of("/link", "\uD83D\uDCD5 Войти");
    public static final List<String> GET_MARKS_USE = List.of("/marks", "\uD83D\uDCD6 Оценки");
    public static final List<String> SKIP_LESSONS_COUNT_USE = List.of("/skips", "\uD83C\uDE32 Пропуски");
    public static final List<String> NEED_TO_REACH_AVERAGE_USE = List.of("/avg", "\uD83E\uDD1E Средний балл");

    // commands text
    public static final String START_ABOUT = "Для начала работы с ботом вам необходимо авторизоваться с помощью своего аккаунта Eljur: /link";
    public static final String MY_ACCOUNT_DATA_FORMAT = "Данные от <b>Eljur</b>:%n - <b>имя</b>: %s%n - <b>школа</b>: %s%n - <b>класс</b>: %s%n - <b>EljurID</b>: %s%n%n" +
            "Данные от <b>NikaSoft</b>:%n - <b>Ссылка</b>: %s";
    public static final String TELEGRAM_HANDLER_ACCESS_DENIED = "Доступ запрещен";
    public static final String LINK_ACCOUNTS_SELECT_VENDOR = "\uD83C\uDFEB <b>Укажите адрес сайта школы:</b>";
    public static final String LINK_ACCOUNTS_INPUT_USERNAME = "\uD83D\uDC64 <b>Введите логин:</b>";
    public static final String NOTIFICATIONS_NEW_MARK = "✅ <b>Новая оценка</b>";
    public static final String NOTIFICATIONS_REMOVED_MARK = "\uD83C\uDE32 <b>Удаленная оценка</b>";
    public static final String LINK_ACCOUNTS_SAVE = "<b>%s</b>%n<span class=\"tg-spoiler\"> школа: %s%n юзернейм: %s%n пароль: %s%n класс: %s%n ID: %s%n Nika URL: %s</span>";
    public static final String LINK_ACCOUNTS_INPUT_PASSWORD = "\uD83D\uDD10 <b>Введите пароль:</b>";
    public static final String LINK_ACCOUNTS_ELJUR_RESPONSE_ERROR = "\uD83C\uDE32 Авторизация <b>не удалась</b>.%n<code>%s</code>%n/link - <i>попробовать заново</i>.";
    public static final String LINK_ACCOUNTS_UNSUCCESSFUL_SAVE = "Сохранение в бд не прошло. Повторите попытку позже.";
    public static final String LINK_ACCOUNTS_SUCCESSFUL_SAVE = "✅ Данные <b>успешно</b> обновлены.";
    public static final String LINK_ACCOUNTS_DATABASE_ERROR = "<strong> Внутрення ошибка приложения при работе с БД. </strong> \n Напишите этому приколисту -> " + BOT_CREATOR_LINK;
    public static final String SKIP_LESSONS_COUNT_USER_NOT_AUTHORIZED = "Вы <b>не авторизированы</b> в системе элжур.\n/link - авторизация.";
    public static final String SKIP_LESSONS_COUNT_RESULT = "<b>Пропусков: </b><span class=\"tg-spoiler\">%d</span>%n<b>Опозданий: </b><span class=\"tg-spoiler\">%d</span>";
    public static final String GET_MARKS_USER_NOT_AUTHORIZED = "Вы <b>не авторизированы</b> в системе элжур.\n/link - авторизация.";
    public static final String FIND_AVERAGE_USER_NOT_AUTHORIZED = "Вы <b>не авторизированы</b> в системе элжур.\n/link - авторизация.";
    public static final String NEED_TO_REACH_AVERAGE_USER_NOT_AUTHORIZED = "Вы <b>не авторизированы</b> в системе элжур.\n/link - авторизация.";
    public static final String NEED_TO_REACH_AVERAGE = "<b>%s</b> > \uD83D\uDFE2<code>%.1f</code>:";
    public static final String NEED_TO_REACH_AVERAGE_AVERAGE_NORMAL = "По <b>всем предметам</b> средний балл превышает <code>%.1f</code>.";
    public static final String SEND_MARKS_ALL_MARKS = "\uD83C\uDF1F все оценки";
    public static final String SEND_MARKS_SELECT_SUBJECT = "Выберите предмет";
    public static final String SEND_MARKS_SUBJECT_NOT_FOUND = "предмет не найден. повторите запрос";
    public static final String SEND_MARKS_MARK_FORMAT = "> <b>%s</b> - <i>%tD</i>%n";
    public static final String SEND_MARKS_NO_MARKS = "Нет оценок";

    public static final List<String> BOOKS = List.of("\uD83D\uDCD5", "\uD83D\uDCD2", "\uD83D\uDCD8", "\uD83D\uDCD7", "\uD83D\uDCD9");

    public static final Map<String, String> GET_MARKS_TO_EMOJI = Map.of(
            "1", "1️⃣",
            "2", "2️⃣",
            "3", "3️⃣",
            "4", "4️⃣",
            "5", "5️⃣"
    );


    // stickers
    public static final String STICKER_MASUNYA_LIKE = "CAACAgIAAxkBAAOMYzXOnDGFpuPwvYxWNNUoJGbk7TkAAn4XAAKGbghK6ipyrIFpvwcqBA";
    public static final String STICKER_MASUNYA_FEAR = "CAACAgIAAxkBAAOXYzXPEA0RLZjwu5n0VZuER3kEtnwAAg8ZAAJDPxFKn0ywxNH4QhEqBA";
    public static final String STICKER_SPIN_DISLIKE = "CAACAgIAAxkBAAIF2WNEf2khU3-EFPHZEC-Afjl1D6MGAAIlGAACRBZASJ7f1ijMDKCwKgQ";

    public static final String GET_MARKS_CHOOSE_ITEM_TEXT = "С/б по всем предметам: <b>%.3f</b>%n";

}
