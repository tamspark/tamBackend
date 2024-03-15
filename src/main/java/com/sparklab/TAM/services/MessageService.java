package com.sparklab.TAM.services;

import com.sparklab.TAM.configuration.SmoobuConfiguration;
import com.sparklab.TAM.dto.MeetingLinkDTO;
import com.sparklab.TAM.dto.message.MessageResponseDTO;
import com.sparklab.TAM.dto.message.MessagesResponseDTO;
import com.sparklab.TAM.dto.message.SmoobuMessageRequest;
import com.sparklab.TAM.dto.reservation.ReservationDTO;
import com.sparklab.TAM.exceptions.ApiCallError;
import com.sun.jdi.InternalException;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class MessageService {

    private final ApiCallService apiCallService;
    private final ConversationService conversationService;
    private final SmoobuConfiguration smoobuConfiguration;
    private final EmailService emailService;
    private final MeetingService meetingService;
    private static final Logger logger = LogManager.getLogger(ChannelService.class);

    public MessageResponseDTO sendMessage(String userId, int reservationId, SmoobuMessageRequest messageRequest) {
        Long parseUserId = Long.parseLong(userId);
        String apiUrl = smoobuConfiguration.getApiURI() + "reservations/" + reservationId + "/messages/send-message-to-guest";
        try {
            return apiCallService.postMethod(apiUrl, MessageResponseDTO.class, parseUserId, messageRequest);
        } catch (ApiCallError e) {
            if (e.getErrorCode() == HttpStatusCode.valueOf(422)) {
                logger.error(e.getMessage(), e);
                throw new ApiCallError(e.getErrorCode(), "Response cannot be parsed as Message object");
            }
            logger.error(e.getMessage(), e);
            throw new InternalException();
        }
    }


    public MessagesResponseDTO getMessageByReservationId(String userId, int reservationId) {
        Long parseUserId = Long.parseLong(userId);
        String apiUrl = smoobuConfiguration.getApiURI() + "reservations/" + reservationId;

        try {
            ReservationDTO reservation = apiCallService.getMethod(apiUrl, ReservationDTO.class, parseUserId);
            conversationService.saveOrUpdate(userId, reservation);

            return apiCallService.getMethod(apiUrl + "/messages", MessagesResponseDTO.class, parseUserId);

        } catch (ApiCallError e) {

            if (e.getErrorCode() == HttpStatusCode.valueOf(422)) {
                logger.error(e.getMessage(), e);
                throw new ApiCallError(e.getErrorCode(), "Response cannot be parsed as Message object");
            }
            logger.error(e.getMessage(), e);
            throw new InternalException();
        }

    }

    public void NewReservationWelcomeMessage(List<ReservationDTO> reservations, Long userId) {
        for (ReservationDTO reservation : reservations) {
            String parsedUserId = String.valueOf(userId);

            conversationService.saveOrUpdate(parsedUserId, reservation);
            SmoobuMessageRequest welcomeMessage = buildReservationWelcomeMessage(reservation);
            String stringUserId = userId.toString();
            try {
                sendMessage(stringUserId, reservation.getId(), welcomeMessage);
            } catch (Exception e) {
                //TODO Suggestion: Send an email to the Admin when this fails
                logger.error(e.getMessage(), e);
            }
        }
    }


    private SmoobuMessageRequest buildReservationWelcomeMessage(ReservationDTO reservation) {
        SmoobuMessageRequest messageRequest = new SmoobuMessageRequest();

        messageRequest.setSubject("Thank you for choosing TAM");
        messageRequest.setMessageBody(
                "<!DOCTYPE html>\n" +
                        "<html lang=\"en\">\n" +
                        "<head>\n" +
                        "    <meta charset=\"UTF-8\">\n" +
                        "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                        "    <title>Reservation Confirmation</title>\n" +
                        "    <style>\n" +
                        "        body {\n" +
                        "            font-family: Arial, sans-serif;\n" +
                        "            background-color: #f4f4f4;\n" +
                        "            margin: 0;\n" +
                        "            padding: 0;\n" +
                        "        }\n" +
                        "        .container {\n" +
                        "            max-width: 600px;\n" +
                        "            margin: 20px auto;\n" +
                        "            background-color: #ffffff;\n" +
                        "            padding: 20px;\n" +
                        "            border-radius: 8px;\n" +
                        "            box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);\n" +
                        "            text-align: center;\n" +
                        "        }\n" +
                        "        .header {\n" +
                        "            text-align: center;\n" +
                        "            margin-bottom: 20px;\n" +
                        "        }\n" +
                        "        .btn-container {\n" +
                        "            display: flex;\n" +
                        "            justify-content: center;\n" +
                        "            margin-top: 20px;\n" +
                        "        }\n" +
                        "        .btn {\n" +
                        "            display: inline-block;\n" +
                        "            padding: 10px 20px;\n" +
                        "            margin: 0 10px;\n" +
                        "            border-radius: 5px;\n" +
                        "            text-decoration: none;\n" +
                        "            font-weight: bold;\n" +
                        "        }\n" +
                        "        .green {\n" +
                        "            background-color: #4CAF50;\n" +
                        "            color: white;\n" +
                        "        }\n" +
                        "        .blue {\n" +
                        "            background-color: #007bff;\n" +
                        "            color: white;\n" +
                        "        }\n" +
                        "    </style>\n" +
                        "</head>\n" +
                        "<body>\n" +
                        "    <div class=\"container\">\n" +
                        "        <div class=\"header\">\n" +
                        "            <h1>Reservation Confirmation</h1>\n" +
                        "        </div>\n" +
                        "        <p>Hello " + reservation.getGuestName() + ",</p>\n" +
                        "        <p>Thank you for choosing " + reservation.getApartment().getName() + " for your stay.</p>\n" +
                        "        <p>Your reservation is scheduled from " + reservation.getArrival() + " to " + reservation.getDeparture() + ".</p>\n" +
                        "        <p>Please click the buttons below for assistance:</p>\n" +
                        "        <div class=\"btn-container\">\n" +
                        "            <a class=\"btn green\" href=\"http://192.168.10.168:3000/kyc/" + reservation.getApartment().getId() + "/" + reservation.getId() + "/" + reservation.getGuestName() + "\">Need assistance</a>\n" +
                        "            <a class=\"btn blue\" href=\"" + meetingService.generateMeetLink(new MeetingLinkDTO((long) reservation.getId(),reservation.getGuestName())) + "\">Check in</a>\n" +
                        "        </div>\n" +
                        "    </div>\n" +
                        "</body>\n" +
                        "</html>"
        );

        return messageRequest;
    }


}
