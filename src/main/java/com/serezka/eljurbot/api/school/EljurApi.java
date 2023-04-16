package com.serezka.eljurbot.api.school;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.serezka.eljurbot.api.ApiUtils;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.PropertySource;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Log4j2
@PropertySource("classpath:eljur.properties")
public class EljurApi {
    // default eljur request date format
    SimpleDateFormat API_DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");

    @ToString.Exclude
    String eljurDevKey = "9235e26e80ac2c509c48fe62db23642c";

    String mainUrl;                     // eljur requests main url

    private static final Gson gson = new Gson();

    @NonFinal
    static EljurApi eljurApi = null;

    public static EljurApi getInstance() {
        if (eljurApi == null) eljurApi = new EljurApi();
        return eljurApi;
    }

    private EljurApi() {
        this.mainUrl = String.format("https://api.eljur.ru/api/{method}?devkey=%s", eljurDevKey);
    }

    private String generateLink(String methodName, Map<String, String> addons) {
        StringBuilder resultUrl = new StringBuilder(mainUrl.replace("{method}", methodName));
        for (Map.Entry<String, String> addon : addons.entrySet())
            resultUrl.append(String.format("&%s=%s", addon.getKey(), addon.getValue()));
        return resultUrl.toString();
    }

    // methods

    // get marks method

    /**
     * @param fromDate - | between witch \
     * @param toDate   -   |                \ dates return marks
     * @return success -> marks for selected dates in format Map<[student name], Map<[lesson]>, List<[marks]>>> | error -> empty map
     */
    public Map<String, List<Marks.Lesson>> requestMarks(Calendar fromDate, Calendar toDate, String eljurAuthorizationToken, String eljurVendor) {
        // generate link
        String requestMarksUrl = generateLink(
                "getmarks",
                Map.of("auth_token", eljurAuthorizationToken, "vendor", eljurVendor,
                        "days", API_DATE_FORMAT.format(fromDate.getTimeInMillis()) + "-" + API_DATE_FORMAT.format(toDate.getTimeInMillis())));

        // parse data
        URL apiRequest;
        String apiResponse;

        try {
            apiRequest = new URI(requestMarksUrl).toURL();
            apiResponse = ApiUtils.parseUrl(apiRequest);
        } catch (URISyntaxException | IOException e) {
            log.warn(e.getMessage());
            return null;
        }

        // get data from url and deserialize to object
        Marks.Response response = gson.fromJson(apiResponse, Marks.Root.class).response;
        if (response.error != null) {
            log.warn("response error: {}", response.error);
            return Collections.emptyMap();
        }

        // format
        Map<String, List<Marks.Lesson>> marks = new HashMap<>();
        response.result.students.forEach((s, student) -> marks.put(s, student.getLessons()));

        return marks;
    }

    // get homework request

    /**
     * @param schoolClass - selected school class name
     * @param fromDate    - | between witch \
     * @param toDate      - |                \ dates return marks
     * @return success -> homework for selected dates in format Map<[day], [day data]> | error -> empty map
     */
    public Map<String, Hometasks.Day> requestHomework(String schoolClass, Calendar fromDate, Calendar toDate, String eljurAuthorizationToken, String eljurVendor) {
        String requestHomeworkUrl = generateLink("gethomework", Map.of(
                "class", schoolClass, "auth_token", eljurAuthorizationToken, "vendor", eljurVendor,
                "days", API_DATE_FORMAT.format(fromDate.getTimeInMillis()) + "-" + API_DATE_FORMAT.format(toDate.getTimeInMillis())));

        // get data from url
        URL apiRequest;
        String apiResponse;

        try {
            apiRequest = new URI(requestHomeworkUrl).toURL();
            apiResponse = ApiUtils.parseUrl(apiRequest);
        } catch (URISyntaxException | IOException e) {
            log.warn(e.getMessage());
            return null;
        }


        // deserialize to object
        Hometasks.Response response = gson.fromJson(apiResponse, Hometasks.Root.class).response;
        if (response.error != null) {
            log.warn("response error: " + response.error);
            return Collections.emptyMap();
        }

        return response.result.days;
    }

    /**
     * @param eljurLogin    - eljur account username
     * @param eljurPassword - eljur account password
     * @return success -> authorization token data | error -> null
     */
    public Authorization.TokenInfo requestAuthorizationToken(String eljurLogin, String eljurPassword, String eljurVendor) {
        String requestAuthorizationTokenUrl = generateLink("auth", Map.of("login", eljurLogin, "password", eljurPassword, "vendor", eljurVendor));

        // get data from url
        URL apiRequest;
        String apiResponse;

        try {
            apiRequest = new URI(requestAuthorizationTokenUrl).toURL();
            apiResponse = ApiUtils.parseUrl(apiRequest);
        } catch (URISyntaxException | IOException e) {
            log.warn(e.getMessage());
            return null;
        }

        // deserialize to object
        Authorization.Root root = gson.fromJson(apiResponse, Authorization.Root.class);
        if (root == null) return null;

        if (root.response.error != null) {
            log.warn(root.response.error);
            return null;
        }

        return root.response.tokenInfo;
    }

