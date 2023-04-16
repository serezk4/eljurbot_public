package com.serezka.eljurbot.api.school;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import com.serezka.eljurbot.api.ApiUtils;
import com.serezka.eljurbot.datapackPresistance.schedule.Class;
import com.serezka.eljurbot.datapackPresistance.schedule.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Log4j2
public class NikasoftApi {
    static SimpleDateFormat NIKA_PERIOD_DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");
    static String MAIN_URL = "http://raspisanie.nikasoft.ru/static/public/%s.js";

    static Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .serializeNulls()
            .create();

    static SimpleDateFormat EXCHANGE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");

    private static String generateUrl(String queryId) {
        return String.format(MAIN_URL, queryId);
    }

    private static final ScheduleInfo emptyScheduleInfo = new ScheduleInfo();

    public static ScheduleInfo getSchedule(String nikaUrl) {
        String nikaParsed;
        try {
            nikaParsed = ApiUtils.parseUrl(new URI(nikaUrl).toURL());
        } catch (URISyntaxException | IOException e) {
            log.warn(e.getMessage());
            return new ScheduleInfo();
        }

        if (nikaParsed == null) return emptyScheduleInfo;

        if (!nikaParsed.contains("schedule_id = ") || !nikaParsed.contains(".js';")) {
            log.warn("can't find initial_schedule_id!");
            return emptyScheduleInfo;
        }

        String requestScheduleUrl = generateUrl(nikaParsed.substring(nikaParsed.indexOf("schedule_id = ") + "schedule_id = '".length(), nikaParsed.indexOf(".js';")));

        URL apiRequest;
        String apiResponse;

        try {
            apiRequest = new URI(requestScheduleUrl).toURL();
            apiResponse = ApiUtils.parseUrl(apiRequest);
        } catch (URISyntaxException | IOException e) {
            log.warn(e.getMessage());
            return emptyScheduleInfo;
        }


        if (apiResponse == null) {
            log.warn("empty apiResponse");
            return emptyScheduleInfo;
        }

        apiResponse = apiResponse.substring(126 + 8); //remove var NIKA = ....
        apiResponse = apiResponse.replaceAll(";", "");
        apiResponse = apiResponse.replaceAll("\n", "");
        apiResponse = apiResponse.replaceAll("\"F\"", "[\"F\"]");

        ScheduleInfo response = gson.fromJson(apiResponse.trim(), ScheduleInfo.class);
        if (response == null) {
            log.warn("an error occured!");
            return emptyScheduleInfo;
        }

        return response;
    }

    public static class Utils {

        @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
        @Getter
        @AllArgsConstructor
        @ToString
        public static class LessonData {
            int lessonPosition;

            List<String> lessonsName;
            List<String> teachersName;
            List<String> roomsName;
        }

