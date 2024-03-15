package com.sparklab.TAM.services;

import com.sparklab.TAM.configuration.SmoobuConfiguration;
import com.sparklab.TAM.converters.ReservationDTOToCalendarResponseDTO;
import com.sparklab.TAM.converters.ReservationDTOToProcessedReservation;
import com.sparklab.TAM.dto.OpenAI.Message.MessageRequest;
import com.sparklab.TAM.dto.OpenAI.Thread.ThreadDtoResponse;
import com.sparklab.TAM.dto.calendar.ApartmentCalendarDTO;
import com.sparklab.TAM.dto.calendar.CalendarResponseDTO;
import com.sparklab.TAM.dto.message.MessageDTO;
import com.sparklab.TAM.dto.message.MessagesResponseDTO;
import com.sparklab.TAM.dto.message.SmoobuMessageRequest;
import com.sparklab.TAM.dto.minStay.MinStayDTO;
import com.sparklab.TAM.dto.rate.RatesDTO;
import com.sparklab.TAM.dto.report.FilterDTO;
import com.sparklab.TAM.dto.report.ReportDTO;
import com.sparklab.TAM.dto.reservation.Operation;
import com.sparklab.TAM.dto.reservation.ReservationDTO;
import com.sparklab.TAM.dto.smoobu.SmoobuAllReservationsResponseDTO;
import com.sparklab.TAM.dto.smoobu.SmoobuShortApartmentDTO;
import com.sparklab.TAM.exceptions.ApiCallError;
import com.sparklab.TAM.model.MinStayRule;
import com.sparklab.TAM.model.ProcessedReservations;
import com.sparklab.TAM.model.User;
import com.sparklab.TAM.repositories.MinStayRepository;
import com.sparklab.TAM.repositories.ProcessedReservationsRepository;
import com.sparklab.TAM.repositories.SmoobuAccountRepository;
import com.sparklab.TAM.repositories.UserRepository;
import com.sun.jdi.InternalException;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;
import org.springframework.http.HttpStatusCode;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@EnableScheduling
public class ReservationService {

    private final ApiCallService apiCallService;
    private final MinStayRepository minStayRepository;
    private final ReservationDTOToCalendarResponseDTO reservationDTOToCalendarResponseDTO;
    private final ProcessedReservationsRepository processedReservationsRepository;
    private final SmoobuAccountRepository smoobuAccountRepository;
    private final ReservationDTOToProcessedReservation toProcessedReservation;
    private static final Logger logger = LogManager.getLogger(ChannelService.class);
    private final SmoobuConfiguration smoobuConfiguration;
    private final ApartmentAvailabilityService apartmentAvailabilityService;
    private final UserRepository userRepository;

    private final FilterService filterService;
    private final MessageService messageService;
    private ReportService reportService;
    private final OpenAIService openAIService;
    private final ApartmentService apartmentService;;

    public List<ApartmentCalendarDTO> getAllReservationsByDateCalendar(String userId, String fromDate, String toDate) {

        try {
            Long parseUserId = Long.parseLong(userId);
            String apiUrl = smoobuConfiguration.getApiURI() + "reservations?from=" + fromDate + "&to=" + toDate;
            List<CalendarResponseDTO> calendar = apiCallService.getMethod(apiUrl, SmoobuAllReservationsResponseDTO.class, parseUserId).getBookings().stream().map(reservationDTOToCalendarResponseDTO::convert).collect(Collectors.toList());
            return getApartmentReservationCalendar(calendar);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new InternalException("Response cannot be parsed as Reservation object");
        }
    }

    public ApartmentCalendarDTO getAllReservationByApartmentId(String userId, int apartmentId, String fromDate, String toDate) {

        List<ApartmentCalendarDTO> apartmentCalendarDTOList = getAllReservationsByDateCalendar(userId, fromDate, toDate);

        Optional<ApartmentCalendarDTO> apartmentCalendarDTO = apartmentCalendarDTOList.stream().filter(a -> a.getApartmentId() == apartmentId).findFirst();

        return apartmentCalendarDTO.orElse(null);
    }

