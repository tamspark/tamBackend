package com.sparklab.TAM.dto.apartment;

import com.sparklab.TAM.model.ApartmentOption;
import lombok.Data;

import java.util.List;

@Data
public class ApartmentLinkedApartmentOption {

    List<ApartmentOptionswithStatuses> apartmentOptionsWithStatuses;
    List<ApartmentOption> apartmentOptions;
    List<ApartmentOptionsWithCategory> apartmentOptionsWithCategories;
    Long apartmentId;
}
