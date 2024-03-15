package com.sparklab.TAM.services;

import com.sparklab.TAM.configuration.SmoobuConfiguration;
import com.sparklab.TAM.dto.GetRatesDTO;
import com.sparklab.TAM.dto.rate.RatesDTO;
import com.sparklab.TAM.dto.rate.RatesResponseDTO;
import com.sparklab.TAM.dto.report.Date;
import com.sparklab.TAM.dto.reservation.Operation;
import com.sparklab.TAM.exceptions.ApiCallError;
import com.sun.jdi.InternalException;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ApartmentAvailabilityService {

    private static final Logger logger = LogManager.getLogger(ApartmentAvailabilityService.class);
    private final SmoobuConfiguration smoobuConfiguration;
    private final ApiCallService apiCallService;

    public String saveApartmentAvailability(RatesDTO ratesDTO, String userId) {

        try {
            long parseUserId = Long.parseLong(userId);
            String apiUrl = smoobuConfiguration.getApiURI() + "rates";
            if (apiCallService.postMethod(apiUrl, RatesResponseDTO.class, parseUserId, ratesDTO).isSuccess())
                return "The apartment's rate price for the specified dates is saved successfully";
            throw new InternalException("An error occurred while saving apartment availability.");
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new InternalException("An error occurred while saving apartment availability.");
        }

    }


    public GetRatesDTO getAllRates(String userId, LocalDate start_date, LocalDate end_date, List<Integer> apartments) throws ApiCallError, InternalException {

        try {
            Long parseUserId = Long.parseLong(userId);

            String apartmentIds = "apartments[]=" + apartments.stream()
                    .map(Object::toString)
                    .collect(Collectors.joining("&apartments[]="));
            String apiUrl = smoobuConfiguration.getApiURI() + "rates?" + apartmentIds
                    + "&start_date=" + start_date + "&end_date="
                    + end_date;
            GetRatesDTO originalRatesDTO = apiCallService.getMethod(apiUrl, GetRatesDTO.class, parseUserId);

            originalRatesDTO.filterDateByPrice(originalRatesDTO.getData());

            //Set the minimum stay suggestion
            setSuggestedMinimumStay(originalRatesDTO);

            //Set the suggested price for each day
            setSuggestedPrice(originalRatesDTO);

            return originalRatesDTO;
        } catch (ApiCallError e) {
            if (e.getErrorCode() == HttpStatusCode.valueOf(422)) {
                logger.error(e.getMessage(), e);
                throw new ApiCallError(e.getErrorCode(), "Response cannot be parsed as Rates object");
            }
            logger.error(e.getMessage(), e);
            throw new InternalException();
        }
    }

    private void setSuggestedMinimumStay(GetRatesDTO originalRatesDTO) {
        Map.Entry<String, Map<String, Date>> firstEntry = originalRatesDTO.getData().entrySet().iterator().next();
        Map<String, Date> dates = new TreeMap<>(firstEntry.getValue());

        List<List<Map<String, Date>>> orphanGaps = findOrphanGaps(dates);

        for (List<Map<String, Date>> period : orphanGaps) {
            for (Map<String, Date> date : period) {
                int index = 1;

                for (Map.Entry<String, Date> entry : date.entrySet()) {
                    String key = entry.getKey();
                    Date value = entry.getValue();

                    if (date.size() == 4) {

                        if (index == 1) {
                            dates.get(key).setSuggestedMinimumStay(1);
                        }

                        if (index == 2) {
                            dates.get(key).setSuggestedMinimumStay(2);
                        }

                        if (index == 3) {
                            dates.get(key).setSuggestedMinimumStay(2);
                        }

                        if (index == 4) {
                            dates.get(key).setSuggestedMinimumStay(1);
                        }

                    }


                    if (date.size() == 3) {

                        if (index == 1) {
                            dates.get(key).setSuggestedMinimumStay(1);
                        }

                        if (index == 2) {
                            dates.get(key).setSuggestedMinimumStay(1);
                        }

                        if (index == 3) {
                            dates.get(key).setSuggestedMinimumStay(1);
                        }

                    }


                    if (date.size() == 2) {
                        if (index == 1) {
                            dates.get(key).setSuggestedMinimumStay(1);
                        }

                        if (index == 2) {
                            dates.get(key).setSuggestedMinimumStay(1);
                        }

                    }

                    if (date.size() == 1) {
                        if (index == 1) {
                            dates.get(key).setSuggestedMinimumStay(1);
                        }
                    }

                    index++;
                }
            }
        }

    }


    private List<List<Map<String, Date>>> findOrphanGaps(Map<String, Date> dates) {
        List<List<Map<String, Date>>> periods = new ArrayList<>();
        Map<String, Date> currentPeriod = new TreeMap<>();

        boolean isValidPeriod = false;

        for (Map.Entry<String, Date> entry : dates.entrySet()) {
            Date value = entry.getValue();

            value.setSuggestedMinimumStay(value.getMin_length_of_stay());

            String date = entry.getKey();
            int available = value.getAvailable();

            if (available == 1) {
                // If previous period was invalid and current date is available, start a new period
                if (!isValidPeriod) {
                    currentPeriod.clear();
                    currentPeriod.put(date, value);
                    isValidPeriod = true;
                } else {
                    // If previous period was valid, continue current period
                    currentPeriod.put(date, value);
                }
            } else {
                // If current date is not available
                if (isValidPeriod) {
                    // If previous period was valid, end current period
                    if (currentPeriod.size() >= 1 && currentPeriod.size() <= 4) {

                        List<Map<String, Date>> periodToAdd = new ArrayList<>();
                        Map<String, Date> dateToAdd = new TreeMap<>(currentPeriod);

                        periodToAdd.add(dateToAdd);

                        periods.add(periodToAdd);
                    }
                    currentPeriod.clear();
                    isValidPeriod = false;
                }
            }
        }

        // Check if the last period is valid
        if (isValidPeriod && currentPeriod.size() >= 1 && currentPeriod.size() <= 4) {
            List<Map<String, Date>> periodToAdd = new ArrayList<>();
            Map<String, Date> dateToAdd = new TreeMap<>(currentPeriod);

            periodToAdd.add(dateToAdd);

            periods.add(periodToAdd);
        }

        for (List<Map<String, Date>> period : periods) {
            System.out.println(period);
        }

        return periods;
    }


    private void setSuggestedPrice(GetRatesDTO originalRatesDTO) {
        Map.Entry<String, Map<String, Date>> firstEntry = originalRatesDTO.getData().entrySet().iterator().next();

        Map<String, Date> dates = firstEntry.getValue();

        Map<String, Date> filteredDates = new TreeMap<>();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        //Get today Date of year
        int todayDayOfTheYear = LocalDate.now().getDayOfYear();


        for (Map.Entry<String, Date> entry : dates.entrySet()) {
            //Skip if it is not available
            if (entry.getValue().getAvailable() == 0) {
                continue;
            }

            //Get the date of the rate object
            String entryDateString = entry.getKey();
            int entryDateDayOfTheYear = LocalDate.parse(entryDateString, formatter).getDayOfYear();

            //Skip if it is past date
            if (entryDateDayOfTheYear < todayDayOfTheYear) {
                continue;
            }

            Date value = entry.getValue();
            int minimumStay = value.getMin_length_of_stay();

            double suggestedPrice = getSuggestedPrice(minimumStay, entryDateDayOfTheYear, todayDayOfTheYear);

            value.setSuggestedPrice(suggestedPrice);

            filteredDates.put(entry.getKey(), value);
        }
        firstEntry.setValue(filteredDates);
    }

    private double getSuggestedPrice(int minimumStay, int entryDateDayOfTheYear, int todayDayOfTheYear) {

//        String apiURL = "";
//        Long userId = Long.valueOf("3");
//
//        SuggestedPriceRequestDTO suggestedPriceRequestDTO = new SuggestedPriceRequestDTO();
//        suggestedPriceRequestDTO.setPricePerNight(0);
//        suggestedPriceRequestDTO.setRoomTypeCategoryPrivateRoom(0);
//
//        suggestedPriceRequestDTO.setRequestDateZero(todayDayOfTheYear);
//        suggestedPriceRequestDTO.setCheckinDateZero(entryDateDayOfTheYear);
//        suggestedPriceRequestDTO.setDaysUntilCheckin(entryDateDayOfTheYear - todayDayOfTheYear);
//
//        suggestedPriceRequestDTO.setStayDuration(minimumStay);
//        suggestedPriceRequestDTO.setCheckoutDateZero(entryDateDayOfTheYear + minimumStay);
//
//        String suggestedPriceString = apiCallService.getMethod(apiURL,String.class,userId);
//
//        double suggestedPrice = Double.parseDouble(suggestedPriceString);

        return 44.33;
    }

    public RatesDTO datesToRatesDTO(List<String> dates, Integer apartmentId) {

        RatesDTO ratesToDelete = new RatesDTO();
        List<Integer> apartments = new ArrayList<>();
        apartments.add(apartmentId);
        ratesToDelete.setApartments(apartments);
        List<Operation> operationList = new ArrayList<>();
        Operation operation = new Operation();
        operation.setDates(dates);
        operation.setDaily_price(0);

        operationList.add(operation);

        ratesToDelete.setOperations(operationList);

        return ratesToDelete;
    }
}
