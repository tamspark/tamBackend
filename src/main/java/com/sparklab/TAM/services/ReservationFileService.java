package com.sparklab.TAM.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.sparklab.TAM.dto.OpenAI.File.FileUploadResponse;
import com.sparklab.TAM.dto.calendar.ApartmentCalendarDTO;
import com.sparklab.TAM.dto.calendar.CalendarResponseDTO;
import lombok.AllArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@Service
@AllArgsConstructor
public class ReservationFileService {

    private final ApiCallService apiCallService;


    public String writeApartmentsToFile() {

        LocalDate dateToday = LocalDate.now();
        LocalDate januaryLastYear = dateToday.minusYears(1).withDayOfYear(1);
        LocalDate dateAfterSixMonths = dateToday.plusMonths(6);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // Format the dates into strings
        String formattedDateLastYear = januaryLastYear.format(formatter);

        String formattedDateAfterThreeMonths = dateAfterSixMonths.format(formatter);

        List<ApartmentCalendarDTO> apartments = getReservations(formattedDateLastYear, formattedDateAfterThreeMonths);

        String directoryPath = "dataFiles";
        String fileName = "hostDataFile.txt";
        File directory = new File(directoryPath);
        if (!directory.exists()) {
            directory.mkdirs(); // Creates directories including any necessary but nonexistent parent directories.
        }

        // Create the file
        File file = new File(directory, fileName);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            // Write the header
            writer.write("GuestName\t" +
                    "Contact\t" +
                    "Nationality\t" +
                    "NumberOfAdults\t" +
                    "NumberOfChildren\t" +
                    "NumberOfInfants\t" +
                    "ArrivalDate\t" +
                    "DepartureDate\t" +
                    "NumberOfNights\t" +
                    "BookedDate\t" +
                    "ListingName\t" +
                    "ChannelName\n");

            // Write apartment data
            for (ApartmentCalendarDTO apartment : apartments) {
                for (CalendarResponseDTO reservation : apartment.getReservations()) {
                    // Extract relevant reservation data
                    String guestName = reservation.getGuestName();
                    String contact = reservation.getEmail(); // Assuming email is used as contact
                    String natinality = checkNationByPhone(reservation.getPhone());
                    int adults = reservation.getAdults();
                    int children = reservation.getChildren();
                    int infants = 0; // Assuming infants are not provided in the data
                    String startDate = reservation.getArrival();
                    String endDate = reservation.getDeparture();
                    // Calculate nights (assuming dates are in ISO format)
                    long nights = java.time.temporal.ChronoUnit.DAYS.between(
                            java.time.LocalDate.parse(startDate),
                            java.time.LocalDate.parse(endDate)
                    );
                    String bookedDate = reservation.getCreatedAt();
                    String listing = apartment.getApartmentName();
                    String channelName = reservation.getChannel().getName();

                    // Write the data to the file
                    writer.write(String.format("%s\t%s\t%s\t%d\t%d\t%d\t%s\t%s\t%d\t%s\t%s\t%s\n",
                            guestName, contact, natinality, adults, children, infants, startDate, endDate, nights, bookedDate, listing, channelName));
                }
            }

            writer.close();
            System.out.println("File created successfully!");

        } catch (IOException e) {
            e.printStackTrace();
        }

        return uploadFile();
    }

    private String uploadFile() {
        //Refactored
        String apiUrl = "https://api.openai.com/v1/files";

        File fileToUpload = new File("dataFiles/hostDataFile.txt");


        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("file", new FileSystemResource(fileToUpload));
        builder.part("purpose", "assistants");


        FileUploadResponse response = apiCallService.postCallMultipartOpenAI(apiUrl, FileUploadResponse.class, builder);

        return response.getId();
    }


    private List<ApartmentCalendarDTO> getReservations(String fromDate, String toDate) {
//        return reservationService.getAllReservationsByDateCalendar("3", fromDate, toDate);

        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("fromDate", fromDate);
        builder.part("toDate", toDate);

        String apiURL = "http://localhost:8080/TAM/3/reservations/calendar?fromDate=" + fromDate + "&toDate=" + toDate;



        List<ApartmentCalendarDTO> response = apiCallService.getApartmentsCallParam(apiURL);


        return response;
    }

    private String checkNationByPhone(String phoneNumber) {
        try {
            PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
            Phonenumber.PhoneNumber parsedPhoneNumber = phoneNumberUtil.parse(phoneNumber, null);

            String regionCode = phoneNumberUtil.getRegionCodeForNumber(parsedPhoneNumber);

            if (regionCode != null) {
                Locale locale = new Locale("", regionCode);
                String countryName = locale.getDisplayCountry();
                return countryName;
            }
        } catch (NumberParseException e) {
            e.printStackTrace();
        }
        return "NoData";
    }

}