    //
    public UserInfo.Result requestUserInfo(String eljurAuthToken, String eljurVendor) {
        String requestUserInfoLink = generateLink("getrules", Map.of("auth_token", eljurAuthToken, "vendor", eljurVendor));

        // get data from url
        URL apiRequest;
        String apiResponse;

        try {
            apiRequest = new URI(requestUserInfoLink).toURL();
            apiResponse = ApiUtils.parseUrl(apiRequest);
        } catch (URISyntaxException | IOException e) {
            log.warn(e.getMessage());
            return null;
        }

        // deserialize to object
        UserInfo.Response response = gson.fromJson(apiResponse, UserInfo.Root.class).getResponse();
        if (response.error != null) {
            log.warn(response.error);
            return null;
        }

        return response.getResult();
    }

    // TODO: 20.11.2022
    // можно добавить методы для каждого пункта из официально api: [https://eljur.ru/api/]
    // TODO: 20.11.2022

    // get user info
    public static class UserInfo {
        @FieldDefaults(level = AccessLevel.PRIVATE)
        @Getter
        public static class Root {
            Response response;
        }

        @FieldDefaults(level = AccessLevel.PRIVATE)
        @Getter
        public static class Response {
            int state;
            Object error;
            Result result;
        }

        @FieldDefaults(level = AccessLevel.PRIVATE)
        @Getter
        public static class Result {
            List<String> roles;
            Relations relations;
            List<String> allowedAds;
            List<String> allowedSections;
            String messageSignature;

            String name;

            @SerializedName("id")
            String eljurId;

            String vuid;
            String id_hash;
            String title;
            String vendor;
            String vendor_id;
            String lastname;
            String firstname;
            String middlename;
            String gender;
            String email;
            boolean email_confirmed;
            String region;
            String regionCode;
            String city;
            boolean rt_licey_school_end_date;
        }

        @FieldDefaults(level = AccessLevel.PRIVATE)
        @Getter
        public static class Relations {
            Map<String, Student> students;
            Map<String, Group> groups;
            List<School> schools;
        }

        @FieldDefaults(level = AccessLevel.PRIVATE)
        @Getter
        public static class Group {
            List<String> rules;

            String rel;
            String name;
            int parallel;
            String hometeacher_id;
            String hometeacher_name;
            String hometeacher_lastname;
            String hometeacher_firstname;
            String hometeacher_middlename;
        }

        @FieldDefaults(level = AccessLevel.PRIVATE)
        @Getter
        public static class Student {
            List<String> rules;

            @SerializedName("rel")
            String role;
            String name;

            String title;
            String lastname;
            String firstname;
            String gender;

            @SerializedName("class")
            String schoolClass;

            int parallel;
            String city;
        }

        @FieldDefaults(level = AccessLevel.PRIVATE)
        @Getter
        public static class School {
            String number;
            String title;
            @SerializedName("title_full")
            String fullTitle;
        }
    }

    // classes to parse json
    public static class Marks {
        @FieldDefaults(level = AccessLevel.PRIVATE)
        @Getter
        public static class Root {
            Response response;
        }

        @FieldDefaults(level = AccessLevel.PRIVATE)
        @Getter
        public static class Response {
            int state;
            Object error;
            Result result;
        }

        @FieldDefaults(level = AccessLevel.PRIVATE)
        @Getter
        public static class Result {
            Map<String, Student> students;
            String errorCode;
        }

        @FieldDefaults(level = AccessLevel.PRIVATE)
        @Getter
        public static class Student {
            String name;
            String title;
            List<Lesson> lessons;
        }

        @FieldDefaults(level = AccessLevel.PRIVATE)
        @Getter
        public static class Lesson {
            String average;
            int averageConvert;
            String name;

            public List<Mark> marks;
        }
        @FieldDefaults(level = AccessLevel.PRIVATE)
        @Getter
        public static class Mark {
            String value;

            String weight;
            float weight_float;
            String countas;
            boolean count;

            Object mtype;

            String comment;
            String lesson_comment;
            String date;
            int convert;
        }
    }

    public static class Hometasks {
        @FieldDefaults(level = AccessLevel.PRIVATE)
        @Getter
        public static class Root {
            Response response;
        }

        @FieldDefaults(level = AccessLevel.PRIVATE)
        @Getter
        public static class Response {
            int state;
            Object error;
            Result result;
        }

        @FieldDefaults(level = AccessLevel.PRIVATE)
        @Getter
        public static class Result {
            Map<String, Day> days;
        }

        @FieldDefaults(level = AccessLevel.PRIVATE)
        @Getter
        public static class Day {
            String name;
            String title;
            List<Item> items;
        }

        @FieldDefaults(level = AccessLevel.PRIVATE)
        @Getter
        public static class Item {
            String name;
            boolean individual_exists;
            String grp;

            Map<Integer, Homework> homework;
            Files files;
        }

        @FieldDefaults(level = AccessLevel.PRIVATE)
        @Getter
        public static class Homework {
            int id;
            String value;
        }

        @FieldDefaults(level = AccessLevel.PRIVATE)
        @Getter
        public static class Files {
            public List<File> file;
        }

        @FieldDefaults(level = AccessLevel.PRIVATE)
        @Getter
        public static class File {
            public int toid;
            public String filename;
            public String link;
        }
    }

    public static class Authorization {
        @FieldDefaults(level = AccessLevel.PRIVATE)
        @Getter
        public static class TokenInfo {
             String token;
             String expires;

             String errorCode;
             String errorText;
        }

        @FieldDefaults(level = AccessLevel.PRIVATE)
        @Getter
        public static class Response {
            int state;
            Object error;
            @SerializedName("result")
             TokenInfo tokenInfo;
        }

        @FieldDefaults(level = AccessLevel.PRIVATE)
        @Getter
        public static class Root {
             Response response;
        }
    }
}