    private List<ApartmentCalendarDTO> getApartmentReservationCalendar(List<CalendarResponseDTO> calendar) {
        Map<Integer, List<CalendarResponseDTO>> apartmentReservations = new HashMap<>();

        for (CalendarResponseDTO reservation : calendar) {
            int apartmentId = reservation.getApartment().getId();
            apartmentReservations.computeIfAbsent(apartmentId, k -> new ArrayList<>()).add(reservation);
            List<String> includedDates = calculateIncludedDates(reservation.getArrival(), reservation.getDeparture());
            reservation.setAllBookedDates(includedDates);
        }

        List<ApartmentCalendarDTO> result = new ArrayList<>();
        for (Map.Entry<Integer, List<CalendarResponseDTO>> entry : apartmentReservations.entrySet()) {
            int apartmentId = entry.getKey();
            List<CalendarResponseDTO> reservations = entry.getValue();
            String apartmentName = reservations.get(0).getApartment().getName();
            result.add(new ApartmentCalendarDTO(apartmentId, apartmentName, reservations));
        }

        return result;

    }

    private List<String> calculateIncludedDates(String arrival, String departure) {
        List<String> includedDates = new ArrayList<>();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-d");
        LocalDate startDate = LocalDate.parse(arrival, dateFormatter);
        LocalDate endDate = LocalDate.parse(departure, dateFormatter);
        while (!startDate.isAfter(endDate)) {
//            includedDates.add(String.valueOf(startDate.getDayOfMonth()));
            includedDates.add(startDate.format(dateFormatter));
            startDate = startDate.plusDays(1);
        }
        return includedDates;
    }


    public ReservationDTO getReservationById(String userId, String id) {
        try {
            Long parseUserId = Long.parseLong(userId);
            int parsedId = Integer.parseInt(id);
            String apiUrl = smoobuConfiguration.getApiURI() + "reservations/" + parsedId;
            // reservationDTOToCalendarResponseDTO.convert(apiCallService.getMethod(apiUrl, ReservationDTO.class));
            return apiCallService.getMethod(apiUrl, ReservationDTO.class, parseUserId);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new InternalException("Response cannot be parsed as Reservation object");
        }
    }

    public List<ReservationDTO> getReservationsFiltered(String userId, FilterDTO filterDTO) {
        try {
            Long parseUserId = Long.parseLong(userId);
            String apiUrl = smoobuConfiguration.getApiURI() + "reservations?from=" + filterDTO.getFromDate() + "&to=" + filterDTO.getToDate();
            return filterService.getFilteredReservations(apiCallService.getMethod(apiUrl, SmoobuAllReservationsResponseDTO.class, parseUserId).getBookings(), filterDTO);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new InternalException("The reservations could not be filtered based on the specifications provided");
        }
    }

    public ReportDTO getReports(String userId, FilterDTO filterDTO) {
        try {
            Long parseUserId = Long.parseLong(userId);
            String apiUrl = smoobuConfiguration.getApiURI() + "reservations?from=" + filterDTO.getFromDate() + "&to=" + filterDTO.getToDate();
            List<ReservationDTO> reservations = filterService.getFilteredReservations(apiCallService.getMethod(apiUrl, SmoobuAllReservationsResponseDTO.class, parseUserId).getBookings(), filterDTO);
            return reportService.generateReport(reservations, filterDTO);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new InternalException("The report could not be generated based on the specifications provided");
        }
    }

    @Scheduled(fixedRate = 1800000)  //1800000 = 30 min
    public void newReservationsLoop() {
//        USE THIS METHOD TO CALL OTHER METHODS EVERY TIME NEW RESERVATIONS ARE MADE

        List<Long> smoobuUserIds = smoobuAccountRepository.findAll()
                .stream()
                .map(smoobuAccount -> smoobuAccount.getUser().getId())
                .toList();

        for (Long userId : smoobuUserIds) {
            List<ReservationDTO> newReservations = getNewReservations(userId);

            if (!newReservations.isEmpty()) {
                processNewReservations(newReservations, userId);
            }
        }
    }

