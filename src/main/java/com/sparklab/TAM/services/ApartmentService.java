package com.sparklab.TAM.services;

import com.sparklab.TAM.configuration.SmoobuConfiguration;
import com.sparklab.TAM.dto.apartment.ApartmentLinkedApartmentOption;
import com.sparklab.TAM.dto.apartment.ApartmentOptionsWithCategory;
import com.sparklab.TAM.dto.apartment.ApartmentOptionswithStatuses;
import com.sparklab.TAM.dto.smoobu.SmoobuGetApartmentsResponse;
import com.sparklab.TAM.dto.apartment.Apartment;
import com.sparklab.TAM.dto.smoobu.SmoobuShortApartmentDTO;
import com.sparklab.TAM.exceptions.ApiCallError;
import com.sparklab.TAM.exceptions.NotFoundException;
import com.sparklab.TAM.model.ApartmentLinkedapartmentOptions;
import com.sparklab.TAM.model.ApartmentOption;
import com.sparklab.TAM.model.ApartmentOptionCategory;
import com.sparklab.TAM.repositories.ApartmentLinkedApartmentOptionRepository;
import com.sparklab.TAM.repositories.ApartmentOptionRepository;
import com.sun.jdi.InternalException;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;


@Service
@AllArgsConstructor
public class ApartmentService {
    private final ApiCallService apiCallService;
    private final SmoobuConfiguration smoobuConfiguration;
    private final ApartmentOptionRepository apartmentOptionRepository;
    private final ApartmentLinkedApartmentOptionRepository apartmentLinkedApartmentOptionRepository;
    private static final Logger logger = LogManager.getLogger(ChannelService.class);

    public SmoobuGetApartmentsResponse getAllApartments(String userId) {
        String apiUrl = smoobuConfiguration.getApiURI()+"apartments";
        Long parseUserId =Long.parseLong(userId);
        try {
            return apiCallService.getMethod(apiUrl, SmoobuGetApartmentsResponse.class,parseUserId);

        } catch (ApiCallError e) {

            if (e.getErrorCode() == HttpStatusCode.valueOf(422)) {
                logger.error(e.getMessage(), e);
                throw new ApiCallError(e.getErrorCode(), "Response cannot be parsed as Apartment object");
            }
            logger.error(e.getMessage(), e);
            throw new InternalException();
        }
    }



    public Apartment getApartmentById(String userId,int id) {
        Long parseUserId =Long.parseLong(userId);
        String apiUrl = smoobuConfiguration.getApiURI()+"apartments/" + id;

        try {
            Apartment apartment=apiCallService.getMethod(apiUrl, Apartment.class,parseUserId);
            return apartment;

        } catch (ApiCallError e) {

            if (e.getErrorCode() == HttpStatusCode.valueOf(422)) {
                logger.error(e.getMessage(), e);
                throw new ApiCallError(e.getErrorCode(), "Response cannot be parsed as Apartment object");
            }
            logger.error(e.getMessage(), e);
            throw new InternalException();
        }
    }

    public String saveApartmentOption(ApartmentLinkedApartmentOption apartmentLinkedApartmentOption, String userId) {
        for (ApartmentOption apartmentOption : apartmentLinkedApartmentOption.getApartmentOptions()) {
            ApartmentLinkedapartmentOptions newapartmentLinkedApartmentOption = new ApartmentLinkedapartmentOptions();
            newapartmentLinkedApartmentOption.setApartmentId(apartmentLinkedApartmentOption.getApartmentId());
            newapartmentLinkedApartmentOption.setApartmentOption(apartmentOption);
            apartmentLinkedApartmentOptionRepository.save(newapartmentLinkedApartmentOption);
        }
        return "Apartment option saved successfully";
    }


    @Transactional
    public String deleteApartmentOption(ApartmentLinkedApartmentOption apartmentLinkedApartmentOption) {
           for(ApartmentOption apartmentOption:apartmentLinkedApartmentOption.getApartmentOptions())
           {apartmentLinkedApartmentOptionRepository.deleteByApartmentIdAndApartmentOptionId(apartmentLinkedApartmentOption.getApartmentId(),apartmentOption.getId());
           }
        return "Apartment Option was deleted successfully";
    }

    public ApartmentLinkedApartmentOption getAllApartmentOptions(String apartmentId) {
        try {
            Long parseApartmentId = Long.parseLong(apartmentId);
            List<ApartmentOption> apartmentOptions = apartmentOptionRepository.findAll();
            List<ApartmentLinkedapartmentOptions> apartmentLinkedapartmentOptionsList = apartmentLinkedApartmentOptionRepository.findByApartmentId(parseApartmentId);


            TreeMap<ApartmentOptionCategory, List<ApartmentOptionswithStatuses>> categoryMap = new TreeMap<>(Comparator.comparingLong(ApartmentOptionCategory::getId));

            for (ApartmentOption apartmentOption : apartmentOptions) {
                ApartmentOptionCategory category = apartmentOption.getApartmentOptionCategory();


                categoryMap.putIfAbsent(category, new ArrayList<>());


                ApartmentOptionswithStatuses newApartmentOptionswithStatuses = new ApartmentOptionswithStatuses();
                newApartmentOptionswithStatuses.setChecked(false);
                newApartmentOptionswithStatuses.setOptionId(apartmentOption.getId());
                newApartmentOptionswithStatuses.setDescription(apartmentOption.getDescription());
                newApartmentOptionswithStatuses.setDetails(apartmentOption.getDetails());
                newApartmentOptionswithStatuses.setApartmentOptionCategory(category);


                for (ApartmentLinkedapartmentOptions linkedApartmentOption : apartmentLinkedapartmentOptionsList) {
                    if (linkedApartmentOption.getApartmentOption().getId() == apartmentOption.getId()) {
                        newApartmentOptionswithStatuses.setChecked(true);
                        break;
                    }
                }


                categoryMap.get(category).add(newApartmentOptionswithStatuses);
            }


            ApartmentLinkedApartmentOption apartmentLinkedApartmentOption = new ApartmentLinkedApartmentOption();
            apartmentLinkedApartmentOption.setApartmentId(parseApartmentId);


            List<ApartmentOptionsWithCategory> apartmentOptionsWithCategories = new ArrayList<>();
            for (Map.Entry<ApartmentOptionCategory, List<ApartmentOptionswithStatuses>> entry : categoryMap.entrySet()) {
                ApartmentOptionsWithCategory optionsWithCategory = new ApartmentOptionsWithCategory();
                optionsWithCategory.setCategory(entry.getKey());
                optionsWithCategory.setApartmentOptions(entry.getValue());
                apartmentOptionsWithCategories.add(optionsWithCategory);
            }

            apartmentLinkedApartmentOption.setApartmentOptionsWithCategories(apartmentOptionsWithCategories);

            return apartmentLinkedApartmentOption;
        } catch (Exception e) {
            throw new NotFoundException("There was an error while calling this request. Please try again later.");
        }
    }

}