        public static Schedule getRefactoredSchedule(ScheduleInfo scheduleInfo) {
            long start = System.currentTimeMillis();

            if (scheduleInfo == null || scheduleInfo.getPeriods() == null ||
                    scheduleInfo.getClassesIds() == null ||
                    scheduleInfo.getTeachersIds() == null ||
                    scheduleInfo.getRoomsIds() == null) {

                log.warn("empty scheduleInfo data");
                return new Schedule(Collections.emptyList());
            }

            // TODO: 11.02.2023 ADD CACHE

            final BiMap<String, List<Day>> scheduleForClasses = HashBiMap.create();

            final Map<String, List<String>> lessonTimes = scheduleInfo.getLessonTimes();

            scheduleInfo.getPeriods().forEach((stringPeriodId, periodData) -> {
                try {

                    long periodCollectTime = System.currentTimeMillis();

                    // collect data about period
                    final Calendar periodStartDate = GregorianCalendar.getInstance();
                    final Calendar periodEndDate = GregorianCalendar.getInstance();

                    // set time
                    periodStartDate.setTime(NIKA_PERIOD_DATE_FORMAT.parse(periodData.get("b")));
                    periodEndDate.setTime(NIKA_PERIOD_DATE_FORMAT.parse(periodData.get("e")));

                    final Map<String, Map<String, Map<String, List<String>>>> scheduleForPeriod = scheduleInfo.getClassSchedule().get(stringPeriodId);
                    final Map<String, Map<String, Map<String, Map<String, List<String>>>>> scheduleExchange = scheduleInfo.getClassExchange();

                    // process
                    scheduleInfo.getClassesIds().forEach((classId, className) -> {
                        // classId -> date -> scheduleExchange
                        final Multimap<Long, Lesson> tempDays = HashMultimap.create();

                        final Map<String, Map<String, Map<String, List<String>>>> classExchange = scheduleExchange.getOrDefault(classId, Collections.emptyMap());

                        // get lesson
                        scheduleForPeriod.get(classId).forEach((positionData, lessonData) -> {
                            if (positionData.length() != 3) return; // check len

                            // collect data about day
                            final int dayPosition = Integer.parseInt(positionData.substring(0, 1));
                            final Calendar calendarDayPosition = GregorianCalendar.getInstance();
                            calendarDayPosition.setTime(periodStartDate.getTime());
                            calendarDayPosition.add(Calendar.DAY_OF_WEEK, dayPosition);

                            final String dayStringDate = EXCHANGE_FORMAT.format(calendarDayPosition.getTimeInMillis());
                            final int lessonPosition = Integer.parseInt(positionData.substring(2, 3));
                            final String stringLessonPosition = String.valueOf(lessonPosition);
                            final Map<String, List<String>> lessonExchange = classExchange
                                    .getOrDefault(dayStringDate, Collections.emptyMap())
                                    .getOrDefault(String.valueOf(dayPosition), Collections.emptyMap());

                            // fill lesson data
                            final Lesson currentLesson = new Lesson();
                            currentLesson.setLessonPosition(lessonPosition);

                            lessonData.forEach((parameterName, parameterData) -> {
                                switch (parameterName) {
                                    case "s": // subjects
                                        if (lessonExchange != null) currentLesson.setSubjects(lessonExchange.get("s"));
                                        else currentLesson.setSubjects(scheduleInfo.getSubjectsIds()
                                                .entrySet().stream()
                                                .filter(e -> parameterData.contains(e.getKey()))
                                                .map(Map.Entry::getValue).toList());
                                    case "t": // teachers
                                        if (lessonExchange != null) currentLesson.setSubjects(lessonExchange.get("t"));
                                        else currentLesson.setTeachers(scheduleInfo.getTeachersIds()
                                                .entrySet().stream()
                                                .filter(e -> parameterData.contains(e.getKey()))
                                                .map(Map.Entry::getValue).toList());

                                    case "r": // rooms
                                        if (lessonExchange != null) currentLesson.setSubjects(lessonExchange.get("r"));
                                        else currentLesson.setRooms(scheduleInfo.getRoomsIds()
                                                .entrySet().stream()
                                                .filter(e -> parameterData.contains(e.getKey()))
                                                .map(Map.Entry::getValue).toList());
                                }
                            });


                            if (lessonTimes.containsKey(stringLessonPosition) && lessonTimes.get(stringLessonPosition).size() == 2) {
                                final List<Integer> startTime = Arrays.stream(lessonTimes.get(stringLessonPosition).get(0).split(":")).map(Integer::parseInt).toList(),
                                        endTime = Arrays.stream(lessonTimes.get(stringLessonPosition).get(1).split(":")).map(Integer::parseInt).toList();

                                if (startTime.size() == 2 && endTime.size() == 2) {
                                    Calendar transferredStartTime = (Calendar) calendarDayPosition.clone(), transferredEndTime = (Calendar) calendarDayPosition.clone();

                                    transferredStartTime.set(Calendar.HOUR, startTime.get(0));
                                    transferredStartTime.set(Calendar.MINUTE, startTime.get(1));

                                    transferredEndTime.set(Calendar.HOUR, endTime.get(0));
                                    transferredEndTime.set(Calendar.MINUTE, endTime.get(1));

                                    currentLesson.setLessonStartTime(transferredStartTime);
                                    currentLesson.setLessonEndTime(transferredEndTime);
                                }
                            }

                            // add to temp map
                            tempDays.put(calendarDayPosition.getTimeInMillis(), currentLesson);
                        });

                        scheduleForClasses.put(className, new ArrayList<>(tempDays.entries()).stream().map(entry -> new Day(entry.getKey(), (List<Lesson>) entry.getValue())).toList());
                        // ...
                    });

                    System.out.println("period: " + (System.currentTimeMillis() - periodCollectTime));
                } catch (ParseException e) {
                    log.warn(e.getMessage());
                }
            });

            System.out.println(System.currentTimeMillis() - start);

            return new Schedule(scheduleForClasses.entrySet().stream().map(raw->new Class(raw.getKey(), raw.getValue().stream().toList())).toList());
        }
    }