    @Scheduled(fixedRate = 300000) // 300000 = 5 minutes
    private void getAllReservations() {
        Long userId = smoobuAccountRepository.findAll()
                .stream()
                .map(smoobuAccount -> smoobuAccount.getUser().getId())
                .findFirst().get();

        String apiUrl = smoobuConfiguration.getApiURI() + "reservations?pageSize=100";
        List<ReservationDTO> allReservations = new ArrayList<>();
        int currentPage = 1;

        do {
            String pageUrl = apiUrl + "&page=" + currentPage;
            SmoobuAllReservationsResponseDTO responseDTO = apiCallService.getMethod(pageUrl, SmoobuAllReservationsResponseDTO.class, userId);
            List<ReservationDTO> reservationsFromGetCall = responseDTO.getBookings();

            if (!reservationsFromGetCall.isEmpty()) {
                allReservations.addAll(reservationsFromGetCall);
                currentPage++; // Move to the next page
            } else {
                break; // No more pages to fetch
            }
        } while (true);


        List<Integer> reservationIds = new ArrayList<>();
        for (ReservationDTO reservationDTO : allReservations) {
            reservationIds.add(reservationDTO.getId());
        }

        System.out.println("All Reservations Scheduled every 10 minutes: \n" + reservationIds);

        runAssistant(reservationIds);

    }

    private void runAssistant(List<Integer> reservationIds) {
        //TODO
        for (Integer reservationId : reservationIds) {

            MessagesResponseDTO messageResponse = messageService.getMessageByReservationId("3", reservationId);
            MessageDTO lastMessage = messageResponse.getMessages().get(messageResponse.getMessages().size() - 1);

            if (lastMessage.getType() != 1) {
                continue;
            }
            System.out.println("Reservation: " + reservationId + "has unread message");

            String incomingMessage = lastMessage.getMessage();

            ThreadDtoResponse threadResponse = openAIService.createThread("Hello, I am communication with you via email", "client", null);

            MessageRequest messageRequest = new MessageRequest();
            messageRequest.setContent(incomingMessage);
            messageRequest.setRole("user");

            MessageRequest response = openAIService.chat(threadResponse.getId(), messageRequest, "client");

            SmoobuMessageRequest smoobuMessageRequest = new SmoobuMessageRequest();
            smoobuMessageRequest.setSubject("Automatic Reply");
            smoobuMessageRequest.setMessageBody(response.getContent());

            messageService.sendMessage("3", reservationId, smoobuMessageRequest);

        }


    }

    private List<ReservationDTO> getNewReservations(Long userId) {
        String apiUrl = smoobuConfiguration.getApiURI() + "reservations?pageSize=100";


        List<ReservationDTO> newReservations;
        List<ReservationDTO> reservationsFromGetCall;

        List<Integer> oldReservationIds;

        try {
            oldReservationIds = processedReservationsRepository.findAll().stream().map(ProcessedReservations::getSmoobuId).toList();

            reservationsFromGetCall = apiCallService.getMethod(apiUrl, SmoobuAllReservationsResponseDTO.class, userId).getBookings();

            newReservations = !oldReservationIds.isEmpty() ? removeOldReservations(reservationsFromGetCall, oldReservationIds) : reservationsFromGetCall;

            return newReservations;
        } catch (ApiCallError e) {

            if (e.getErrorCode() == HttpStatusCode.valueOf(422)) {
                logger.error(e.getMessage(), e);
                throw new ApiCallError(e.getErrorCode(), "Response cannot be parsed as Apartment object");
            }
            logger.error(e.getMessage(), e);
            throw new InternalException();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw e;
        }


    }

    private List<ReservationDTO> removeOldReservations(List<ReservationDTO> reservations, List<Integer> oldIds) {
        List<ReservationDTO> newReservations = new ArrayList<>();

        for (ReservationDTO reservation : reservations) {
            if (!oldIds.contains(reservation.getId())) {
                newReservations.add(reservation);
            }
        }
        return newReservations;
    }


    private void processNewReservations(List<ReservationDTO> reservations, Long userId) {
        List<ProcessedReservations> processedReservations = reservations.stream()
                .map(toProcessedReservation::convert)
                .collect(Collectors.toList());

        messageService.NewReservationWelcomeMessage(reservations, userId);
        processedReservationsRepository.saveAll(processedReservations);
    }


    //The automatically way
    @Scheduled(fixedRate = 600000)
    public void updateMinStayForReservationsAllUsers(){
       List<User> users= userRepository.findAll();
       for(User user:users){
           updateMinStayForReservations(String.valueOf(user.getId()));
       }

    }




