package com.serezka.eljurbot.api.school;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.serezka.eljurbot.api.ApiUtils;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Log4j2
public class SibsauApi {
    static String BASE_URL = "https://timetable.pallada.sibsau.ru/timetable/group/%d";

    static Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .serializeNulls()
            .create();

    private static String generateUrl(long groupId) {
        return String.format(BASE_URL, groupId);
    }

    public static String getSchedule(long groupId) {
        String requestScheduleUrl = generateUrl(groupId);

        // parse data
        URL apiRequest;
        String apiResponse;

        try {
            apiRequest = new URI(requestScheduleUrl).toURL();
            apiResponse = ApiUtils.parseUrl(apiRequest);
        } catch (URISyntaxException | IOException e) {
            log.warn(e.getMessage());
            return null;
        }

        System.out.println(apiResponse);

        return "";
    }
}