    @FieldDefaults(level = AccessLevel.PRIVATE)
    @Getter
    public static class ScheduleInfo {

        String HOMEPAGE_BTN;
        String HOMEPAGE_URL;

        String CLASSES_BTN;
        String TEACHERS_BTN;
        int WEEKDAYNUM;
        int LESSONSINDAY;
        int FIRSTLESSONNUM;

        boolean USEROOMS;
        boolean VERTICAL_CLASSES;
        boolean SHOW_TEACHERS;
        boolean SECOND_RELATIVE;
        boolean STRIKEOUT_FREE_LSN;
        boolean SHOW_EXCHANGES_TERM;
        boolean DISABLE_LINK_LOGO;

        String SCHOOL_NAME;
        String CITY_NAME;

        String EXPORT_DATE;
        String EXPORT_TIME;

        boolean SHOW_EXPORT_DT;
        List<String> DAY_NAMES;

        @SerializedName("DAY_NAMESH")
        List<String> DAY_NAMES_SHORT;

        List<String> MONTHS;
        List<String> MONTHS2;
        List<String> MONTHS3;

        String LESSON_NUM_STR;
        String LESSON_STR;
        String DAY_NUM_STR;
        String CLASS_STR;
        String FOR_CLASS_STR;
        String SCHEDULE_STR;
        String FOR_TEACHER_STR;
        String PERIOD_STR;
        String SECOND_SHIFT_STR;
        String TIME_GO;
        String TIME_START;
        String TIME_REMAIN;
        String NO_STR;
        String NO_LESSONS_STR;
        String LESSON_CANCELED_STR;
        String METHOD_STR;

        String MINUTES1;
        String MINUTES2;
        String MINUTES3;
        String YEAR_STR;
        String FROM_STR;
        String FOR_WEEK;
        String CHANGES_STR;
        String PREVIOUS_STR;
        String PREVIOUS2_STR;
        String NEXT_STR;
        String NEXT2_STR;
        String MONTH_STR;
        String WEEK_STR;
        String IN_STR;
        String SCHEDULE2_STR;
        String REFRESH_STR;
        String TODAY_STR;
        String YESTERDAY_STR;
        String ROOM_FREE_STR;
        String DAY_NO_LSN_STR;
        String DAY_NO_PERIOD_STR;

        @SerializedName("TEACHERS")
        Map<String, String> teachersIds;
        @SerializedName("SUBJECTS")
        Map<String, String> subjectsIds;
        @SerializedName("CLASSES")
        Map<String, String> classesIds;
        @SerializedName("CLASS_COURSES")
        Map<String, String> classCoursesIds;
        @SerializedName("ROOMS")
        Map<String, String> roomsIds;
        @SerializedName("CLASSGROUPS")
        Map<String, String> classGroupsIds;
        @SerializedName("PERIODS")
        Map<String, Map<String, String>> periods;
        @SerializedName("LESSON_TIMES")
        Map<String, List<String>> lessonTimes;
        @SerializedName("CLASS_SCHEDULE")
        Map<String, Map<String, Map<String, Map<String, List<String>>>>> classSchedule;
        @SerializedName("CLASS_EXCHANGE")
        Map<String, Map<String, Map<String, Map<String, List<String>>>>> classExchange;
        @SerializedName("TEACH_SCHEDULE")
        Object teachSchedule;// TODO: 06.02.2023
        @SerializedName("TEACH_EXCHANGE")
        Object teachExchange;// TODO: 06.02.2023
    }

    @FieldDefaults(level = AccessLevel.PRIVATE)
    @Getter
    public static class ClassExchangeData {
        @SerializedName("r")
        List<String> rooms;

        @SerializedName("t")
        List<String> teachers;

        @SerializedName("g")
        List<String> groups;

        @SerializedName("s")
        List<String> subjects;
    }
}