    public String updateMinStayForReservations(String userId) {
        List<SmoobuShortApartmentDTO> allApartmentsByUser = apartmentService.getAllApartments(userId).getApartments();
        List<MinStayRule> rules = minStayRepository.findAll();
        for (SmoobuShortApartmentDTO smoobuShortApartmentDTO : allApartmentsByUser) {
            List<MinStayDTO> minStayDTOPerApartment = findMinStayForReservations(rules, String.valueOf(smoobuShortApartmentDTO.getId()));
            if (minStayDTOPerApartment != null) {
                RatesDTO ratesDTOPerApartment = updateApartmentAvailability(minStayDTOPerApartment);
                System.out.println(ratesDTOPerApartment);
                apartmentAvailabilityService.saveApartmentAvailability(ratesDTOPerApartment, String.valueOf(userId));
            }
        }
        return "Updated successfully";
    }

    public List<MinStayDTO> findMinStayForReservations(List<MinStayRule> minStayRules,String apartmentId) {

        try {
//            String apartmentId = "1874674";
            String beginingDate = "2024-01-01";
            String endDate = "2025-12-31";
            Long userId = minStayRules.get(0).getUserId();
            String apiUrl = smoobuConfiguration.getApiURI() + "reservations?from=" + beginingDate + "&to=" + endDate + "&apartmentId=" + apartmentId;
            List<ReservationDTO> allReservations = apiCallService.getMethod(apiUrl, SmoobuAllReservationsResponseDTO.class, userId).getBookings();
            if(allReservations.size()==0){
                return null;
            }
            System.out.println(allReservations.size());
            Collections.sort(allReservations, new Comparator<ReservationDTO>() {
                @Override
                public int compare(ReservationDTO o1, ReservationDTO o2) {
                    LocalDate date1 = LocalDate.parse(o1.getArrival());
                    LocalDate date2 = LocalDate.parse(o2.getArrival());
                    return date1.compareTo(date2);
                }
            });

            List<MinStayDTO> theListOfAllDaysWithMinStayBasedOnRules = new ArrayList<>();

            int days10minStay=minStayRepository.findByDay(10).getMinStay();
            System.out.println(allReservations.size());
            for (int i = 0; i <= allReservations.size() - 2; i++) {
                int minStayUpdated = 0;
                LocalDate actualDate = LocalDate.parse(allReservations.get(i).getDeparture());
                LocalDate laterDate = LocalDate.parse(allReservations.get(i + 1).getArrival());
                long dayDifferenceBetweenTwoReservations = ChronoUnit.DAYS.between( actualDate,laterDate);
                for (MinStayRule minStayRule : minStayRules) {

                    if (minStayRule.getDay() == dayDifferenceBetweenTwoReservations)
                        minStayUpdated = minStayRule.getMinStay();
//                    else if(dayDifferenceBetweenTwoReservations>10){
//                        //TODO to be checked for more than  10 days
//                        minStayUpdated=days10minStay;
//                    }
                }
                MinStayDTO newMinStayDTO = new MinStayDTO();
                newMinStayDTO.setMinStay(minStayUpdated);
                newMinStayDTO.setApartmentId(Integer.parseInt(apartmentId));
                List<String> allDatesBetweendifferenceDaysReservartion = new ArrayList<>();
                LocalDate currentDate = actualDate;
                while (!currentDate.isAfter(laterDate)) {
                    allDatesBetweendifferenceDaysReservartion.add(String.valueOf(currentDate));
                    currentDate = currentDate.plusDays(1);
                }
                newMinStayDTO.setDates(allDatesBetweendifferenceDaysReservartion);
                theListOfAllDaysWithMinStayBasedOnRules.add(newMinStayDTO);
            }
            return theListOfAllDaysWithMinStayBasedOnRules;

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new InternalException("The reservations could not be filtered based on the specifications provided");
        }
    }


    public RatesDTO updateApartmentAvailability(List<MinStayDTO> minStayDTOList) {

        RatesDTO ratesDTO = new RatesDTO();
        List<Operation> allOperationsForThisApartment = new ArrayList<>();
        List<Integer> apartmentsIds = new ArrayList<>();
        for (MinStayDTO minStayDTO : minStayDTOList) {
            Operation newOperation = new Operation();
            newOperation.setDates(minStayDTO.getDates());
            newOperation.setMin_length_of_stay(minStayDTO.getMinStay());
            //TODO
            newOperation.setDaily_price(15);
            allOperationsForThisApartment.add(newOperation);
        }
        apartmentsIds.add(minStayDTOList.get(0).getApartmentId());
        ratesDTO.setApartments(apartmentsIds);
        ratesDTO.setOperations(allOperationsForThisApartment);
        return ratesDTO;
    }
}
