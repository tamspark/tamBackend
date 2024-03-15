package com.sparklab.TAM.services;

import com.google.api.client.util.DateTime;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sparklab.TAM.dto.MeetingLinkDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;


@Service
@RequiredArgsConstructor
public class MeetingService {



//    public String generateMeetLink(MeetingLinkDTO meetingLinkDTO) throws Exception {
//        String apiKey = "apikey";
//
//        String meetApiUrl = "https://meet.googleapis.com/create";
//
//        String requestUrl = meetApiUrl + "?key=" + apiKey;
//
//        String requestBody = "conferenceSolutionKey.type=hangoutsMeet&requestId=" + URLEncoder.encode(String.valueOf(meetingLinkDTO.getReservationId()), "UTF-8");
//
//        URL url = new URL(requestUrl);
//        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//        connection.setRequestMethod("POST");
//        connection.setDoOutput(true);
//        connection.getOutputStream().write(requestBody.getBytes("UTF-8"));
//
//
//        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
//        StringBuilder response = new StringBuilder();
//        String line;
//        while ((line = reader.readLine()) != null) {
//            response.append(line);
//        }
//        reader.close();
//
//
//        String meetLink = extractMeetLinkFromResponse(response.toString(), String.valueOf(meetingLinkDTO.getReservationId()));
//
//        System.out.println(meetLink);
//        return meetLink;
//    }
//
//    private String extractMeetLinkFromResponse(String response, String reservationId) throws UnsupportedEncodingException {
//
//        JsonParser parser = new JsonParser();
//        JsonObject jsonResponse = parser.parse(response).getAsJsonObject();
//
//
//        if (jsonResponse.has("conferenceData") && jsonResponse.getAsJsonObject("conferenceData").has("entryPoints")) {
//
//            JsonArray entryPoints = jsonResponse.getAsJsonObject("conferenceData").getAsJsonArray("entryPoints");
//
//
//            for (JsonElement entryPoint : entryPoints) {
//                JsonObject entryPointObj = entryPoint.getAsJsonObject();
//                if (entryPointObj.has("entryPointType") && entryPointObj.get("entryPointType").getAsString().equals("video")) {
//
//                    String meetLink = entryPointObj.get("uri").getAsString();
//
//                    meetLink += "?reservationId=" + URLEncoder.encode(reservationId, "UTF-8");
//
//                    return meetLink;
//                }
//            }
//        }
//        return null;
//    }

    public String generateMeetLink(MeetingLinkDTO meetingLinkDTO) {
        return "https://meet.jit.si/" + meetingLinkDTO.getReservationId()
                + "guests=" + meetingLinkDTO.getGuestName();

    }


}
