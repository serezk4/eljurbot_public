package com.serezka.eljurbot.api;

import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

@Log4j2
public class ApiUtils {
    public static String parseUrl(URL url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("User-Agent", "Mozilla/5.0");

        int responseCode = connection.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK) {
            log.warn("response code: {}", responseCode);
            return null;
        }

        StringBuilder responseText = new StringBuilder();
        InputStream inputStream = connection.getInputStream();

        byte[] buffer = new byte[1024];
        for (int temp; (temp = inputStream.read(buffer)) != -1; )
            responseText.append(new String(buffer, 0, temp));

        return responseText.toString();
    }
}